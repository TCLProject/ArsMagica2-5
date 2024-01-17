package com.tfc.effekseer4j.enums;

import Effekseer.swig.EffekseerCoreDeviceType;

@SuppressWarnings({"unused", "RedundantSuppression"})
public enum DeviceType {
	UNKNOWN(EffekseerCoreDeviceType.Unknown),
	OPENGL(EffekseerCoreDeviceType.OpenGL);
	
	EffekseerCoreDeviceType type;
	
	DeviceType(EffekseerCoreDeviceType type) {
		this.type = type;
	}
	
	@Override
	public String toString() {
		return type.toString();
	}
	
	public int ord() {
		return type.swigValue();
	}
	
	public static DeviceType fromOrd(int ord) {
		for (DeviceType value : DeviceType.values())
			if (value.ord() == ord)
				return value;
		return null;
	}
	
	public EffekseerCoreDeviceType unwrap() {
		return type;
	}
}
