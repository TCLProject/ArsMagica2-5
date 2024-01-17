/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package net.tclproject.mysteriumlib.render.gecko.animation.snapshot;

import net.tclproject.mysteriumlib.render.gecko.animation.render.AnimatedModelRenderer;

import java.util.ArrayList;

public class EntityDirtyTracker extends ArrayList<DirtyTracker>
{
	public DirtyTracker get(AnimatedModelRenderer bone)
	{
		return this.stream().filter(x -> x.model.name.equals(bone.name)).findFirst().orElseThrow(ArrayIndexOutOfBoundsException::new);
	}
}
