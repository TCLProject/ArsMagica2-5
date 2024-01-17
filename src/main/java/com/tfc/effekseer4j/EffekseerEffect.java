package com.tfc.effekseer4j;

import Effekseer.swig.EffekseerEffectCore;
import com.tfc.effekseer4j.enums.TextureType;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

@SuppressWarnings({"unused", "RedundantSuppression"})
public class EffekseerEffect {
	protected final EffekseerEffectCore core;
	
	private boolean isLoaded = false;
	
	public EffekseerEffect() {
		core = new EffekseerEffectCore();
	}
	
	public void delete() {
		core.delete();
	}
	
	public boolean load(InputStream stream, int length, float amplifier) throws IOException {
		byte[] bytes = new byte[stream.available()];
		stream.read(bytes);
		stream.close();
		return load(bytes, length, amplifier);
	}
	
	public boolean load(byte[] data, int length, float amplifier) {
		isLoaded = core.Load(data, length, amplifier);
		return isLoaded;
	}
	
	public boolean isLoaded() {
		return isLoaded;
	}
	
	public boolean loadTexture(InputStream stream, int length, int index, TextureType type) throws IOException {
		byte[] bytes = new byte[stream.available()];
		stream.read(bytes);
		stream.close();
		return loadTexture(bytes, length, index, type);
	}
	
	public boolean loadTexture(byte[] data, int length, int index, TextureType type) {
		return core.LoadTexture(data, length, index, type.unwrap());
	}
	
	public boolean loadCurve(InputStream stream, int length, int index) throws IOException {
		byte[] bytes = new byte[stream.available()];
		stream.read(bytes);
		stream.close();
		return loadCurve(bytes, length, index);
	}
	
	public boolean loadCurve(byte[] data, int length, int index) {
		return core.LoadCurve(data, length, index);
	}
	
	public boolean loadMaterial(InputStream stream, int length, int index) throws IOException {
		byte[] bytes = new byte[stream.available()];
		stream.read(bytes);
		stream.close();
		return loadMaterial(bytes, length, index);
	}
	
	public boolean loadMaterial(byte[] data, int length, int index) {
		return core.LoadMaterial(data, length, index);
	}
	
	public boolean loadModel(InputStream stream, int length, int index) throws IOException {
		byte[] bytes = new byte[stream.available()];
		stream.read(bytes);
		stream.close();
		return loadModel(bytes, length, index);
	}
	
	public boolean loadModel(byte[] data, int length, int index) {
		return core.LoadModel(data, length, index);
	}
	
	public boolean isModelLoaded(int index) {
		return core.HasModelLoaded(index);
	}
	
	public boolean isCurveLoaded(int index) {
		return core.HasCurveLoaded(index);
	}
	
	public boolean isMaterialLoaded(int index) {
		return core.HasMaterialLoaded(index);
	}
	
	public boolean isTextureLoaded(int index, TextureType type) {
		return core.HasTextureLoaded(index, type.unwrap());
	}
	
	public int curveCount() {
		return core.GetCurveCount();
	}
	
	public int modelCount() {
		return core.GetModelCount();
	}
	
	public int materialCount() {
		return core.GetMaterialCount();
	}
	
	public int textureCount(TextureType type) {
		return core.GetTextureCount(type.unwrap());
	}
	
	public int textureCount() {
		int amt = 0;
		for (TextureType value : TextureType.values()) amt += core.GetTextureCount(value.unwrap());
		return amt;
	}
	
	public String getTexturePath(int index, TextureType type) {
		return core.GetTexturePath(index, type.unwrap());
	}
	
	public String getCurvePath(int index) {
		return core.GetCurvePath(index);
	}
	
	public String getMaterialPath(int index) {
		return core.GetMaterialPath(index);
	}
	
	public String getModelPath(int index) {
		return core.GetModelPath(index);
	}
	
	public int minTerm() {
		return core.GetTermMin();
	}
	
	public int maxTerm() {
		return core.GetTermMax();
	}
	
	public EffekseerEffectCore unwrap() {
		return core;
	}
}
