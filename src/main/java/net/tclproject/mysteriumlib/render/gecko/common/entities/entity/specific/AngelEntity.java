package net.tclproject.mysteriumlib.render.gecko.common.entities.entity.specific;

import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.world.World;
import net.tclproject.mysteriumlib.render.gecko.animation.builder.AnimationBuilder;
import net.tclproject.mysteriumlib.render.gecko.animation.controller.EntityAnimationController;
import net.tclproject.mysteriumlib.render.gecko.entity.IAnimatedEntity;
import net.tclproject.mysteriumlib.render.gecko.event.AnimationTestEvent;
import net.tclproject.mysteriumlib.render.gecko.manager.EntityAnimationManager;

public class AngelEntity extends EntityMob implements IAnimatedEntity {
    EntityAnimationManager manager = new EntityAnimationManager();
    EntityAnimationController controller = new EntityAnimationController(this, "walkController", 20, this::animationPredicate);

    private <E extends Entity> boolean animationPredicate(AnimationTestEvent<E> event)
    {
        controller.setAnimation(new AnimationBuilder().addAnimation("FlyNefelim"));
        return true;
    }

    public AngelEntity(World worldIn) {
        super(worldIn);
        manager.addAnimationController(controller);
    }

    @Override
    public EntityAnimationManager getAnimationManager()
    {
        return manager;
    }
}