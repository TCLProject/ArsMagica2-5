package am2.bosses;

import am2.AMCore;
import am2.blocks.BlocksCommonProxy;
import am2.buffs.BuffList;
import am2.entities.EntityLightMage;
import am2.entities.SpawnBlacklists;
import am2.items.ItemsCommonProxy;
import am2.playerextensions.ExtendedProperties;
import am2.spell.SpellSoundHelper;
import am2.spell.components.Dig;
import am2.worldgen.dynamic.DynamicBossWorldHelper;
import am2.worldgen.dynamic.DynamicBossWorldProvider;
import am2.worldgen.dynamic.EmptyTeleporter;
import cpw.mods.fml.relauncher.ReflectionHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDynamicLiquid;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockStaticLiquid;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IEntityMultiPart;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.boss.EntityDragonPart;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

import java.util.ArrayList;
import java.util.List;

public abstract class AM2Boss extends EntityMob implements IArsMagicaBoss, IEntityMultiPart{

	protected BossActions currentAction = BossActions.IDLE;
	protected int ticksInCurrentAction;
	protected int timer;
	protected EntityDragonPart[] parts;

	public boolean playerCanSee = false;
	;

	public AM2Boss(World par1World){
		super(par1World);
		this.stepHeight = 1.02f;
		ExtendedProperties.For(this).setMagicLevelWithMana(50);
		initAI();
		disallowedBlocks = new ArrayList<Block>();
		disallowedBlocks.add(Blocks.bedrock);
		disallowedBlocks.add(Blocks.command_block);
		disallowedBlocks.add(BlocksCommonProxy.everstone);
		disallowedBlocks.add(Blocks.water);
		disallowedBlocks.add(Blocks.lava);
		disallowedBlocks.add(Blocks.flowing_water);
		disallowedBlocks.add(Blocks.flowing_lava);

		for (String i : AMCore.config.getDigBlacklist()){
			if (i == null || i == "") continue;
			disallowedBlocks.add(Block.getBlockFromName(i.replace("tile.", "")));
		}
	}

	public void onDeath(DamageSource p_70645_1_)
	{
		super.onDeath(p_70645_1_);
		if (!this.worldObj.isRemote) {
			List playerEntities = new ArrayList<>();
			playerEntities.addAll(worldObj.playerEntities);
			for (Object objectPlayer : playerEntities) {
				EntityPlayerMP entityplayer1 = (EntityPlayerMP) objectPlayer;
				DynamicBossWorldHelper.returnPlayerToOriginalPosition(entityplayer1);
			}
			DynamicBossWorldHelper.unregisterDimension(this.worldObj.provider.dimensionId, this.worldObj);
		}
		SpellSoundHelper.currentlyPlayingMusic.fadeOut();
	}

	//Bosses should be able to follow players through doors and hallways, so setSize is overridden to instead add a
	//damageable entity based bounding box of the specified size, unless a boss already uses parts.
	@Override
	public void setSize(float width, float height){
		if (parts == null){
			parts = new EntityDragonPart[]{new EntityDragonPart(this, "defaultBody", width, height){
				@Override
				public void onUpdate(){
					super.onUpdate();
					this.isDead = ((Entity)entityDragonObj).isDead;
				}

				@Override
				public boolean shouldRenderInPass(int pass){
					return false;
				}
			}};
		}else{
			super.setSize(width, height);
		}
	}

	@Override
	protected boolean isAIEnabled(){
		return true;
	}

	@Override
	protected void applyEntityAttributes(){
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(48);
	}

	/**
	 * This contains the default AI tasks.  To add new ones, override {@link #initSpecificAI()}
	 */
	protected void initAI(){
		this.getNavigator().setBreakDoors(true);
		this.tasks.addTask(0, new EntityAISwimming(this));
		this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true));
		this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 0, true));
		this.targetTasks.addTask(3, new EntityAINearestAttackableTarget(this, EntityLightMage.class, 0, true));

		initSpecificAI();
	}

	/**
	 * Initializer for class-specific AI
	 */
	protected abstract void initSpecificAI();

	protected abstract String getMusic();

	@Override
	public BossActions getCurrentAction(){
		return currentAction;
	}

	@Override
	public void setCurrentAction(BossActions action){
		currentAction = action;
		ticksInCurrentAction = 0;
	}

	@Override
	public int getTicksInCurrentAction(){
		return ticksInCurrentAction;
	}

	@Override
	public boolean isActionValid(BossActions action){
		return true;
	}

	@Override
	public abstract String getAttackSound();

	@Override
	protected boolean canDespawn(){
		return false;
	}

	@Override
	public Entity[] getParts() {
		return parts;
	}

	@Override
	public boolean canBeCollidedWith(){
		return false;
	}

	public boolean isWithinDistanceToPlayer() {
		for (Object objectPlayer : worldObj.playerEntities){
			EntityPlayer entityplayer1 = (EntityPlayer)objectPlayer;
			if (getDistanceSqToEntity(entityplayer1) < (27 * 27)) return true;
		}
		return false;
	}

	@Override
	public boolean attackEntityFrom(DamageSource par1DamageSource, float par2){

		boolean magicMod = false; // more of acceptable magic mod damage methods will be added if suggested
		for (StackTraceElement ste : new Exception().getStackTrace()) {
			if (ste.toString().contains("thaumcraft.common.entities.projectile")) magicMod = true; // if only thaumcraft had a distinct damage source...
		}

		if (par1DamageSource.isUnblockable() && !par1DamageSource.isMagicDamage() && !par1DamageSource.isDamageAbsolute() && !par1DamageSource.canHarmInCreative()) {
			ReflectionHelper.setPrivateValue(DamageSource.class, par1DamageSource, false, "isUnblockable", "field_76374_o");
		} // anti-TiC-rapier

		if (magicMod) ReflectionHelper.setPrivateValue(DamageSource.class, par1DamageSource, true, "isUnblockable", "field_76374_o");

		// more anti-farming
		if (par1DamageSource == DamageSource.drown || par1DamageSource == DamageSource.cactus || par1DamageSource == DamageSource.wither) return false;
		if (!par1DamageSource.damageType.startsWith("am2")
			&& !par1DamageSource.damageType.contains("magic")
			&& !par1DamageSource.damageType.contains("Magic")
			&& !par1DamageSource.damageType.contains("MAGIC")
			&& !magicMod)
			return false;

		if (par1DamageSource == DamageSource.inWall){
			if (!worldObj.isRemote){// dead code? (calling canSnowAt() without using the result) could it be a buggy upgrade to 1.7.10?
				for (int i = -1; i <= 1; ++i){
					for (int j = 0; j < 3; ++j){
						for (int k = -1; k <= 1; ++k){
							worldObj.func_147478_e(i, j, k, true);
						}
					}
				}
			}
			return false;
		}

		if (par1DamageSource.getSourceOfDamage() != null){

			if (par1DamageSource.getSourceOfDamage() instanceof EntityPlayer){
				EntityPlayer player = (EntityPlayer)par1DamageSource.getSourceOfDamage();
				if (player.capabilities.isCreativeMode && player.getCurrentEquippedItem() != null && player.getCurrentEquippedItem().getItem() == ItemsCommonProxy.woodenLeg){
					if (!worldObj.isRemote)
						this.setDead();
					return false;
				}
			}else if (par1DamageSource.getSourceOfDamage() instanceof EntityArrow){
				Entity shooter = ((EntityArrow)par1DamageSource.getSourceOfDamage()).shootingEntity;
				if (shooter != null && this.getDistanceSqToEntity(shooter) > 900){
					this.setPositionAndUpdate(shooter.posX, shooter.posY, shooter.posZ);
				}
				return false;
			}else if (this.getDistanceSqToEntity(par1DamageSource.getSourceOfDamage()) > 900){
				Entity shooter = (par1DamageSource.getSourceOfDamage());
				if (shooter != null){
					this.setPositionAndUpdate(shooter.posX, shooter.posY, shooter.posZ);
				}
			}
		}

		if (par2 > 15) par2 = 15;

		par2 = modifyDamageAmount(par1DamageSource, par2);

		if (par2 <= 0){
			heal(-par2);
			return false;
		}

		if (super.attackEntityFrom(par1DamageSource, par2)){
			this.hurtResistantTime = 40;
			return true;
		}
		return false;
	}

	protected abstract float modifyDamageAmount(DamageSource source, float damageAmt);

	public boolean attackEntityFromPart(EntityDragonPart part, DamageSource source, float damage){
		return this.attackEntityFrom(source, damage);
	}

	private byte stuckTicks = -127;

	@Override
	public void onUpdate(){

		if (parts != null && parts[0] != null && parts[0].field_146032_b == "defaultBody"){
			parts[0].setPosition(this.posX, this.posY, this.posZ);
			if (worldObj.isRemote){
			      parts[0].setVelocity(this.motionX, this.motionY, this.motionZ);
			}
			if (!parts[0].addedToChunk){
				this.worldObj.spawnEntityInWorld(parts[0]);
			}
		}

		this.ticksInCurrentAction++;
		this.timer++;

		if (ticksInCurrentAction > 200){
			setCurrentAction(BossActions.IDLE);
		}

		if (this.getAttackTarget() == null && !this.worldObj.isRemote) {
			heal(1.5F); // Do not the boss
		}

		if (timer % 10 == 0) {
//			boolean conditionSeeDistance = (!isWithinDistanceToPlayer() && this.getAttackTarget() == null);
			boolean conditionWorld = !(this.worldObj.provider instanceof DynamicBossWorldProvider);
			if (conditionWorld) { // Despawn if brought outside domain
				if (worldObj.isRemote){
					SpellSoundHelper.currentlyPlayingMusic.fadeOut();
//					if (conditionSeeDistance) AMCore.proxy.getLocalPlayer().addChatMessage(new ChatComponentText("You feel the presence of a Guardian dissipating, unable to sustain itself without the vital mana provided by its challengers."));
					AMCore.proxy.getLocalPlayer().addChatMessage(new ChatComponentText("The Guardian, forced out of its Domain, feels threatened and retreats onto the elemental planes.")); // conditionWorld
				} else this.setDead();
			}
			timer = 0;
		}

		// anti-stuck (smarter logic than previous attempt, thankfully)
		if (this.getAttackTarget() != null && (this.motionX > 0 || this.motionZ > 0)) {
			if (((int)lastTickPosX == (int)posX) && ((int)lastTickPosZ == (int)posZ)) {
				stuckTicks++;
			}
		}
		if (stuckTicks == 127) { // max byte value. It's an int undercover, but so what? No worse than any other magic number
			tpBackToStartingPlayerPoint();
			stuckTicks = -127;
		}

		if (worldObj.isRemote){
			SpellSoundHelper.playLoopingMusicSound(AMCore.proxy.getLocalPlayer(), this.getMusic(), 1.0f, 1.0f, this.getMusic());
			playerCanSee = AMCore.proxy.getLocalPlayer().canEntityBeSeen(this);
			this.ignoreFrustumCheck =  AMCore.proxy.getLocalPlayer().getDistanceToEntity(this) < 32;
		}

		if (this.posY < 5) { // pushed off
			tpBackToStartingPoint();
			// to discourage pushing off: boss becomes stronger
			float maxHealth = this.getMaxHealth();
			heal(maxHealth / 4);
			this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(maxHealth * 1.1f);
			this.setAIMoveSpeed(this.getAIMoveSpeed() * 1.1f);
			this.getEntityAttribute(SharedMonsterAttributes.knockbackResistance).setBaseValue(((float)this.getEntityAttribute(SharedMonsterAttributes.knockbackResistance).getAttributeValue() + 0.06f) * 1.1f);
			if (worldObj.isRemote) AMCore.proxy.getLocalPlayer().addChatMessage(new ChatComponentText("You feel the presence of the Guardian grow stronger as it returns from the abyss below."));
		}

		// No longer needed because of changes from TCLProject Vs. Boss Farmers, Episode 4
		// ~~~break all non-unbreakable blocks to prevent guardian from being locked up and farmed~~~
//		for (int x = -1; x <= 1; x++){
//			for (int y = 0; y <= 2; y++){
//				for (int z = -1; z <= 1; z++){
//					Block block = this.worldObj.getBlock((int)this.posX + x, (int)this.posY + y, (int)this.posZ + z);
//					if (!this.worldObj.isAirBlock((int)this.posX + x, (int)this.posY + y, (int)this.posZ + z)){
//						if (this.worldObj.rand.nextDouble() > 0.993D &&
//								block.getBlockHardness(this.worldObj, (int)this.posX + x, (int)this.posY + y, (int)this.posZ + z) > 0.1f
//						&& !(block instanceof BlockLiquid) && !(disallowedBlocks.contains(block)) && worldObj.getGameRules().getGameRuleBooleanValue("mobGriefing")){
//							block.breakBlock(this.worldObj, (int)this.posX + x, (int)this.posY + y, (int)this.posZ + z,
//									block,
//									this.worldObj.getBlockMetadata((int)this.posX + x, (int)this.posY + y, (int)this.posZ + z));
//							block.dropBlockAsItem(this.worldObj, (int)this.posX + x, (int)this.posY + y, (int)this.posZ + z,
//									this.worldObj.getBlockMetadata((int)this.posX + x, (int)this.posY + y, (int)this.posZ + z),
//									Block.getIdFromBlock(block));
//							worldObj.setBlockToAir((int)this.posX + x, (int)this.posY + y, (int)this.posZ + z);
//						}
//					}
//				}
//			}
//		}

		super.onUpdate();
	}

	private void tpBackToStartingPlayerPoint() {
		int[] bossSpawn = BossSpawnHelper.playerBossfightCoordinates[BossSpawnHelper.getIntFromBoss(this)];
		this.setPositionAndUpdate(bossSpawn[0] + 0.5D, bossSpawn[1] + 0.5D, bossSpawn[2] + 0.5D);
		this.worldObj.playSoundEffect(bossSpawn[0] + 0.5D, bossSpawn[1], bossSpawn[2] + 0.5D, "mob.endermen.portal", 1.0F, 1.0F);
	}

	private void tpBackToStartingPoint() {
		int[] bossSpawn = BossSpawnHelper.bossBossfightCoordinates[BossSpawnHelper.getIntFromBoss(this)][this.rand.nextInt(BossSpawnHelper.bossBossfightCoordinates[BossSpawnHelper.getIntFromBoss(this)].length)];
		this.setPositionAndUpdate(bossSpawn[0] + 0.5D, bossSpawn[1] + 0.5D, bossSpawn[2] + 0.5D);
		this.worldObj.playSoundEffect(bossSpawn[0] + 0.5D, bossSpawn[1], bossSpawn[2] + 0.5D, "mob.endermen.portal", 1.0F, 1.0F);
	}

	private ArrayList<Block> disallowedBlocks = new ArrayList<Block>();

	@Override
	public boolean allowLeashing(){
		return false;
	}

	@Override
	public void addPotionEffect(PotionEffect effect){
		if (effect.getPotionID() == BuffList.silence.id || effect.getPotionID() == Potion.blindness.id
			|| effect.getEffectName().contains("blindness") || effect.getEffectName().contains("ink")
		|| effect.getPotionID() == BuffList.entangled.id || effect.getPotionID() == BuffList.wateryGrave.id)
			return;
		super.addPotionEffect(effect);
	}

	public World func_82194_d(){
		return this.worldObj;
	}

	@Override
	public boolean getCanSpawnHere(){
		if (!SpawnBlacklists.getPermanentBlacklistValue(worldObj, this))
			return false;
		return super.getCanSpawnHere();
	}

	@Override
	public EntityItem entityDropItem(ItemStack p_70099_1_, float p_70099_2_) // function replaced to give player items directly or spawn at correct position
	{
		if (p_70099_1_.stackSize != 0 && p_70099_1_.getItem() != null && !this.worldObj.isRemote) {
			for (Object objectPlayer : worldObj.playerEntities) {
				EntityPlayerMP entityplayer = (EntityPlayerMP) objectPlayer;
				if (!entityplayer.inventory.addItemStackToInventory(p_70099_1_)) {
					ExtendedProperties extendedProperties = ExtendedProperties.For(entityplayer);
					if (extendedProperties.hasExtraVariable("origPos")) {
						String[] originalPosition = extendedProperties.getExtraVariable("origPos").split(",");
						World origWorld = DimensionManager.getWorld(Integer.parseInt(originalPosition[3]));
						EntityItem entityitem = new EntityItem(origWorld, Double.parseDouble(originalPosition[0]), Double.parseDouble(originalPosition[1]) + 0.5D + (double) p_70099_2_, Double.parseDouble(originalPosition[2]), p_70099_1_);
						entityitem.delayBeforeCanPickup = 10;
						origWorld.spawnEntityInWorld(entityitem);
						return entityitem;
					}
				}
			}
		}
		return null;
	}
}
