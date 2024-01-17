package com.tfc.effekseer4j;

import Effekseer.swig.EffekseerBackendCore;
import com.tfc.effekseer4j.enums.DeviceType;
import cz.adamh.utils.NativeUtils;

import java.io.IOException;
import java.util.Objects;

@SuppressWarnings({"unused", "RedundantSuppression"})
public class Effekseer {
	private final EffekseerBackendCore core;
	
	public Effekseer() {
		core = new EffekseerBackendCore();
	}
	
	public void delete() {
		core.delete();
	}
	
	public static DeviceType getDevice() {
		return DeviceType.fromOrd(EffekseerBackendCore.GetDevice().swigValue());
	}
	
	public static boolean setupForOpenGL() {
		return EffekseerBackendCore.InitializeAsOpenGL();
	}
	
	public static void finish() {
		EffekseerBackendCore.Terminate();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Effekseer)) return false;
		return EffekseerBackendCore.getCPtr(core) == EffekseerBackendCore.getCPtr(((Effekseer) obj).core);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(core);
	}
	
	public EffekseerBackendCore unwrap() {
		return core;
	}
	
	public static void init(){}
}
