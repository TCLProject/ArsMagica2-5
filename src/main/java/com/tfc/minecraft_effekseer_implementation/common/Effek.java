package com.tfc.minecraft_effekseer_implementation.common;

import com.google.common.collect.ImmutableList;
import com.tfc.effekseer4j.EffekseerManager;
import com.tfc.minecraft_effekseer_implementation.FinalizedReference;
import com.tfc.minecraft_effekseer_implementation.common.api.EffekEmitter;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class Effek implements Closeable {
	private final LoaderIndependentIdentifier id;
	private final Function<EffekseerManager, EffekEmitter> newEffect;
	
	private boolean hasClosed = false;
	
	private EffekseerManager manager = new EffekseerManager();
	
	private final Map<LoaderIndependentIdentifier, EffekEmitter> effectsPresent = new HashMap<>();
	private final Map<LoaderIndependentIdentifier, EffekEmitter> effectsFree = new HashMap<>();
	
	public static final FinalizedReference<Supplier<Integer>> widthGetter = new FinalizedReference<>();
	public static final FinalizedReference<Supplier<Integer>> heightGetter = new FinalizedReference<>();
	
	private List<Runnable> preDraw = new ArrayList<>();
	private List<Runnable> postDraw = new ArrayList<>();
	private List<Consumer<Float>> preUpdate = new ArrayList<>();
	
	public Effek(LoaderIndependentIdentifier id, Function<EffekseerManager, EffekEmitter> newEffect, int requestedMaxSprites, boolean srgb) {
		this.id = id;
		this.newEffect = newEffect;
		manager.initialize(requestedMaxSprites, srgb);
		manager.setupWorkerThreads(2);
	}
	
	public void addPreupdateHandler(Consumer<Float> runnable) {
		preUpdate.add(runnable);
	}
	
	public void addPredrawHandler(Runnable runnable) {
		preDraw.add(runnable);
	}
	
	public void addPostdrawHandler(Runnable runnable) {
		postDraw.add(runnable);
	}
	
	public void lock() {
		preDraw = ImmutableList.copyOf(preDraw);
		postDraw = ImmutableList.copyOf(postDraw);
		preUpdate = ImmutableList.copyOf(preUpdate);
	}
	
	public void draw(float[][] cameraMatrix, float[][] projectionMatrix, float delta) {
		if (hasClosed) return;
		manager.setViewport(widthGetter.get().get(), heightGetter.get().get());
		manager.setCameraMatrix(cameraMatrix);
		manager.setProjectionMatrix(projectionMatrix);
		for (Consumer<Float> runnable : preUpdate) runnable.accept(delta);
		manager.update(delta);
		for (Runnable runnable : preDraw) runnable.run();
		manager.draw();
		for (Runnable runnable : postDraw) runnable.run();
	}
	
	public LoaderIndependentIdentifier getId() {
		return id;
	}
	
	@Override
	public void close() {
		if (hasClosed) return;
		hasClosed = true;
		for (EffekEmitter value : effectsFree.values()) value.emitter.stop();
		for (EffekEmitter value : effectsPresent.values()) value.emitter.stop();
		manager.stopEffects();
//		manager.delete();
		manager = null;
	}
	
	public EffekEmitter getOrCreate(String name) {
		if (hasClosed) return null;
		LoaderIndependentIdentifier id = new LoaderIndependentIdentifier(name);
		if (effectsPresent.containsKey(id)) return effectsPresent.get(new LoaderIndependentIdentifier(name));
		else if (effectsFree.containsKey(id)) {
			EffekEmitter emitter = effectsFree.get(id);
			effectsFree.remove(id);
			effectsPresent.put(id, emitter);
			return emitter;
		}
		EffekEmitter emitter = newEffect.apply(manager);
		effectsPresent.put(id, emitter);
		return emitter;
	}
	
	public void markFree(EffekEmitter emitter) {
		if (hasClosed) return;
		AtomicReference<LoaderIndependentIdentifier> idMove = new AtomicReference<>();
		effectsPresent.forEach((id, emitter1)->{
			if (idMove.get() != null) return;
			if (emitter.equals(emitter1)) {
				idMove.set(id);
			}
		});
		if (idMove.get() == null) throw new NullPointerException("Could not find emitter " + emitter.emitter.handle + " for effect type " + id.toString());
		emitter.resetAndHide();
		effectsPresent.remove(idMove.get());
		effectsFree.put(idMove.get(), emitter);
	}
	
	public void delete(EffekEmitter emitter) {
		if (hasClosed) return;
		markFree(emitter);
		emitter.emitter.stop();
		AtomicReference<LoaderIndependentIdentifier> idMove = new AtomicReference<>();
		effectsFree.forEach((id, emitter1)->{
			if (idMove.get() != null) return;
			if (emitter.equals(emitter1)) {
				idMove.set(id);
			}
		});
		emitter.setPaused(true);
		emitter.setVisible(false);
		emitter.setPlayProgress(1f);
		effectsFree.remove(idMove.get());
	}

	public void draw(float delta) {
		if (hasClosed) return;
		manager.setViewport(widthGetter.get().get(), heightGetter.get().get());
		for (Consumer<Float> runnable : preUpdate) runnable.accept(delta);
		manager.update(delta);
		for (Runnable runnable : preDraw) runnable.run();
		manager.draw();
		for (Runnable runnable : postDraw) runnable.run();
	}
}
