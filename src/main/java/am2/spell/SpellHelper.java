package am2.spell;

import am2.AMCore;
import am2.api.blocks.IKeystoneLockable;
import am2.api.events.ManaCostEvent;
import am2.api.events.SpellCastingEvent;
import am2.api.items.KeystoneAccessType;
import am2.api.spell.ItemSpellBase;
import am2.api.spell.component.interfaces.ISpellComponent;
import am2.api.spell.component.interfaces.ISpellModifier;
import am2.api.spell.component.interfaces.ISpellShape;
import am2.api.spell.enums.SpellCastResult;
import am2.api.spell.enums.SpellModifiers;
import am2.armor.ArmorHelper;
import am2.armor.ArsMagicaArmorMaterial;
import am2.blocks.BlocksCommonProxy;
import am2.buffs.BuffList;
import am2.entities.EntityDarkMage;
import am2.entities.EntityLightMage;
import am2.entities.EntitySpellEffect;
import am2.items.ItemKeystone;
import am2.items.ItemsCommonProxy;
import am2.network.AMDataWriter;
import am2.network.AMNetHandler;
import am2.network.AMPacketIDs;
import am2.playerextensions.ExtendedProperties;
import am2.spell.SpellUtils.SpellRequirements;
import am2.spell.components.FireDamage;
import am2.spell.modifiers.Colour;
import am2.utility.EntityUtilities;
import am2.utility.KeystoneUtilities;
import cpw.mods.fml.common.Loader;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class SpellHelper{

	public static final SpellHelper instance = new SpellHelper();

	private SpellHelper(){
	}

	public SpellCastResult applyStageToGround(ItemStack stack, EntityLivingBase caster, World world, int blockX, int blockY, int blockZ, int blockFace, double impactX, double impactY, double impactZ, int stage, boolean consumeMBR){
		ISpellShape stageShape = SpellUtils.instance.getShapeForStage(stack, 0);
		if (stageShape == null || stageShape == SkillManager.instance.missingShape){
			return SpellCastResult.MALFORMED_SPELL_STACK;
		}

		float mana = SpellUtils.instance.getSpellRequirements(stack, caster).manaCost;

		ISpellComponent[] components = SpellUtils.instance.getComponentsForStage(stack, 0);

		for (ISpellComponent component : components){

			if (SkillTreeManager.instance.isSkillDisabled(component))
				continue;

			//special logic for spell sealed doors
			if (BlocksCommonProxy.spellSealedDoor.applyComponentToDoor(world, component, blockX, blockY, blockZ))
				continue;

			if (canApplyToBlock(stack, caster, world, blockX, blockY, blockZ)){
				if (component.applyEffectBlock(stack, world, blockX, blockY, blockZ, blockFace, impactX, impactY, impactZ, caster)){
					if (world.isRemote){
						int color = -1;
						if (SpellUtils.instance.modifierIsPresent(SpellModifiers.COLOR, stack, 0)){
							ISpellModifier[] mods = SpellUtils.instance.getModifiersForStage(stack, 0);
							int ordinalCount = 0;
							for (ISpellModifier mod : mods){
								if (mod instanceof Colour){
									byte[] meta = SpellUtils.instance.getModifierMetadataFromStack(stack, mod, 0, ordinalCount++);
									color = (int)mod.getModifier(SpellModifiers.COLOR, null, null, null, meta);
								}
							}
						}
						component.spawnParticles(world, blockX, blockY, blockZ, caster, caster, world.rand, color);
					}
					if (consumeMBR)
						SpellUtils.instance.doAffinityShift(caster, component, stageShape, mana);
				}
			}
		}

		if (lingeringSpellZoneList.contains(stack)) {
			lingeringSpellZoneList.remove(stack);
			return SpellCastResult.SUCCESS;
		}
		int persistenceModifiers = SpellUtils.instance.getModifiedInt_Add(0, stack, caster, caster, world, 0, SpellModifiers.LINGERING);
		if (persistenceModifiers > 0){
			int timeToNextCast = SpellUtils.instance.getModifiedInt_Mul(15, stack, caster, caster, world, 0, SpellModifiers.DURATION);
			lingeringSpellList.add(new LingeringSpell(persistenceModifiers, stack, world, caster, blockX, blockY, blockZ, blockFace, impactX, impactY, impactZ, timeToNextCast));
		}
		return SpellCastResult.SUCCESS;
	}

	public SpellCastResult applyStageToEntity(ItemStack stack, EntityLivingBase caster, World world, Entity target, int stage, boolean shiftAffinityAndXP){
		ISpellShape stageShape = SpellUtils.instance.getShapeForStage(stack, 0);
		if (stageShape == null) return SpellCastResult.MALFORMED_SPELL_STACK;

		if ((!AMCore.config.getAllowCreativeTargets()) && target instanceof EntityPlayerMP && ((EntityPlayerMP) target).capabilities.isCreativeMode) {
			return SpellCastResult.EFFECT_FAILED;
		}

		float mana = SpellUtils.instance.getSpellRequirements(stack, caster).manaCost;

		ISpellComponent[] components = SpellUtils.instance.getComponentsForStage(stack, 0);

		boolean appliedOneComponent = false;

		for (ISpellComponent component : components){

			if (SkillTreeManager.instance.isSkillDisabled(component))
				continue;

			if (canApplyToEntity(stack, caster, world, target)){
				if (component.applyEffectEntity(stack, world, caster, target)){
					appliedOneComponent = true;
					if (world.isRemote){
						int color = -1;
						if (SpellUtils.instance.modifierIsPresent(SpellModifiers.COLOR, stack, 0)){
							ISpellModifier[] mods = SpellUtils.instance.getModifiersForStage(stack, 0);
							int ordinalCount = 0;
							for (ISpellModifier mod : mods){
								if (mod instanceof Colour){
									byte[] meta = SpellUtils.instance.getModifierMetadataFromStack(stack, mod, 0, ordinalCount++);
									color = (int)mod.getModifier(SpellModifiers.COLOR, null, null, null, meta);
								}
							}
						}
						component.spawnParticles(world, target.posX, target.posY + target.getEyeHeight(), target.posZ, caster, target, world.rand, color);
					}
					if (shiftAffinityAndXP)
						SpellUtils.instance.doAffinityShift(caster, component, stageShape, mana);
				}
			}
		}

		if (lingeringSpellZoneList.contains(stack)) {
			lingeringSpellZoneList.remove(stack);
			return SpellCastResult.SUCCESS;
		}
		if (appliedOneComponent){
			int persistenceModifiers = SpellUtils.instance.getModifiedInt_Add(0, stack, caster, target, world, 0, SpellModifiers.LINGERING);
			if (persistenceModifiers > 0){
				int timeToNextCast = SpellUtils.instance.getModifiedInt_Mul(15, stack, caster, target, world, 0, SpellModifiers.DURATION);
				lingeringSpellList.add(new LingeringSpell(persistenceModifiers, stack, world, caster, target, timeToNextCast));
			}
			return SpellCastResult.SUCCESS;
		} else{
			return SpellCastResult.EFFECT_FAILED;
		}
	}

	private boolean canApplyToEntity(ItemStack stack, EntityLivingBase caster, World world, Entity target){
		if (SpellUtils.instance.getModifiedInt_Add(SpellModifiers.KEYSTONE_REC, stack, caster, target, world, 0) != 0 && target instanceof EntityPlayer && caster instanceof EntityPlayer) {
			long casterKey = -1;
			long targetKey = -1;
			for (int i = 0; i < ((EntityPlayer)caster).inventory.mainInventory.length; ++i) {
				ItemStack stack1 = ((EntityPlayer)caster).inventory.getStackInSlot(i);
				if (stack1 == null || stack1.getItem() != ItemsCommonProxy.keystone) continue;
				casterKey = ((ItemKeystone) stack1.getItem()).getKey(stack1);
				break;
			}
			for (int i = 0; i < ((EntityPlayer)target).inventory.mainInventory.length; ++i) {
				ItemStack stack1 = ((EntityPlayer)target).inventory.getStackInSlot(i);
				if (stack1 == null || stack1.getItem() != ItemsCommonProxy.keystone) continue;
				targetKey = ((ItemKeystone) stack1.getItem()).getKey(stack1);
				break;
			}
			if (casterKey != -1 && (casterKey == targetKey)) return false;
		}
		if (SpellUtils.instance.getModifiedInt_Add(SpellModifiers.TARGET_PLAYERS, stack, caster, target, world, 0) != 0 && !(target instanceof EntityPlayer)) {
			return false;
		} else if (SpellUtils.instance.getModifiedInt_Add(SpellModifiers.TARGET_MONSTERS, stack, caster, target, world, 0) != 0 && !(target instanceof EntityMob)) {
			return false;
		} else if (SpellUtils.instance.getModifiedInt_Add(SpellModifiers.TARGET_CREATURES, stack, caster, target, world, 0) != 0 && !(target instanceof EntityAgeable)) {
			return false;
		}
		if (SpellUtils.instance.getModifiedInt_Add(SpellModifiers.TARGET_BLOCKS, stack, caster, target, world, 0) != 0) {
			return false;
		}
		return true;
	}

	private boolean canApplyToBlock(ItemStack stack, EntityLivingBase caster, World world, int x, int y, int z){
		if (SpellUtils.instance.getModifiedInt_Add(SpellModifiers.KEYSTONE_REC, stack, caster, caster, world, 0) != 0 && caster instanceof EntityPlayer) {
			TileEntity te = world.getTileEntity(x, y, z);
			if (te != null) {
				if (te instanceof IKeystoneLockable) {
					if (KeystoneUtilities.instance.canPlayerAccess((IKeystoneLockable)te, (EntityPlayer)caster, KeystoneAccessType.NONE)) {
						return false; // don't want to break keystone blocks you are the owner of
					}
				} else if (te instanceof IInventory) {
					long casterKey = -1;
					for (int i = 0; i < ((EntityPlayer)caster).inventory.mainInventory.length; ++i) {
						ItemStack stack1 = ((EntityPlayer)caster).inventory.getStackInSlot(i);
						if (stack1 == null || stack1.getItem() != ItemsCommonProxy.keystone) continue;
						casterKey = ((ItemKeystone) stack1.getItem()).getKey(stack1);
						break;
					}
					long blockKey = -1;
					for (int i = 0; i < ((IInventory)te).getSizeInventory(); ++i) {
						ItemStack stack1 = ((IInventory)te).getStackInSlot(i);
						if (stack1 == null || stack1.getItem() != ItemsCommonProxy.keystone) continue;
						blockKey = ((ItemKeystone) stack1.getItem()).getKey(stack1);
						break;
					}
					if (casterKey != -1 && (casterKey == blockKey)) return false; // if matching keystone in inventory of TE, don't want to break
				}
			}
		}
		if (SpellUtils.instance.getModifiedInt_Add(SpellModifiers.TARGET_BLOCKS, stack, caster, caster, world, 0) == 0){
			if (SpellUtils.instance.getModifiedInt_Add(SpellModifiers.TARGET_PLAYERS, stack, caster, caster, world, 0) != 0){
				return false;
			}else if (SpellUtils.instance.getModifiedInt_Add(SpellModifiers.TARGET_MONSTERS, stack, caster, caster, world, 0) != 0){
				return false;
			}else if (SpellUtils.instance.getModifiedInt_Add(SpellModifiers.TARGET_CREATURES, stack, caster, caster, world, 0) != 0){
				return false;
			}
		}
		return true;
	}

	private SpellCastingEvent.Pre preSpellCast(ItemStack stack, EntityLivingBase caster, boolean isChanneled){

		SpellRequirements reqs = SpellUtils.instance.getSpellRequirements(stack, caster);

		float manaCost = reqs.manaCost;
		float burnout = reqs.burnout;
		ArrayList<ItemStack> reagents = reqs.reagents;

		ManaCostEvent mce = new ManaCostEvent(stack, caster, manaCost, burnout);

		MinecraftForge.EVENT_BUS.post(mce);

		manaCost = mce.manaCost;
		burnout = mce.burnout;

		SpellCastingEvent.Pre event = new SpellCastingEvent().new Pre(stack, (ItemSpellBase)stack.getItem(), caster, manaCost, burnout, isChanneled);

		if (MinecraftForge.EVENT_BUS.post(event)){
			event.castResult = SpellCastResult.EFFECT_FAILED;
			return event;
		}

		event.castResult = SpellCastResult.SUCCESS;

		if (!SpellUtils.instance.casterHasAllReagents(caster, reagents))
			event.castResult = SpellCastResult.REAGENTS_MISSING;
		if (!SpellUtils.instance.casterHasMana(caster, manaCost))
			event.castResult = SpellCastResult.NOT_ENOUGH_MANA;

		return event;
	}

	public SpellCastResult applyStackStage(ItemStack stack, EntityLivingBase caster, EntityLivingBase target, double x, double y, double z, int side, World world, boolean consumeMBR, boolean giveXP, int ticksUsed){

		if (caster.isPotionActive(BuffList.silence.id))
			return SpellCastResult.SILENCED;

		ItemStack parsedStack = SpellUtils.instance.constructSpellStack(stack);

		if (SpellUtils.instance.numStages(parsedStack) == 0){
			return SpellCastResult.SUCCESS;
		}
		ISpellShape shape = SpellUtils.instance.getShapeForStage(parsedStack, 0);
		ItemSpellBase item = (ItemSpellBase)parsedStack.getItem();

		if (SkillTreeManager.instance.isSkillDisabled(shape))
			return SpellCastResult.EFFECT_FAILED;

		if (!(caster instanceof EntityPlayer)){
			consumeMBR = false;
		}

		SpellCastingEvent.Pre checkEvent = null;
		if (consumeMBR){
			checkEvent = preSpellCast(parsedStack, caster, false);
			if (checkEvent.castResult != SpellCastResult.SUCCESS){
				if (checkEvent.castResult == SpellCastResult.NOT_ENOUGH_MANA && caster.worldObj.isRemote && caster instanceof EntityPlayer){
					AMCore.proxy.flashManaBar();
				}
				SpellCastingEvent.Post event = new SpellCastingEvent().new Post(parsedStack, (ItemSpellBase)parsedStack.getItem(), caster, checkEvent.manaCost, checkEvent.burnout, false, checkEvent.castResult);
				MinecraftForge.EVENT_BUS.post(event);

				return checkEvent.castResult;
			}
		}

		SpellCastResult result = SpellCastResult.MALFORMED_SPELL_STACK;

		if (shape != null){
			int cloaking = SpellUtils.instance.countModifiers(SpellModifiers.CLOAKING, stack, 0);
			if (cloaking > 0){
				double radius = SpellUtils.instance.getModifiedDouble_Add(10, stack, caster, target, world, 0, SpellModifiers.RADIUS);
				int duration = SpellUtils.instance.getModifiedInt_Mul(BuffList.default_buff_duration, stack, caster, target, world, 0, SpellModifiers.DURATION);
				duration = SpellUtils.instance.modifyDurationBasedOnArmor(caster, duration);
				AMDataWriter writer1 = new AMDataWriter();
				writer1.add(duration);
				if (!world.isRemote) AMNetHandler.INSTANCE.sendPacketToAllClientsNear(world.provider.dimensionId, x, y, z, radius, AMPacketIDs.CLOAKING, writer1.generate());
			}
			result = shape.beginStackStage(item, parsedStack, caster, target, world, x, y, z, side, giveXP, ticksUsed);

			if (!world.isRemote){
				AMDataWriter writer = new AMDataWriter();
				writer.add(parsedStack);
				writer.add(caster.getEntityId());
				if (target != null){
					writer.add(true);
					writer.add(target.getEntityId());
				}else{
					writer.add(false);
				}
				writer.add(x).add(y).add(z);
				writer.add(side);
				writer.add(ticksUsed);

				AMNetHandler.INSTANCE.sendPacketToAllClientsNear(world.provider.dimensionId, x, y, z, 32, AMPacketIDs.SPELL_CAST, writer.generate());
			}
		}

		float manaCost = 0;
		float burnout = 0;

		if (consumeMBR){
			manaCost = checkEvent.manaCost;
			burnout = checkEvent.burnout;

			if (result == SpellCastResult.SUCCESS_REDUCE_MANA){
				result = SpellCastResult.SUCCESS;
				manaCost *= 0.2f;
				burnout *= 0.2f;
			}
		}

		if (result == SpellCastResult.SUCCESS){
			if (consumeMBR){
				ExtendedProperties.For(caster).deductMana(manaCost);
				ExtendedProperties.For(caster).addBurnout(burnout);
			}
			if (world.isRemote){
				String sfx = shape.getSoundForAffinity(SpellUtils.instance.mainAffinityFor(parsedStack), parsedStack, null);
				if (sfx != null){
					if (!shape.isChanneled()){
						world.playSound(caster.posX, caster.posY, caster.posZ, sfx, 0.4f, world.rand.nextFloat() * 0.1F + 0.9F, false);
					}else{
						if (world.isRemote) SpellSoundHelper.playLoopingSound((float)x, (float)y, (float)z, sfx, 0.6f, 1f, sfx);
					}
				}
			}
		}

		SpellCastingEvent.Post event = new SpellCastingEvent().new Post(parsedStack, (ItemSpellBase)parsedStack.getItem(), caster, manaCost, burnout, false, result);
		MinecraftForge.EVENT_BUS.post(event);

		return result;
	}

	public SpellCastResult applyStackStageOnUsing(ItemStack stack, EntityLivingBase caster, EntityLivingBase target, double x, double y, double z, World world, boolean consumeMBR, boolean giveXP, int ticks){

		if (SpellUtils.instance.numStages(stack) == 0){
			return SpellCastResult.SUCCESS;
		}

		if (!SpellUtils.instance.spellIsChanneled(stack)){
			return SpellCastResult.EFFECT_FAILED;
		}
		return applyStackStage(stack, caster, target, x, y, z, 0, world, consumeMBR, giveXP, ticks);
	}

	public boolean attackTargetSpecial(ItemStack spellStack, Entity target, DamageSource damagesource, float magnitude){

		if (target.worldObj.isRemote)
			return true;

		EntityPlayer dmgSrcPlayer = null;

		if (damagesource.getSourceOfDamage() != null){
			if (damagesource.getSourceOfDamage() instanceof EntityLivingBase){
				EntityLivingBase source = (EntityLivingBase)damagesource.getSourceOfDamage();
				if ((source instanceof EntityLightMage || source instanceof EntityDarkMage) && target.getClass() == EntityCreeper.class){
					return false;
				}else if (source instanceof EntityLightMage && target instanceof EntityLightMage){
					return false;
				}else if (source instanceof EntityDarkMage && target instanceof EntityDarkMage){
					return false;
				}else if (source instanceof EntityPlayer && target instanceof EntityPlayer && !target.worldObj.isRemote && (!MinecraftServer.getServer().isPVPEnabled() || ((EntityPlayer)target).capabilities.isCreativeMode)){
					return false;
				}

				if (source.isPotionActive(BuffList.fury))
					magnitude += 4;
			}

			if (damagesource.getSourceOfDamage() instanceof EntityPlayer){
				dmgSrcPlayer = (EntityPlayer)damagesource.getSourceOfDamage();
				int armorSet = ArmorHelper.getFullArsMagicaArmorSet(dmgSrcPlayer);
				if (armorSet == ArsMagicaArmorMaterial.MAGE.getMaterialID()){
					magnitude *= 1.05f;
				}else if (armorSet == ArsMagicaArmorMaterial.BATTLEMAGE.getMaterialID()){
					magnitude *= 1.025f;
				}else if (armorSet == ArsMagicaArmorMaterial.ARCHMAGE.getMaterialID()){
					magnitude *= 1.1f;
				}

				ItemStack equipped = (dmgSrcPlayer).getCurrentEquippedItem();
				if (equipped != null && equipped.getItem() == ItemsCommonProxy.arcaneSpellbook){
					magnitude *= 1.1f;
				}
			}
		}

		if (target instanceof EntityLivingBase){
			if (EntityUtilities.isSummon((EntityLivingBase)target) && damagesource.damageType.equals("magic")){
				magnitude *= 3.0f;
			}
		}

		magnitude *= AMCore.config.getDamageMultiplier();

		ItemStack oldItemStack = null;

		boolean success = false;
		if (target instanceof EntityDragon){
			success = ((EntityDragon)target).attackEntityFromPart(((EntityDragon)target).dragonPartHead, damagesource, magnitude);
		}else if ((Loader.isModLoaded("botania") || Loader.isModLoaded("Botania")) && target instanceof EntityLiving && (damagesource.isMagicDamage() || damagesource.isUnblockable() || damagesource.isDamageAbsolute()) && target.getClass().getSimpleName().toLowerCase().contains("doppleganger")) {
			EntityLiving elb = (EntityLiving) target;
			if (elb.getHealth() < magnitude) { // Should be killed
				try {
					Class<?> clazz = Class.forName("vazkii.botania.common.entity.EntityDoppleganger.java");
					Method method = clazz.getMethod("dropFewItems", new Class<?>[]{boolean.class, int.class});
					method.setAccessible(true);
					method.invoke(target, new Object[]{true, 0});
				} catch (Exception e) {
					e.printStackTrace(); // How did we get here?
				}
			}
			FireDamage.causeUnblockableDamage(elb, magnitude);
			success = true;
		} else {
			success = target.attackEntityFrom(damagesource, magnitude);
		}

		if (dmgSrcPlayer != null){
			if (spellStack != null && target instanceof EntityLivingBase){
				if (!target.worldObj.isRemote &&
						((EntityLivingBase)target).getHealth() <= 0 &&
						SpellUtils.instance.modifierIsPresent(SpellModifiers.DISMEMBERING_LEVEL, spellStack, 0)){
					double chance = SpellUtils.instance.getModifiedDouble_Add(0, spellStack, dmgSrcPlayer, target, dmgSrcPlayer.worldObj, 0, SpellModifiers.DISMEMBERING_LEVEL);
					if (dmgSrcPlayer.worldObj.rand.nextDouble() <= chance){
						dropHead(target, dmgSrcPlayer.worldObj);
					}
				}
			}
		}

		return success;
	}

	private void dropHead(Entity target, World world){
		if (target.getClass() == EntitySkeleton.class){
			if (((EntitySkeleton)target).getSkeletonType() == 1){
				dropHead_do(world, target.posX, target.posY, target.posZ, 1);
			}else{
				dropHead_do(world, target.posX, target.posY, target.posZ, 0);
			}
		}else if (target.getClass() == EntityZombie.class){
			dropHead_do(world, target.posX, target.posY, target.posZ, 2);
		}else if (target.getClass() == EntityCreeper.class){
			dropHead_do(world, target.posX, target.posY, target.posZ, 4);
		}else if (target instanceof EntityPlayer){
			dropHead_do(world, target.posX, target.posY, target.posZ, 3);
		}
	}

	private void dropHead_do(World world, double x, double y, double z, int type){
		EntityItem item = new EntityItem(world);
		ItemStack stack = new ItemStack(Items.skull, 1, type);
		item.setEntityItemStack(stack);
		item.setPosition(x, y, z);
		world.spawnEntityInWorld(item);
	}

	public static List<LingeringSpell> lingeringSpellList = new ArrayList<LingeringSpell>();
	// used to not add zone spells twice
	public static List<ItemStack> lingeringSpellZoneList = new ArrayList<ItemStack>();

	public static class LingeringSpell {

		public ItemStack stack = null;
		public World world = null;
		public EntityLivingBase caster = null;
		public EntitySpellEffect zoneEntity = null;
		public Entity target = null;
		// zone spells linger in a special way, think of a 'Cloak' spell
		public boolean zoneSpell = false;
		// if zone, determines how long it lasts
		public int timeToNextCastConstant = -1;
		private int timeToNextCast = -1;
		private int amountOfModifiers = 1;
		public boolean applyToBlock = false;

		public int blockX = -1;
		public int blockY = -1;
		public int blockZ = -1;
		public int blockFace = -1;
		public double impactX = -1;
		public double impactY = -1;
		public double impactZ = -1;

		private LingeringSpell(int amountOfModifiers, ItemStack stack, World world, EntityLivingBase caster, int timeToNextCast) {
			this.amountOfModifiers = amountOfModifiers;
			this.stack = stack;
			this.caster = caster;
			this.world = world;
			this.timeToNextCast = timeToNextCast;
			this.timeToNextCastConstant = timeToNextCast;
		}

	    // lingers on an entity
		public LingeringSpell(int amountOfModifiers, ItemStack stack, World world, EntityLivingBase caster, Entity target, int timeToNextCast) {
			this(amountOfModifiers, stack, world, caster, timeToNextCast);
			this.target = target;
			this.applyToBlock = false;
			this.zoneSpell = false;
		}

		// lingers on an entity, zone
		public LingeringSpell(int amountOfModifiers, ItemStack stack, World world, EntityLivingBase caster, Entity target, int timeToNextCast, EntitySpellEffect zoneEntity) {
			this(amountOfModifiers, stack, world, caster, target, timeToNextCast);
			this.zoneEntity = zoneEntity;
			this.zoneSpell = true;
		}

		// lingers on a block
		public LingeringSpell(int amountOfModifiers, ItemStack stack, World world, EntityLivingBase caster, int blockX, int blockY, int blockZ, int blockFace, double impactX, double impactY, double impactZ, int timeToNextCast) {
			this(amountOfModifiers, stack, world, caster, timeToNextCast);
			this.blockX = blockX;
			this.blockY = blockY;
			this.blockZ = blockZ;
			this.blockFace = blockFace;
			this.impactX = impactX;
			this.impactY = impactY;
			this.impactZ = impactZ;
			this.applyToBlock = true;
		}

		// true if needs to delete from list
		public boolean doUpdate() {
			if (this.zoneSpell) {
				if (this.zoneEntity != null) {
					if (this.target != null) this.zoneEntity.setPosition(this.target.posX, this.target.posY, this.target.posZ);
					else this.zoneEntity.setPosition(this.caster.posX, this.caster.posY, this.caster.posZ);
				}
				// counter code still present
				timeToNextCast--;
				if (timeToNextCast <= 0) {
					timeToNextCast = timeToNextCastConstant;
					amountOfModifiers--;
					if (amountOfModifiers <= 0) return true;
				}
			} else {
				timeToNextCast--;
				if (timeToNextCast <= 0) {
					timeToNextCast = timeToNextCastConstant;
					if (this.applyToBlock) doCastBlock();
					else doCastEntity();
					amountOfModifiers--;
					if (amountOfModifiers <= 0) return true;
				}
			}
			return false;
		}

		private void doCastEntity(){
			ISpellComponent[] components = SpellUtils.instance.getComponentsForStage(stack, 0);
			for (ISpellComponent component : components){
				if (SpellHelper.instance.canApplyToEntity(stack, caster, world, target)){
					if (component.applyEffectEntity(stack, world, caster, target)){
						if (world.isRemote){
							int color = -1;
							if (SpellUtils.instance.modifierIsPresent(SpellModifiers.COLOR, stack, 0)){
								ISpellModifier[] mods = SpellUtils.instance.getModifiersForStage(stack, 0);
								int ordinalCount = 0;
								for (ISpellModifier mod : mods){
									if (mod instanceof Colour){
										byte[] meta = SpellUtils.instance.getModifierMetadataFromStack(stack, mod, 0, ordinalCount++);
										color = (int)mod.getModifier(SpellModifiers.COLOR, null, null, null, meta);
									}
								}
							}
							component.spawnParticles(world, target.posX, target.posY + target.getEyeHeight(), target.posZ, caster, target, world.rand, color);
						}
					}
				}
			}
		}

		private void doCastBlock(){
			ISpellComponent[] components = SpellUtils.instance.getComponentsForStage(stack, 0);
			for (ISpellComponent component : components){
				if (BlocksCommonProxy.spellSealedDoor.applyComponentToDoor(world, component, blockX, blockY, blockZ))
					continue;
				if (SpellHelper.instance.canApplyToBlock(stack, caster, world, blockX, blockY, blockZ)){
					if (component.applyEffectBlock(stack, world, blockX, blockY, blockZ, blockFace, impactX, impactY, impactZ, caster)){
						if (world.isRemote){
							int color = -1;
							if (SpellUtils.instance.modifierIsPresent(SpellModifiers.COLOR, stack, 0)){
								ISpellModifier[] mods = SpellUtils.instance.getModifiersForStage(stack, 0);
								int ordinalCount = 0;
								for (ISpellModifier mod : mods){
									if (mod instanceof Colour){
										byte[] meta = SpellUtils.instance.getModifierMetadataFromStack(stack, mod, 0, ordinalCount++);
										color = (int)mod.getModifier(SpellModifiers.COLOR, null, null, null, meta);
									}
								}
							}
							component.spawnParticles(world, blockX, blockY, blockZ, caster, caster, world.rand, color);
						}
					}
				}
			}
		}
	}
}
