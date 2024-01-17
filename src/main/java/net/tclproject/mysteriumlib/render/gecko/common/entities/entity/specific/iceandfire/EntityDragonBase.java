package net.tclproject.mysteriumlib.render.gecko.common.entities.entity.specific.iceandfire;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.ilexiconn.llibrary.client.model.tools.ChainBuffer;
import net.ilexiconn.llibrary.server.animation.Animation;
import net.ilexiconn.llibrary.server.animation.AnimationHandler;
import net.ilexiconn.llibrary.server.animation.IAnimatedEntity;
import net.ilexiconn.llibrary.server.entity.multipart.IMultipartEntity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.world.World;
import net.tclproject.mysteriumlib.render.gecko.iceandfire.IFChainBuffer;
import net.tclproject.mysteriumlib.render.gecko.iceandfire.ReversedBuffer;
import net.tclproject.mysteriumlib.render.gecko.iceandfire.util.LegSolverQuadruped;

public class EntityDragonBase extends EntityLiving implements IMultipartEntity, IAnimatedEntity {
   public static Animation ANIMATION_EAT;
   public static Animation ANIMATION_SPEAK;
   public static Animation ANIMATION_BITE;
   public static Animation ANIMATION_SHAKEPREY;
   public static Animation ANIMATION_WINGBLAST;
   public static Animation ANIMATION_ROAR;
   public static Animation ANIMATION_EPIC_ROAR;
   public static Animation ANIMATION_TAILWHACK;
   @SideOnly(Side.CLIENT)
   public IFChainBuffer roll_buffer;
   @SideOnly(Side.CLIENT)
   public IFChainBuffer pitch_buffer;
   @SideOnly(Side.CLIENT)
   public IFChainBuffer pitch_buffer_body;
   @SideOnly(Side.CLIENT)
   public ReversedBuffer turn_buffer;
   @SideOnly(Side.CLIENT)
   public ChainBuffer tail_buffer;
   public float sitProgress;
   public float sleepProgress;
   public float hoverProgress;
   public float flyProgress;
   public float fireBreathProgress;
   public float diveProgress;
   public float prevDiveProgress;
   public float prevFireBreathProgress;
   public int fireStopTicks;
   public int flyTicks;
   public float modelDeadProgress;
   public float ridingProgress;
   public float tackleProgress;
   public boolean isDaytime;
   public int flightCycle;
   public boolean hasHomePosition = false;
   public int spacebarTicks;
   public float[][] growth_stages;
   public LegSolverQuadruped legSolver;
   public int walkCycle;
   public int burnProgress;
   public double burnParticleX;
   public double burnParticleY;
   public double burnParticleZ;
   public float prevDragonPitch;
   public boolean usingGroundAttack = true;
   public int hoverTicks;
   public int tacklingTicks;
   public int ticksStill;
   public int navigatorType;
   public InventoryBasic dragonInventory;
   public String prevArmorResLoc = "0|0|0|0";
   public String armorResLoc = "0|0|0|0";
   protected int flyHovering;
   protected boolean hasHadHornUse = false;
   protected int fireTicks;
   protected int blockBreakCounter;
   private int prevFlightCycle;
   private boolean isSleeping;
   private boolean isSitting;
   private boolean isHovering;
   private boolean isFlying;
   private boolean isBreathingFire;
   private boolean isTackling;
   private boolean isModelDead;
   private int animationTick;
   private Animation currentAnimation;

   public EntityDragonBase(World p_i1604_1_) {
      super(p_i1604_1_);
   }

   public void onLivingUpdate() {
      super.onLivingUpdate();
      AnimationHandler.INSTANCE.updateAnimations(this);
      if (this.animationTick > this.getAnimation().getDuration() && !this.worldObj.isRemote) {
         this.animationTick = 0;
      }

   }

   public boolean isHovering() {
      return !this.onGround && this.motionX == 0.0D && this.motionY == 0.0D && this.motionZ == 0.0D;
   }

   public boolean isFlying() {
      return !this.onGround && (this.motionX != 0.0D || this.motionY != 0.0D || this.motionZ != 0.0D);
   }

   public boolean isSleeping() {
      return false;
   }

   public boolean isMale() {
      return true;
   }

   public boolean isModelDead() {
      return this.isDead;
   }

   public boolean isActuallyBreathingFire() {
      return false;
   }

   public int getAnimationTick() {
      return this.animationTick;
   }

   public void setAnimationTick(int i) {
      this.animationTick = i;
   }

   public Animation getAnimation() {
      return this.currentAnimation;
   }

   public void setAnimation(Animation animation) {
      this.currentAnimation = animation;
   }

   public Animation[] getAnimations() {
      return new Animation[]{IAnimatedEntity.NO_ANIMATION, ANIMATION_EAT, ANIMATION_SPEAK, ANIMATION_BITE, ANIMATION_SHAKEPREY, EntityIceDragon.ANIMATION_TAILWHACK, EntityIceDragon.ANIMATION_FIRECHARGE, EntityIceDragon.ANIMATION_WINGBLAST, EntityIceDragon.ANIMATION_ROAR};
   }
}
