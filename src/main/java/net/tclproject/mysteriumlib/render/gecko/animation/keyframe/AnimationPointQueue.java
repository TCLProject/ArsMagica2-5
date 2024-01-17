/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package net.tclproject.mysteriumlib.render.gecko.animation.keyframe;

import net.tclproject.mysteriumlib.render.gecko.animation.render.AnimatedModelRenderer;

import java.util.LinkedList;

/**
 * An animation point queue holds a queue of Animation Points which are used in the AnimatedEntityModel to lerp between values
 */
public class AnimationPointQueue extends LinkedList<AnimationPoint>
{
	public AnimatedModelRenderer model;
}
