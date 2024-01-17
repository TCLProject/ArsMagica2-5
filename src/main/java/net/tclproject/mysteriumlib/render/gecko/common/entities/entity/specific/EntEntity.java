package net.tclproject.mysteriumlib.render.gecko.common.entities.entity.specific;

import net.minecraft.world.World;
import net.tclproject.mysteriumlib.render.gecko.animation.builder.AnimationBuilder;
import net.tclproject.mysteriumlib.render.gecko.common.entities.entity.AbstractGeckoEntity;
import net.tclproject.mysteriumlib.render.gecko.event.AnimationTestEvent;

public class EntEntity extends AbstractGeckoEntity {
   public EntEntity(World worldIn) {
      super(worldIn);
      this.setSize(2.5F, 5.0F);
   }

   public boolean autonomousAnimationPredicate(AnimationTestEvent event) {
      if (event.isWalking()) {
         this.autonomousController.setAnimation((new AnimationBuilder()).addAnimation("Ent.walk"));
      } else {
         this.autonomousController.setAnimation((new AnimationBuilder()).addAnimation("Ent.init"));
      }

      return this.isAutonomousEnabled;
   }
}
