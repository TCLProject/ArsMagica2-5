package net.tclproject.mysteriumlib.render.gecko.common.entities.entity.specific;

import net.minecraft.world.World;
import net.tclproject.mysteriumlib.render.gecko.animation.builder.AnimationBuilder;
import net.tclproject.mysteriumlib.render.gecko.common.entities.entity.AbstractGeckoEntity;
import net.tclproject.mysteriumlib.render.gecko.event.AnimationTestEvent;

public class CrowEntity extends AbstractGeckoEntity {
   public CrowEntity(World worldIn) {
      super(worldIn);
      this.setSize(8.0F, 2.0F);
   }

   public void onLivingUpdate() {
      super.onLivingUpdate();
      if (!this.onGround && this.motionY < 0.0D) {
         this.motionY *= 0.6D;
      }

   }

   public boolean autonomousAnimationPredicate(AnimationTestEvent event) {
      this.autonomousController.setAnimation((new AnimationBuilder()).addAnimation("idle"));
      return this.isAutonomousEnabled;
   }
}
