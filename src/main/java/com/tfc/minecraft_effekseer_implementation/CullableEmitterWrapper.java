package com.tfc.minecraft_effekseer_implementation;

import com.tfc.minecraft_effekseer_implementation.common.api.EffekEmitter;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;

public class CullableEmitterWrapper {

	private long lasttime = 0;
	private boolean culled = false;
	private boolean outOfCamera = false;

	public EffekEmitter emitter;
	public Entity entity;

	public CullableEmitterWrapper(EffekEmitter e) {
		emitter = e;
	}

	public CullableEmitterWrapper(Entity entity1) {
		entity = entity1;
	}

	public boolean hasEmitter() {
		return emitter != null;
	}

	public void setTimeout() {
		lasttime = System.currentTimeMillis() + 1000;
	}

	public boolean isForcedVisible() {
		return lasttime > System.currentTimeMillis();
	}

	public void setCulled(boolean value) {
//		if (value || this.culled) { // if it's setting it to true (to cull), OR simply undoing what it has done (setting it to false), to avoid unnecessary rendering. Possibly needs testing
			this.culled = value;
//			emitter.setVisible(!value);
//			emitter.setPaused(value);
			if (!value) {
				setTimeout();
			}
//		}
	}

	public boolean isCulled() {
		return culled;
	}

	public void setOutOfCamera(boolean value) {
		this.outOfCamera = value;
	}

	public boolean isOutOfCamera() {
		return outOfCamera;
	}
}
