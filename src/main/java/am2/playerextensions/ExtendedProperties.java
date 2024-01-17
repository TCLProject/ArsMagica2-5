package am2.playerextensions;

import am2.AMCore;
import am2.api.ArsMagicaApi;
import am2.api.IExtendedProperties;
import am2.api.events.PlayerMagicLevelChangeEvent;
import am2.api.math.AMVector2;
import am2.api.math.AMVector3;
import am2.api.spell.enums.ContingencyTypes;
import am2.api.spell.enums.SkillPointTypes;
import am2.armor.ArmorHelper;
import am2.armor.ArsMagicaArmorMaterial;
import am2.armor.infusions.GenericImbuement;
import am2.armor.infusions.ImbuementRegistry;
import am2.bosses.EntityLifeGuardian;
import am2.buffs.BuffList;
import am2.damage.DamageSourceDarkNexus;
import am2.damage.DamageSources;
import am2.guis.AMGuiHelper;
import am2.items.ItemManaStone;
import am2.items.ItemSoulspike;
import am2.items.ItemsCommonProxy;
import am2.lore.ArcaneCompendium;
import am2.lore.CompendiumEntry;
import am2.network.AMDataReader;
import am2.network.AMDataWriter;
import am2.network.AMNetHandler;
import am2.network.AMPacketIDs;
import am2.particles.AMLineArc;
import am2.spell.SkillManager;
import am2.spell.SkillTreeManager;
import am2.spell.SpellHelper;
import am2.spell.components.FireDamage;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemSnowball;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;
import net.minecraftforge.common.MinecraftForge;

import java.util.*;

public class ExtendedProperties implements IExtendedProperties, IExtendedEntityProperties{
	private EntityLivingBase entity;

	public static final String identifier = "ArsMagicaExProps";
	public static final int maxMagicLevel = 99;

	private static float magicRegenPerLevelPerTick = 0.15f;
	private static float entityMagicPerLevelBase = 0.20f;
	private static int baseTicksForFullRegen = 2400;
	private int ticksForFullRegen = baseTicksForFullRegen;

	private ArrayList<Integer> summon_ent_ids = new ArrayList<Integer>();

	// I AM SO BLOODY TIRED OF MAKING NEW VARIABLES FOR EEEP!!!!!!!
	// I AM SO BLOODY TIRED OF MAKING NEW VARIABLES FOR EEEP!!!!!!!
	// I AM SO BLOODY TIRED OF MAKING NEW VARIABLES FOR EEEP!!!!!!!
	// I AM SO BLOODY TIRED OF MAKING NEW VARIABLES FOR EEEP!!!!!!!
	// I AM SO BLOODY TIRED OF MAKING NEW VARIABLES FOR EEEP!!!!!!!
	// I AM SO BLOODY TIRED OF MAKING NEW VARIABLES FOR EEEP!!!!!!!
	/** I AM SO BLOODY TIRED OF MAKING NEW VARIABLES FOR EEEP!!!!!!!*/
	private Map<String, String> extra_properties = new HashMap<String, String>();

	private Map<String, String> compendium_entries = new HashMap<String, String>();

	private double markX;
	private double markY;
	private double markZ;
	private int markDimension;

	private int healCooldown;

	private float flipRotation;
	private float prevFlipRotation;

	private float shrinkPct;
	private float prevShrinkPct;
	public float shrinkAmount = 0;
	public AMVector2 originalSize;
	public float yOffsetOrig = 0.75f;

	private float currentMana;
	private float maxMana;
	private float currentFatigue;
	private float maxFatigue;

	private int magicLevel;
	private float magicXP;

	private int numSummons;
	private ArrayList<ManaLinkEntry> manaLinks;

	private byte bitFlag;
	private boolean hasDoneFullSync;

	public int sprintTimer = 0;
	public int sneakTimer = 0;
	public int itemUseTimer = 0;

	private int fallProtection = 0;
	private int previousBreath = 300;

	private ContingencyTypes[] contingencyTypes = {ContingencyTypes.DAMAGE_TAKEN, ContingencyTypes.DEATH,
			ContingencyTypes.FALL, ContingencyTypes.ON_FIRE, ContingencyTypes.HEALTH_LOW};
	private ItemStack[] contingencyStacks = {new ItemStack(Items.snowball), new ItemStack(Items.snowball),
			new ItemStack(Items.snowball), new ItemStack(Items.snowball), new ItemStack(Items.snowball)};

	public float TK_Distance = 8;

	public float bankedInfusionHelm = 0.0f;
	public float bankedInfusionChest = 0.0f;
	public float bankedInfusionLegs = 0.0f;
	public float bankedInfusionBoots = 0.0f;

	private Entity inanimateTarget;

	public int[] armorProcCooldowns = new int[4];

	private int ticksSinceLastRegen = 0;
	private int ticksToRegen = 0;

	private boolean needsArmorTickCounterSync = false;

	private int AuraIndex = 15;
	private int AuraBehaviour = 0;
	private float AuraScale = 0.5f;
	private int AuraColor = 0xFFFFFF;
	private float AuraAlpha = 1f;
	private boolean AuraColorRandomize = true;
	private boolean AuraColorDefault = true;
	private int AuraDelay = 1;
	private int AuraQuantity = 2;
	private float AuraSpeed = 0.5f;

	private boolean hasInitialized = false;
	public boolean astralBarrierBlocked = false;
	private boolean forcingSync = false;
	private boolean isCritical;
	public boolean isRecoveringKeystone = false;
	public boolean hadFlight = false;
	private boolean disableGravity = false;

	public boolean guardian1 = false;
	public boolean guardian2 = false;
	public boolean guardian3 = false;
	public boolean guardian4 = false;
	public boolean guardian5 = false;
	public boolean guardian6 = false;
	public boolean guardian7 = false;
	public boolean guardian8 = false;
	public boolean guardian9 = false;
	public boolean guardian10 = false;

	public boolean hasRitual = false;

	public static final int UPD_CURRENT_MANA_FATIGUE = 0x1;
	public static final int UPD_MAX_MANA_FATIGUE = 0x2;
	public static final int UPD_MAGIC_LEVEL = 0x4;
	public static final int UPD_DISABLE_GRAVITY = 0x8;
	public static final int UPD_NUM_SUMMONS = 0x10;
	public static final int UPD_MARK = 0x20;
	public static final int UPD_CONTINGENCY = 0x40;
	public static final int UPD_BITFLAG = 0x80;
	public static final int UPD_BETA_PARTICLES = 0x100;
	public static final int UPD_TK_DISTANCE = 0x200;
	public static final int UPD_MANALINK = 0x400;
	public static final int BIT_MARK_SET = 0x2;
	public static final int BIT_FLIPPED = 0x4;
	public static final int BIT_SHRUNK = 0x8;

	private int updateFlags;
	private static final int syncTickDelay = 50; //2.5 seconds
	private int ticksToSync;
	public int redGlint = 0;
	private int lightningsLeft = 0;
	private int ticksLeft = 0;

	public ExtendedProperties(){
		hasInitialized = false;
		hasDoneFullSync = false;
		manaLinks = new ArrayList<ManaLinkEntry>();
	}

	public static ExtendedProperties For(EntityLivingBase living){
		return (ExtendedProperties)living.getExtendedProperties(identifier);
	}

	//=======================================================================================
	// Getters
	//=======================================================================================
	public boolean getHasDoneFullSync(){
		return this.hasDoneFullSync;
	}

	@Override
	public boolean getHasUnlockedAugmented(){
		if (entity instanceof EntityPlayer){
			return SkillData.For((EntityPlayer)entity).isEntryKnown(SkillTreeManager.instance.getSkillTreeEntry(SkillManager.instance.getSkill("AugmentedCasting")));
		}
		return true;
	}

	public int getBreathAmount(){
		return this.previousBreath;
	}

	@Override
	public int getNumSummons(){
		return this.numSummons;
	}

	@Override
	public AMVector3 getMarkLocation(){
		return new AMVector3(markX, markY, markZ);
	}

	@Override
	public boolean getMarkSet(){
		return (this.bitFlag & BIT_MARK_SET) == BIT_MARK_SET;
	}

	public int getAuraBehaviour(){
		return AuraBehaviour;
	}

	public int getAuraIndex(){
		return AuraIndex;
	}

	public float getAuraScale(){
		return AuraScale;
	}

	public float getAuraAlpha(){
		return AuraAlpha;
	}

	public boolean getAuraColorDefault(){
		return AuraColorDefault;
	}

	public boolean getAuraColorRandomize(){
		return AuraColorRandomize;
	}

	public int getAuraColor(){
		return AuraColor;
	}

	public int getAuraDelay(){
		return AuraDelay;
	}

	public int getAuraQuantity(){
		return AuraQuantity;
	}

	public float getAuraSpeed(){
		return AuraSpeed;
	}

	public ContingencyTypes getContingencyType(int num){
		return this.contingencyTypes[num];
	}

	public ItemStack getContingencyEffect(int num){
		return this.contingencyStacks[num];
	}

	public int getFallProtection(){
		return fallProtection;
	}

	@Override
	public int getMarkDimension(){
		return this.markDimension;
	}

	public double getMarkX(){
		return this.markX;
	}

	public double getMarkY(){
		return this.markY;
	}

	public double getMarkZ(){
		return this.markZ;
	}

	public float getCurrentFatigue(){
		return this.currentFatigue;
	}

	public float getMaxFatigue(){
		return (float)(this.maxFatigue + this.entity.getAttributeMap().getAttributeInstance(ArsMagicaApi.maxBurnoutBonus).getAttributeValue());
	}

	@Override
	public float getCurrentMana(){
		return this.currentMana;
	}

	@Override
	public float getMaxMana(){
		float max = this.maxMana;
		if (this.entity instanceof EntityPlayer) {
			if (SkillData.For((EntityPlayer)this.entity).isEntryKnown(SkillTreeManager.instance.getSkillTreeEntry(SkillManager.instance.getSkill("ManaCapacityIII")))) {
				max += 15000;
			} else if (SkillData.For((EntityPlayer)this.entity).isEntryKnown(SkillTreeManager.instance.getSkillTreeEntry(SkillManager.instance.getSkill("ManaCapacityII")))) {
				max += 7000;
			} else if (SkillData.For((EntityPlayer)this.entity).isEntryKnown(SkillTreeManager.instance.getSkillTreeEntry(SkillManager.instance.getSkill("ManaCapacityI")))) {
				max += 2000;
			}
		}
		if (guardian1) { // this looks like awful code and it is
			max += 2000;
		}
		if (guardian2) {
			max += 2000;
		}
		if (guardian3) {
			max += 2000;
		}
		if (guardian4) {
			max += 2000;
		}
		if (guardian5) {
			max += 2000;
		}
		if (guardian6) {
			max += 2000;
		}
		if (guardian7) {
			max += 2000;
		}
		if (guardian8) {
			max += 2000;
		}
		if (guardian9) {
			max += 2000;
		}
		if (guardian10) {
			max += 2000;
		}
		if (hasRitual) {
			max += 23000; // changed from 25000
		}
		try {
			if (this.entity instanceof EntityPlayer) {
				if (this.entity.getActivePotionEffect(BuffList.manaBoost) != null) {
					max *= 1 + (0.25 * (this.entity.getActivePotionEffect(BuffList.manaBoost).getAmplifier() + 1));
				}
			}
		} catch (NullPointerException npe) {;} // just in case
		try {
			return (float) (max + this.entity.getAttributeMap().getAttributeInstance(ArsMagicaApi.maxManaBonus).getAttributeValue());
		} catch (NullPointerException npe) { // fix for entities which crash with the above code
			return max;
		}
	}

	public void setMaxMana(float maxMana){
		if (AMCore.config.getManaCap() > 0){
			this.maxMana = (float)Math.min(maxMana, AMCore.config.getManaCap());
		}else{
			this.maxMana = maxMana;
		}
		this.setUpdateFlag(UPD_MAX_MANA_FATIGUE);
	}

	public byte[] getUpdateData(){
		AMDataWriter writer = new AMDataWriter();
		writer.add(this.entity.getEntityId());
		writer.add(this.updateFlags);

		if ((this.updateFlags & UPD_BITFLAG) == UPD_BITFLAG){
			writer.add(this.bitFlag);
		}
		if ((this.updateFlags & UPD_CURRENT_MANA_FATIGUE) == UPD_CURRENT_MANA_FATIGUE){
			writer.add(this.currentMana);
			writer.add(this.currentFatigue);
			writer.add(guardian1);
			writer.add(guardian2);
			writer.add(guardian3);
			writer.add(guardian4);
			writer.add(guardian5);
			writer.add(guardian6);
			writer.add(guardian7);
			writer.add(guardian8);
			writer.add(guardian9);
			writer.add(guardian10);
			writer.add(hasRitual);
		}
		if ((this.updateFlags & UPD_MAGIC_LEVEL) == UPD_MAGIC_LEVEL){
			writer.add(this.magicLevel);
			writer.add(this.magicXP);
		}
		if ((this.updateFlags & UPD_MARK) == UPD_MARK){
			writer.add(this.markX);
			writer.add(this.markY);
			writer.add(this.markZ);
			writer.add(this.markDimension);
			writer.add(this.getMarkSet());
		}
		if ((this.updateFlags & UPD_MAX_MANA_FATIGUE) == UPD_MAX_MANA_FATIGUE){
			writer.add(this.maxMana);
			writer.add(this.maxFatigue);
		}
		if ((this.updateFlags & UPD_NUM_SUMMONS) == UPD_NUM_SUMMONS){
			writer.add(this.numSummons);
		}
		if ((this.updateFlags & UPD_BETA_PARTICLES) == UPD_BETA_PARTICLES && entity instanceof EntityPlayer && AMCore.proxy.playerTracker.hasAA((EntityPlayer)entity)){
			writer.add(this.getAuraIndex());
			writer.add(this.getAuraBehaviour());
			writer.add(this.getAuraScale());
			writer.add(this.getAuraAlpha());
			writer.add(this.getAuraColorRandomize());
			writer.add(this.getAuraColorDefault());
			writer.add(this.getAuraColor());
			writer.add(this.getAuraDelay());
			writer.add(this.getAuraQuantity());
			writer.add(this.getAuraSpeed());
		}
		if ((this.updateFlags & UPD_CONTINGENCY) == UPD_CONTINGENCY){
			for (int i = 0; i < 5; i++){
				if (this.contingencyStacks[i] != null){
					writer.add(this.contingencyStacks[i]);
				} else {
					writer.add(new ItemStack(Items.snowball));
				}
			}
		}
		if ((this.updateFlags & UPD_MANALINK) == UPD_MANALINK){
			writer.add(this.manaLinks.size());
			for (ManaLinkEntry entry : this.manaLinks)
				writer.add(entry.entityID);
		}
		if ((this.updateFlags & UPD_DISABLE_GRAVITY) == UPD_DISABLE_GRAVITY){
			writer.add(this.disableGravity);
		}

		NBTTagCompound extra_data = new NBTTagCompound();
		int c = 0;
		for (Object o : extra_properties.keySet()) {
			String iS = (String)o;
			String iValue = extra_properties.get(iS);
			extra_data.setString("persistentobj" + c, iValue);
			extra_data.setString("persistentobjname" + c, iS);
			c++;
		}
		extra_data.setInteger("persistentobjsize", extra_properties.size());
		writer.add(extra_data);

		this.updateFlags = 0;
		this.forcingSync = false;
		return writer.generate();
	}

	@Override
	public int getMagicLevel(){
		return this.magicLevel;
	}

	public float getXPToNextLevel(){
		return (float)Math.pow(magicLevel * 0.25f, 1.5f);
	}

	public float getMagicXP(){
		return this.magicXP;
	}

	public Entity getInanimateTarget(){
		return this.inanimateTarget;
	}

	public boolean getHasUpdate(){
		if (!(this.entity instanceof EntityPlayer) && !forcingSync){
			return false;
		}
		this.ticksToSync--;
		if (this.ticksToSync <= 0) this.ticksToSync = this.syncTickDelay;
		return this.updateFlags != 0 && this.ticksToSync == this.syncTickDelay;
	}

	public boolean getCanHaveMoreSummons(){

		if (entity instanceof EntityLifeGuardian)
			return true;

		int maxSummons = 1;
		if (entity instanceof EntityPlayer && SkillData.For((EntityPlayer)entity).isEntryKnown(SkillTreeManager.instance.getSkillTreeEntry(SkillManager.instance.getSkill("ExtraSummon"))))
			maxSummons++;

		verifySummons();

		return this.numSummons < maxSummons;
	}

	public boolean getIsFlipped(){
		return (bitFlag & BIT_FLIPPED) == BIT_FLIPPED;
	}

	public boolean getIsShrunk(){
		return (bitFlag & BIT_SHRUNK) == BIT_SHRUNK;
	}

	public float getFlipRotation(){
		return this.flipRotation;
	}

	public float getPrevFlipRotation(){
		return this.prevFlipRotation;
	}

	public float getShrinkPct(){
		return this.shrinkPct;
	}

	public float getPrevShrinkPct(){
		return this.prevShrinkPct;
	}

	public void setShrinkPct(float pct){
		this.prevShrinkPct = this.shrinkPct;
		this.shrinkPct = pct;
	}

	public boolean shouldReverseInput(){
		return getFlipRotation() > 0 || this.entity.isPotionActive(BuffList.scrambleSynapses.id);
	}

	public AMVector2 getOriginalSize(){
		return this.originalSize;
	}

	public boolean getCanHeal(){
		return healCooldown <= 0;
	}

	//=======================================================================================
	// Setters
	//=======================================================================================
	public void setMarkLocation(double x, double y, double z, int dimension){
		setMarkX(x);
		setMarkY(y);
		setMarkZ(z);
		setMarkDimension(dimension);
		setMarkSet(true);
		setUpdateFlag(UPD_MARK);
	}

	public void setNoMarkLocation(){
		setMarkSet(false);
		setUpdateFlag(UPD_MARK);
	}

	public void setBreathAmount(int breath){
		this.previousBreath = breath;
	}

	@Override
	public boolean setMagicLevelWithMana(int level){

		if (level > maxMagicLevel) level = maxMagicLevel;
		if (level < 0) level = 0;
		setMagicLevel(level);
//		if (getMaxMana() > ((float)(Math.pow(level, 1.5f) * (85f * ((float)level / maxMagicLevel)) + 500f) / 2F) + 17000) {
//			setMaxMana(((float)(Math.pow(level, 1.5f) * (85f * ((float)level / maxMagicLevel)) + 500f) / 2F) + 23000); // this isn't needed to keep the ritual effect, it autoapplies on getMaxMana
//		} else {
			setMaxMana((float)(Math.pow(level, 1.5f) * (85f * ((float)level / maxMagicLevel)) + 500f) / 2F); // max mana without bosses, talents or rituals is 42k
//		}
		setCurrentMana(getMaxMana());
		setCurrentFatigue(0);
		setMaxFatigue(level * 10 + 1);

		return true;
	}

	public void setInanimateTarget(Entity ent){
		if (ent instanceof EntityLivingBase)
			return;
		this.inanimateTarget = ent;
	}

	public void setMarkY(double markY){
		this.markY = markY;
	}

	public void setMarkZ(double markZ){
		this.markZ = markZ;
	}

	public void setCurrentFatigue(float currentFatigue){
		if (currentFatigue < 0) currentFatigue = 0;
		if (currentFatigue >= getMaxFatigue()) {
			currentFatigue = getMaxFatigue() - 1;
			// BURNOUT NEGATIVE EFFECTS
			if (this.entity instanceof EntityPlayer && !((EntityPlayer)this.entity).capabilities.isCreativeMode){
				Random random = new Random();
				if (currentFatigue > 50){ // lvl 5+
					int roll = random.nextInt(4);
					if (roll == 0){
						if (currentFatigue < 250){
							if (!this.entity.worldObj.isRemote)
								this.entity.addPotionEffect(new PotionEffect(Potion.blindness.id, random.nextInt(100), 1));
						}
					}else if (roll == 1){
						this.deductMana(this.currentMana / 4);
						if (!this.entity.worldObj.isRemote){
							List entitiesNear = this.entity.worldObj.getEntitiesWithinAABBExcludingEntity(this.entity, AxisAlignedBB.getBoundingBox(this.entity.posX - 5, this.entity.posY - 5, this.entity.posZ - 5, this.entity.posX + 5, this.entity.posY + 5, this.entity.posZ + 5));
							for (Object o : entitiesNear){
								if (!this.entity.worldObj.isRemote && !MinecraftServer.getServer().isPVPEnabled()) continue;
								((Entity)o).attackEntityFrom(DamageSource.magic, currentFatigue / 25);
								float f = (random.nextFloat() - 0.5F) * 0.2F;
								float f1 = (random.nextFloat() - 0.5F) * 0.2F;
								float f2 = (random.nextFloat() - 0.5F) * 0.2F;
								this.entity.worldObj.spawnParticle("reddust", ((Entity)o).posX, ((Entity)o).posY, ((Entity)o).posZ, f, f1, f2);
							}
							this.entity.attackEntityFrom(DamageSource.magic, currentFatigue / 25);
						}
						float f = (random.nextFloat() - 0.5F) * 0.2F;
						float f1 = (random.nextFloat() - 0.5F) * 0.2F;
						float f2 = (random.nextFloat() - 0.5F) * 0.2F;
						this.entity.worldObj.spawnParticle("reddust", this.entity.posX, this.entity.posY, this.entity.posZ, f, f1, f2);
					}else if (roll == 2){
						if (currentFatigue < 250){
							if (!this.entity.worldObj.isRemote)
								this.entity.addPotionEffect(new PotionEffect(Potion.wither.id, random.nextInt(60), 1));
						}
					}else if (roll == 3){
						if (currentFatigue < 500){
							if (!this.entity.worldObj.isRemote){
								this.entity.addPotionEffect(new PotionEffect(Potion.digSlowdown.id, random.nextInt(160), 1));
								this.entity.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, random.nextInt(160), 1));
							}
						}
					}
				}
				if (currentFatigue > 250){ // lvl 25+
					int roll = random.nextInt(3);
					if (roll == 0) { // red bolts
						this.lightningsLeft = random.nextInt(5) + 5;
					} else if (roll == 1){ // red glint on screen
						this.redGlint = 2000;
					}
					// roll 2 does nothing: lucky roll
				}
				if (currentFatigue > 950){ // lvl 95+
					FireDamage.causeUnblockableDamage(entity, entity.getMaxHealth()+10);
				}
				if (currentFatigue > 500){ // lvl 50+
					int roll = random.nextInt(3);
					if (roll == 0){
						if (!this.entity.worldObj.isRemote){
							this.entity.addPotionEffect(new PotionEffect(Potion.blindness.id, random.nextInt(120), 1));
							this.entity.addPotionEffect(new PotionEffect(Potion.wither.id, random.nextInt(60), 1));
							this.entity.addPotionEffect(new PotionEffect(Potion.digSlowdown.id, random.nextInt(160), 1));
							this.entity.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, random.nextInt(160), 1));
						}
					}else if (roll == 1){
						this.entity.worldObj.addWeatherEffect(new EntityLightningBolt(this.entity.worldObj, this.entity.posX, this.entity.posY, this.entity.posZ));
					}else if (roll == 2){
						for (int i = 0; i < 2; i++){
							double x = this.entity.posX + random.nextInt(10) - 5,
									y = this.entity.posY + random.nextInt(10) - 5,
									z = this.entity.posZ + random.nextInt(10) - 5;
							if (this.entity.worldObj.isAirBlock((int)x, (int)y, (int)z) && this.entity.worldObj.isAirBlock((int)x, (int)y - 1, (int)z) && this.entity.worldObj.isAirBlock((int)x, (int)y + 1, (int)z)){
								this.entity.setPosition(x, y, z);
								float f = (random.nextFloat() - 0.5F) * 0.2F;
								float f1 = (random.nextFloat() - 0.5F) * 0.2F;
								float f2 = (random.nextFloat() - 0.5F) * 0.2F;
								this.entity.worldObj.spawnParticle("portal", x, y, z, (double)f, (double)f1, (double)f2);
								break;
							}
						}
						currentFatigue = 0; // to prevent teleport chains
					}
				}
			}
		}
		this.currentFatigue = currentFatigue;
		this.setUpdateFlag(UPD_CURRENT_MANA_FATIGUE);
		this.handleExtendedPropertySync();
	}

	public void setMaxFatigue(float maxFatigue){
		this.maxFatigue = maxFatigue;
		this.setUpdateFlag(UPD_MAX_MANA_FATIGUE);
	}

	@Override
	public void setCurrentMana(float currentMana){
		if (currentMana < 0) currentMana = 0;
		if (currentMana > getMaxMana()) currentMana = getMaxMana();
		this.currentMana = currentMana;
		this.setUpdateFlag(UPD_CURRENT_MANA_FATIGUE);
	}

	public void setMagicLevel(int magicLevel){
		if (magicLevel < 0) magicLevel = 0;
		if (magicLevel > maxMagicLevel) magicLevel = maxMagicLevel;

		if (entity instanceof EntityPlayer)
			MinecraftForge.EVENT_BUS.post(new PlayerMagicLevelChangeEvent(entity, magicLevel));

		ticksForFullRegen = (int)Math.round(baseTicksForFullRegen * (0.75 - (0.25 * (getMagicLevel() / maxMagicLevel))));

		this.magicLevel = magicLevel;
		this.setUpdateFlag(UPD_MAGIC_LEVEL);
	}

	public void setFallProtection(int protection){
		this.fallProtection = protection;
	}

	public void setContingency(ContingencyTypes type, ItemStack effect){
		int num = 0;
		switch (type) {
			case DAMAGE_TAKEN:
				num = 0;
				break;
			case DEATH:
				num = 1;
				break;
			case FALL:
				num = 2;
				break;
			case ON_FIRE:
				num = 3;
				break;
			case HEALTH_LOW:
				num = 4;
				break;
		}
		this.contingencyStacks[num] = effect;
		this.setUpdateFlag(UPD_CONTINGENCY);
	}

	public void setFullSync(){
		this.ticksToSync = 0;
		this.setUpdateFlag(UPD_CONTINGENCY);
		this.setUpdateFlag(UPD_BITFLAG);
		this.setUpdateFlag(UPD_CURRENT_MANA_FATIGUE);
		this.setUpdateFlag(UPD_MAGIC_LEVEL);
		this.setUpdateFlag(UPD_MARK);
		this.setUpdateFlag(UPD_MAX_MANA_FATIGUE);
		this.setUpdateFlag(UPD_NUM_SUMMONS);
		this.setUpdateFlag(UPD_BETA_PARTICLES);
		this.hasDoneFullSync = true;
		this.forcingSync = true;
	}

	public void updateManaLink(EntityLivingBase entity){
		if (!entity.worldObj.isRemote){
			this.updateFlags |= UPD_MANALINK;
		}
		ManaLinkEntry mle = new ManaLinkEntry(entity.getEntityId(), 20);
		if (!this.manaLinks.contains(mle))
			this.manaLinks.add(mle);
		else
			this.manaLinks.remove(mle);
	}

	public float getBonusCurrentMana(){
		float bonus = 0;
		for (ManaLinkEntry entry : this.manaLinks){
			bonus += entry.getAdditionalCurrentMana(entity.worldObj, entity);
		}
		return bonus;
	}

	public float getBonusMaxMana(){
		float bonus = 0;
		for (ManaLinkEntry entry : this.manaLinks){
			bonus += entry.getAdditionalMaxMana(entity.worldObj, entity);
		}
		return bonus;
	}

	public void setIsFlipped(boolean flipped){
		if (flipped)
			this.bitFlag |= BIT_FLIPPED;
		else
			this.bitFlag &= ~BIT_FLIPPED;

		if (!entity.worldObj.isRemote)
			this.setUpdateFlag(UPD_BITFLAG);
	}

	public void setIsShrunk(boolean shrunk){
		if (shrunk)
			this.bitFlag |= BIT_SHRUNK;
		else
			this.bitFlag &= ~BIT_SHRUNK;

		if (!entity.worldObj.isRemote){
			this.setUpdateFlag(UPD_BITFLAG);
			this.forceSync();
		}
	}

	public void setDisableGravity(boolean disabled){
		this.disableGravity = disabled;
		if (!this.entity.worldObj.isRemote){
			this.setUpdateFlag(UPD_DISABLE_GRAVITY);
			this.forceSync();
		}
	}

	public void setOriginalSize(AMVector2 size){
		this.originalSize = size;
	}

	public void setHealCooldown(int length){
		this.healCooldown = length;
	}
	//=======================================================================================
	// Private Setters
	//=======================================================================================

	private void setMarkSet(boolean markSet){
		byte curValue = this.bitFlag;
		if (markSet){
			curValue |= BIT_MARK_SET;
		}else{
			curValue &= ~BIT_MARK_SET;
		}
		this.bitFlag = curValue;
		this.setUpdateFlag(UPD_BITFLAG);
	}

	private void setMarkDimension(int markDimension){
		this.markDimension = markDimension;
	}

	private void setMarkX(double markX){
		this.markX = markX;
	}

	private void setNumSummons(int numSummons){
		if (this.entity == null || this.entity.worldObj == null){
			return;
		}
		this.numSummons = numSummons;
	}

	public void setUpdateFlag(int flag){
		this.updateFlags |= flag;
	}

	private void clearUpdateFlag(int flag){
		this.updateFlags &= ~flag;
	}

	//=======================================================================================
	// Utility Methods
	//=======================================================================================

	public void setEntityReference(EntityLivingBase entity){
		this.entity = entity;
		setOriginalSize(new AMVector2(entity.width, entity.height));
		hasInitialized = true;
		isCritical = entity instanceof EntityPlayerMP;
		yOffsetOrig = entity.yOffset;

		if (isCritical)
			ticksToRegen = 5;
		else
			ticksToRegen = 20;

		if (isCritical){
			if (armorProcCooldowns[3] > 0){
				AMCore.instance.proxy.blackoutArmorPiece((EntityPlayerMP)entity, 3, armorProcCooldowns[3]);
			}
			if (armorProcCooldowns[1] > 0){
				AMCore.instance.proxy.blackoutArmorPiece((EntityPlayerMP)entity, 1, armorProcCooldowns[1]);
			}
			if (armorProcCooldowns[2] > 0){
				AMCore.instance.proxy.blackoutArmorPiece((EntityPlayerMP)entity, 2, armorProcCooldowns[2]);
			}
			if (armorProcCooldowns[0] > 0){
				AMCore.instance.proxy.blackoutArmorPiece((EntityPlayerMP)entity, 0, armorProcCooldowns[0]);
			}
		}

		if (entity.worldObj != null && entity.worldObj.isRemote && entity instanceof EntityPlayer && AMCore.proxy.playerTracker.hasAA((EntityPlayer)entity)){
			EntityLivingBase localPlayer = AMCore.instance.proxy.getLocalPlayer();
			if (entity != localPlayer)
				AMNetHandler.INSTANCE.requestAuras((EntityPlayer)entity);
		}

	}

	public void handleSpecialSyncData(){
		if (needsArmorTickCounterSync && entity instanceof EntityPlayerMP){
			needsArmorTickCounterSync = false;
			if (armorProcCooldowns[3] > 0){
				AMCore.instance.proxy.blackoutArmorPiece((EntityPlayerMP)entity, 3, armorProcCooldowns[3]);
			}
			if (armorProcCooldowns[1] > 0){
				AMCore.instance.proxy.blackoutArmorPiece((EntityPlayerMP)entity, 1, armorProcCooldowns[1]);
			}
			if (armorProcCooldowns[2] > 0){
				AMCore.instance.proxy.blackoutArmorPiece((EntityPlayerMP)entity, 2, armorProcCooldowns[2]);
			}
			if (armorProcCooldowns[0] > 0){
				AMCore.instance.proxy.blackoutArmorPiece((EntityPlayerMP)entity, 0, armorProcCooldowns[0]);
			}
		}
	}

	private void verifySummons(){
		for (int i = 0; i < summon_ent_ids.size(); ++i){
			int id = summon_ent_ids.get(i);
			Entity e = entity.worldObj.getEntityByID(id);
			if (e == null || !(e instanceof EntityLivingBase)){
				summon_ent_ids.remove(i);
				i--;
				removeSummon();
			} else if (e.getDistanceToEntity(entity) > 200) {
				e.setDead();
				summon_ent_ids.remove(i);
				i--;
				removeSummon();
			}
		}
	}

	public boolean addSummon(EntityLivingBase entity){
		if (!entity.worldObj.isRemote){
			summon_ent_ids.add(entity.getEntityId());
			setNumSummons(getNumSummons() + 1);
			setUpdateFlag(UPD_NUM_SUMMONS);
		}
		return true;
	}

	public boolean removeSummon(){
		if (getNumSummons() == 0){
			return false;
		}
		if (!entity.worldObj.isRemote){
			setNumSummons(getNumSummons() - 1);
			setUpdateFlag(UPD_NUM_SUMMONS);
		}
		return true;
	}

	public void setSyncAuras(){
		if (entity instanceof EntityPlayer && AMCore.proxy.playerTracker.hasAA((EntityPlayer)entity))
			this.setUpdateFlag(UPD_BETA_PARTICLES);
	}

	public boolean handleDataPacket(byte[] data){
		AMDataReader rdr = new AMDataReader(data, false);
		int entID = rdr.getInt();

		if (entID != this.entity.getEntityId()){
			return false;
		}
		int flags = rdr.getInt();

		if ((flags & UPD_BITFLAG) == UPD_BITFLAG){
			this.bitFlag = rdr.getByte();
		}
		if ((flags & UPD_CURRENT_MANA_FATIGUE) == UPD_CURRENT_MANA_FATIGUE){
			this.currentMana = rdr.getFloat();
			this.currentFatigue = rdr.getFloat();
			this.guardian1 = rdr.getBoolean();
			this.guardian2 = rdr.getBoolean();
			this.guardian3 = rdr.getBoolean();
			this.guardian4 = rdr.getBoolean();
			this.guardian5 = rdr.getBoolean();
			this.guardian6 = rdr.getBoolean();
			this.guardian7 = rdr.getBoolean();
			this.guardian8 = rdr.getBoolean();
			this.guardian9 = rdr.getBoolean();
			this.guardian10 = rdr.getBoolean();
			this.hasRitual = rdr.getBoolean();
		}
		if ((flags & UPD_MAGIC_LEVEL) == UPD_MAGIC_LEVEL){
			this.magicLevel = rdr.getInt();
			float newMagicXP = rdr.getFloat();
			if (entity.worldObj.isRemote && newMagicXP != magicXP){
				AMGuiHelper.instance.showMagicXPBar();
			}
			this.magicXP = newMagicXP;
		}
		if ((flags & UPD_MARK) == UPD_MARK){
			this.markX = rdr.getDouble();
			this.markY = rdr.getDouble();
			this.markZ = rdr.getDouble();
			this.markDimension = rdr.getInt();
			this.setMarkSet(rdr.getBoolean());
		}
		if ((flags & UPD_MAX_MANA_FATIGUE) == UPD_MAX_MANA_FATIGUE){
			this.maxMana = rdr.getFloat();
			this.maxFatigue = rdr.getFloat();
		}
		if ((flags & UPD_NUM_SUMMONS) == UPD_NUM_SUMMONS){
			this.numSummons = rdr.getInt();
		}
		if ((flags & UPD_BETA_PARTICLES) == UPD_BETA_PARTICLES && entity instanceof EntityPlayer && AMCore.proxy.playerTracker.hasAA((EntityPlayer)entity)){
			this.AuraIndex = rdr.getInt();
			this.AuraBehaviour = rdr.getInt();
			this.AuraScale = rdr.getFloat();
			this.AuraAlpha = rdr.getFloat();
			this.AuraColorRandomize = rdr.getBoolean();
			this.AuraColorDefault = rdr.getBoolean();
			this.AuraColor = rdr.getInt();
			this.AuraDelay = rdr.getInt();
			this.AuraQuantity = rdr.getInt();
			this.AuraSpeed = rdr.getFloat();
		}
		if ((flags & UPD_CONTINGENCY) == UPD_CONTINGENCY){
			for (int i = 0; i < 5; i++){
				ItemStack stack = rdr.getItemStack();
				if (!(stack.getItem() instanceof ItemSnowball)){
					this.contingencyStacks[i] = stack;
				}
			}
		}
		if ((flags & UPD_MANALINK) == UPD_MANALINK){
			this.manaLinks.clear();
			int numLinks = rdr.getInt();
			for (int i = 0; i < numLinks; ++i){
				Entity e = entity.worldObj.getEntityByID(rdr.getInt());
				if (e != null && e instanceof EntityLivingBase)
					updateManaLink((EntityLivingBase)e);
			}
		}
		if ((flags & UPD_DISABLE_GRAVITY) == UPD_DISABLE_GRAVITY){
			this.disableGravity = rdr.getBoolean();
		}

		extra_properties.clear();
		NBTTagCompound nbt = rdr.getNBTTagCompound();
		for (int j = 0; j < nbt.getInteger("persistentobjsize"); j++) {
			extra_properties.put(nbt.getString("persistentobjname" + j), nbt.getString("persistentobj" + j));
		}

		return true;
	}

	public void setDelayedSync(int delay){
		setFullSync();
		this.ticksToSync = delay;
	}

	public void forceSync(){
		this.ticksToSync = 0;
		this.forcingSync = true;
	}

	public void updateAuraData(int index, int behaviour, float scale, float alpha, boolean colorRandom, boolean colorDefault, int color, int delay, int quantity, float speed){
		this.AuraIndex = index;
		this.AuraBehaviour = behaviour;
		this.AuraScale = scale;
		this.AuraAlpha = alpha;
		this.AuraColorRandomize = colorRandom;
		this.AuraColorDefault = colorDefault;
		this.AuraColor = color;
		this.AuraDelay = delay;
		this.AuraQuantity = quantity;
		this.AuraSpeed = speed;

		this.setUpdateFlag(UPD_BETA_PARTICLES);
	}

	public byte[] getAuraData(){
		AMDataWriter writer = new AMDataWriter();
		writer.add(this.AuraIndex);
		writer.add(this.AuraBehaviour);
		writer.add(this.AuraScale);
		writer.add(this.AuraAlpha);
		writer.add(this.AuraColorRandomize);
		writer.add(this.AuraColorDefault);
		writer.add(this.AuraColor);
		writer.add(this.AuraDelay);
		writer.add(this.AuraQuantity);
		writer.add(this.AuraSpeed);

		return writer.generate();
	}

	public void readAuraData(byte[] data){
		AMDataReader rdr = new AMDataReader(data, false);
		this.AuraIndex = rdr.getInt();
		this.AuraBehaviour = rdr.getInt();
		this.AuraScale = rdr.getFloat();
		this.AuraAlpha = rdr.getFloat();
		this.AuraColorRandomize = rdr.getBoolean();
		this.AuraColorDefault = rdr.getBoolean();
		this.AuraColor = rdr.getInt();
		this.AuraDelay = rdr.getInt();
		this.AuraQuantity = rdr.getInt();
		this.AuraSpeed = rdr.getFloat();
	}

	public void handleWaterMovement(){
		if (this.entity.isPotionActive(BuffList.swiftSwim.id)){
			this.entity.motionX *= 0.96;
			this.entity.motionY *= 0.96;
			this.entity.motionZ *= 0.96;
		}
	}

	public boolean detectPossibleDesync(){
		return false;
	}

	@Override
	public void saveNBTData(NBTTagCompound compound){
		compound.setFloat("curMana", getCurrentMana());
		compound.setFloat("curFatigue", getCurrentFatigue());
		compound.setShort("magicLevel", (short)getMagicLevel());
		compound.setBoolean("hasUnlockedAugmented", getHasUnlockedAugmented());

		compound.setBoolean("guardian1", guardian1);
		compound.setBoolean("guardian2", guardian2);
		compound.setBoolean("guardian3", guardian3);
		compound.setBoolean("guardian4", guardian4);
		compound.setBoolean("guardian5", guardian5);
		compound.setBoolean("guardian6", guardian6);
		compound.setBoolean("guardian7", guardian7);
		compound.setBoolean("guardian8", guardian8);
		compound.setBoolean("guardian9", guardian9);
		compound.setBoolean("guardian10", guardian10);

		compound.setBoolean("hasRitual", hasRitual);

		compound.setIntArray("armorCooldowns", armorProcCooldowns);
		//compound.setBoolean("isFlipped", this.getIsFlipped());
		compound.setBoolean("isShrunk", this.getIsShrunk());

		compound.setBoolean("isCritical", isCritical);

		compound.setFloat("magicXP", magicXP);

		for (int i = 0; i < 5; i++) {
			NBTTagCompound effectSave = contingencyStacks[i].writeToNBT(new NBTTagCompound());
			compound.setTag("contingency_effect" + i, effectSave);
		}

		//mark location
		if (getMarkSet()){
			compound.setDouble("marklocationx", this.getMarkX());
			compound.setDouble("marklocationy", this.getMarkY());
			compound.setDouble("marklocationz", this.getMarkZ());
			compound.setInteger("markdimension", this.getMarkDimension());
		}

		int c = 0;
		for (Object o : extra_properties.keySet()) {
			String iS = (String)o;
			String iValue = extra_properties.get(iS);
			compound.setString("persistentobj" + c, iValue);
			compound.setString("persistentobjname" + c, iS);
			c++;
		}
		compound.setInteger("persistentobjsize", extra_properties.size());

		for (Object o : compendium_entries.keySet()) {
			String iS = (String)o;
			String iValue = compendium_entries.get(iS);
			compound.setString("compentry" + c, iValue);
			compound.setString("compentryname" + c, iS);
			c++;
		}
		compound.setInteger("compendiumsize", compendium_entries.size());
	}

	@Override
	public void loadNBTData(NBTTagCompound compound){
		setMagicLevelWithMana(compound.getShort("magicLevel"));
		setCurrentMana(compound.getFloat("curMana"));
		setCurrentFatigue(compound.getFloat("curFatigue"));

		armorProcCooldowns = compound.getIntArray("armorCooldowns");
		if (armorProcCooldowns == null)
			armorProcCooldowns = new int[4];
		else if (armorProcCooldowns.length != 4){
			int[] tmp = armorProcCooldowns;
			armorProcCooldowns = new int[4];
			for (int i = 0; i < Math.min(tmp.length, 4); ++i){
				armorProcCooldowns[i] = tmp[i];
			}
		}

		//setIsFlipped(compound.getBoolean("isFlipped"));
		setIsShrunk(compound.getBoolean("isShrunk"));
		//flipRotation = getIsFlipped() ? 0 : 180;

		isCritical = compound.getBoolean("isCritical");

		magicXP = compound.getFloat("magicXP");

		guardian1 = compound.getBoolean("guardian1");
		guardian2 = compound.getBoolean("guardian2");
		guardian3 = compound.getBoolean("guardian3");
		guardian4 = compound.getBoolean("guardian4");
		guardian5 = compound.getBoolean("guardian5");
		guardian6 = compound.getBoolean("guardian6");
		guardian7 = compound.getBoolean("guardian7");
		guardian8 = compound.getBoolean("guardian8");
		guardian9 = compound.getBoolean("guardian9");
		guardian10 = compound.getBoolean("guardian10");

		hasRitual = compound.getBoolean("hasRitual");

		for (int i = 0; i < 4; ++i){
			if (armorProcCooldowns[i] > 0){
				needsArmorTickCounterSync = true;
				break;
			}
		}

		if (compound.hasKey("marklocationx")){
			setMarkX(compound.getDouble("marklocationx"));
			setMarkY(compound.getDouble("marklocationy"));
			setMarkZ(compound.getDouble("marklocationz"));
			setMarkDimension(compound.getInteger("markdimension"));
			this.setMarkSet(true);
		}

		for (int i = 0; i < 5; i++) {
			try{
				this.contingencyStacks[i] = ItemStack.loadItemStackFromNBT((NBTTagCompound)compound.getTag("contingency_effect" + i));
			} catch (NullPointerException e) {
				// prevent console spam that doesn't mean anything, I don't mind that it can be null
			}
		}

		for (int j = 0; j < compound.getInteger("persistentobjsize"); j++) {
			extra_properties.put(compound.getString("persistentobjname" + j), compound.getString("persistentobj" + j));
		}

		for (int j = 0; j < compound.getInteger("compendiumsize"); j++) {
			compendium_entries.put(compound.getString("compentryname" + j), compound.getString("compentry" + j));
		}
	}

	public void addToExtraVariables(String name, String value) {
		extra_properties.put(name, value);
		setFullSync();
	}

	public void setCompendiumEntry(String name, String value) {
		compendium_entries.put(name, value);
		syncCompendiumEntries(name, value);
	}

	public void requestEntriesUpdateFromServer() {
		AMDataWriter writer = new AMDataWriter();
		AMNetHandler.INSTANCE.sendPacketToServer(AMPacketIDs.SYNCCOMPENDIUMREQUEST, writer.generate());
	}

	private void syncCompendiumEntries(String name, String value) { // this is separate from the rest of syncing to ease load on network
		AMDataWriter writer = new AMDataWriter();
		NBTTagCompound compendium_data = new NBTTagCompound();
		if (name == null && value == null) {
			int c = 0;
			for (Object o : compendium_entries.keySet()) {
				String iS = (String) o;
				String iValue = compendium_entries.get(iS);
				compendium_data.setString("compentry" + c, iValue);
				compendium_data.setString("compentryname" + c, iS);
				c++;
			}
			compendium_data.setInteger("compendiumsize", compendium_entries.size());
			writer.add(compendium_data);
			AMNetHandler.INSTANCE.sendPacketToServer(AMPacketIDs.SYNCCOMPENDIUM, writer.generate());
		} else {
			compendium_data.setString("compentry" + 0, value);
			compendium_data.setString("compentryname" + 0, name);
			compendium_data.setInteger("compendiumsize", 1);
			writer.add(compendium_data);
			AMNetHandler.INSTANCE.sendPacketToServer(AMPacketIDs.SYNCCOMPENDIUM, writer.generate());
		}
	}

	@SideOnly(Side.CLIENT)
	public void onSyncCompendiumDataPacket(byte[] remaining) {
		onSyncCompendiumDataPacketServer(remaining);

		Iterator it = this.getCompendiumIterator(); // update the clientside compendium list
		while (it.hasNext()) {
			Map.Entry<String, String> pair = (Map.Entry<String, String>) it.next();
			CompendiumEntry entry = ArcaneCompendium.instance.getEntryAbsolute(pair.getKey());
			if (entry == null) continue;
			entry.isLocked = pair.getValue().contains("L");
			entry.isNew = pair.getValue().contains("N");
		}
	}

	public void onSyncCompendiumDataPacketServer(byte[] remaining) {
		AMDataReader rdr = new AMDataReader(remaining, false);
		NBTTagCompound nbt = rdr.getNBTTagCompound();
		if (nbt.getInteger("compendiumsize") == 0) { // if it's a new, previously-not-loaded world
			setDefaultCompendiumEntryValues();
		}
		if (nbt.getInteger("compendiumsize") > 1) compendium_entries.clear(); // full sync
		for (int j = 0; j < nbt.getInteger("compendiumsize"); j++) {
			compendium_entries.put(nbt.getString("compentryname" + j), nbt.getString("compentry" + j));
		}
	}

	private void setDefaultCompendiumEntryValues() {
		// set them for client
		ArcaneCompendium.instance.init(Minecraft.getMinecraft().getLanguageManager().getCurrentLanguage());
		// sync all the defaults to server.
		ArcaneCompendium.instance.saveUnlockData();
	}

	public void setCompendiumEntryNoSync(String name, String value) {
		compendium_entries.put(name, value);
	}

	public void removeFromExtraVariables(String name) {
		extra_properties.remove(name);
		setFullSync();
	}

	public void clearExtraVariables() {
		extra_properties.clear();
		setFullSync();
	}

	public Map<String, String> getExtraVariablesContains(String contains) {
		Map<String, String> toReturn = new HashMap<>();
		for (String key : extra_properties.keySet()) {
			if (key.contains(contains)) toReturn.put(key, extra_properties.get(key));
		}
		return toReturn;
	}

	public String getExtraVariable(String name) {
		return extra_properties.get(name);
	}

	public boolean hasExtraVariable(String name) {
		return extra_properties.get(name) != null;
	}

	public Map<String, String> getAllCompendiumEntries() {
		return compendium_entries;
	}

	public boolean hasCompendiumEntry(String name) {
		return compendium_entries.get(name) != null;
	}

	public boolean isEntryLocked(String name) {
		return compendium_entries.get(name).contains("L");
	}

	public boolean isEntryNew(String name) {
		return compendium_entries.get(name).contains("N");
	}

	public Iterator<Map.Entry<String, String>> getCompendiumIterator() {
		return compendium_entries.entrySet().iterator();
	}


	@Override
	public void init(Entity entity, World world){
		if (world == null || entity == null || !(entity instanceof EntityLivingBase)) return;

		setEntityReference((EntityLivingBase)entity);

		if (entity instanceof EntityPlayer){
			maxMana = 0;
			currentMana = 0;
			magicLevel = 0;
			maxFatigue = 1;
			currentFatigue = 0;
		}else{
			maxMana = 100;
			currentMana = 100;
			magicLevel = 1;
			maxFatigue = 1;
			currentFatigue = 0;
		}
		numSummons = 0;
		armorProcCooldowns = new int[4];

		markX = 0;
		markY = 0;
		markZ = 0;
		markDimension = -512;

		updateFlags = 0;

		ticksToSync = world.rand.nextInt(syncTickDelay);

		hasInitialized = true;
	}

	public void deductMana(float manaCost){
		float leftOver = manaCost - currentMana;
		this.setCurrentMana(currentMana - manaCost);
		if (leftOver > 0){
			if (this.entity instanceof EntityPlayer){
				EntityPlayer casterPlayer = (EntityPlayer) this.entity;
				for (int i = 0; i < casterPlayer.inventory.mainInventory.length; i++) {
					if (casterPlayer.inventory.mainInventory[i] != null){
						if (casterPlayer.inventory.mainInventory[i].getItem() instanceof ItemManaStone){
							int availablemana = ItemManaStone.getManaInStone(casterPlayer.inventory.mainInventory[i]);
							float amt = Math.min(availablemana, leftOver);
							if (amt > 0){
								ItemManaStone.deductManaFromStone(casterPlayer.inventory.mainInventory[i], (int)amt);
								leftOver -= amt;
								if (leftOver <= 0)
									break;
							}
						} else if (casterPlayer.inventory.mainInventory[i].getItem() instanceof ItemSoulspike){
							int availablemana = ItemSoulspike.getManaInSpike(casterPlayer.inventory.mainInventory[i]);
							float amt = Math.min(availablemana, leftOver);
							if (amt > 0){
								ItemSoulspike.deductManaFromSpike(casterPlayer.inventory.mainInventory[i], (int)amt);
								leftOver -= amt;
								if (leftOver <= 0)
									break;
							}
						}
					}
				}
			}
		}
		if (leftOver > 0){
			for (ManaLinkEntry entry : this.manaLinks){
				leftOver -= entry.deductMana(entity.worldObj, entity, leftOver);
				if (leftOver <= 0)
					break;
			}
		}
	}

	public void toggleFlipped(){
		if (entity.worldObj.isRemote){
			AMNetHandler.INSTANCE.sendExPropCommandToServer(this.BIT_FLIPPED);
		}
		if (this.getIsFlipped())
			this.setIsFlipped(false);
		else
			this.setIsFlipped(true);
	}

	public void spawnManaLinkParticles(){
		if (entity.worldObj != null && entity.worldObj.isRemote){
			for (ManaLinkEntry entry : this.manaLinks){
				Entity e = entity.worldObj.getEntityByID(entry.entityID);
				if (e != null && e.getDistanceSqToEntity(entity) < entry.range && e.ticksExisted % 90 == 0){
					AMLineArc arc = (AMLineArc)AMCore.proxy.particleManager.spawn(entity.worldObj, "textures/blocks/oreblockbluetopaz.png", e, entity);
					if (arc != null){
						arc.setIgnoreAge(false);
						arc.setRBGColorF(0.17f, 0.88f, 0.88f);
					}
				}
			}
		}
	}

	public void addBurnout(float burnout){
		if (entity.isPotionActive(BuffList.burnoutReduction))
			burnout *= 0.75f;
		this.setCurrentFatigue(currentFatigue + burnout);
	}
	
	@Override
	public String toString() {
		try {
			return hashCode() + " " + entity;
		} catch(Exception exception) {
			return hashCode() + " (error)";
		}
	}
	
	public void handleExtendedPropertySync(){
		if (!entity.worldObj.isRemote && !this.getHasDoneFullSync()) {
			this.setFullSync();
		}

		if (!entity.worldObj.isRemote && this.getHasUpdate()){
			byte[] data = this.getUpdateData();
			AMNetHandler.INSTANCE.sendPacketToAllClientsNear(entity.dimension, entity.posX, entity.posY, entity.posZ, 32, AMPacketIDs.SYNC_EXTENDED_PROPS, data);
		}

		if (entity.worldObj.isRemote){
			if (this.detectPossibleDesync()){
				AMNetHandler.INSTANCE.sendPacketToServer(AMPacketIDs.POSSIBLE_CLIENT_EXPROP_DESYNC, new AMDataWriter().add(entity.getEntityId()).generate());
			}
		}
	}

	public void syncTKDistance(){
		AMDataWriter writer = new AMDataWriter();
		writer.add(this.TK_Distance);
		AMNetHandler.INSTANCE.sendPacketToServer(AMPacketIDs.TK_DISTANCE_SYNC, writer.generate());
	}

	public void addMagicXP(float amt){
		if (magicLevel == maxMagicLevel || !(this.entity instanceof EntityPlayer)) return;

		this.magicXP += amt;
		float xpToLevel = getXPToNextLevel();
		if (magicXP >= xpToLevel){
			magicXP = 0;

			setMagicLevelWithMana(magicLevel + 1);

			if (this.entity instanceof EntityPlayer && magicLevel % 2 == 0){
				EntityPlayer ent = (EntityPlayer)this.entity;
				if (magicLevel <= 20)
					SkillData.For(ent).incrementSpellPoints(SkillPointTypes.BLUE);
				else if (magicLevel <= 40)
					SkillData.For(ent).incrementSpellPoints(SkillPointTypes.GREEN);
				else if (magicLevel <= 50)
					SkillData.For(ent).incrementSpellPoints(SkillPointTypes.RED);

			}
			this.entity.worldObj.playSoundAtEntity(entity, "arsmagica2:misc.event.magic_level_up", 1, 1);
		}
		setUpdateFlag(UPD_MAGIC_LEVEL);
		forceSync();
	}

	public void procContingency(int num, EntityLivingBase attacker){
		ItemStack spell = contingencyStacks[num].copy();
		this.setContingency(getContingencyType(num), new ItemStack(Items.snowball));
		SpellHelper.instance.applyStackStage(spell, entity, attacker == null ? entity : attacker, entity.posX, entity.posY, entity.posZ, 0, entity.worldObj, false, false, 0);
		AMNetHandler.INSTANCE.sendSpellApplyEffectToAllAround(entity, attacker == null ? entity : attacker, entity.posX, entity.posY, entity.posZ, entity.worldObj, spell);
		this.forceSync();
	}

	public void manaBurnoutTick(){
		ticksSinceLastRegen++;
		healCooldown--;

		if (disableGravity){
			this.entity.motionY = 0;
		}

		if (ticksLeft > 0){
			ticksLeft--;
		}
		if (this.lightningsLeft > 0 && ticksLeft <= 0) {
			lightningsLeft--;
			ticksLeft = 15;
			Random random = new Random();
			this.entity.attackEntityFrom(DamageSource.magic, 0.5f);
			for (int i = 0; i < 10; i++) AMCore.proxy.particleManager.BoltFromPointToPoint(this.entity.worldObj, this.entity.posX + random.nextInt(14) - 7, this.entity.posY + random.nextInt(6) - 3, this.entity.posZ + random.nextInt(14) - 7, this.entity.posX, this.entity.posY, this.entity.posZ, 1, 0xE40000);
		}
		
		if (ticksSinceLastRegen >= ticksToRegen){
			//mana regeneration
			float actualMaxMana = getMaxMana();
			if (getCurrentMana() < actualMaxMana){
				if (entity instanceof EntityPlayer && ((EntityPlayer)entity).capabilities.isCreativeMode){
					setCurrentMana(actualMaxMana);
				}else{
					if (getCurrentMana() < 0){
						setCurrentMana(0);
					}

					int regenTicks = (int)Math.ceil(ticksForFullRegen * entity.getAttributeMap().getAttributeInstance(ArsMagicaApi.manaRegenTimeModifier).getAttributeValue());

					//mana regen buff
					if (entity.isPotionActive(BuffList.manaRegen.id)){
						PotionEffect pe = entity.getActivePotionEffect(BuffList.manaRegen);
						regenTicks *= (1.0f - Math.max(0.9f, (0.25 * (pe.getAmplifier() + 1))));
					}

					//mana scepter handling - 10% boost to mana regen
					if (entity instanceof EntityPlayer){
						EntityPlayer player = (EntityPlayer)entity;
						int armorSet = ArmorHelper.getFullArsMagicaArmorSet(player);
						if (armorSet == ArsMagicaArmorMaterial.MAGE.getMaterialID()){
							regenTicks *= 0.8;
						}else if (armorSet == ArsMagicaArmorMaterial.BATTLEMAGE.getMaterialID()){
							regenTicks *= 0.95;
						}else if (armorSet == ArsMagicaArmorMaterial.ARCHMAGE.getMaterialID()){
							regenTicks *= 0.5;
						}

						if (SkillData.For(player).isEntryKnown(SkillTreeManager.instance.getSkillTreeEntry(SkillManager.instance.getSkill("ManaRegenIII")))){
							regenTicks *= 0.7f;
						}else if (SkillData.For(player).isEntryKnown(SkillTreeManager.instance.getSkillTreeEntry(SkillManager.instance.getSkill("ManaRegenII")))){
							regenTicks *= 0.85f;
						}else if (SkillData.For(player).isEntryKnown(SkillTreeManager.instance.getSkillTreeEntry(SkillManager.instance.getSkill("ManaRegenI")))){
							regenTicks *= 0.95f;
						}

						//armor infusions
						int numArmorPieces = 0;
						for (int i = 0; i < 4; ++i){
							ItemStack stack = player.inventory.armorItemInSlot(i);
							if (ImbuementRegistry.instance.isImbuementPresent(stack, GenericImbuement.manaRegen))
								numArmorPieces++;
						}
						regenTicks *= 1.0f - (0.15f * numArmorPieces);
					}

					//actual mana regen
					float manaToAdd = (actualMaxMana / regenTicks) * ticksSinceLastRegen;

					setCurrentMana(getCurrentMana() + manaToAdd);
				}
			}
			//fatigue decrease
			if (getCurrentFatigue() > 0){
				int numArmorPieces = 0;
				if (entity instanceof EntityPlayer){
					EntityPlayer player = (EntityPlayer)entity;
					for (int i = 0; i < 4; ++i){
						ItemStack stack = player.inventory.armorItemInSlot(i);
						if (ImbuementRegistry.instance.isImbuementPresent(stack, GenericImbuement.burnoutReduction))
							numArmorPieces++;
					}
				}
				float factor = (float)((0.01f + (0.015f * numArmorPieces)) * entity.getAttributeMap().getAttributeInstance(ArsMagicaApi.burnoutReductionRate).getAttributeValue());
				float decreaseamt = (factor * getMagicLevel()) * ticksSinceLastRegen;
				//actual fatigue decrease
				setCurrentFatigue(getCurrentFatigue() - decreaseamt);
				if (getCurrentFatigue() < 0){
					setCurrentFatigue(0);
				}
			}

			ticksSinceLastRegen = 0;
		}
	}

	public void flipTick(){
		boolean flipped = getIsFlipped();

		if (entity instanceof EntityPlayer){
			ItemStack boots = ((EntityPlayer)entity).inventory.armorInventory[0];
			if (boots == null || boots.getItem() != ItemsCommonProxy.enderBoots)
				setIsFlipped(false);
		}

		prevFlipRotation = flipRotation;
		if (flipped && flipRotation < 180)
			flipRotation += 15;
		else if (!flipped && flipRotation > 0)
			flipRotation -= 15;
	}

	public void cleanupManaLinks(){
		Iterator<ManaLinkEntry> it = this.manaLinks.iterator();
		while (it.hasNext()){
			ManaLinkEntry entry = it.next();
			Entity e = this.entity.worldObj.getEntityByID(entry.entityID);
			if (e == null)
				it.remove();
		}
	}

	public boolean isManaLinkedTo(EntityLivingBase entity){
		for (ManaLinkEntry entry : manaLinks){
			if (entry.entityID == entity.getEntityId())
				return true;
		}
		return false;
	}

	private class ManaLinkEntry{
		private final int entityID;
		private final int range;

		public ManaLinkEntry(int entityID, int range){
			this.entityID = entityID;
			this.range = range * range;
		}

		private EntityLivingBase getEntity(World world){
			Entity e = world.getEntityByID(entityID);
			if (e == null || !(e instanceof EntityLivingBase))
				return null;
			return (EntityLivingBase)e;
		}

		public float getAdditionalCurrentMana(World world, EntityLivingBase host){
			EntityLivingBase e = getEntity(world);
			if (e == null || e.getDistanceSqToEntity(host) > range)
				return 0;
			return For(e).getCurrentMana();
		}

		public float getAdditionalMaxMana(World world, EntityLivingBase host){
			EntityLivingBase e = getEntity(world);
			if (e == null || e.getDistanceSqToEntity(host) > range)
				return 0;
			return For(e).getMaxMana();
		}

		public float deductMana(World world, EntityLivingBase host, float amt){
			EntityLivingBase e = getEntity(world);
			if (e == null || e.getDistanceSqToEntity(host) > range)
				return 0;
			amt = Math.min(For(e).getCurrentMana(), amt);
			For(e).deductMana(amt);
			if (!world.isRemote)
				For(e).forceSync();
			return amt;
		}

		@Override
		public int hashCode(){
			return entityID;
		}

		@Override
		public boolean equals(Object obj){
			if (obj instanceof ManaLinkEntry)
				return ((ManaLinkEntry)obj).entityID == this.entityID;
			return false;
		}
	}

	public void performRemoteOp(int mask){
		if (entity.worldObj.isRemote)
			return;
		switch (mask){
		case BIT_FLIPPED:
			toggleFlipped();
			forceSync();
			break;
		}
	}
}

