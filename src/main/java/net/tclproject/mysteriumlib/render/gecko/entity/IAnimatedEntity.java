/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */
package net.tclproject.mysteriumlib.render.gecko.entity;

import net.tclproject.mysteriumlib.render.gecko.manager.EntityAnimationManager;

/**
 * This interface must be applied to any Entity that uses an AnimatedEntityModel
 */
public interface IAnimatedEntity
{
	/**
	 * This method MUST return an Animation Manager, otherwise no animations will be played.
	 *
	 * @return the animation controllers
	 */
	EntityAnimationManager getAnimationManager();
}
