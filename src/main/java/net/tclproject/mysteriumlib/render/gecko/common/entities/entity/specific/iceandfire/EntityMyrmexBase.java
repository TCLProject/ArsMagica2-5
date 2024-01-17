package net.tclproject.mysteriumlib.render.gecko.common.entities.entity.specific.iceandfire;

import net.ilexiconn.llibrary.server.animation.Animation;
import net.ilexiconn.llibrary.server.animation.IAnimatedEntity;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public abstract class EntityMyrmexBase extends EntityAnimal implements IAnimatedEntity {
   private static final ResourceLocation TEXTURE_JUNGLE_LARVA = new ResourceLocation("arsmagica2", "textures/model/entity/myrmex/myrmex_jungle_larva.png");
   private static final ResourceLocation TEXTURE_JUNGLE_PUPA = new ResourceLocation("arsmagica2", "textures/model/entity/myrmex/myrmex_jungle_pupa.png");
   private int animationTick;
   private Animation currentAnimation;
   public static final Animation ANIMATION_PUPA_WIGGLE = Animation.create(20);

   public EntityMyrmexBase(World p_i1681_1_) {
      super(p_i1681_1_);
   }

   public int getGrowthStage() {
      return 2;
   }

   public int getAnimationTick() {
      return this.animationTick;
   }

   public void setAnimationTick(int tick) {
      this.animationTick = tick;
   }

   public Animation getAnimation() {
      return this.currentAnimation;
   }

   public void setAnimation(Animation animation) {
      this.currentAnimation = animation;
   }

   public Animation[] getAnimations() {
      return new Animation[]{ANIMATION_PUPA_WIGGLE};
   }
}
