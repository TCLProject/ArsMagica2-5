package com.tfc.minecraft_effekseer_implementation;

public class FinalizedReference<T> {
	T obj = null;
	
	public void set(T newVal) {
		if (obj == null) obj = newVal;
		else throw new RuntimeException(new IllegalAccessException("Cannot set a final value"));
	}
	
	public T get() {
		return obj;
	}
}
