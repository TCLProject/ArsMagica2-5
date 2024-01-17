package net.tclproject.mysteriumlib.render.gecko.common.entities.entity.specific;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.world.World;
import net.tclproject.mysteriumlib.render.gecko.animation.builder.AnimationBuilder;
import net.tclproject.mysteriumlib.render.gecko.animation.controller.AnimationController;
import net.tclproject.mysteriumlib.render.gecko.animation.controller.EntityAnimationController;
import net.tclproject.mysteriumlib.render.gecko.entity.IAnimatedEntity;
import net.tclproject.mysteriumlib.render.gecko.event.AnimationTestEvent;
import net.tclproject.mysteriumlib.render.gecko.manager.EntityAnimationManager;

public class BrownEntity extends EntityMob implements IAnimatedEntity
{
	EntityAnimationManager manager = new EntityAnimationManager();
	AnimationController controller = new EntityAnimationController(this, "controller", 30, this::predicate);

	private <ENTITY extends Entity> boolean predicate(AnimationTestEvent<ENTITY> event)
	{
		controller.setAnimation(new AnimationBuilder().addAnimation("crawling", true));
		return true;
	}

	public BrownEntity(World worldIn)
	{
		super(worldIn);
		registerAnimationControllers();
	}

	public void registerAnimationControllers()
	{
		if (worldObj.isRemote)
		{
			controller.setAnimation(new AnimationBuilder().addAnimation("running"));
			this.manager.addAnimationController(controller);
		}
	}

	@Override
	public EntityAnimationManager getAnimationManager()
	{
		return manager;
	}
}
