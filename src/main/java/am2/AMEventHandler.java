package am2;

import am2.affinity.AffinityHelper;
import am2.api.ArsMagicaApi;
import am2.api.events.ManaCostEvent;
import am2.api.power.IPowerNode;
import am2.api.power.PowerTypes;
import am2.api.spell.enums.Affinity;
import am2.api.spell.enums.BuffPowerLevel;
import am2.armor.ArmorHelper;
import am2.armor.infusions.GenericImbuement;
import am2.blocks.BlocksCommonProxy;
import am2.blocks.tileentities.TileEntityAstralBarrier;
import am2.blocks.tileentities.TileEntityInfusedStem;
import am2.bosses.BossSpawnHelper;
import am2.buffs.BuffEffectScrambleSynapses;
import am2.buffs.BuffEffectTemporalAnchor;
import am2.buffs.BuffList;
import am2.buffs.BuffStatModifiers;
import am2.damage.DamageSourceFire;
import am2.damage.DamageSources;
import am2.entities.*;
import am2.items.ItemSoulspike;
import am2.items.ItemsCommonProxy;
import am2.network.AMNetHandler;
import am2.playerextensions.AffinityData;
import am2.playerextensions.ExtendedProperties;
import am2.playerextensions.RiftStorage;
import am2.playerextensions.SkillData;
import am2.power.PowerNodeRegistry;
import am2.spell.SkillManager;
import am2.spell.SkillTreeManager;
import am2.spell.SpellSoundHelper;
import am2.utility.*;
import am2.worldgen.dynamic.DynamicBossWorldHelper;
import am2.worldgen.dynamic.DynamicBossWorldProvider;
import am2.worldgen.smartgen.WorldGenStructures;
import am2.worldgen.smartgen.generic.SelectionRenderer;
import am2.worldgen.smartgen.struct.info.StructureEntityInfo;
import cofh.api.energy.IEnergyHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.Event.Result;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent;
import cpw.mods.fml.relauncher.ReflectionHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ivorius.ivtoolkit.rendering.grid.GridRenderer;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.stats.AchievementList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraftforge.client.event.RenderBlockOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.event.brewing.PotionBrewedEvent;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.living.LivingEvent.LivingJumpEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.player.*;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.tclproject.mysteriumlib.asm.fixes.MysteriumPatchesFixesMagicka;
import org.lwjgl.opengl.GL11;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

import static am2.PlayerTracker.soulbound_Storage;
import static am2.PlayerTracker.storeSoulboundItemsForRespawn;
import static am2.blocks.liquid.BlockLiquidEssence.liquidEssenceMaterial;

public class AMEventHandler{

	@SubscribeEvent
	public void onPotionBrewed(PotionBrewedEvent brewEvent){
		for (ItemStack stack : brewEvent.brewingStacks){
			if (stack == null) continue;
			if (stack.getItem() instanceof ItemPotion){
				ItemPotion ptn = ((ItemPotion)stack.getItem());
				List<PotionEffect> fx = ptn.getEffects(stack.getItemDamage());
				if (fx == null) return;
				for (PotionEffect pe : fx){
					if (pe.getPotionID() == BuffList.greaterManaPotion.id){
						stack = InventoryUtilities.replaceItem(stack, ItemsCommonProxy.greaterManaPotion);
						break;
					}else if (pe.getPotionID() == BuffList.epicManaPotion.id){
						stack = InventoryUtilities.replaceItem(stack, ItemsCommonProxy.epicManaPotion);
						break;
					}else if (pe.getPotionID() == BuffList.legendaryManaPotion.id){
						stack = InventoryUtilities.replaceItem(stack, ItemsCommonProxy.legendaryManaPotion);
						break;
					}
				}
			}
		}
	}

	@SubscribeEvent
	public void onPlayerTick(TickEvent.PlayerTickEvent event)
	{
		StructureEntityInfo structureEntityInfo = StructureEntityInfo.getStructureEntityInfo(event.player);
		if (structureEntityInfo != null)
		{
			structureEntityInfo.update(event.player);
		}
	}

	// no inter mod events to the bundled rccomplex lite
//	@SubscribeEvent
//	public void onTick(TickEvent event)
//	{
//		if ((event.type == TickEvent.Type.CLIENT || event.type == TickEvent.Type.SERVER) && event.phase == TickEvent.Phase.END)
//		{
//			AMCore.communicationHandler.handleMessages(event.type == TickEvent.Type.SERVER, true);
//		}
//	}

	public static List<EntityGeneric.BiomeEntitySpawnEntry> spawnEntries = new ArrayList<>();

	// custom mob spawning system for more customization

	@SubscribeEvent
	public void entitySpawnEvent(LivingSpawnEvent.CheckSpawn event) {
		if (event.world.provider.terrainType != WorldType.FLAT && event.getResult() != Result.DENY && !(event.entity instanceof EntityGeneric))
		{
			for (EntityGeneric.BiomeEntitySpawnEntry spawnEntry : spawnEntries) {
				if (contains(spawnEntry.dimensions, event.world.provider.dimensionId)) {
					int x = MathHelper.floor_float(event.x);
					int y = MathHelper.floor_float(event.y);
					int z = MathHelper.floor_float(event.z);
					if (!spawnEntry.spawnOnSurface || event.world.canBlockSeeTheSky(x, y + 1, z)) {
						if (!spawnEntry.spawnInDark || isValidLightLevelForSpawningInDark(event.world, x, y, z)) {
							if ((spawnEntry.minYSpawn < event.y || spawnEntry.minYSpawn == -1) && (spawnEntry.maxYSpawn > event.y || spawnEntry.maxYSpawn == -1)) {
								BiomeGenBase biome = event.world.getBiomeGenForCoords(x, z);
								for (int i = 0; i < spawnEntry.biomeTypes.length; i++) {
									if (BiomeDictionary.isBiomeOfType(biome, spawnEntry.biomeTypes[i])) {
										if (rand.nextFloat() < (spawnEntry.biomeWeights[i] * AMCore.config.getSpecialEntitySpawnMultiplier())) { // basic chance check to spawn the entity
											EntityGeneric entityToSpawn = GenericEntityTemplateRegistry.INSTANCE.createBasicEntityInstance(event.world, spawnEntry.builder);
											if (SpawnBlacklists.entityCanSpawnHere(x, z, event.world, entityToSpawn)) {
												entityToSpawn.setLocationAndAngles(event.x, event.y, event.z, rand.nextFloat() * 360.0F, 0.0F);
												event.world.spawnEntityInWorld(entityToSpawn);
											}
										}
										return;
									}
								}
							}
						}
					}
				}
			}
		}
	}

	public static boolean contains(final int[] array, final int key) {
		for(int i = 0; i < array.length; i++) {
			if(array[i]==key) return true;
		}
		return false;
	}

	// from the Mob class, slightly modified
	public static boolean isValidLightLevelForSpawningInDark(World worldObj, int x, int y, int z)
	{
		if (worldObj.getSavedLightValue(EnumSkyBlock.Sky, x, y, z) > rand.nextInt(32))
		{
			return false;
		}
		else
		{
			int l = worldObj.getBlockLightValue(x, y, z);

			if (worldObj.isThundering())
			{
				int i1 = worldObj.skylightSubtracted;
				worldObj.skylightSubtracted = 10;
				l = worldObj.getBlockLightValue(x, y, z);
				worldObj.skylightSubtracted = i1;
			}

			return l <= rand.nextInt(7);
		}
	}

	public static final Set<StructureBoundingBox> disabledTileDropAreas = new HashSet<>();
	private WorldGenStructures worldGenStructures = new WorldGenStructures();;

	@SubscribeEvent
	public void onPreChunkDecoration(PopulateChunkEvent.Pre event)
	{
		worldGenStructures.generate(event.rand, event.chunkX, event.chunkZ, event.world, event.chunkProvider, event.chunkProvider);
	}

	@SubscribeEvent
	public void onEntityConstruction(EntityEvent.EntityConstructing event)
	{
		if (event.entity instanceof EntityPlayer)
		{
			StructureEntityInfo.initInEntity(event.entity);
		}
	}

	@SubscribeEvent
	public void onEntityDrop(EntityJoinWorldEvent event)
	{
		if (event.entity instanceof EntityItem)
		{
			final int entityX = MathHelper.floor_double(event.entity.posX);
			final int entityY = MathHelper.floor_double(event.entity.posY);
			final int entityZ = MathHelper.floor_double(event.entity.posZ);

			if (disabledTileDropAreas.stream().anyMatch(input -> input.isVecInside(entityX, entityY, entityZ)))
				event.setCanceled(true);
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onDrawWorld(RenderWorldLastEvent event)
	{
		Minecraft mc = Minecraft.getMinecraft();
		int ticks = mc.thePlayer.ticksExisted;

		EntityLivingBase renderEntity = mc.renderViewEntity;
		StructureEntityInfo info = StructureEntityInfo.getStructureEntityInfo(mc.thePlayer);
		double entityX = renderEntity.lastTickPosX + (renderEntity.posX - renderEntity.lastTickPosX) * (double) event.partialTicks;
		double entityY = renderEntity.lastTickPosY + (renderEntity.posY - renderEntity.lastTickPosY) * (double) event.partialTicks;
		double entityZ = renderEntity.lastTickPosZ + (renderEntity.posZ - renderEntity.lastTickPosZ) * (double) event.partialTicks;

		GL11.glPushMatrix();
		GL11.glTranslated(-entityX, -entityY, -entityZ);

		if (info != null && info.showGrid)
		{
			int spacing = 10;
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glColor3f(0.5f, 0.5f, 0.5f);
			GL11.glPushMatrix();
			GL11.glTranslatef(MathHelper.floor_double(entityX / spacing) * spacing, MathHelper.floor_double(entityY / spacing) * spacing, MathHelper.floor_double(entityZ / spacing) * spacing);
			GridRenderer.renderGrid(8, spacing, 100, 0.05f);
			GL11.glPopMatrix();
			GL11.glEnable(GL11.GL_TEXTURE_2D);
		}

		SelectionRenderer.renderSelection(mc.thePlayer, ticks, event.partialTicks);

		if (info != null && info.danglingOperation != null)
			info.danglingOperation.renderPreview(info.getPreviewType(), mc.theWorld, ticks, event.partialTicks);

		GL11.glPopMatrix();
	}

	@SubscribeEvent
	public void onUnload(WorldEvent.Unload we) {
		soulbound_Storage.clear();
	}

	@SubscribeEvent
	public void onEndermanTeleport(EnderTeleportEvent event){
		EntityLivingBase ent = event.entityLiving;


		ArrayList<Long> keystoneKeys = KeystoneUtilities.instance.GetKeysInInvenory(ent);
		TileEntityAstralBarrier blockingBarrier = DimensionUtilities.GetBlockingAstralBarrier(event.entityLiving.worldObj, (int)event.targetX, (int)event.targetY, (int)event.targetZ, keystoneKeys);

		if (ent.isPotionActive(BuffList.astralDistortion.id) || blockingBarrier != null){
			event.setCanceled(true);
			if (blockingBarrier != null){
				blockingBarrier.onEntityBlocked(ent);
			}
			return;
		}

		if (!ent.worldObj.isRemote && ent instanceof EntityEnderman && ent.worldObj.rand.nextDouble() < 0.01f){
			EntityFlicker flicker = new EntityFlicker(ent.worldObj);
			flicker.setPosition(ent.posX, ent.posY, ent.posZ);
			flicker.setFlickerType(Affinity.ENDER);
			ent.worldObj.spawnEntityInWorld(flicker);
		}
	}

	@SubscribeEvent
	public void onEntityConstructing(EntityConstructing event){
		if (event.entity instanceof EntityLivingBase){
			event.entity.registerExtendedProperties(ExtendedProperties.identifier, new ExtendedProperties());
			((EntityLivingBase)event.entity).getAttributeMap().registerAttribute(ArsMagicaApi.maxManaBonus);
			((EntityLivingBase)event.entity).getAttributeMap().registerAttribute(ArsMagicaApi.maxBurnoutBonus);
			((EntityLivingBase)event.entity).getAttributeMap().registerAttribute(ArsMagicaApi.xpGainModifier);
			((EntityLivingBase)event.entity).getAttributeMap().registerAttribute(ArsMagicaApi.burnoutReductionRate);
			((EntityLivingBase)event.entity).getAttributeMap().registerAttribute(ArsMagicaApi.manaRegenTimeModifier);

			if (event.entity instanceof EntityPlayer){
				event.entity.registerExtendedProperties(RiftStorage.identifier, new RiftStorage());
				event.entity.registerExtendedProperties(AffinityData.identifier, new AffinityData());
				event.entity.registerExtendedProperties(SkillData.identifier, new SkillData((EntityPlayer)event.entity));
			}
		}else if (event.entity instanceof EntityItemFrame){
			AMCore.proxy.itemFrameWatcher.startWatchingFrame((EntityItemFrame)event.entity);
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onEntityDeathChrono(LivingDeathEvent event){
		EntityLivingBase soonToBeDead = event.entityLiving;
		if (soonToBeDead.isPotionActive(BuffList.temporalAnchor.id)){
			event.setCanceled(true);
			PotionEffect pe = soonToBeDead.getActivePotionEffect(BuffList.temporalAnchor);
			if (pe instanceof BuffEffectTemporalAnchor){
				BuffEffectTemporalAnchor buff = (BuffEffectTemporalAnchor)pe;
				buff.stopEffect(soonToBeDead);
			}
			soonToBeDead.removePotionEffect(BuffList.temporalAnchor.id);
			return;
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onEntityDeathHighPriority(LivingDeathEvent event){
		EntityLivingBase soonToBeDead = event.entityLiving;

		if (soonToBeDead instanceof EntityPlayer) { // soul fragments: die with at least 5 rare items
			if (soonToBeDead.isPotionActive(BuffList.psychedelic)){
				if (soonToBeDead.worldObj.provider.dimensionId == 1 && soonToBeDead.getActivePotionEffect(BuffList.psychedelic).getAmplifier() == 1) {
					EntityPlayer player = (EntityPlayer)soonToBeDead;
					int slotCount = 0;
					int rareCount = 0;
					for (ItemStack stack : player.inventory.mainInventory){
						if (stack != null) {
							if (stack.getRarity() != EnumRarity.common) {
								player.inventory.setInventorySlotContents(slotCount, null);
								rareCount++;
							}
						}
						slotCount++;
					}
					slotCount = 0;
					for (ItemStack stack : player.inventory.armorInventory){
						if (stack != null) {
							if (stack.getRarity() != EnumRarity.common) {
								player.inventory.setInventorySlotContents(slotCount + player.inventory.mainInventory.length, null);
								rareCount++;
							}
						}
						slotCount++;
					}
					if (rareCount >= 5) {
						EntityItem fragment = new EntityItem(player.worldObj);
						ItemStack stack = new ItemStack(ItemsCommonProxy.itemOre, 1, ItemsCommonProxy.itemOre.META_SOULFRAGMENT);
						fragment.setPosition(player.posX+rand.nextInt(5)-2, player.posY + 10, player.posZ+rand.nextInt(5)-2);
						fragment.setEntityItemStack(stack);
						player.worldObj.spawnEntityInWorld(fragment);
						player.worldObj.playSoundAtEntity(player, "ambient.weather.thunder",2F, 2F);
						// compatibility with Hard Mode: The player doesn't actually die
						event.setCanceled(true);
						player.removePotionEffect(BuffList.psychedelic.id);
						player.setHealth(2); // 1 heart
					}
				}
			}
		}

		if (soonToBeDead instanceof EntityPlayer){
			storeSoulboundItemsForRespawn((EntityPlayer)soonToBeDead);
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onEntityDeathLowPriority(LivingDeathEvent event){
		EntityLivingBase soonToBeNotDead = event.entityLiving; // prevents items disappearing when player not killed (event cancel by white stones, etc)
		if(event.isCanceled() && soonToBeNotDead instanceof EntityPlayer) restoreSoulboundItems((EntityPlayer) soonToBeNotDead);
	}

	public void restoreSoulboundItems(EntityPlayer player) {
		if (soulbound_Storage.containsKey(player.getUniqueID())){
			HashMap<Integer, ItemStack> soulboundItems = soulbound_Storage.get(player.getUniqueID());
			for (Integer i : soulboundItems.keySet()){
				if (i < player.inventory.getSizeInventory()){
					player.inventory.setInventorySlotContents(i, soulboundItems.get(i));
				}else{
					boolean done = false;
					for (int l = 0; l < player.inventory.getSizeInventory(); l++){
						if (player.inventory.getStackInSlot(l) == null){
							player.inventory.setInventorySlotContents(l, soulboundItems.get(i));
							done = true;
							break;
						}
					}
					if (!done) player.entityDropItem(soulboundItems.get(i), 0);
				}
			}
			soulbound_Storage.remove(player.getUniqueID());
		}
	}

	public static boolean playerHasMagitech(EntityPlayer player) {
		if (player.getCurrentArmor(3) != null && (player.getCurrentArmor(3).getItem() == ItemsCommonProxy.magitechGoggles ||
				ArmorHelper.isInfusionPreset(player.getCurrentArmor(3), GenericImbuement.magitechGoggleIntegration))) return true;
		return false;
	}

	@SubscribeEvent
	public void onAttack(AttackEntityEvent event) {
		if (event.entityPlayer.getHeldItem() != null) {
			if (event.entityPlayer.getHeldItem().getItem() instanceof ItemSoulspike) {
				if (event.target instanceof EntityHallucination) {
					ItemSoulspike.addManaToSpike(event.entityPlayer.getHeldItem(), 15);
					event.target.attackEntityFrom(DamageSource.outOfWorld, 15);
					event.target.attackEntityFrom(DamageSource.magic, 15);
				} else if (event.target instanceof EntityLivingBase) {
					if (event.target instanceof EntityPlayer) {
						EntityPlayer pl = (EntityPlayer) event.target;
						if (!AMCore.config.getAllowCreativeTargets() && pl.capabilities.isCreativeMode) return;
						ExtendedProperties properties = ExtendedProperties.For(pl);
						if (properties.hasExtraVariable("ethereal")) {
							ItemSoulspike.addManaToSpike(event.entityPlayer.getHeldItem(), (int)(properties.getCurrentMana()/4));
							properties.deductMana(properties.getCurrentMana()/4);
						}
					}
					ItemSoulspike.addManaToSpike(event.entityPlayer.getHeldItem(), 3);
					event.target.attackEntityFrom(DamageSource.outOfWorld, 3);
				}
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.LOW)
	public void onEntityDeath(LivingDeathEvent event){
		EntityLivingBase soonToBeDead = event.entityLiving;
		if (!(ExtendedProperties.For(soonToBeDead).getContingencyEffect(1).getItem() instanceof ItemSnowball)){
			ExtendedProperties.For(soonToBeDead).procContingency(1, null);
		}

		if (soonToBeDead instanceof EntityHallucination && event.source.getSourceOfDamage() != null) {
			if (event.source.getSourceOfDamage() instanceof EntityPlayer) { // bad karma with the world
				ExtendedProperties exProps = ExtendedProperties.For((EntityPlayer)event.source.getSourceOfDamage());
				exProps.addToExtraVariables("karma", "bad");
			}
		}

		if (soonToBeDead instanceof EntityPlayer){
			AMCore.proxy.playerTracker.onPlayerDeath((EntityPlayer)soonToBeDead);
		}else if (soonToBeDead instanceof EntityCreature){
			if (!EntityUtilities.isSummon(soonToBeDead) && EntityUtilities.isAIEnabled((EntityCreature)soonToBeDead) && event.source.getSourceOfDamage() instanceof EntityPlayer){
				EntityUtilities.handleCrystalPhialAdd((EntityCreature)soonToBeDead, (EntityPlayer)event.source.getSourceOfDamage());
			}
		}

		if (EntityUtilities.isSummon(soonToBeDead)){
			ReflectionHelper.setPrivateValue(EntityLivingBase.class, soonToBeDead, 0, "field_70718_bc", "recentlyHit");
			int ownerID = EntityUtilities.getOwner(soonToBeDead);
			Entity e = soonToBeDead.worldObj.getEntityByID(ownerID);
			if (e != null & e instanceof EntityLivingBase){
				ExtendedProperties.For((EntityLivingBase)e).removeSummon();
			}
		}

		if (soonToBeDead instanceof EntityVillager && ((EntityVillager)soonToBeDead).isChild()){
			BossSpawnHelper.instance.onVillagerChildKilled((EntityVillager)soonToBeDead);
		}

		if (soonToBeDead instanceof EntityLiving && !soonToBeDead.worldObj.isRemote) {
			int x = (int)Math.floor(soonToBeDead.posX);
			int y = (int)Math.floor(soonToBeDead.posY);
			int z = (int)Math.floor(soonToBeDead.posZ);
			for (int newx = x-3; newx <= x+3; newx++) {
				for (int newy = y-3; newy <= y+3; newy++) {
					for (int newz = z-3; newz <= z+3; newz++) {
						if (soonToBeDead.worldObj.getTileEntity(newx, newy, newz) instanceof TileEntityInfusedStem) {
							((TileEntityInfusedStem)soonToBeDead.worldObj.getTileEntity(newx, newy, newz)).killedEntities.add((EntityLiving)soonToBeDead);
							return;
						}
					}
				}
			}
		}
	}

	@SubscribeEvent
	public void onPlayerGetAchievement(AchievementEvent event){
		if (!event.entityPlayer.worldObj.isRemote && event.achievement == AchievementList.theEnd2){
			AMCore.instance.proxy.playerTracker.storeExtendedPropertiesForRespawn(event.entityPlayer);
			// AMCore.instance.proxy.playerTracker.storeSoulboundItemsForRespawn(event.entityPlayer);
		}
	}

	@SubscribeEvent
	public void onLivingDrops(LivingDropsEvent event){
		if (EntityUtilities.isSummon(event.entityLiving) && !(event.entityLiving instanceof EntityHorse)){
			event.setCanceled(true);
		}
		if (event.source == DamageSources.darkNexus){
			event.setCanceled(true);
		}
		if (!event.entityLiving.worldObj.isRemote && event.entityLiving instanceof EntityPig && event.entityLiving.getRNG().nextDouble() < 0.3f){
			EntityItem animalFat = new EntityItem(event.entityLiving.worldObj);
			ItemStack stack = new ItemStack(ItemsCommonProxy.itemOre, 1, ItemsCommonProxy.itemOre.META_ANIMALFAT);
			animalFat.setPosition(event.entity.posX, event.entity.posY, event.entity.posZ);
			animalFat.setEntityItemStack(stack);
			event.drops.add(animalFat);
		}
	}

	@SubscribeEvent
	public void onEntityJump(LivingJumpEvent event){
		if (event.entityLiving.isPotionActive(BuffList.agility.id)){
			event.entityLiving.motionY *= 1.5f;
		}
		if (event.entityLiving.isPotionActive(BuffList.leap.id)){

			Entity velocityTarget = event.entityLiving;

			if (event.entityLiving.ridingEntity != null){
				if (event.entityLiving.ridingEntity instanceof EntityMinecart){
					event.entityLiving.ridingEntity.setPosition(event.entityLiving.ridingEntity.posX, event.entityLiving.ridingEntity.posY + 1.5, event.entityLiving.ridingEntity.posZ);
				}
				velocityTarget = event.entityLiving.ridingEntity;
			}

			double yVelocity = 0;
			double xVelocity = 0;
			double zVelocity = 0;

			Vec3 vec = event.entityLiving.getLookVec().normalize();
			switch (event.entityLiving.getActivePotionEffect(BuffList.leap).getAmplifier()){
			case BuffPowerLevel.Low:
				yVelocity = 0.4;
				xVelocity = velocityTarget.motionX * 1.08 * Math.abs(vec.xCoord);
				zVelocity = velocityTarget.motionZ * 1.08 * Math.abs(vec.zCoord);
				break;
			case BuffPowerLevel.Medium:
				yVelocity = 0.7;
				xVelocity = velocityTarget.motionX * 1.25 * Math.abs(vec.xCoord);
				zVelocity = velocityTarget.motionZ * 1.25 * Math.abs(vec.zCoord);
				break;
			case BuffPowerLevel.High:
				yVelocity = 1;
				xVelocity = velocityTarget.motionX * 1.75 * Math.abs(vec.xCoord);
				zVelocity = velocityTarget.motionZ * 1.75 * Math.abs(vec.zCoord);
				break;
			default:
				break;
			}

			float maxHorizontalVelocity = 1.45f;

			if (event.entityLiving.ridingEntity != null && (event.entityLiving.ridingEntity instanceof EntityMinecart || event.entityLiving.ridingEntity instanceof EntityBoat) || event.entityLiving.isPotionActive(BuffList.haste.id)){
				maxHorizontalVelocity += 25;
				xVelocity *= 2.5;
				zVelocity *= 2.5;
			}

			if (xVelocity > maxHorizontalVelocity){
				xVelocity = maxHorizontalVelocity;
			}else if (xVelocity < -maxHorizontalVelocity){
				xVelocity = -maxHorizontalVelocity;
			}

			if (zVelocity > maxHorizontalVelocity){
				zVelocity = maxHorizontalVelocity;
			}else if (zVelocity < -maxHorizontalVelocity){
				zVelocity = -maxHorizontalVelocity;
			}

			if (ExtendedProperties.For(event.entityLiving).getIsFlipped()){
				yVelocity *= -1;
			}

			velocityTarget.addVelocity(xVelocity, yVelocity, zVelocity);
		}
		if (event.entityLiving.isPotionActive(BuffList.entangled.id)){
			event.entityLiving.motionY = 0;
		}

		if (event.entityLiving instanceof EntityPlayer){
			ItemStack boots = ((EntityPlayer)event.entityLiving).inventory.armorInventory[0];
			if (boots != null && boots.getItem() == ItemsCommonProxy.enderBoots && event.entityLiving.isSneaking()){
				ExtendedProperties.For(event.entityLiving).toggleFlipped();
			}
		}
		if (ExtendedProperties.For(event.entityLiving).getFlipRotation() > 0)
			((EntityPlayer)event.entityLiving).addVelocity(0, -2 * event.entityLiving.motionY, 0);

	}

	@SubscribeEvent
	public void onEntityFall(LivingFallEvent event){

		EntityLivingBase ent = event.entityLiving;
		float f = event.distance;
		ent.isAirBorne = false;

		//slowfall buff
		if (ent.isPotionActive(BuffList.slowfall.id) || ent.isPotionActive(BuffList.shrink.id) || (ent instanceof EntityPlayer && AffinityData.For(ent).getAffinityDepth(Affinity.NATURE) == 1.0f)){
			event.setCanceled(true);
			ent.fallDistance = 0;
			return;
		}

		if (ent instanceof EntityPlayer && ((EntityPlayer)ent).inventory.armorInventory[0] != null){
			if (((EntityPlayer)ent).inventory.armorInventory[0].getItem() == ItemsCommonProxy.archmageBoots){
				event.setCanceled(true);
				return;
			}
		}

		//gravity well
		if (ent.isPotionActive(BuffList.gravityWell.id)){
			ent.fallDistance *= 1.5f;
		}

		//fall protection stat
		f -= ExtendedProperties.For(ent).getFallProtection();
		ExtendedProperties.For(ent).setFallProtection(0);
		if (f <= 0){
			ent.fallDistance = 0;
			event.setCanceled(true);
			return;
		}
	}

	public static List<String> forceShielded = new ArrayList<>();
	public static Map<String, Integer> slowedTiles = new HashMap<>();
	public static Map<String, Integer> acceleratedEntitiesUUIDs = new HashMap<>();
	public static Map<String, Integer> slowedEntitiesUUIDs = new HashMap<>();

	@SubscribeEvent
	public void onBlockBreak(BlockEvent.BreakEvent event) {
		if (forceShielded.contains(event.x + "_" + event.y + "_" + event.z + "_" + event.world.provider.dimensionId)) {
			event.setCanceled(true);
			event.world.playSoundAtEntity(event.getPlayer(), "arsmagica2:spell.cast.arcane", 1F, rand.nextFloat() + 0.5f);
			event.world.spawnParticle("depthsuspend", event.x+0.5, event.y+0.5, event.z+0.5,0,0,0);
			event.world.spawnParticle("depthsuspend", event.x+rand.nextFloat(), event.y+rand.nextFloat(), event.z+rand.nextFloat(),0,0,0);
			event.world.spawnParticle("depthsuspend", event.x+rand.nextFloat(), event.y+rand.nextFloat(), event.z+rand.nextFloat(),0,0,0);
		}
	}

	@SubscribeEvent
	public void disconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) { // fired at the client. this is to reset it to normal after disconnecting
		MysteriumPatchesFixesMagicka.changeServerTickrate(MysteriumPatchesFixesMagicka.clienttickratedefault); // this is ok because it converts it to the server format anyway
		MysteriumPatchesFixesMagicka.changeClientTickratePublic(null, MysteriumPatchesFixesMagicka.clienttickratedefault);
	}

	@SubscribeEvent
	public void connect(FMLNetworkEvent.ClientConnectedToServerEvent event) { // fired at client when connects to server
		if(event.isLocal) { // single player game
			float tickrate = MysteriumPatchesFixesMagicka.clienttickratedefault;
			MysteriumPatchesFixesMagicka.changeServerTickrate(tickrate); // this is ok because it converts it to the server format anyway
			MysteriumPatchesFixesMagicka.changeClientTickratePublic(null, tickrate);
		} else {
			MysteriumPatchesFixesMagicka.changeClientTickratePublic(null, 20F); // it forces it serverside anyway
		}
	}

	@SubscribeEvent
	public void connect(PlayerEvent.PlayerLoggedInEvent event) {
		if(FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER) {
			float tickrate = 1000L / MysteriumPatchesFixesMagicka.servertickrate; // 1000/50 = 20 ticks, etc
			MysteriumPatchesFixesMagicka.changeClientTickratePublic(event.player, tickrate);
		}
	}

	public static int tick = 0;

	private static int[] getMinIndex(int[] array) {
		int min = array[0];
		int indexForMin = 0;
		for (int i = 0; i < array.length; i++) {
			int score = array[i];
			if (min > score) {
				min = score;
				indexForMin = i;
			}
		}
		return new int[]{indexForMin, min};
	}

	private static int[] getMaxIndex(int[] array) {
		int max = array[0];
		int indexForMax = 0;
		for (int i = 0; i < array.length; i++) {
			int score = array[i];
			if (max < score) {
				max = score;
				indexForMax = i;
			}
		}
		return new int[]{indexForMax, max};
	}


	@SubscribeEvent
	public void onEntityLiving(LivingUpdateEvent event){

		EntityLivingBase ent = event.entityLiving;

		if (!SkillTreeManager.instance.isSkillDisabled(SkillManager.instance.getSkill("DiluteTime"))) {
			String UUID = ent.getUniqueID().toString();
			if (slowedEntitiesUUIDs.containsKey(UUID)) {
				if (ent.ticksExisted % slowedEntitiesUUIDs.get(UUID) != 0) {
					event.setCanceled(true);
					return;
				}
			}
		}

		World world = ent.worldObj;

		boolean inBossWorld = (world.provider instanceof DynamicBossWorldProvider);
		boolean inOverworld = world.provider.dimensionId == 0;

		BuffStatModifiers.instance.applyStatModifiersBasedOnBuffs(ent);

		ExtendedProperties extendedProperties;
		extendedProperties = ExtendedProperties.For(ent);
		extendedProperties.handleSpecialSyncData();
		extendedProperties.manaBurnoutTick();

		//================================================================================
		//soulbound items
		//================================================================================
		if (ent instanceof EntityPlayer){
			EntityPlayer player = (EntityPlayer)ent;
			if (!ent.isDead){
				if (ent.ticksExisted > 5 && !ent.worldObj.isRemote){
					if (ent.ticksExisted < 10) restoreSoulboundItems(player);
					if (ent.ticksExisted < 8 && inOverworld) DynamicBossWorldHelper.returnPlayerToOriginalPosition(player); // if player dies during bossfight. Deletion of world and music handled separately
				}
			}

			if (!inBossWorld) {
				if (player.worldObj.isRemote) {
					if (SpellSoundHelper.currentlyPlayingMusic != null) SpellSoundHelper.currentlyPlayingMusic.fadeOut(); // have to handle this clientside
				}
			} else { // in boss world
				// some bossfight-dependent code will likely be here
			}

			if (extendedProperties.hasExtraVariable("ethereal")) { // ethereal form handling
				int durationLeft = Integer.valueOf(extendedProperties.getExtraVariable("ethereal"));
				if (durationLeft < 3) {
					// deactivate form
					ItemSoulspike.removeTagFromBoots(player.inventory.armorInventory[0]);
					player.capabilities.disableDamage = false;
					player.capabilities.allowEdit = true;
					player.capabilities.isFlying = false;
					player.noClip = false;
					player.setInvisible(false);
					extendedProperties.removeFromExtraVariables("ethereal");
				} else {
					// tick ethereal form
					// decrease duration
					extendedProperties.addToExtraVariables("ethereal", String.valueOf(durationLeft-1));
				}
			} else { // isn't ethereal
				if (ItemSoulspike.bootsHaveEtherealTag(player.inventory.armorInventory[0])) { // but boots have tag
					ItemSoulspike.removeTagFromBoots(player.inventory.armorInventory[0]); // remove tag; prevent possible workaround
				}
			}

			// accelerated blocks and entities are done outside of the 5-tick performance optimisation to make them smooth
			Map<String, String> acceleratedBlocks = extendedProperties.getExtraVariablesContains("accelerated_fast_tile_");
			for (Map.Entry<String, String> entry : acceleratedBlocks.entrySet()) {
				if (Integer.valueOf(entry.getValue()) < 3) {
					extendedProperties.removeFromExtraVariables(entry.getKey());
				} else {
					extendedProperties.addToExtraVariables(entry.getKey(), String.valueOf(Integer.valueOf(entry.getValue()) - 1));
					String[] entryvalues = entry.getKey().split("_");
					int x = Integer.valueOf(entryvalues[3]);
					int y = Integer.valueOf(entryvalues[4]);
					int z = Integer.valueOf(entryvalues[5]);
					int dim = Integer.valueOf(entryvalues[6]);
					int power = Integer.valueOf(entryvalues[7]);
					World wrld = DimensionManager.getWorld(dim);
					if (wrld != null) {
						for (int i = 0; i < power; i++) {
							if (wrld.getTileEntity(x, y, z) != null && wrld.getTileEntity(x, y, z).canUpdate()) {
								wrld.getTileEntity(x, y, z).updateEntity();
							}
							if (wrld.getBlock(x, y, z).getTickRandomly() && wrld.rand.nextInt(100) == 0) {
								wrld.getBlock(x, y, z).updateTick(wrld, x, y, z, wrld.rand);
							}
						}
					}
				}
			}
			WorldServer[] worlds = DimensionManager.getWorlds();
			int s1 = worlds.length;
			try {
				for (int l = 0; l < s1; l++) { // do this outside of for loop to save performance
					if (l >= worlds.length) break;
					int s2 = worlds[l].loadedEntityList.size();
					for (int f = 0; f < s2; f++) {
						if (f >= worlds[l].loadedEntityList.size())
							break; // fix for the most obscene bug ever, where it doesn't respect indexes, or arbitrarily chooses to delete entities while I'm iterating over them
						Object entityobj = worlds[l].loadedEntityList.get(f);
						if (entityobj instanceof EntityLivingBase) {
							String UUID = ((EntityLivingBase) entityobj).getUniqueID().toString();
							if (acceleratedEntitiesUUIDs.containsKey(UUID)) {
								for (int i = 0; i < acceleratedEntitiesUUIDs.get(UUID); i++) {
									((EntityLivingBase) entityobj).onUpdate();
								}
							}
						}
					}
				}
			} catch (IndexOutOfBoundsException e) {
				; // sometimes it's just unavoidable
			} catch (Exception e) {
				e.printStackTrace();
			}

			tick++;
			if (tick > 100000) tick = 0;
			if ((tick % ((ent.worldObj.playerEntities.size() + 1) * 5)) == 0) { // only does behavior every 5 player ticks to ease computing load
				// e.g. if there's 4 players, they tick like 1,2,3,4,1,2,3,4,1,2,3,4, etc (In theory, hopefully), and we pick 25th (1st), 50th (2nd), 75th(3rd), etc. ticks.
				// there are some free ticks in-between, e.g. with 1 player it ticks every 10 ticks instead of 5. That eases load further (but causes double the duration - not a big deal for these spells).
				Map<String, String> shieldedBlocks = extendedProperties.getExtraVariablesContains("shielded_tile_");
				Map<String, String> slowedBlocks = extendedProperties.getExtraVariablesContains("accelerated_slow_tile_");
				Map<String, String> loadedBlocks = extendedProperties.getExtraVariablesContains("timefortified_tile_");

				Map<String, String> slowedEntities = extendedProperties.getExtraVariablesContains("accelerated_slow_entity_");
				Map<String, String> acceleratedEntities = extendedProperties.getExtraVariablesContains("accelerated_fast_entity_");

				if (SkillTreeManager.instance.isSkillDisabled(SkillManager.instance.getSkill("ConcentrateTime"))) {
					acceleratedEntities.clear();
					acceleratedBlocks.clear();
				}
				if (SkillTreeManager.instance.isSkillDisabled(SkillManager.instance.getSkill("DiluteTime"))) {
					slowedEntities.clear();
					slowedBlocks.clear();
				}

				Map<String, String> spatialVortices = extendedProperties.getExtraVariablesContains("spatialvortex_");
				if (spatialVortices.size() > 0) {
					int[] totalenergy = new int[spatialVortices.size()];
					int[] totalenergyExternallyLimited = new int[spatialVortices.size()];
					int[] totaletheriumdark = new int[spatialVortices.size()];
					int[] totaletheriumlight = new int[spatialVortices.size()];

					int vIndex = 0;
					for (Map.Entry<String, String> entry : spatialVortices.entrySet()) {
						String[] entryvalues = entry.getKey().split("_");
						int x = Integer.valueOf(entryvalues[1]);
						int y = Integer.valueOf(entryvalues[2]);
						int z = Integer.valueOf(entryvalues[3]);
						World thisdim = DimensionManager.getWorld(Integer.valueOf(entryvalues[4]));
						totalenergy[vIndex] = 0;
						totaletheriumdark[vIndex] = 0;
						totaletheriumlight[vIndex] = 0;
						for (int xadd = -1; xadd <= 1; xadd += 2) {
							for (int zadd = -1; zadd <= 1; zadd += 2) {
								TileEntity te = thisdim.getTileEntity(x + xadd, y, z + zadd);
								if (te != null) {
									if (te instanceof IEnergyHandler) {
										totalenergy[vIndex] += ((IEnergyHandler) te).getEnergyStored(ForgeDirection.UNKNOWN);
										totalenergyExternallyLimited[vIndex] += ((IEnergyHandler) te).extractEnergy(ForgeDirection.UNKNOWN, 50000, true); // sim only
									}
									if (te instanceof IPowerNode) {
										totaletheriumdark[vIndex] += PowerNodeRegistry.For(thisdim).getPower((IPowerNode) te, PowerTypes.DARK);
										totaletheriumlight[vIndex] += PowerNodeRegistry.For(thisdim).getPower((IPowerNode) te, PowerTypes.LIGHT);
									}
								}
							}
						}
						vIndex++;
					}
					if (AMCore.config.getDebugVortex()) {
						System.out.println(Arrays.toString(totalenergy));
						System.out.println(Arrays.toString(totalenergyExternallyLimited));
						System.out.println(Arrays.toString(totaletheriumdark));
						System.out.println(Arrays.toString(totaletheriumlight));
					}
					int[] maxE = getMaxIndex(totalenergy);
					int[] maxD = getMaxIndex(totaletheriumdark);
					int[] maxL = getMaxIndex(totaletheriumlight);
					int[] minE = getMinIndex(totalenergy);
					int[] minD = getMinIndex(totaletheriumdark);
					int[] minL = getMinIndex(totaletheriumlight);

					if (AMCore.config.getDebugVortex()) {
						System.out.println("E" + Arrays.toString(minE));
						System.out.println(Arrays.toString(maxE));
						System.out.println("D" + Arrays.toString(minD));
						System.out.println(Arrays.toString(maxD));
						System.out.println("L" + Arrays.toString(minL));
						System.out.println(Arrays.toString(maxL));
					}

					int halfDiffE = (maxE[1] - minE[1]) / 2;
					int halfDiffD = (maxD[1] - minD[1]) / 2;
					int halfDiffL = (maxL[1] - minL[1]) / 2;

					if (AMCore.config.getDebugVortex()) {
						System.out.println(halfDiffD + "," + halfDiffE + "," + halfDiffL + " half diff");
					}

					// total energy available to transfer, limited by: 50,000 per 5 ticks, half the diff between max and min, and external factors (such as device's throughput rate)
					int toTransferE = Math.min(totalenergyExternallyLimited[maxE[0]], halfDiffE); // 50000 is the max externallyLimited can be anyways
					int toTransferD = Math.min(50000, halfDiffD);
					int toTransferL = Math.min(50000, halfDiffL);

					if (AMCore.config.getDebugVortex()) {
						System.out.println(toTransferD + "," + toTransferE+ "," + toTransferL +" to transfer");
					}

					int maximumEnergyMinimumVortexCanAccept = 0;
					if (toTransferE > 0) {
						// maximum energy we can *accept* right now (RF only)
						int tIndex = 0;
						for (Map.Entry<String, String> entry : spatialVortices.entrySet()) {
							if (AMCore.config.getDebugVortex()) {
								System.out.println(minE[0]);
							}
							if (tIndex == minE[0]) {
								String[] entryvalues = entry.getKey().split("_");
								int x = Integer.valueOf(entryvalues[1]);
								int y = Integer.valueOf(entryvalues[2]);
								int z = Integer.valueOf(entryvalues[3]);
								World thisdim = DimensionManager.getWorld(Integer.valueOf(entryvalues[4]));
								for (int xadd = -1; xadd <= 1; xadd += 2) {
									for (int zadd = -1; zadd <= 1; zadd += 2) {
										TileEntity te = thisdim.getTileEntity(x + xadd, y, z + zadd);
										if (te != null) {
											if (te instanceof IEnergyHandler) {
												if (AMCore.config.getDebugVortex()) {
													System.out.println("got here!");
												}
												maximumEnergyMinimumVortexCanAccept += ((IEnergyHandler) te).receiveEnergy(ForgeDirection.UNKNOWN, 50000, true); // sim only
											}
										}
									}
								}
								break;
							}
							tIndex++;
						}
					}

					if (AMCore.config.getDebugVortex()) {
						System.out.println(maximumEnergyMinimumVortexCanAccept + " max can accept");
					}

					int toTransferEActual = Math.min(toTransferE, maximumEnergyMinimumVortexCanAccept);
					boolean ELeft = toTransferEActual > 0;
					boolean LLeft = toTransferL > 0;
					boolean DLeft = toTransferD > 0;

					int localIndex = 0;
					// energy subtraction. BEWARE: Terrible code to follow!! Prepare bleach for eyes.
					if (ELeft) {
						int toSubtractE = toTransferEActual;
						for (Map.Entry<String, String> entry : spatialVortices.entrySet()) {
							if (localIndex == maxE[0]) {
								String[] entryvalues = entry.getKey().split("_");
								int x = Integer.valueOf(entryvalues[1]);
								int y = Integer.valueOf(entryvalues[2]);
								int z = Integer.valueOf(entryvalues[3]);
								World thisdim = DimensionManager.getWorld(Integer.valueOf(entryvalues[4]));
								for (int xadd = -1; xadd <= 1; xadd += 2) {
									for (int zadd = -1; zadd <= 1; zadd += 2) {
										TileEntity te = thisdim.getTileEntity(x + xadd, y, z + zadd);
										if (te != null) {
											if (te instanceof IEnergyHandler) {
												if (toSubtractE > 0) toSubtractE -= ((IEnergyHandler) te).extractEnergy(ForgeDirection.UNKNOWN, toSubtractE, false); // real
											}
										}
									}
								}
							}
							localIndex++;
						}
						localIndex = 0;
					}
					if (DLeft) {
						int toSubtractD = toTransferD;
						for (Map.Entry<String, String> entry : spatialVortices.entrySet()) {
							if (localIndex == maxD[0]) {
								String[] entryvalues = entry.getKey().split("_");
								int x = Integer.valueOf(entryvalues[1]);
								int y = Integer.valueOf(entryvalues[2]);
								int z = Integer.valueOf(entryvalues[3]);
								World thisdim = DimensionManager.getWorld(Integer.valueOf(entryvalues[4]));
								for (int xadd = -1; xadd <= 1; xadd += 2) {
									for (int zadd = -1; zadd <= 1; zadd += 2) {
										TileEntity te = thisdim.getTileEntity(x + xadd, y, z + zadd);
										if (te != null) {
											if (te instanceof IPowerNode) {
												if (toSubtractD > 0) toSubtractD -= PowerNodeRegistry.For(thisdim).consumePower((IPowerNode)te, PowerTypes.DARK, toSubtractD); // real
											}
										}
									}
								}
							}
							localIndex++;
						}
						localIndex = 0;
					}
					if (LLeft) {
						int toSubtractL = toTransferL;
						for (Map.Entry<String, String> entry : spatialVortices.entrySet()) {
							if (localIndex == maxL[0]) {
								String[] entryvalues = entry.getKey().split("_");
								int x = Integer.valueOf(entryvalues[1]);
								int y = Integer.valueOf(entryvalues[2]);
								int z = Integer.valueOf(entryvalues[3]);
								World thisdim = DimensionManager.getWorld(Integer.valueOf(entryvalues[4]));
								for (int xadd = -1; xadd <= 1; xadd += 2) {
									for (int zadd = -1; zadd <= 1; zadd += 2) {
										TileEntity te = thisdim.getTileEntity(x + xadd, y, z + zadd);
										if (te != null) {
											if (te instanceof IPowerNode) {
												if (toSubtractL > 0) toSubtractL -= PowerNodeRegistry.For(thisdim).consumePower((IPowerNode)te, PowerTypes.LIGHT, toSubtractL); // real
											}
										}
									}
								}
							}
							localIndex++;
						}
					}

					if (ELeft || DLeft || LLeft) {
						// actually do the energy transfer. Finally. Note: I *know* this whole algo I came up with is badly optimized, and I welcome any PRs to optimize it.
						int fIndex = 0;
						for (Map.Entry<String, String> entry : spatialVortices.entrySet()) {
							if (fIndex == minE[0] && ELeft) {
								String[] entryvalues = entry.getKey().split("_");
								int x = Integer.valueOf(entryvalues[1]);
								int y = Integer.valueOf(entryvalues[2]);
								int z = Integer.valueOf(entryvalues[3]);
								World thisdim = DimensionManager.getWorld(Integer.valueOf(entryvalues[4]));
								for (int xadd = -1; xadd <= 1; xadd += 2) {
									for (int zadd = -1; zadd <= 1; zadd += 2) {
										TileEntity te = thisdim.getTileEntity(x + xadd, y, z + zadd);
										if (te != null) {
											if (te instanceof IEnergyHandler) {
												if (toTransferEActual > 0) toTransferEActual -= ((IEnergyHandler) te).receiveEnergy(ForgeDirection.UNKNOWN, toTransferEActual, false); // real
												else ELeft = false;
											}
										}
									}
								}
							}
							if (fIndex == minD[0] && DLeft) {
								if (AMCore.config.getDebugVortex()) {
									System.out.println(fIndex + " fIndex when minD");
								}
								String[] entryvalues = entry.getKey().split("_");
								int x = Integer.valueOf(entryvalues[1]);
								int y = Integer.valueOf(entryvalues[2]);
								int z = Integer.valueOf(entryvalues[3]);
								World thisdim = DimensionManager.getWorld(Integer.valueOf(entryvalues[4]));
								for (int xadd = -1; xadd <= 1; xadd += 2) {
									for (int zadd = -1; zadd <= 1; zadd += 2) {
										TileEntity te = thisdim.getTileEntity(x + xadd, y, z + zadd);
										if (te != null) {
											if (te instanceof IPowerNode) {
												if (toTransferD > 0) toTransferD -= PowerNodeRegistry.For(thisdim).insertPower((IPowerNode) te, PowerTypes.DARK, toTransferD);
												else DLeft = false;
											}
										}
									}
								}
							}
							if (fIndex == minL[0] && LLeft) {
								if (AMCore.config.getDebugVortex()) {
									System.out.println(fIndex + " fIndex when minL");
								}
								String[] entryvalues = entry.getKey().split("_");
								int x = Integer.valueOf(entryvalues[1]);
								int y = Integer.valueOf(entryvalues[2]);
								int z = Integer.valueOf(entryvalues[3]);
								World thisdim = DimensionManager.getWorld(Integer.valueOf(entryvalues[4]));
								for (int xadd = -1; xadd <= 1; xadd += 2) {
									for (int zadd = -1; zadd <= 1; zadd += 2) {
										TileEntity te = thisdim.getTileEntity(x + xadd, y, z + zadd);
										if (te != null) {
											if (te instanceof IPowerNode) {
												if (toTransferL > 0) toTransferL -= PowerNodeRegistry.For(thisdim).insertPower((IPowerNode) te, PowerTypes.LIGHT, toTransferL);
												else LLeft = false;
											}
										}
									}
								}
							}
							fIndex++;
						}
					}
				}

				for (Map.Entry<String, String> entry : acceleratedEntities.entrySet()) {
					String[] entryvalues = entry.getKey().split("_");
					if (Integer.valueOf(entry.getValue()) < 3) {
						extendedProperties.removeFromExtraVariables(entry.getKey());
						acceleratedEntitiesUUIDs.remove(entryvalues[4]);
					} else {
						extendedProperties.addToExtraVariables(entry.getKey(), String.valueOf(Integer.valueOf(entry.getValue()) - 5)); // entryvalue 4 is uuid, 3 is power
						if (!(acceleratedEntitiesUUIDs.containsKey(entryvalues[4]))) acceleratedEntitiesUUIDs.put(entryvalues[4], Integer.valueOf(entryvalues[3]));
					}
				}
				for (Map.Entry<String, String> entry : slowedEntities.entrySet()) {
					String[] entryvalues = entry.getKey().split("_");
					if (Integer.valueOf(entry.getValue()) < 3) {
						extendedProperties.removeFromExtraVariables(entry.getKey());
						slowedEntitiesUUIDs.remove(entryvalues[4]);
					} else {
						extendedProperties.addToExtraVariables(entry.getKey(), String.valueOf(Integer.valueOf(entry.getValue()) - 5)); // entryvalue 4 is uuid, 3 is power
						if (!(slowedEntitiesUUIDs.containsKey(entryvalues[4]))) slowedEntitiesUUIDs.put(entryvalues[4], Integer.valueOf(entryvalues[3]));
					}
				}
				for (Map.Entry<String, String> entry : loadedBlocks.entrySet()) {
					if (Integer.valueOf(entry.getValue()) < 3) {
						extendedProperties.removeFromExtraVariables(entry.getKey());
						if (!world.isRemote) {
							String[] entryvalues = entry.getKey().split("_");
							int x = Integer.valueOf(entryvalues[2]);
							int y = Integer.valueOf(entryvalues[3]);
							int z = Integer.valueOf(entryvalues[4]);
							int dim = Integer.valueOf(entryvalues[5]);
							if (DimensionManager.getWorld(dim) != null) AMChunkLoader.INSTANCE.releaseStaticChunkLoad(DimensionManager.getWorld(dim).getTileEntity(x, y, z).getClass(), x, y, z, DimensionManager.getWorld(dim));
						}
					} else {
						extendedProperties.addToExtraVariables(entry.getKey(), String.valueOf(Integer.valueOf(entry.getValue()) - 5));
					}
				}
				for (Map.Entry<String, String> entry : shieldedBlocks.entrySet()) {
					if (Integer.valueOf(entry.getValue()) < 3) {
						extendedProperties.removeFromExtraVariables(entry.getKey());
						if (!world.isRemote) {
							String[] entryvalues = entry.getKey().split("_");
							int x = Integer.valueOf(entryvalues[2]);
							int y = Integer.valueOf(entryvalues[3]);
							int z = Integer.valueOf(entryvalues[4]);
							int dim = Integer.valueOf(entryvalues[5]);
							forceShielded.remove(x + "_" + y + "_" + z + "_" + dim);
						}
					} else {
						String[] entryvalues = entry.getKey().split("_");
						String represent = Integer.valueOf(entryvalues[2]) + "_" + Integer.valueOf(entryvalues[3]) + "_" + Integer.valueOf(entryvalues[4]) + "_" + Integer.valueOf(entryvalues[5]);
						if (!(forceShielded.contains(represent))) forceShielded.add(represent);
						extendedProperties.addToExtraVariables(entry.getKey(), String.valueOf(Integer.valueOf(entry.getValue()) - 5));
					}
				}
				for (Map.Entry<String, String> entry : slowedBlocks.entrySet()) {
					String[] entryvalues = entry.getKey().split("_");
					if (Integer.valueOf(entry.getValue()) < 3) {
						extendedProperties.removeFromExtraVariables(entry.getKey());
						slowedTiles.remove(entryvalues[3] + "_" + entryvalues[4] + "_" + entryvalues[5] + "_" + entryvalues[6]);
					} else {
						String represent = Integer.valueOf(entryvalues[3]) + "_" + Integer.valueOf(entryvalues[4]) + "_" + Integer.valueOf(entryvalues[5]) + "_" + Integer.valueOf(entryvalues[6]);
						if (!(slowedTiles.containsKey(represent))) slowedTiles.put(represent, Integer.valueOf(entryvalues[7])); // 7 is power
						extendedProperties.addToExtraVariables(entry.getKey(), String.valueOf(Integer.valueOf(entry.getValue()) - 5));
					}
				}
			}
		}
		//================================================================================

		// unflip flipped players
		if (tempFlipped.containsKey(extendedProperties)) {
			tempFlipped.put(extendedProperties, tempFlipped.get(extendedProperties)-1);
			if (tempFlipped.get(extendedProperties) <= 0) {
				tempFlipped.remove(extendedProperties);
				extendedProperties.toggleFlipped();
			}
		}

		extendedProperties.flipTick();

		if (extendedProperties.getIsFlipped()){
			if ((ent).motionY < 2)
				(ent).motionY += 0.15f;

			double posY = ent.posY + ent.height;
			if (!world.isRemote)
				posY += ent.getEyeHeight();
			if (world.rayTraceBlocks(Vec3.createVectorHelper(ent.posX, posY, ent.posZ), Vec3.createVectorHelper(ent.posX, posY + 1, ent.posZ), true) != null){
				if (!ent.onGround){
					if (ent.fallDistance > 0){
						try{
							Method m = ReflectionHelper.findMethod(Entity.class, ent, new String[]{"func_70069_a", "fall"}, float.class);
							m.setAccessible(true);
							m.invoke(ent, ent.fallDistance);
						}catch (Throwable e){
							e.printStackTrace();
						}
						ent.fallDistance = 0;
					}
				}
				ent.onGround = true;
			}else{
				if (event.entityLiving instanceof EntityPlayer){
					if (ent.motionY > 0){
						if (world.isRemote)
							ent.fallDistance += ent.posY - ent.prevPosY;
						else
							ent.fallDistance += (((EntityPlayer)ent).field_71095_bQ - ((EntityPlayer)ent).field_71096_bN) * 2;
					}
				}
				ent.onGround = false;
			}
		}

		//armor effects & infusion
		if (ent instanceof EntityPlayer){

			if (ent.worldObj.isRemote){
				int divisor = extendedProperties.getAuraDelay() > 0 ? extendedProperties.getAuraDelay() : 1;
				if (ent.ticksExisted % divisor == 0)
					AMCore.instance.proxy.particleManager.spawnAuraParticles(ent);
				AMCore.proxy.setViewSettings();
			}

			ArmorHelper.HandleArmorInfusion((EntityPlayer)ent);
			ArmorHelper.HandleArmorEffects((EntityPlayer)ent, world);

			if (ArmorHelper.isInfusionPreset(((EntityPlayer)ent).getCurrentArmor(1), GenericImbuement.stepAssist)){
				ent.stepHeight = 1.0111f;
			}else if (ent.stepHeight == 1.0111f){
				ent.stepHeight = 0.5f;
			}

			IAttributeInstance attr = ent.getEntityAttribute(SharedMonsterAttributes.movementSpeed);
			if (ArmorHelper.isInfusionPreset(((EntityPlayer)ent).getCurrentArmor(0), GenericImbuement.runSpeed)){
				if (attr.getModifier(GenericImbuement.imbuedHasteID) == null){
					attr.applyModifier(GenericImbuement.imbuedHaste);
				}
			}else{
				if (attr.getModifier(GenericImbuement.imbuedHasteID) != null){
					attr.removeModifier(GenericImbuement.imbuedHaste);
				}
			}
		}

		if (!ent.onGround && ent.fallDistance >= 4f && !(extendedProperties.getContingencyEffect(2).getItem() instanceof ItemSnowball)){
			int distanceToGround = MathUtilities.getDistanceToGround(ent, world);
			if (distanceToGround < -8 * ent.motionY){
				extendedProperties.procContingency(2, null);
			}
		}
		if (!(extendedProperties.getContingencyEffect(3).getItem() instanceof ItemSnowball) && ent.isBurning()){
			extendedProperties.procContingency(3, null);
		}

		if (!ent.worldObj.isRemote && ent.ticksExisted % 200 == 0){
			extendedProperties.setSyncAuras();
		}

		//buff particles
		//if (ent.worldObj.isRemote)
		//	AMCore.instance.proxy.particleManager.spawnBuffParticles(ent);

		//data sync
		extendedProperties.handleExtendedPropertySync();

		if (ent instanceof EntityPlayer){
			AffinityData.For(ent).handleExtendedPropertySync();
			SkillData.For((EntityPlayer)ent).handleExtendedPropertySync();

			if (ent.isPotionActive(BuffList.flight.id) || ent.isPotionActive(BuffList.levitation.id) || ((EntityPlayer)ent).capabilities.isCreativeMode){
				extendedProperties.hadFlight = true;
				if (ent.isPotionActive(BuffList.levitation)){
					if (((EntityPlayer)ent).capabilities.isFlying){
						float factor = 0.4f;
						ent.motionX *= factor;
						ent.motionZ *= factor;
						ent.motionY *= 0.0001f;
					}
				}
			}else if (extendedProperties.hadFlight){
				((EntityPlayer)ent).capabilities.allowFlying = false;
				((EntityPlayer)ent).capabilities.isFlying = false;
				extendedProperties.hadFlight = false;
			}
		}

		if (ent.isPotionActive(BuffList.agility.id)){
			ent.stepHeight = 1.01f;
		}else if (ent.stepHeight == 1.01f){
			ent.stepHeight = 0.5f;
		}

		if (!ent.worldObj.isRemote && EntityUtilities.isSummon(ent) && !EntityUtilities.isTileSpawnedAndValid(ent)){
			int owner = EntityUtilities.getOwner(ent);
			Entity ownerEnt = ent.worldObj.getEntityByID(owner);
			if (!EntityUtilities.decrementSummonDuration(ent)){
				ent.attackEntityFrom(DamageSources.unsummon, 5000);
			}
			if (owner == -1 || ownerEnt == null || ownerEnt.isDead || ownerEnt.getDistanceSqToEntity(ent) > 900){
				if (ent instanceof EntityLiving && !((EntityLiving)ent).getCustomNameTag().equals("")){
					EntityUtilities.setOwner(ent, null);
					EntityUtilities.setSummonDuration(ent, -1);
					EntityUtilities.revertAI((EntityCreature)ent);
				}else{
					ent.attackEntityFrom(DamageSources.unsummon, 5000);
				}
			}
		}

		//leap buff
		if (event.entityLiving.isPotionActive(BuffList.leap)){
			int amplifier = event.entityLiving.getActivePotionEffect(BuffList.leap).getAmplifier();

			switch (amplifier){
			case BuffPowerLevel.Low:
				extendedProperties.setFallProtection(8);
				break;
			case BuffPowerLevel.Medium:
				extendedProperties.setFallProtection(20);
				break;
			case BuffPowerLevel.High:
				extendedProperties.setFallProtection(45);
				break;
			default:
				break;
			}
		}

		if (event.entityLiving.isPotionActive(BuffList.gravityWell)){
			if (event.entityLiving.motionY < 0 && event.entityLiving.motionY > -3f){
				event.entityLiving.motionY *= 1.59999999999999998D;
			}
		}


		//slowfall/shrink buff
		// (isSneaking calls DataWatcher which are slow, so we test it late)
		if (event.entityLiving.isPotionActive(BuffList.slowfall)
				|| event.entityLiving.isPotionActive(BuffList.shrink)
				|| (ent instanceof EntityPlayer && AffinityData.For(ent).getAffinityDepth(Affinity.NATURE) == 1.0f && !ent.isSneaking())){
			if (!event.entityLiving.onGround && event.entityLiving.motionY < 0.0D){
				event.entityLiving.motionY *= 0.79999999999999998D;
			}
		}

		//swift swim
		if (event.entityLiving.isPotionActive(BuffList.swiftSwim)){
			if (event.entityLiving.isInWater()){
				if (!(event.entityLiving instanceof EntityPlayer) || !((EntityPlayer)event.entityLiving).capabilities.isFlying){
					event.entityLiving.motionX *= (1.133f + 0.03 * event.entityLiving.getActivePotionEffect(BuffList.swiftSwim).getAmplifier());
					event.entityLiving.motionZ *= (1.133f + 0.03 * event.entityLiving.getActivePotionEffect(BuffList.swiftSwim).getAmplifier());

					if (event.entityLiving.motionY > 0){
						event.entityLiving.motionY *= 1.134;
					}
				}
			}
		}

		if (event.entityLiving.worldObj.isMaterialInBB(event.entityLiving.boundingBox.expand(-0.10000000149011612D, -0.4000000059604645D, -0.10000000149011612D), liquidEssenceMaterial)) {
			handleEtherMovement(event.entityLiving);
		} else {
			wasInEther = false;
			etherTicks++;
		}

		//watery grave
		if (event.entityLiving.isPotionActive(BuffList.wateryGrave)){
			if (event.entityLiving.isInWater()){
				double pullVel = -0.5f;
				pullVel *= (event.entityLiving.getActivePotionEffect(BuffList.wateryGrave).getAmplifier() + 1);
				if (event.entityLiving.motionY > pullVel)
					event.entityLiving.motionY -= 0.1;
			}
		}

		if (ent instanceof EntityPlayer && !ent.worldObj.isRemote){ // hallucination effects
			if (ent.isPotionActive(BuffList.psychedelic)){
				if (ent.worldObj.provider.dimensionId == -1) { // any amplifier can cause lower level hallucinations
					if (ent.getActivePotionEffect(BuffList.psychedelic).getDuration() > 10) lowerHallucinationTick((EntityPlayer)ent);
					else cleanupLowerHallucination((EntityPlayer)ent);
				} else if (ent.worldObj.provider.dimensionId == 1 && ent.getActivePotionEffect(BuffList.psychedelic).getAmplifier() == 1) { // only amplified for higher
					if (ent.getActivePotionEffect(BuffList.psychedelic).getDuration() > 10) higherHallucinationTick((EntityPlayer)ent);
					else cleanupHigherHallucination((EntityPlayer)ent);
				}
			}
		}

		//mana link pfx
		if (ent.worldObj.isRemote)
			extendedProperties.spawnManaLinkParticles();

		if (ent.ticksExisted % 20 == 0)
			extendedProperties.cleanupManaLinks();

		if (world.isRemote){
			AMCore.proxy.sendLocalMovementData(ent);
		}
	}

	// currently unused
	private void breakTechnologyTick(EntityPlayer player) {
		boolean changed = false;
		for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
			ItemStack stack = player.inventory.getStackInSlot(i);
			if (MysteriumPatchesFixesMagicka.isItemTech(stack)) { // null checking is builtin
				player.inventory.setInventorySlotContents(i, disruptionEMP(stack));
				changed = true;
			}
		}
		if (changed && player.worldObj.isRemote) {
			player.worldObj.playSound(player.posX, player.posY, player.posZ, "random.anvil_break", 1.0F, rand.nextFloat() * 0.1F + 0.9F, false);
			AMCore.proxy.getLocalPlayer().addChatMessage(new ChatComponentText("You feel a wave of peculiar vibrations traversing your body. Pockets of space oddly curve and distort, and you sense something is out of place."));
		}
	}

	private void breakTechnologyTick(EntityItem ei) {
		ItemStack stack = ei.getEntityItem();
		if (MysteriumPatchesFixesMagicka.isItemTech(stack)) { // null checking is builtin
			ei.setEntityItemStack(disruptionEMP(stack));
			ei.worldObj.playSoundEffect(ei.posX, ei.posY, ei.posZ, "random.anvil_break", 1.0F, rand.nextFloat() * 0.1F + 0.9F);
		}
	}

	private ItemStack disruptionEMP(ItemStack stack) { // In modern warfare, weapons delivering a high energy EMP pulse are designed to disrupt communications equipment, the computers needed to operate modern warplanes, or even put the entire electrical network of a target country out of commission
		String oldItem = MysteriumPatchesFixesMagicka.getFullId(stack.getItem());
		boolean isRealItem = (oldItem != null);

		ItemStack replacement = new ItemStack(ItemsCommonProxy.techJunk, 1 + rand.nextInt(3), rand.nextInt(5));
		NBTTagCompound oldItemInfo = new NBTTagCompound();
		oldItemInfo.setString("item", isRealItem ? oldItem : MysteriumPatchesFixesMagicka.getFullId(ItemsCommonProxy.itemOre)); // makeItemStack to make item based on name string, meta, nbt, etc
		oldItemInfo.setInteger("meta", isRealItem ? stack.getItemDamage() : ItemsCommonProxy.itemOre.META_ARCANECOMPOUND);
		oldItemInfo.setInteger("size", isRealItem ? stack.stackSize : rand.nextInt(10));
		oldItemInfo.setTag("old_nbt", isRealItem ? stack.getTagCompound() : new NBTTagCompound());
		replacement.setTagCompound(oldItemInfo);

		stack.stackSize = 0;
		return replacement;
	}

	public static Map<EntityPlayer, ArrayList<EntityCreature>> hallucinationMap = new HashMap<EntityPlayer, ArrayList<EntityCreature>>();
	public static Map<EntityPlayer, ArrayList<EntityItem>> dustMap = new HashMap<EntityPlayer, ArrayList<EntityItem>>();
	public static Map<EntityCreature, Integer> tempCurseMap = new HashMap<EntityCreature, Integer>();

	private void cleanupLowerHallucination(EntityPlayer player) {
		player.removePotionEffect(BuffList.scrambleSynapses.id);
		player.extinguish();
		player.removePotionEffect(Potion.confusion.id);
		if (hallucinationMap.get(player) != null) {
			for (EntityCreature creature : hallucinationMap.get(player)) {
				if (creature != null) creature.setDead();
			}
			hallucinationMap.get(player).clear();
		}
		if (dustMap.get(player) != null) {
			for (EntityItem item : dustMap.get(player)) {
				if (item != null) item.setDead();
			}
			dustMap.get(player).clear();
		}
	}

	private void cleanupHigherHallucination(EntityPlayer player) {
		cleanupLowerHallucination(player);
		player.curePotionEffects(new ItemStack(Items.milk_bucket));
	}

	private void lowerHallucinationTick(EntityPlayer player) {
		if (player.worldObj.rand.nextInt(103) == 0) { // scramble
			player.addPotionEffect(new BuffEffectScrambleSynapses(50, 0));
		}
		if (player.worldObj.rand.nextInt(75) == 0) { // spontaneous combustion
			player.setFire(7);
		}
		if (player.worldObj.rand.nextInt(50) == 0) { // nausea
			player.addPotionEffect(new PotionEffect(Potion.confusion.id, 100, 0));
		}

		int x = MathHelper.floor_double(player.posX); // creatures
		int y = MathHelper.floor_double(player.posY);
		int z = MathHelper.floor_double(player.posZ);
		if(player.worldObj.rand.nextInt(550) == 0) {
			Class halclass = null;
			switch (player.worldObj.rand.nextInt(5)) {
				case 0:
				default:
					halclass = EntitySpecificHallucinations.EntityHallucinationCreeper.class;
					break;
				case 1:
					halclass = EntitySpecificHallucinations.EntityHallucinationZombie.class;
					break;
				case 2:
					halclass = EntitySpecificHallucinations.EntityHallucinationSpider.class;
					break;
				case 3:
					halclass = EntitySpecificHallucinations.EntityHallucinationWitherSkeleton.class;
					break;
				case 4:
					halclass = EntitySpecificHallucinations.EntityHallucinationMagmacube.class;
					break;
			}

			EntityCreature summoned = summonCreature(player.worldObj, halclass, x, y, z, player, 4, 9);
			if (hallucinationMap.get(player) == null) {
				hallucinationMap.put(player, new ArrayList<EntityCreature>());
			}
			hallucinationMap.get(player).add(summoned);
		}
		if(player.worldObj.rand.nextInt(100) == 0) { // dust
			EntityItem item = new EntityItem(player.worldObj);
			item.setPosition((x-15) + player.worldObj.rand.nextInt(16), y + player.worldObj.rand.nextInt(6), (z-15) + player.worldObj.rand.nextInt(16));
			item.setEntityItemStack(new ItemStack(ItemsCommonProxy.itemOre, 1, ItemsCommonProxy.itemOre.META_COGNITIVEDUST));
			player.worldObj.spawnEntityInWorld(item);
			if (dustMap.get(player) == null) {
				dustMap.put(player, new ArrayList<EntityItem>());
			}
			dustMap.get(player).add(item);
		}
	}

	private void higherHallucinationTick(EntityPlayer player) {
		lowerHallucinationTick(player); // all the effects of the lower... and more!
		if(player.worldObj.rand.nextInt(75) == 0) { // random effects
			player.addPotionEffect(new PotionEffect(player.worldObj.rand.nextInt(20)+1, 100, 0));
		}
		int x = MathHelper.floor_double(player.posX);
		int y = MathHelper.floor_double(player.posY);
		int z = MathHelper.floor_double(player.posZ);
		if(player.worldObj.rand.nextInt(80) == 0) { // random noises
			String soundString = null;
			switch (player.worldObj.rand.nextInt(8)) {
				case 0:
				default:
					soundString = "mob.zombie.say";
					break;
				case 1:
					soundString = "mob.skeleton.say";
					break;
				case 2:
					soundString = "game.tnt.primed";
					break;
				case 3:
					soundString = "random.bow";
					break;
				case 4:
					soundString = "random.chestopen";
					break;
				case 5:
					soundString = "random.chestclosed";
					break;
				case 6:
					soundString = "random.door_open";
					break;
				case 7:
					soundString = "random.fuse";
					break;
			}
			player.worldObj.playSoundAtEntity(player, soundString,1F, 1F);
		}
		if (player.worldObj.rand.nextInt(150) == 0) { // random floating explosion things
			int explosionX = (x-30) + player.worldObj.rand.nextInt(31);
			int explosionY = y + player.worldObj.rand.nextInt(25);
			int explosionZ = (z-30) + player.worldObj.rand.nextInt(31);
			player.worldObj.spawnParticle("largeexplode", explosionX, explosionY, explosionZ, 1.0D, 0.0D, 0.0D);
			player.worldObj.spawnParticle("hugeexplosion", explosionX, explosionY, explosionZ, 0.0D, 0.0D, 0.0D);
			player.worldObj.playSoundEffect(explosionX, explosionY, explosionZ, "random.explode", 4.0F, (1.0F + (rand.nextFloat() - rand.nextFloat()) * 0.2F) * 0.7F);
			player.attackEntityFrom(DamageSource.magic, 1 + player.worldObj.rand.nextInt(10));
		}
		if (player.worldObj.rand.nextInt(50) == 0) { // random teleporations and snaps
			int cx = 0, cy = 0, cz = 0;
			if (player.worldObj.rand.nextBoolean()) { // teleport
				cx = rand.nextInt(50);
				cy = rand.nextInt(7);
				cz = rand.nextInt(50);
				if (!player.worldObj.isAirBlock((int)player.posX + cx, (int)player.posY + cy, (int)player.posZ + cz) ||
						!player.worldObj.isAirBlock((int)player.posX + cx, (int)player.posY + cy - 1, (int)player.posZ + cz)) {
					cx = 0;
					cy = 0;
					cz = 0;
				}
			}
			player.setPositionAndRotation(player.posX + cx, player.posY + cy, player.posZ + cz, player.rotationYaw * (rand.nextFloat() + rand.nextFloat()), player.rotationPitch * (rand.nextFloat() + rand.nextFloat()));
		}
		if(player.worldObj.rand.nextInt(750) == 0) { // even more hallucinations + ones specific to end
			Class halclass = null;
			switch (player.worldObj.rand.nextInt(8)) {
				case 0:
				default:
					halclass = EntitySpecificHallucinations.EntityHallucinationCreeper.class;
					break;
				case 1:
					halclass = EntitySpecificHallucinations.EntityHallucinationZombie.class;
					break;
				case 2:
					halclass = EntitySpecificHallucinations.EntityHallucinationSpider.class;
					break;
				case 3:
					halclass = EntitySpecificHallucinations.EntityHallucinationWitherSkeleton.class;
					break;
				case 4:
					halclass = EntitySpecificHallucinations.EntityHallucinationEnderman.class;
					break;
				case 5:
				case 6:
				case 7:
					halclass = EntitySpecificHallucinations.EntityHallucinationEndermite.class;
					break;
			}

			EntityCreature summoned = summonCreature(player.worldObj, halclass, x, y, z, player, 5, 13);
			if (hallucinationMap.get(player) == null) {
				hallucinationMap.put(player, new ArrayList<EntityCreature>());
			}
			hallucinationMap.get(player).add(summoned);
		}
	}

	public static EntityCreature summonCreature(World world, Class creatureClass, int x, int y, int z, EntityLivingBase target, int minRange, int maxRange) {
		if(!world.isRemote) {
			int activeRadius = maxRange - minRange;
			int ax = world.rand.nextInt(activeRadius * 2 + 1);
			if(ax > activeRadius) {
				ax += minRange * 2;
			}
			int nx = x - maxRange + ax;
			int az = world.rand.nextInt(activeRadius * 2 + 1);
			if(az > activeRadius) {
				az += minRange * 2;
			}
			int nz = z - maxRange + az;
			int ny;
			for(ny = y; !world.isAirBlock(nx, ny, nz) && ny < y + 8; ++ny) {
				;
			}
			while(world.isAirBlock(nx, ny, nz) && ny > 0) {
				--ny;
			}
			int hy;
			for(hy = 0; world.isAirBlock(nx, ny + hy + 1, nz) && hy < 6; ++hy) {
				;
			}
			if(hy >= 2) {
				try {
					Constructor ex = creatureClass.getConstructor(new Class[]{World.class});
					EntityCreature creature = (EntityCreature)ex.newInstance(new Object[]{world});
					if(target instanceof EntityPlayer) {
						EntityPlayer player = (EntityPlayer)target;
						if(creature instanceof EntityHallucination) {
							((EntityHallucination)creature).setTarget(player.getCommandSenderName());
						}
					}

					creature.setLocationAndAngles(0.5D + (double)nx, 0.05D + (double)ny + 1.0D, 0.5D + (double)nz, 0.0F, 0.0F);
					world.spawnEntityInWorld(creature);

					return creature;
				} catch (NoSuchMethodException var20) {
					;
				} catch (InvocationTargetException var21) {
					;
				} catch (InstantiationException var22) {
					;
				} catch (IllegalAccessException var23) {
					;
				}
			}
		}
		return null;
	}

	private static Random rand = new Random();

	private boolean wasInEther = false;
	private int etherTicks = 0;

	public void handleEtherMovement(EntityLivingBase e){
		double d0 = e.posY;
		e.moveEntity(e.motionX, e.motionY, e.motionZ);
		e.motionX *= 0.500000011920929D;
		e.motionY *= 0.0500000011920929D;
		e.motionZ *= 0.500000011920929D;
		e.motionY += 0.05D;

		if (e.isCollidedHorizontally && e.isOffsetPositionInLiquid(e.motionX, e.motionY + 0.6000000238418579D - e.posY + d0, e.motionZ)) {
			e.motionY = 0.30000001192092896D;
		}

		float f = MathHelper.sqrt_double(e.motionX * e.motionX * 0.20000000298023224D + e.motionY * e.motionY + e.motionZ * e.motionZ * 0.20000000298023224D) * 0.2F;

		if (f > 1.0F) {
			f = 1.0F;
		}

		float f1 = (float)MathHelper.floor_double(e.boundingBox.minY);
		int i;
		float f2;
		float f3;

		if (!wasInEther && etherTicks > 1000) {
			e.playSound("game.neutral.swim.splash", 0.13f, 1.0F + (this.rand.nextFloat() - this.rand.nextFloat()));
			wasInEther = true;
		}

		etherTicks = 0;

		for (i = 0; (float)i < 1.0F + e.width * 20.0F; ++i) {
			f2 = (this.rand.nextFloat() * 2.0F - 1.0F) * e.width;
			f3 = (this.rand.nextFloat() * 2.0F - 1.0F) * e.width;
			e.worldObj.spawnParticle("bubble", e.posX + (double)f2, (double)(f1 + 1.0F), e.posZ + (double)f3, e.motionX, e.motionY - (double)(this.rand.nextFloat() * 0.2F), e.motionZ);
		}

		for (i = 0; (float)i < 1.0F + e.width * 20.0F; ++i) {
			f2 = (this.rand.nextFloat() * 2.0F - 1.0F) * e.width;
			f3 = (this.rand.nextFloat() * 2.0F - 1.0F) * e.width;
			e.worldObj.spawnParticle("splash", e.posX + (double)f2, (double)(f1 + 1.0F), e.posZ + (double)f3, e.motionX, e.motionY, e.motionZ);
		}

		e.fallDistance = 0;
		if (e.isBurning()) e.extinguish();
	}

	@SubscribeEvent
	public void onBucketFill(FillBucketEvent event){
		ItemStack result = attemptFill(event.world, event.target);

		if (result != null){
			event.result = result;
			event.setResult(Result.ALLOW);
		}
	}

	private ItemStack attemptFill(World world, MovingObjectPosition p){
		Block block = world.getBlock(p.blockX, p.blockY, p.blockZ);

		if (block == BlocksCommonProxy.liquidEssence){
			if (world.getBlockMetadata(p.blockX, p.blockY, p.blockZ) == 0) // Check that it is a source block
			{
				world.setBlock(p.blockX, p.blockY, p.blockZ, Blocks.air); // Remove the fluid block

				return new ItemStack(ItemsCommonProxy.itemAMBucket);
			}
		}

		return null;
	}

	@SubscribeEvent
	public void onEntityInteract(EntityInteractEvent event){
		if (!(event.entityLiving instanceof FakePlayer) && event.target instanceof EntityItemFrame)
			AMCore.proxy.itemFrameWatcher.startWatchingFrame((EntityItemFrame)event.target);
	}

	@SubscribeEvent
	public void onPlayerTossItem(ItemTossEvent event){
		if (!event.entityItem.worldObj.isRemote)
			EntityItemWatcher.instance.addWatchedItem(event.entityItem);
	}

	@SubscribeEvent
	public void onEntityAttacked(LivingAttackEvent event){
		if (event.source.isFireDamage() && event.entityLiving instanceof EntityPlayer && ((EntityPlayer)event.entityLiving).inventory.armorInventory[3] != null){
			if (((EntityPlayer)event.entityLiving).inventory.armorInventory[3].getItem() == ItemsCommonProxy.fireEars || ((EntityPlayer)event.entityLiving).inventory.armorInventory[3].getItem() == ItemsCommonProxy.archmageHood){
				((EntityPlayer)event.entityLiving).setFire(0);
				event.setCanceled(true);
				return;
			}
		} else if (event.entityLiving instanceof EntityPlayer && ((EntityPlayer)event.entityLiving).inventory.armorInventory[3] != null && ((EntityPlayer)event.entityLiving).inventory.armorInventory[3].getItem() == ItemsCommonProxy.archmageHood) {
			if (event.source.getEntity() != null) {
				event.source.getEntity().attackEntityFrom(new DamageSourceFire(event.entityLiving), event.entityLiving.worldObj.rand.nextInt(3));
				event.source.getEntity().setFire(8);
			}
		}

		if (event.entityLiving.isPotionActive(BuffList.manaShield)){
			if (ExtendedProperties.For(event.entityLiving).getCurrentMana() >= event.ammount * 250f){
				ExtendedProperties.For(event.entityLiving).deductMana(event.ammount * 100f);
				ExtendedProperties.For(event.entityLiving).forceSync();
				for (int i = 0; i < Math.min(event.ammount, 5 * AMCore.config.getGFXLevel()); ++i)
					AMCore.proxy.particleManager.BoltFromPointToPoint(event.entityLiving.worldObj,
							event.entityLiving.posX,
							event.entityLiving.posY + event.entityLiving.worldObj.rand.nextFloat() * event.entityLiving.getEyeHeight(),
							event.entityLiving.posZ,
							event.entityLiving.posX - 1 + event.entityLiving.worldObj.rand.nextFloat() * 2,
							event.entityLiving.posY - 1 + event.entityLiving.worldObj.rand.nextFloat() * 2,
							event.entityLiving.posZ - 1 + event.entityLiving.worldObj.rand.nextFloat() * 2, 6, -1);
				event.entityLiving.worldObj.playSoundAtEntity(event.entityLiving, "arsmagica2:spell.cast.arcane", 1.0f, event.entityLiving.worldObj.rand.nextFloat() + 0.5f);
				event.setCanceled(true);
				return;
			}
		}
	}

	private double oldKnockbackValue = -1;

	@SubscribeEvent
	public void onEntityHurt(LivingHurtEvent event){

		if (event.source.isFireDamage() && event.entityLiving instanceof EntityPlayer && ((EntityPlayer)event.entityLiving).inventory.armorInventory[3] != null){
			if (((EntityPlayer)event.entityLiving).inventory.armorInventory[3].getItem() == ItemsCommonProxy.fireEars || ((EntityPlayer)event.entityLiving).inventory.armorInventory[3].getItem() == ItemsCommonProxy.archmageHood){
				event.setCanceled(true);
				return;
			}
		} else if (event.entityLiving instanceof EntityPlayer && ((EntityPlayer)event.entityLiving).inventory.armorInventory[3] != null && ((EntityPlayer)event.entityLiving).inventory.armorInventory[3].getItem() == ItemsCommonProxy.archmageHood) {
			if (event.source.getEntity() != null) {
				event.source.getEntity().attackEntityFrom(new DamageSourceFire(event.entityLiving), event.entityLiving.worldObj.rand.nextInt(3));
				event.source.getEntity().setFire(8);
			}
		}

		if (event.entityLiving instanceof EntityPlayer && ((EntityPlayer)event.entityLiving).inventory.armorInventory[2] != null){
			if (((EntityPlayer)event.entityLiving).inventory.armorInventory[2].getItem() == ItemsCommonProxy.archmageArmor){
				if (oldKnockbackValue == -1) {
					oldKnockbackValue = event.entityLiving.getEntityAttribute(SharedMonsterAttributes.knockbackResistance).getAttributeValue();
				}
				event.entityLiving.getEntityAttribute(SharedMonsterAttributes.knockbackResistance).setBaseValue(1);
			} else {
				event.entityLiving.getEntityAttribute(SharedMonsterAttributes.knockbackResistance).setBaseValue(oldKnockbackValue);
				oldKnockbackValue = -1;
			}
		} else {
			event.entityLiving.getEntityAttribute(SharedMonsterAttributes.knockbackResistance).setBaseValue(oldKnockbackValue);
			oldKnockbackValue = -1;
		}

		PotionEffect magicShield = event.entityLiving.getActivePotionEffect(BuffList.magicShield);
		if (magicShield != null){
			event.ammount *= 0.5f;
			event.ammount *= (1 / (magicShield.getAmplifier() + 1));
		}

		if (event.entityLiving.isPotionActive(BuffList.manaShield)){
			float manaToTake = Math.min(ExtendedProperties.For(event.entityLiving).getCurrentMana(), event.ammount * 250f);
			event.ammount -= manaToTake / 250f;
			ExtendedProperties.For(event.entityLiving).deductMana(manaToTake);
			ExtendedProperties.For(event.entityLiving).forceSync();
			for (int i = 0; i < Math.min(event.ammount, 5 * AMCore.config.getGFXLevel()); ++i)
				AMCore.proxy.particleManager.BoltFromPointToPoint(event.entityLiving.worldObj,
						event.entityLiving.posX,
						event.entityLiving.posY + event.entityLiving.worldObj.rand.nextFloat() * event.entityLiving.getEyeHeight(),
						event.entityLiving.posZ,
						event.entityLiving.posX - 1 + event.entityLiving.worldObj.rand.nextFloat() * 2,
						event.entityLiving.posY + event.entityLiving.getEyeHeight() - 1 + event.entityLiving.worldObj.rand.nextFloat() * 2,
						event.entityLiving.posZ - 1 + event.entityLiving.worldObj.rand.nextFloat() * 2, 6, -1);
			event.entityLiving.worldObj.playSoundAtEntity(event.entityLiving, "arsmagica2:spell.cast.arcane", 1.0f, event.entityLiving.worldObj.rand.nextFloat() + 0.5f);
			if (event.ammount <= 0){
				event.setCanceled(true);
				return;
			}
		}

		Entity entitySource = event.source.getSourceOfDamage();
		if (entitySource instanceof EntityPlayer
				&& ((EntityPlayer)entitySource).inventory.armorInventory[2] != null
				&& ((EntityPlayer)entitySource).inventory.armorInventory[2].getItem() == ItemsCommonProxy.earthGuardianArmor
				&& ((EntityPlayer)entitySource).getCurrentEquippedItem() == null){
			event.ammount += 4;

			double deltaZ = event.entityLiving.posZ - entitySource.posZ;
			double deltaX = event.entityLiving.posX - entitySource.posX;
			double angle = Math.atan2(deltaZ, deltaX);
			double speed = ((EntityPlayer)entitySource).isSprinting() ? 3 : 2;
			double vertSpeed = ((EntityPlayer)entitySource).isSprinting() ? 0.5 : 0.325;

			if (event.entityLiving instanceof EntityPlayer){
				AMNetHandler.INSTANCE.sendVelocityAddPacket(event.entityLiving.worldObj, event.entityLiving, speed * Math.cos(angle), vertSpeed, speed * Math.sin(angle));
			}else{
				event.entityLiving.motionX += (speed * Math.cos(angle));
				event.entityLiving.motionZ += (speed * Math.sin(angle));
				event.entityLiving.motionY += vertSpeed;
			}
			event.entityLiving.worldObj.playSoundAtEntity(event.entityLiving, "arsmagica2:spell.cast.earth", 0.4f, event.entityLiving.worldObj.rand.nextFloat() * 0.1F + 0.9F);
		}
		if (entitySource instanceof EntityPlayer
				&& ((EntityPlayer)entitySource).inventory.armorInventory[2] != null
				&& ((EntityPlayer)entitySource).inventory.armorInventory[2].getItem() == ItemsCommonProxy.archmageArmor){
			event.ammount += 5;

			double deltaZ = event.entityLiving.posZ - entitySource.posZ;
			double deltaX = event.entityLiving.posX - entitySource.posX;
			double angle = Math.atan2(deltaZ, deltaX);
			double speed = ((EntityPlayer)entitySource).isSprinting() ? 5 : 3;
			double vertSpeed = ((EntityPlayer)entitySource).isSprinting() ? 1 : 0.65;

			if (event.entityLiving instanceof EntityPlayer){
				AMNetHandler.INSTANCE.sendVelocityAddPacket(event.entityLiving.worldObj, event.entityLiving, speed * Math.cos(angle), vertSpeed, speed * Math.sin(angle));
			}else{
				event.entityLiving.motionX += (speed * Math.cos(angle));
				event.entityLiving.motionZ += (speed * Math.sin(angle));
				event.entityLiving.motionY += vertSpeed;
			}
			event.entityLiving.worldObj.playSoundAtEntity(event.entityLiving, "arsmagica2:spell.cast.earth", 0.4f, event.entityLiving.worldObj.rand.nextFloat() * 0.1F + 0.9F);
		}

		ExtendedProperties extendedProperties = ExtendedProperties.For(event.entityLiving);
		EntityLivingBase ent = event.entityLiving;
		if (!(extendedProperties.getContingencyEffect(0).getItem() instanceof ItemSnowball)){
			extendedProperties.procContingency(0, (event.source.getEntity() != null && event.source.getEntity() instanceof EntityLivingBase) ? (EntityLivingBase)event.source.getEntity() : null);
		}
		if (!(extendedProperties.getContingencyEffect(4).getItem() instanceof ItemSnowball) && ent.getHealth() <= ent.getMaxHealth() / 3){
			extendedProperties.procContingency(4, (event.source.getEntity() != null && event.source.getEntity() instanceof EntityLivingBase) ? (EntityLivingBase)event.source.getEntity() : null);
		}

		if (entitySource instanceof EntityPlayer
				&& ((EntityPlayer)entitySource).inventory.armorInventory[3] != null
				&& ((EntityPlayer)entitySource).inventory.armorInventory[2] != null
				&& ((EntityPlayer)entitySource).inventory.armorInventory[1] != null
				&& ((EntityPlayer)entitySource).inventory.armorInventory[0] != null
				&& ((EntityPlayer)entitySource).inventory.armorInventory[3].getItem() == ItemsCommonProxy.archmageHood
				&& ((EntityPlayer)entitySource).inventory.armorInventory[2].getItem() == ItemsCommonProxy.archmageArmor
				&& ((EntityPlayer)entitySource).inventory.armorInventory[1].getItem() == ItemsCommonProxy.archmageLeggings
				&& ((EntityPlayer)entitySource).inventory.armorInventory[0].getItem() == ItemsCommonProxy.archmageBoots){
			if (entitySource.isSneaking()) {
				extendedProperties.toggleFlipped();
				tempFlipped.put(extendedProperties, 50);
			}
		}

		if (ent.isPotionActive(BuffList.fury.id))
			event.ammount /= 2;

		if (entitySource instanceof EntityLivingBase
				&& ((EntityLivingBase)entitySource).isPotionActive(BuffList.shrink))
			event.ammount /= 2;
	}

	private static Map<ExtendedProperties, Integer> tempFlipped = new HashMap<ExtendedProperties, Integer>();

	@SubscribeEvent
	public void onBlockOverlay(RenderBlockOverlayEvent event) {
		if (event.overlayType == RenderBlockOverlayEvent.OverlayType.WATER) {
			if (AffinityHelper.isNotInWaterActually.contains(event.player)) {
				event.setCanceled(true);
			}
		} else if (event.overlayType == RenderBlockOverlayEvent.OverlayType.FIRE) {
			if (event.player instanceof EntityPlayer && ((EntityPlayer)event.player).inventory.armorInventory[3] != null && ((EntityPlayer)event.player).inventory.armorInventory[3].getItem() == ItemsCommonProxy.archmageHood) {
				event.setCanceled(true);
			}
		}
	}

	@SubscribeEvent
	public void onEntityJoinWorld(EntityJoinWorldEvent event){
		if (event.entity instanceof EntityLivingBase && ((EntityLivingBase)event.entity).isPotionActive(BuffList.temporalAnchor.id)){
			((EntityLivingBase)event.entity).removePotionEffect(BuffList.temporalAnchor.id);
		}
	}

	@SubscribeEvent
	public void onBreakSpeed(BreakSpeed event){
		EntityPlayer player = event.entityPlayer;
		if (player.isPotionActive(BuffList.fury.id))
			event.newSpeed = event.originalSpeed * 5;
	}

	@SubscribeEvent
	public void onManaCost(ManaCostEvent event){
		if (event.caster.getHeldItem() != null && event.caster.getHeldItem().getItem() == ItemsCommonProxy.arcaneSpellbook){
			event.manaCost *= 0.75f;
			event.burnout *= 0.4f;
		}
	}

	@SubscribeEvent
	public void onPlayerPickupItem(EntityItemPickupEvent event){
		if (event.entityPlayer == null)
			return;

		if (!event.entityPlayer.worldObj.isRemote && event.item.getEntityItem().getItem() == ItemsCommonProxy.arcaneCompendium){
			AMNetHandler.INSTANCE.sendCompendiumUnlockPacket((EntityPlayerMP)event.entityPlayer, "shapes", true);
			AMNetHandler.INSTANCE.sendCompendiumUnlockPacket((EntityPlayerMP)event.entityPlayer, "components", true);
			AMNetHandler.INSTANCE.sendCompendiumUnlockPacket((EntityPlayerMP)event.entityPlayer, "modifiers", true);
			if (ExtendedProperties.For(event.entityPlayer).getMagicLevel() <= 0) {
				event.entityPlayer.addChatMessage(new ChatComponentText("You have unlocked the secrets of the arcane!"));
				ExtendedProperties.For(event.entityPlayer).setMagicLevelWithMana(1);
				ExtendedProperties.For(event.entityPlayer).forceSync();
			}
			return;
		}

		if (event.item.getEntityItem().getItem() == ItemsCommonProxy.spell){
			if (event.entityPlayer.worldObj.isRemote){
				AMNetHandler.INSTANCE.sendCompendiumUnlockPacket((EntityPlayerMP)event.entityPlayer, "spell_book", false);
				AMNetHandler.INSTANCE.sendCompendiumUnlockPacket((EntityPlayerMP)event.entityPlayer, "shapes", true);
				AMNetHandler.INSTANCE.sendCompendiumUnlockPacket((EntityPlayerMP)event.entityPlayer, "components", true);
				AMNetHandler.INSTANCE.sendCompendiumUnlockPacket((EntityPlayerMP)event.entityPlayer, "modifiers", true);
			}
		}else{
			Item item = event.item.getEntityItem().getItem();
			int meta = event.item.getEntityItem().getItemDamage();

			if (event.entityPlayer.worldObj.isRemote &&
					item.getUnlocalizedName() != null && (
					AMCore.proxy.items.getArsMagicaItems().contains(item)) ||
					(item instanceof ItemBlock && AMCore.proxy.blocks.getArsMagicaBlocks().contains(((ItemBlock)item).field_150939_a))){
				AMNetHandler.INSTANCE.sendCompendiumUnlockPacket((EntityPlayerMP)event.entityPlayer, item.getUnlocalizedName().replace("item.", "").replace("arsmagica2:", "").replace("tile.", "") + ((meta > -1) ? "@" + meta : ""), false);
			}
		}
	}
}

