package com.tfc.minecraft_effekseer_implementation;

import cpw.mods.fml.common.Loader;

public class ModDetectionUtil {
	
	public static boolean isLoaded(String name) {
		return Loader.isModLoaded(name);
	}
}
