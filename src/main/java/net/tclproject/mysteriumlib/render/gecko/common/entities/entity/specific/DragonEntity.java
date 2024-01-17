package net.tclproject.mysteriumlib.render.gecko.common.entities.entity.specific;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.world.World;
import net.tclproject.mysteriumlib.render.gecko.animation.builder.AnimationBuilder;
import net.tclproject.mysteriumlib.render.gecko.animation.controller.EntityAnimationController;
import net.tclproject.mysteriumlib.render.gecko.entity.ICustomAnimatedEntity;
import net.tclproject.mysteriumlib.render.gecko.event.AnimationTestEvent;
import net.tclproject.mysteriumlib.render.gecko.manager.EntityAnimationManager;

public class DragonEntity extends EntityCreature implements ICustomAnimatedEntity {
   public EntityAnimationManager manager = new EntityAnimationManager();
   public String manualAnimation = "";
   boolean isAutonomousEnabled = true;
   boolean isManualEnabled = false;
   boolean isSitting = false;
   public EntityAnimationController autonomousController = new EntityAnimationController(this, "autonomousController", 20.0F, this::autonomousAnimationPredicate);
   public EntityAnimationController manualController = new EntityAnimationController(this, "manualController", 20.0F, this::manualAnimationPredicate);

   private boolean autonomousAnimationPredicate(AnimationTestEvent event) {
      if (((Entity)event.getEntity()).onGround) {
         if (event.isWalking()) {
            this.autonomousController.setAnimation((new AnimationBuilder()).addAnimation("animation.model.walk"));
         }
      } else {
         this.autonomousController.setAnimation((new AnimationBuilder()).addAnimation("animation.model.flight"));
      }

      return this.isAutonomousEnabled;
   }

   private boolean manualAnimationPredicate(AnimationTestEvent event) {
      if (!this.manualAnimation.equals("")) {
         this.manualController.setAnimation((new AnimationBuilder()).addAnimation(this.manualAnimation));
      }

      return this.isManualEnabled;
   }

   public DragonEntity(World worldIn) {
      super(worldIn);
      this.setSize(2.0F, 1.0F);
      this.setRotation(0.0F, 0.0F);
      this.manager.addAnimationController(this.autonomousController);
      this.manager.addAnimationController(this.manualController);
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
