/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package net.tclproject.mysteriumlib.render.gecko.animation.render;

import com.google.common.collect.Lists;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.tclproject.mysteriumlib.render.gecko.animation.snapshot.BoneSnapshot;

import java.util.List;

import static org.lwjgl.opengl.GL11.*;

public class AnimatedModelRenderer extends ModelRenderer
{
	private BoneSnapshot boneSnapshot;
	public String currentAnimationName;

	public float scaleValueX = 1;
	public float scaleValueY = 1;
	public float scaleValueZ = 1;

	public float positionOffsetX = 0;
	public float positionOffsetY = 0;
	public float positionOffsetZ = 0;

	public String name;
	public BoneSnapshot initialSnapshot;
	public List<AnimatedModelRenderer> childModelsAnimated;

	public AnimatedModelRenderer(ModelBase model)
	{
		super(model);
	}

	public void addChild(AnimatedModelRenderer renderer)
	{
		if (this.childModelsAnimated == null)
			this.childModelsAnimated = Lists.<AnimatedModelRenderer>newArrayList();

		this.childModelsAnimated.add(renderer);
	}

	public void setModelRendererName(String modelRendererName)
	{
		this.name = modelRendererName;
	}

	public AnimatedModelRenderer setTextureOffset(int x, int y)
	{
		this.textureOffsetX = x;
		this.textureOffsetY = y;
		return this;
	}

	public void addBox(float x, float y, float z, float dx, float dy, float dz, float inflate)
	{
		ModelBox box = new ModelBox(this, this.textureOffsetX, this.textureOffsetY, x, y, z, Math.round(dx),
				Math.round(dy), Math.round(dz), inflate);
		this.cubeList.add(box);

	}

	@Override
	public void render(float scale)
	{
		if (!this.isHidden)
			if (this.showModel) {
				if (!this.compiled)
					this.compileDisplayList(scale);

				glTranslatef(this.offsetX, this.offsetY, this.offsetZ);

				glPushMatrix();

				glTranslatef(((rotationPointX + positionOffsetX) / 16F),
						((rotationPointY - positionOffsetY) / 16F),
						((rotationPointZ + positionOffsetZ) / 16F));
				glScalef(scaleValueX, scaleValueY, scaleValueZ);

				if (this.rotateAngleZ != 0.0F)
					glRotatef(this.rotateAngleZ * (180F / (float) Math.PI), 0.0F, 0.0F, 1.0F);

				if (this.rotateAngleY != 0.0F)
					glRotatef(this.rotateAngleY * (180F / (float) Math.PI), 0.0F, 1.0F, 0.0F);

				if (this.rotateAngleX != 0.0F)
					glRotatef(this.rotateAngleX * (180F / (float) Math.PI), 1.0F, 0.0F, 0.0F);

				glCallList(this.displayList);

				if (this.childModelsAnimated != null)
					for (AnimatedModelRenderer animatedModelRenderer : this.childModelsAnimated)
						animatedModelRenderer.render(scale);

				glPopMatrix();
				glTranslatef(-this.offsetX, -this.offsetY, -this.offsetZ);
			}
	}

	public void saveInitialSnapshot()
	{
		this.initialSnapshot = new BoneSnapshot(this);
	}

	public BoneSnapshot getInitialSnapshot()
	{
		return this.initialSnapshot;
	}

	public BoneSnapshot saveSnapshot()
	{
		return new BoneSnapshot(this);
	}
}