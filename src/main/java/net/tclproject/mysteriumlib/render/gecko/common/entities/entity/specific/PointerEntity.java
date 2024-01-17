package net.tclproject.mysteriumlib.render.gecko.common.entities.entity.specific;

import net.minecraft.world.World;
import net.tclproject.mysteriumlib.render.gecko.common.entities.entity.AbstractGeckoEntity;
import net.tclproject.mysteriumlib.render.gecko.event.AnimationTestEvent;

public class PointerEntity extends AbstractGeckoEntity {
   public PointerEntity(World worldIn) {
      super(worldIn);
      this.setSize(1.0F, 2.5F);
   }

   public boolean autonomousAnimationPredicate(AnimationTestEvent event) {
      return this.isAutonomousEnabled;
   }
}
