package net.tclproject.mysteriumlib.render.gecko.common.entities.entity.specific;

import net.minecraft.world.World;
import net.tclproject.mysteriumlib.render.gecko.animation.builder.AnimationBuilder;
import net.tclproject.mysteriumlib.render.gecko.common.entities.entity.AbstractGeckoEntity;
import net.tclproject.mysteriumlib.render.gecko.event.AnimationTestEvent;

public class PortalEntity extends AbstractGeckoEntity {
   public PortalEntity(World worldIn) {
      super(worldIn);
      this.setSize(2.0F, 4.0F);
   }

   public boolean autonomousAnimationPredicate(AnimationTestEvent event) {
      this.autonomousController.setAnimation((new AnimationBuilder()).addAnimation("idle.fault"));
      return this.isAutonomousEnabled;
   }
}
