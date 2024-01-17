package net.tclproject.mysteriumlib.render.gecko.common.entities.entity.specific;

import net.minecraft.world.World;
import net.tclproject.mysteriumlib.render.gecko.common.entities.entity.AbstractGeckoEntity;
import net.tclproject.mysteriumlib.render.gecko.event.AnimationTestEvent;

public class TentacleEntity extends AbstractGeckoEntity {
   public TentacleEntity(World worldIn) {
      super(worldIn);
      this.setSize(0.8F, 3.1F);
   }

   public void onLivingUpdate() {
      super.onLivingUpdate();
      if (!this.onGround && this.motionY < 0.0D) {
         this.motionY *= 0.6D;
      }

   }

   public boolean autonomousAnimationPredicate(AnimationTestEvent event) {
      return this.isAutonomousEnabled;
   }
}
