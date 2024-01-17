package net.tclproject.mysteriumlib.render.gecko.common.entities.entity.specific.iceandfire;

import net.ilexiconn.llibrary.server.animation.Animation;
import net.minecraft.world.World;

public class EntityIceDragon extends EntityDragonBase {
   public static Animation ANIMATION_FIRECHARGE;
   public int swimProgress;
   public int swimCycle;

   public EntityIceDragon(World p_i1604_1_) {
      super(p_i1604_1_);
      ANIMATION_SPEAK = Animation.create(20);
      ANIMATION_BITE = Animation.create(35);
      ANIMATION_SHAKEPREY = Animation.create(65);
      ANIMATION_TAILWHACK = Animation.create(40);
      ANIMATION_FIRECHARGE = Animation.create(25);
      ANIMATION_WINGBLAST = Animation.create(50);
      ANIMATION_ROAR = Animation.create(40);
      ANIMATION_EPIC_ROAR = Animation.create(60);
   }
}
