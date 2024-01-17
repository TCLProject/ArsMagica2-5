package com.tfc.minecraft_effekseer_implementation.common;

import com.tfc.minecraft_effekseer_implementation.FinalizedReference;

import java.util.function.BiFunction;
import java.util.function.Function;

public class LoaderIndependentIdentifier {
	private final Object wrapped;
	
	public static final FinalizedReference<Function<String, Object>> rlConstructor1 = new FinalizedReference<>();
	public static final FinalizedReference<BiFunction<String, String, Object>> rlConstructor2 = new FinalizedReference<>();
	
	public LoaderIndependentIdentifier(Object wrapped) {
		this.wrapped = wrapped;
	}
	
	public LoaderIndependentIdentifier(String location) {
		wrapped = rlConstructor1.get().apply(location);
	}
	
	public LoaderIndependentIdentifier(String namespace, String path) {
		wrapped = rlConstructor2.get().apply(namespace, path);
	}
	
	@Override
	public String toString() {
		return wrapped.toString();
	}
	
	@Override
	public boolean equals(Object object) {
		return
				wrapped.equals(object) ||
						wrapped.equals(((LoaderIndependentIdentifier)object).wrapped)
				;
	}
	
	@Override
	public int hashCode() {
		return wrapped.hashCode();
	}
	
	public String namespace() {
		return wrapped.toString().split(":")[0];
	}
	
	public String path() {
		return wrapped.toString().split(":")[1];
	}
}
