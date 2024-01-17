package net.tclproject.mysteriumlib.render.gecko.common.entities.entity.specific;

import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.world.World;

public class HorseEntity extends EntityHorse {
   public HorseEntity(World p_i1685_1_) {
      super(p_i1685_1_);
   }

   public double getMountedYOffset() {
      return 2.0D;
   }
}
