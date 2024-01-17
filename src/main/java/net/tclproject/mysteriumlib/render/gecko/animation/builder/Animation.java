/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package net.tclproject.mysteriumlib.render.gecko.animation.builder;

import net.tclproject.mysteriumlib.render.gecko.animation.keyframe.BoneAnimation;
import net.tclproject.mysteriumlib.render.gecko.animation.keyframe.EventKeyFrame;
import net.tclproject.mysteriumlib.render.gecko.animation.keyframe.ParticleEventKeyFrame;

import java.util.ArrayList;
import java.util.List;

/**
 * A specific animation instance
 */
public class Animation
{
	public String animationName;
	public double animationLength;
	public boolean loop = true;
	public List<BoneAnimation> boneAnimations;
	public List<EventKeyFrame<String>> soundKeyFrames = new ArrayList<>();
	public List<ParticleEventKeyFrame> particleKeyFrames = new ArrayList<>();
	public List<EventKeyFrame<List<String>>> customInstructionKeyframes = new ArrayList<>();

}
