package net.tclproject.mysteriumlib.render.gecko.common.entities.entity;

import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import net.ilexiconn.llibrary.client.model.tools.IntermittentAnimation;
import net.ilexiconn.llibrary.server.animation.Animation;
import net.ilexiconn.llibrary.server.animation.AnimationAI;
import net.ilexiconn.llibrary.server.animation.AnimationHandler;
import net.ilexiconn.llibrary.server.animation.IAnimatedEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public abstract class AdvancedEntity extends EntityCreature implements IAnimatedEntity, IntermittentAnimatableEntity {
   private static final byte START_IA_HEALTH_UPDATE_ID = 4;
   public int frame;
   public float targetDistance;
   public float targetAngle;
   public AnimationAI currentAnim = null;
   public boolean active;
   public EntityLivingBase blockingEntity = null;
   private int animationTick;
   private Animation animation;
   private List intermittentAnimations;

   public AdvancedEntity(World world) {
      super(world);
      this.animation = NO_ANIMATION;
      this.intermittentAnimations = new ArrayList();
   }

   public void onUpdate() {
      super.onUpdate();
      ++this.frame;
      if (this.getAnimation() != NO_ANIMATION) {
         ++this.animationTick;
      }

      if (this.getAttackTarget() != null) {
         this.targetDistance = (float)Math.sqrt((this.getAttackTarget().posZ - this.posZ) * (this.getAttackTarget().posZ - this.posZ) + (this.getAttackTarget().posX - this.posX) * (this.getAttackTarget().posX - this.posX));
         this.targetAngle = (float)this.getAngleBetweenEntities(this, this.getAttackTarget());
      }

   }

   protected boolean isAIEnabled() {
      return true;
   }

   public int getAttack() {
      return 0;
   }

   public double getAngleBetweenEntities(Entity first, Entity second) {
      return Math.atan2(second.posZ - first.posZ, second.posX - first.posX) * 57.29577951308232D + 90.0D;
   }

   public List getPlayersNearby(double distanceX, double distanceY, double distanceZ, double radius) {
      List nearbyEntities = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.boundingBox.expand(distanceX, distanceY, distanceZ));
      ArrayList listEntityPlayers = (ArrayList)nearbyEntities.stream().filter((entityNeighbor) -> {
         return entityNeighbor instanceof EntityPlayer && (double)this.getDistanceToEntity((Entity) entityNeighbor) <= radius;
      }).map((entityNeighbor) -> {
         return (EntityPlayer)entityNeighbor;
      }).collect(Collectors.toCollection(ArrayList::new));
      return listEntityPlayers;
   }

   public List getEntityLivingBaseNearby(double distanceX, double distanceY, double distanceZ, double radius) {
      List nearbyEntities = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.boundingBox.expand(distanceX, distanceY, distanceZ));
      ArrayList listEntityLivingBase = (ArrayList)nearbyEntities.stream().filter((entityNeighbor) -> {
         return entityNeighbor instanceof EntityLivingBase && (double)this.getDistanceToEntity((Entity) entityNeighbor) <= radius && ((Entity) entityNeighbor).posY + ((Entity) entityNeighbor).boundingBox.maxY > this.posY + 2.0D && ((Entity) entityNeighbor).posY <= this.posY + distanceY;
      }).map((entityNeighbor) -> {
         return (EntityLivingBase)entityNeighbor;
      }).collect(Collectors.toCollection(ArrayList::new));
      return listEntityLivingBase;
   }

   protected void onDeathUpdate() {
      ++this.deathTime;
      if (this.deathTime == this.getDeathAnimation().getDuration() - 20) {
         int experience;
         if (!this.worldObj.isRemote && (this.recentlyHit > 0 || this.isPlayer()) && this.func_146066_aG() && this.worldObj.getGameRules().getGameRuleBooleanValue("doMobLoot")) {
            experience = this.getExperiencePoints(this.attackingPlayer);

            while(experience > 0) {
               int j = EntityXPOrb.getXPSplit(experience);
               experience -= j;
               this.worldObj.spawnEntityInWorld(new EntityXPOrb(this.worldObj, this.posX, this.posY, this.posZ, j));
            }
         }

         this.setDead();

         for(experience = 0; experience < 20; ++experience) {
            double d2 = this.rand.nextGaussian() * 0.02D;
            double d0 = this.rand.nextGaussian() * 0.02D;
            double d1 = this.rand.nextGaussian() * 0.02D;
            this.worldObj.spawnParticle("explode", this.posX + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, this.posY + (double)(this.rand.nextFloat() * this.height), this.posZ + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, d2, d0, d1);
         }
      }

   }

   public boolean attackEntityFrom(DamageSource source, float damage) {
      boolean attack = super.attackEntityFrom(source, damage);
      if (attack) {
         if (this.getHealth() > 0.0F && this.getAnimation() == NO_ANIMATION) {
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

   protected void addIntermittentAnimation(IntermittentAnimation animation) {
      animation.setID((byte)this.intermittentAnimations.size());
      this.intermittentAnimations.add(animation);
   }

   public void handleHealthUpdate(byte id) {
      if (id >= 4 && id - 4 < this.intermittentAnimations.size()) {
         ((IntermittentAnimation)this.intermittentAnimations.get(id - 4)).start();
      } else {
         super.handleHealthUpdate(id);
      }
   }

   public byte getOffsetEntityState() {
      return 4;
   }

   public void circleEntity(Entity target, float radius, float speed, boolean direction, int circleFrame, float offset, float moveSpeedMultiplier) {
      int directionInt = direction ? 1 : -1;
      this.getNavigator().tryMoveToXYZ(target.posX + (double)radius * Math.cos((double)(directionInt * circleFrame) * 0.5D * (double)speed / (double)radius + (double)offset), target.posY, target.posZ + (double)radius * Math.sin((double)(directionInt * circleFrame) * 0.5D * (double)speed / (double)radius + (double)offset), (double)(speed * moveSpeedMultiplier));
   }

   protected void repelEntities(float x, float y, float z, float radius) {
      List nearbyEntities = this.getEntityLivingBaseNearby((double)x, (double)y, (double)z, (double)radius);

      Entity entity;
      double angle;
      for(Iterator var6 = nearbyEntities.iterator(); var6.hasNext(); entity.motionZ = -0.1D * Math.sin(angle)) {
         entity = (Entity)var6.next();
         angle = (this.getAngleBetweenEntities(this, entity) + 90.0D) * 3.141592653589793D / 180.0D;
         entity.motionX = -0.1D * Math.cos(angle);
      }

   }

   public int getAnimationTick() {
      return this.animationTick;
   }

   public void setAnimationTick(int tick) {
      this.animationTick = tick;
   }

   public Animation getAnimation() {
      return this.animation;
   }

   public void setAnimation(Animation animation) {
      this.animation = animation;
   }

   public abstract Animation getDeathAnimation();

   public abstract Animation getHurtAnimation();
}
