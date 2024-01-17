package net.tclproject.mysteriumlib.render.gecko.common.entities.entity.specific;

import net.minecraft.world.World;
import net.tclproject.mysteriumlib.render.gecko.animation.builder.AnimationBuilder;
import net.tclproject.mysteriumlib.render.gecko.common.entities.entity.AbstractGeckoEntity;
import net.tclproject.mysteriumlib.render.gecko.event.AnimationTestEvent;

public class DuckEntity extends AbstractGeckoEntity {
   public DuckEntity(World worldIn) {
      super(worldIn);
      this.setSize(1.0F, 1.0F);
   }

   public void onLivingUpdate() {
      super.onLivingUpdate();
      if (!this.onGround && this.motionY < 0.0D) {
         this.motionY *= 0.6D;
      }

   }

   public boolean autonomousAnimationPredicate(AnimationTestEvent event) {
      if (!this.onGround) {
         this.autonomousController.setAnimation((new AnimationBuilder()).addAnimation("fly"));
      } else if (event.isWalking()) {
         if (this.isWet()) {
            this.autonomousController.setAnimation((new AnimationBuilder()).addAnimation("swim"));
         } else {
            this.autonomousController.setAnimation((new AnimationBuilder()).addAnimation("walk"));
         }
      } else if (this.isWet()) {
         this.autonomousController.setAnimation((new AnimationBuilder()).addAnimation("idle_swim"));
      } else {
         this.autonomousController.setAnimation((new AnimationBuilder()).addAnimation("idle"));
      }

      return this.isAutonomousEnabled;
   }
}
