package com.tfc.effekseer4j;

import Effekseer.swig.EffekseerManagerCore;

import java.util.Objects;

@SuppressWarnings({"unused", "RedundantSuppression"})
public class EffekseerManager {
	protected final EffekseerManagerCore core;
	
	protected EffekseerManager(EffekseerManagerCore core) {
		this.core = core;
	}
	
	public EffekseerManager() {
		core = new EffekseerManagerCore();
	}
	
	public boolean initialize(int maxSprites, boolean srgb) {
		return core.Initialize(maxSprites, srgb);
	}
	
	public boolean initialize(int maxSprites) {
		return core.Initialize(maxSprites);
	}
	
	public void update(float delta) {
		core.Update(delta);
	}
	
	public void startUpdate() {
		core.BeginUpdate();
	}
	
	public void endUpdate() {
		core.EndUpdate();
	}
	
	public void delete() {
		core.delete();
	}
	
	public EffekseerParticleEmitter createParticle(EffekseerEffect effect) {
		int handle = core.Play(effect.core);
		return new EffekseerParticleEmitter(handle, this);
	}
	
	public void stopEffects() {
		core.StopAllEffects();
	}
	
	public void draw() {
		drawBack();
		drawFront();
	}
	
	public void drawBack() {
		core.DrawBack();
	}
	
	public void drawFront() {
		core.DrawFront();
	}
	
	public void setViewport(int width, int height) {
		core.SetViewProjectionMatrixWithSimpleWindow(width, height);
	}
	
	public void setupWorkerThreads(int count) {
		core.LaunchWorkerThreads(count);
	}
	
	public void setCameraMatrix(float[] matrix) {
		core.SetCameraMatrix(
				matrix[0], matrix[1], matrix[2], matrix[3],
				matrix[4], matrix[5], matrix[6], matrix[7],
				matrix[8], matrix[9], matrix[10], matrix[11],
				matrix[12], matrix[13], matrix[14], matrix[15]
		);
	}
	
	public void setCameraMatrix(float[][] matrix) {
		byte indx = 0;
		core.SetCameraMatrix(
				matrix[indx][0], matrix[indx][1], matrix[indx][2], matrix[indx++][3],
				matrix[indx][0], matrix[indx][1], matrix[indx][2], matrix[indx++][3],
				matrix[indx][0], matrix[indx][1], matrix[indx][2], matrix[indx++][3],
				matrix[indx][0], matrix[indx][1], matrix[indx][2], matrix[indx][3]
		);
	}
	
	public void setProjectionMatrix(float[] matrix) {
		core.SetProjectionMatrix(
				matrix[0], matrix[1], matrix[2], matrix[3],
				matrix[4], matrix[5], matrix[6], matrix[7],
				matrix[8], matrix[9], matrix[10], matrix[11],
				matrix[12], matrix[13], matrix[14], matrix[15]
		);
	}
	
	public void setProjectionMatrix(float[][] matrix) {
		byte indx = 0;
		core.SetProjectionMatrix(
				matrix[indx][0], matrix[indx][1], matrix[indx][2], matrix[indx++][3],
				matrix[indx][0], matrix[indx][1], matrix[indx][2], matrix[indx++][3],
				matrix[indx][0], matrix[indx][1], matrix[indx][2], matrix[indx++][3],
				matrix[indx][0], matrix[indx][1], matrix[indx][2], matrix[indx][3]
		);
	}
	
	@Override
	public boolean equals(Object object) {
		if (!(object instanceof EffekseerManager)) return false;
		return EffekseerManagerCore.getCPtr(core) == EffekseerManagerCore.getCPtr(((EffekseerManager) object).core);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(core);
	}
	
	public EffekseerManagerCore unwrap() {
		return core;
	}
}
