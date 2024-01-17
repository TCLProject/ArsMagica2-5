package net.tclproject.mysteriumlib.render.gecko.common.entities.entity.specific;

import net.minecraft.world.World;
import net.tclproject.mysteriumlib.render.gecko.animation.builder.AnimationBuilder;
import net.tclproject.mysteriumlib.render.gecko.common.entities.entity.AbstractGeckoEntity;
import net.tclproject.mysteriumlib.render.gecko.event.AnimationTestEvent;

public class TrapEntity extends AbstractGeckoEntity {
   public TrapEntity(World worldIn) {
      super(worldIn);
      this.setSize(4.0F, 4.0F);
   }

   public boolean autonomousAnimationPredicate(AnimationTestEvent event) {
      if (event.isWalking()) {
         this.autonomousController.setAnimation((new AnimationBuilder()).addAnimation("walk.trap"));
      } else {
         this.autonomousController.setAnimation((new AnimationBuilder()).addAnimation("idle.trap"));
      }

      return this.isAutonomousEnabled;
   }
}
