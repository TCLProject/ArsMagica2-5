package com.tfc.minecraft_effekseer_implementation.common;

import java.util.HashMap;
import java.util.function.BiConsumer;

/**
 * Fully loader independent registry style class
 * everything is registered using asset loaders, you have no reason to be registering stuff through code
 * instead, just use the "get" method with a string which represents the identifier/resource location of your effect's name
 * for example: Effeks.get("mc_effekseer_impl:example")
 */
public class Effeks {
	protected static Effeks MapHandler = new Effeks();
	
	private static boolean hasConstructedMapHandler = false;
	
	protected Effeks() {
		if (!hasConstructedMapHandler) {
			hasConstructedMapHandler = true;
		} else {
			throw new RuntimeException(new IllegalAccessException("what the heck are you doing?"));
		}
	}
	
	private static final HashMap<LoaderIndependentIdentifier, Effek> effeks = new HashMap<>();
	
	public static void forEach(BiConsumer<String, Effek> effekConsumer) {
//		effeks.forEach((name, effect)->{
//			effekConsumer.accept(name.toString(), effect);
//		});
		effeks.entrySet().stream().filter(entry -> !entry.getKey().toString().contains("_hand")).forEach((entry)->{
			effekConsumer.accept(entry.getKey().toString(), entry.getValue());
		});
	}

	public static void forEachHand(BiConsumer<String, Effek> effekConsumer) {
		effeks.entrySet().stream().filter(entry -> entry.getKey().toString().contains("_hand_firstperson")).forEach((entry)->{
			effekConsumer.accept(entry.getKey().toString(), entry.getValue());
		});
	}

	public static void forEachThirdPersonHand(BiConsumer<String, Effek> effekConsumer) {
		effeks.entrySet().stream().filter(entry -> entry.getKey().toString().contains("_hand_thirdperson")).forEach((entry)->{
			effekConsumer.accept(entry.getKey().toString(), entry.getValue());
		});
	}
	
	private static boolean isReloading = false;
	private static int timeSinceReload = 0;
	
	public void markUnsafe(boolean reloading) {
		isReloading = reloading;
	}
	
	public void setTimeSinceReload(int time) {
		timeSinceReload = time;
	}
	
	public void put(String name, Effek effect) {
		effeks.put(new LoaderIndependentIdentifier(name), effect);
	}
	
	public static int count() {
		return effeks.size();
	}
	
	public Effek get(int index) {
		return effeks.values().toArray(new Effek[0])[index];
	}
	
	public static Effek get(String s) {
		if (isReloading || (timeSinceReload < 3)) return null;
		return effeks.get(new LoaderIndependentIdentifier(s));
	}
	
	public void clear() {
		effeks.clear();
	}
	
	public static Effeks getMapHandler() {
		if (!hasConstructedMapHandler) {
			hasConstructedMapHandler = true;
			return MapHandler;
		} else if (MapHandler != null) {
			Effeks handler = MapHandler;
			MapHandler = null;
			return handler;
		} else {
			throw new RuntimeException("no.");
		}
	}
	
	public static int getTimeSinceReload() {
		return timeSinceReload;
	}
}
