package com.tfc.minecraft_effekseer_implementation.common.api;

import com.tfc.effekseer4j.EffekseerParticleEmitter;
import com.tfc.minecraft_effekseer_implementation.FinalizedReference;
import com.tfc.minecraft_effekseer_implementation.MEI;
import com.tfc.minecraft_effekseer_implementation.MatrixStack;
import com.tfc.minecraft_effekseer_implementation.vector.Matrix4f;
import com.tfc.minecraft_effekseer_implementation.vector.Vector3f;

import java.util.HashMap;
import java.util.Objects;

/**
 * A more mc implementation friendly wrapper for EffekseerParticleEmitter
 */
public class EffekEmitter {
	// this is intentionally not private/protected, so you can access it if need be
	public final EffekseerParticleEmitter emitter;
//	public Matrix4f translationMatrix = new Matrix4f();
//	public Matrix4f rotationMatrix = new Matrix4f();
//	public Matrix4f scaleMatrix = new Matrix4f();
	
	public EffekEmitter(EffekseerParticleEmitter emitter) {
		this.emitter = emitter;
//		translationMatrix.setIdentity();
//		rotationMatrix.setIdentity();
//		scaleMatrix.setIdentity();
	}
	
	public void resetAndHide() {
		emitter.pause();
		emitter.setVisibility(false);
		emitter.setProgress(0);
//		translationMatrix.setIdentity();
//		rotationMatrix.setIdentity();
//		scaleMatrix.setIdentity();
	}
	
	public void setPaused(boolean paused) {
		if (paused) emitter.pause();
		else emitter.resume();
	}

//	public void scale3f(float x, float y, float z) {
////		emitter.setBaseTransformMatrix(new float[] {x, 0, 0, transx,
////													0, y, 0, transy,
////													0, 0, z, transz,
////													0, 0, 0, 1});
//		scaleMatrix.multiply(Matrix4f.createScaleMatrix(x, y, z));
//	}
//
//	public void translate3f(float x, float y, float z) {
//		translationMatrix.multiply(Matrix4f.createTranslateMatrix((float)x, (float)y, (float)z));
//	}
//
//
//	public void rotate3f(float x, float y, float z) {
//		rotationMatrix.multiply(Vector3f.XP.rotationDegrees(x));
//		rotationMatrix.multiply(Vector3f.YP.rotationDegrees(y));
//		rotationMatrix.multiply(Vector3f.ZP.rotationDegrees(z));
//	}

//	public void applyMatrixChanges(float sx, float sy, float sz, float rx, float ry, float rz, float tx, float ty, float tz) {
////		float[] newMatrix = new float[] {(float)Math.cos(ry) * sx, 0, (float)Math.sin(ry), tx,
////				0, sy, 0, ty,
////				-(float)Math.sin(ry), 0, (float)Math.cos(ry) * sz, tz,
////				0, 0, 0, 1};
//		float[] newMatrix = new float[] {(float)Math.cos(ry) * sx, 0, (float)Math.sin(ry) * sx, tx,
//				0, sy, 0, ty,
//				-(float)Math.sin(ry) * sz, 0, (float)Math.cos(ry) * sz, tz,
//				0, 0, 0, 1};
////		newMatrix.setIdentity();
////		newMatrix.multiply(scaleMatrix); // scale, rotate, translate
////		newMatrix.multiply(rotationMatrix);
////		newMatrix.multiply(translationMatrix);
//		emitter.setBaseTransformMatrix(newMatrix);
////		translationMatrix.setIdentity(); // reset
////		rotationMatrix.setIdentity();
////		scaleMatrix.setIdentity();
//	}

	public void setVisible(boolean visible) {
		emitter.setVisibility(visible);
	}
	
	public void setPlayProgress(float progress) {
		emitter.setProgress(progress);
	}
	
	public void setDynamicInput(int index, float value) {
		emitter.setDynamicInput(index, value);
	}
	
	public float getDynamicInput(int index) {
		return emitter.getDynamicInput(index);
	}
	
	@Override
	public boolean equals(Object object) {
		if (this == object) return true;
		if (object == null || getClass() != object.getClass()) return false;
		EffekEmitter emitter1 = (EffekEmitter) object;
		return Objects.equals(emitter, emitter1.emitter);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(emitter);
	}
	
	public void setPosition(int x, int y, int z) {
		emitter.move(x, y, z);
	}
	
	public void setPosition(double x, double y, double z) {
		emitter.move((float) x - 0.5f, (float) y - 0.5f, (float) z - 0.5f);
	}
	
	public boolean exists() {
		return emitter.exists();
	}
}
