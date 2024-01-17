package net.tclproject.mysteriumlib.render.gecko.common.entities.entity.specific;

import net.ilexiconn.llibrary.server.animation.Animation;
import net.ilexiconn.llibrary.server.animation.AnimationHandler;
import net.minecraft.block.Block;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.tclproject.mysteriumlib.render.gecko.common.entities.entity.AdvancedEntity;

public class CrystalCrabEntity extends AdvancedEntity {
   public static final Animation DIE_ANIMATION = Animation.create(70);
   public static final Animation HURT_ANIMATION = Animation.create(10);
   public static final Animation ATTACK_ANIMATION = Animation.create(24);
   public static final Animation IDLE_ANIMATION = Animation.create(35);
   public static final Animation ACTIVATE_ANIMATION = Animation.create(20);
   public static final Animation DIG_ANIMATION = Animation.create(60);
   public boolean circleDirection = true;
   public int circleTick = 0;
   protected boolean attacking = false;
   protected int timeSinceAttack = 0;
   protected int timeSinceDig = 0;
   boolean prevHasTarget = false;
   boolean prevprevHasTarget = false;
   boolean prevprevprevHasTarget = false;

   public CrystalCrabEntity(World world) {
      super(world);
      this.tasks.addTask(0, new EntityAISwimming(this));
      this.tasks.addTask(2, new EntityAIAttackOnCollide(this, EntityPlayer.class, 0.5D, false));
      this.tasks.addTask(4, new EntityAIMoveTowardsRestriction(this, 0.5D));
      this.tasks.addTask(6, new EntityAIWander(this, 0.5D));
      this.tasks.addTask(7, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
      this.tasks.addTask(7, new EntityAILookIdle(this));
      this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true));
      this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 0, true));
      this.setSize(1.0F, 0.8F);
      this.stepHeight = 1.0F;
      this.frame += this.rand.nextInt(50);
      this.experienceValue = 8;
      this.active = true;
   }

   public int getAttack() {
      return 4;
   }

   public Animation getDeathAnimation() {
      return DIE_ANIMATION;
   }

   public Animation getHurtAnimation() {
      return HURT_ANIMATION;
   }

   protected String getLivingSound() {
      if (!this.active) {
         return null;
      } else {
         if (this.getAttackTarget() == null) {
            int i = MathHelper.getRandomIntegerInRange(this.rand, 0, 11);
            if (i <= 7) {
               AnimationHandler.INSTANCE.sendAnimationMessage(this, IDLE_ANIMATION);
            }
         }

         return null;
      }
   }

   public boolean attackEntityFrom(DamageSource source, float damage) {
      boolean attack = super.attackEntityFrom(source, damage);
      if (attack) {
         if (this.getHealth() > 0.0F) {
            AnimationHandler.INSTANCE.sendAnimationMessage(this, this.getHurtAnimation());
         } else if (this.getHealth() <= 0.0F) {
            if (this.currentAnim != null) {
               this.currentAnim.resetTask();
            }

            AnimationHandler.INSTANCE.sendAnimationMessage(this, this.getDeathAnimation());
         }
      }

      return attack;
   }

   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.knockbackResistance).setBaseValue(1.0D);
      this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(20.0D);
      this.getAttributeMap().registerAttribute(SharedMonsterAttributes.attackDamage);
      this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(0.4D);
   }

   public boolean attackEntityAsMob(Entity p_70652_1_) {
      float f = (float)this.getEntityAttribute(SharedMonsterAttributes.attackDamage).getAttributeValue();
      if (p_70652_1_ instanceof EntityLivingBase) {
         f += EnchantmentHelper.getEnchantmentModifierLiving(this, (EntityLivingBase)p_70652_1_);
      }

      boolean flag = p_70652_1_.attackEntityFrom(DamageSource.causeMobDamage(this), f);
      return flag;
   }

   protected void updateAttackAI() {
      if (this.timeSinceAttack < 20) {
         ++this.timeSinceAttack;
      }

      if (this.getAttackTarget() != null) {
         if (this.rand.nextInt(20) == 0 && this.timeSinceAttack >= 20) {
            this.attacking = true;
            if (this.getAnimation() == NO_ANIMATION) {
               this.getNavigator().tryMoveToEntityLiving(this.getAttackTarget(), 0.5D);
            }
         }

         if (this.attacking && this.targetDistance <= 3.0F) {
            this.attacking = false;
            this.timeSinceAttack = 0;
            AnimationHandler.INSTANCE.sendAnimationMessage(this, ATTACK_ANIMATION);
         }
      } else {
         this.attacking = false;
         if (this.rand.nextInt(20) == 0 && this.timeSinceDig >= 60 && this.getAnimation() != DIG_ANIMATION && this.getHealth() > 0.0F) {
            AnimationHandler.INSTANCE.sendAnimationMessage(this, DIG_ANIMATION);
            this.timeSinceDig = 0;
         }
      }

   }

   public void onUpdate() {
      super.onUpdate();
      if (!this.worldObj.isRemote && this.active && this.getActive() == 0) {
         this.setActive(1);
      }

      this.active = this.getActive() == 1;
      if (this.timeSinceDig < 60) {
         ++this.timeSinceDig;
      }

      if (!this.active) {
         this.getNavigator().clearPathEntity();
         this.rotationYaw = this.prevRotationYaw;
         this.renderYawOffset = this.rotationYaw;
      } else {
         this.updateAttackAI();
         if (this.getAnimation() != NO_ANIMATION) {
            this.getNavigator().clearPathEntity();
         }

         if (this.worldObj.isRemote && this.getAnimation() == DIG_ANIMATION && this.getAnimationTick() == 10) {
            this.worldObj.playSound(this.posX, this.posY, this.posZ, "arsmagica2:crab.dig", 1.0F, 1.0F, false);
         }

         if (this.worldObj.isRemote && this.getAnimation() == ATTACK_ANIMATION) {
            int tick = this.getAnimationTick();
            if (tick == 2 || tick == 8 || tick == 14 || tick == 20) {
               this.worldObj.playSound(this.posX, this.posY, this.posZ, "arsmagica2:crab.attack", 1.0F, 1.0F, false);
            }
         }

         if (!this.worldObj.isRemote && this.timeSinceDig == 50) {
            EntityItem item = new EntityItem(this.worldObj, this.posX, this.posY, this.posZ, new ItemStack(Items.diamond));
            item.addVelocity(0.1D * this.rand.nextDouble(), -1.0D, 0.1D * this.rand.nextDouble());
            this.worldObj.spawnEntityInWorld(item);
         }

         this.prevprevprevHasTarget = this.prevprevHasTarget;
         this.prevprevHasTarget = this.prevHasTarget;
         this.prevHasTarget = this.getAttackTarget() != null;
      }
   }

   protected String getHurtSound() {
      return "arsmagica2:crab.hit";
   }

   protected String getDeathSound() {
      return "arsmagica2:crab.die";
   }

   protected void entityInit() {
      super.entityInit();
      this.dataWatcher.addObject(28, 0);
      this.dataWatcher.addObject(29, 0);
      this.dataWatcher.addObject(30, 0);
      this.dataWatcher.addObject(27, 1);
   }

   protected void func_145780_a(int p_145780_1_, int p_145780_2_, int p_145780_3_, Block p_145780_4_) {
      this.playSound("arsmagica2:crab.steps", 1.0F, 1.0F);
   }

   public int getActive() {
      return this.dataWatcher.getWatchableObjectInt(27);
   }

   public void setActive(Integer active) {
      this.dataWatcher.updateObject(27, active);
   }

   public void onDeath(DamageSource p_70645_1_) {
      super.onDeath(p_70645_1_);
   }

   public boolean canBeCollidedWith() {
      return this.active;
   }

   protected void fall(float p_70069_1_) {
      if (this.active) {
         super.fall(p_70069_1_);
      }
   }

   public Animation[] getAnimations() {
      return new Animation[]{DIE_ANIMATION, HURT_ANIMATION, ATTACK_ANIMATION, IDLE_ANIMATION, ACTIVATE_ANIMATION, DIG_ANIMATION};
   }
}
