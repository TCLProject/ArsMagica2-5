package net.tclproject.mysteriumlib.render.gecko.common.entities.entity;

import net.minecraft.entity.EntityCreature;
import net.minecraft.world.World;
import net.tclproject.mysteriumlib.render.gecko.animation.builder.AnimationBuilder;
import net.tclproject.mysteriumlib.render.gecko.animation.controller.EntityAnimationController;
import net.tclproject.mysteriumlib.render.gecko.entity.ICustomAnimatedEntity;
import net.tclproject.mysteriumlib.render.gecko.event.AnimationTestEvent;
import net.tclproject.mysteriumlib.render.gecko.manager.EntityAnimationManager;

public class IMEntity extends EntityCreature implements ICustomAnimatedEntity {
   public EntityAnimationManager manager = new EntityAnimationManager();
   public String manualAnimation = "";
   boolean isAutonomousEnabled = false;
   boolean isManualEnabled = true;
   public EntityAnimationController autonomousController = new EntityAnimationController(this, "autonomousController", 20.0F, this::autonomousAnimationPredicate);
   public EntityAnimationController manualController = new EntityAnimationController(this, "manualController", 20.0F, this::manualAnimationPredicate);

   private boolean autonomousAnimationPredicate(AnimationTestEvent event) {
      return this.isAutonomousEnabled;
   }

   private boolean manualAnimationPredicate(AnimationTestEvent event) {
      if (!this.manualAnimation.equals("")) {
         this.manualController.setAnimation((new AnimationBuilder()).addAnimation(this.manualAnimation));
      }

      return this.isManualEnabled;
   }

   public IMEntity(World worldIn) {
      super(worldIn);
      this.setSize(1.0F, 2.0F);
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
