package net.tclproject.mysteriumlib.render.gecko.common.entities.entity.specific;

import net.minecraft.world.World;
import net.tclproject.mysteriumlib.render.gecko.animation.builder.AnimationBuilder;
import net.tclproject.mysteriumlib.render.gecko.common.entities.entity.AbstractGeckoEntity;
import net.tclproject.mysteriumlib.render.gecko.event.AnimationTestEvent;

public class TankEntity extends AbstractGeckoEntity {
   public TankEntity(World worldIn) {
      super(worldIn);
      this.setSize(6.0F, 6.0F);
   }

   public boolean autonomousAnimationPredicate(AnimationTestEvent event) {
      if (event.isWalking()) {
         this.autonomousController.setAnimation((new AnimationBuilder()).addAnimation("walk.tank"));
      }

      return this.isAutonomousEnabled;
   }
}
