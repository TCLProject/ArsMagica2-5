/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package net.tclproject.mysteriumlib.render.gecko.common.entities.client.renderer.entity;


import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.tclproject.mysteriumlib.render.gecko.common.entities.client.renderer.model.StingrayModel;

import javax.annotation.Nullable;

public class StingrayRenderer extends RenderLiving
{
	public StingrayRenderer()
	{
		super(new StingrayModel(), 0.5F);
	}

	@Nullable
	@Override
	public ResourceLocation getEntityTexture(Entity entity)
	{
		return new ResourceLocation("arsmagica2" +  ":textures/model/entity/stingray.png");
	}
}