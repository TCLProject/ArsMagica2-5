package net.tclproject.mysteriumlib.render.gecko.common.entities.entity.specific.iceandfire;

import net.ilexiconn.llibrary.server.animation.Animation;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.world.World;

public class EntityMyrmexSentinel extends EntityMyrmexBase {
   public static final Animation ANIMATION_GRAB = Animation.create(15);
   public static final Animation ANIMATION_NIBBLE = Animation.create(10);
   public static final Animation ANIMATION_STING = Animation.create(25);
   public static final Animation ANIMATION_SLASH = Animation.create(25);
   public int hidingProgress = 0;
   public int holdingProgress = 0;

   public EntityMyrmexSentinel(World p_i1681_1_) {
      super(p_i1681_1_);
      this.setSize(2.0F, 3.0F);
   }

   public EntityAgeable createChild(EntityAgeable p_90011_1_) {
      return new EntityMyrmexSentinel(p_90011_1_.worldObj);
   }

   public boolean isHiding() {
      return false;
   }
}
