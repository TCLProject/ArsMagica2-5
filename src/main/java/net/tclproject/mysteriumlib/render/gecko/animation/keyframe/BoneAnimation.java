/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package net.tclproject.mysteriumlib.render.gecko.animation.keyframe;


public class BoneAnimation
{
	public String boneName;
	public VectorKeyFrameList<KeyFrame<Double>> rotationKeyFrames;
	public VectorKeyFrameList<KeyFrame<Double>> positionKeyFrames;
	public VectorKeyFrameList<KeyFrame<Double>> scaleKeyFrames;
}
