/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package net.tclproject.mysteriumlib.render.gecko.common.entities.entity.specific;

import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.world.World;
import net.tclproject.mysteriumlib.render.gecko.animation.builder.AnimationBuilder;
import net.tclproject.mysteriumlib.render.gecko.animation.controller.AnimationController;
import net.tclproject.mysteriumlib.render.gecko.animation.controller.EntityAnimationController;
import net.tclproject.mysteriumlib.render.gecko.entity.IAnimatedEntity;
import net.tclproject.mysteriumlib.render.gecko.event.AnimationTestEvent;
import net.tclproject.mysteriumlib.render.gecko.manager.EntityAnimationManager;

public class StingrayTestEntity extends EntityMob implements IAnimatedEntity
{
	public EntityAnimationManager animationControllers = new EntityAnimationManager();
	private AnimationController wingController = new EntityAnimationController(this, "wingController", 1, this::wingAnimationPredicate);

	@Override
	public EntityAnimationManager getAnimationManager()
	{
		return animationControllers;
	}

	public StingrayTestEntity(World worldIn)
	{
		super(worldIn);
		registerAnimationControllers();
	}

	public void registerAnimationControllers()
	{
		if(worldObj.isRemote)
		{
			wingController.setAnimation(new AnimationBuilder().addAnimation("swimmingAnimation"));
			this.animationControllers.addAnimationController(wingController);
		}
	}

	public boolean wingAnimationPredicate(AnimationTestEvent<? extends Entity> event)
	{
		Entity entity = event.getEntity();
		World entityWorld = entity.worldObj;
		if(entityWorld.rainingStrength > 0)
		{
			wingController.transitionLengthTicks = 40;
			wingController.setAnimation(new AnimationBuilder().addAnimation("thirdAnimation"));
		}
		else {
			wingController.transitionLengthTicks = 40;
			wingController.setAnimation(new AnimationBuilder().addAnimation("secondAnimation"));
		}
		return true;
	}


}
