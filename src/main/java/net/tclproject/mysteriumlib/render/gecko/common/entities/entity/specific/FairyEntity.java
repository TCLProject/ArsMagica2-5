package net.tclproject.mysteriumlib.render.gecko.common.entities.entity.specific;

import net.minecraft.entity.monster.EntityMob;
import net.minecraft.world.World;
import net.tclproject.mysteriumlib.render.gecko.animation.builder.AnimationBuilder;
import net.tclproject.mysteriumlib.render.gecko.animation.controller.EntityAnimationController;
import net.tclproject.mysteriumlib.render.gecko.entity.IAnimatedEntity;
import net.tclproject.mysteriumlib.render.gecko.event.AnimationTestEvent;
import net.tclproject.mysteriumlib.render.gecko.manager.EntityAnimationManager;

public class FairyEntity extends EntityMob implements IAnimatedEntity {
   EntityAnimationManager manager = new EntityAnimationManager();
   EntityAnimationController controller = new EntityAnimationController(this, "flyController", 20.0F, this::animationPredicate);

   private boolean animationPredicate(AnimationTestEvent event) {
      this.controller.setAnimation((new AnimationBuilder()).addAnimation("animation.pixie.fly"));
      return false;
   }

   public FairyEntity(World worldIn) {
      super(worldIn);
      this.manager.addAnimationController(this.controller);
   }

   public EntityAnimationManager getAnimationManager() {
      return this.manager;
   }
}
