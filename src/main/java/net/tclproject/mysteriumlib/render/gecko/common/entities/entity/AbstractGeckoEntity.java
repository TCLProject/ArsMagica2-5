package net.tclproject.mysteriumlib.render.gecko.common.entities.entity;

import net.minecraft.entity.EntityCreature;
import net.minecraft.world.World;
import net.tclproject.mysteriumlib.render.gecko.animation.builder.AnimationBuilder;
import net.tclproject.mysteriumlib.render.gecko.animation.controller.EntityAnimationController;
import net.tclproject.mysteriumlib.render.gecko.entity.ICustomAnimatedEntity;
import net.tclproject.mysteriumlib.render.gecko.event.AnimationTestEvent;
import net.tclproject.mysteriumlib.render.gecko.manager.EntityAnimationManager;

public abstract class AbstractGeckoEntity extends EntityCreature implements ICustomAnimatedEntity {
   public EntityAnimationManager manager = new EntityAnimationManager();
   public String manualAnimation = "";
   public boolean isAutonomousEnabled = true;
   public boolean isManualEnabled = false;
   public boolean isSitting = false;
   public EntityAnimationController autonomousController = new EntityAnimationController(this, "autonomousController", 20.0F, this::autonomousAnimationPredicate);
   public EntityAnimationController manualController = new EntityAnimationController(this, "manualController", 20.0F, this::manualAnimationPredicate);

   public AbstractGeckoEntity(World worldIn) {
      super(worldIn);
      this.setRotation(0.0F, 0.0F);
      this.manager.addAnimationController(this.autonomousController);
      this.manager.addAnimationController(this.manualController);
   }

   protected abstract boolean autonomousAnimationPredicate(AnimationTestEvent var1);

   private boolean manualAnimationPredicate(AnimationTestEvent event) {
      if (!this.manualAnimation.equals("")) {
         this.manualController.setAnimation((new AnimationBuilder()).addAnimation(this.manualAnimation));
      }

      return this.isManualEnabled;
   }

   public void onUpdate() {
      super.onUpdate();
      this.setRotation(0.0F, 0.0F);
   }

   public EntityAnimationManager getAnimationManager() {
      return this.manager;
   }

   public EntityAnimationController getManualController() {
      return this.manualController;
   }

   public void setManualAnimation(String name) {
      this.manualAnimation = name;
   }

   public void setAutonomousEnabled(boolean isEnabled) {
      this.isAutonomousEnabled = isEnabled;
   }

   public void setManualEnabled(boolean isEnabled) {
      this.isManualEnabled = isEnabled;
   }

   public void setRevertTicks(int ticks) {
      this.manager.setResetSpeedInTicks((double)ticks);
   }
}
