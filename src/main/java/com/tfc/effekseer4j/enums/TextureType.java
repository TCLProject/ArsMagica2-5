package com.tfc.effekseer4j.enums;

import Effekseer.swig.EffekseerTextureType;

@SuppressWarnings({"unused", "RedundantSuppression"})
public enum TextureType {
	COLOR(EffekseerTextureType.Color),
	NORMAL(EffekseerTextureType.Normal),
	DISTORTION(EffekseerTextureType.Distortion);
	
	EffekseerTextureType type;
	
	TextureType(EffekseerTextureType type) {
		this.type = type;
	}
	
	@Override
	public String toString() {
		return type.toString();
	}
	
	public int ord() {
		return type.swigValue();
	}
	
	public static TextureType fromOrd(int ord) {
		for (TextureType value : values())
			if (value.ord() == ord)
				return value;
		return null;
	}
	
	public EffekseerTextureType unwrap() {
		return type;
	}
}
