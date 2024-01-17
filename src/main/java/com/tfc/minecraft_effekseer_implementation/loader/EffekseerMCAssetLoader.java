package com.tfc.minecraft_effekseer_implementation.loader;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

import am2.AMCore;
import com.tfc.minecraft_effekseer_implementation.MEI;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.tfc.effekseer4j.Effekseer;
import com.tfc.effekseer4j.EffekseerEffect;
import com.tfc.effekseer4j.enums.DeviceType;
import com.tfc.effekseer4j.enums.TextureType;
import com.tfc.minecraft_effekseer_implementation.common.Effek;
import com.tfc.minecraft_effekseer_implementation.common.Effeks;
import com.tfc.minecraft_effekseer_implementation.common.LoaderIndependentIdentifier;
import com.tfc.minecraft_effekseer_implementation.common.api.EffekEmitter;

import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.util.ResourceLocation;

public class EffekseerMCAssetLoader implements IResourceManagerReloadListener {
	private static final Gson gson = new GsonBuilder().create();
	public static final EffekseerMCAssetLoader INSTANCE = new EffekseerMCAssetLoader();
	private static final Effeks mapHandler = Effeks.getMapHandler();
	
	private static final Logger LOGGER = LogManager.getLogger();
	
	private static final HashMap<String, Consumer<Effek>> effekConsumers = new HashMap<>();
	
	public static void addReloadListener(String effekName, Consumer<Effek> effekConsumer) {
		effekConsumers.put(effekName, effekConsumer);
	}
	
//	@Override
//	protected void apply(Effek objectIn, IResourceManager resourceManagerIn) {
//		
//	}
	
	private static InputStream getStream(IResourceManager manager, ResourceLocation location) {
		try {
			IResource resource = manager.getResource(location);
			if (resource == null) return null;
			else return resource.getInputStream();
		} catch (Throwable err) {
			return null;
		}
	}
	
	private static byte[] readFully(InputStream stream) throws IOException {
		ByteArrayOutputStream stream1 = new ByteArrayOutputStream();
		int b;
		while (((b = stream.read()) != -1)) stream1.write(b);
		byte[] bytes = stream1.toByteArray();
		stream1.close();
		stream1.flush();
		return bytes;
	}
	
	@Override
	public void onResourceManagerReload(IResourceManager p_110549_1_)
    {
		if (Effekseer.getDevice() != DeviceType.OPENGL) {
			LOGGER.info("Initializing Effekseer Library");
			Effekseer.init();
			if (Effekseer.setupForOpenGL()) LOGGER.info("Successfully initalized Effekseer for OpenGL");
			else LOGGER.warn("Something went wrong while setting up Effekseer");
		}
		LOGGER.info("Offloading effeks:");
		mapHandler.markUnsafe(true);
		ArrayList<Effek> effeks = new ArrayList<>();
		for (int i = 0; i < Effeks.count(); i++) effeks.add(mapHandler.get(i));
		mapHandler.clear();
		for (Effek effek : effeks) {
			effek.close();
			LOGGER.info(" Offloaded effek " + effek.getId());
		}
		LOGGER.info("Done offloading effeks");
		
		LOGGER.info("Loading Effekseer assets");
		Iterator iterator = p_110549_1_.getResourceDomains().iterator();
		while (iterator.hasNext())
        {
            String s = (String)iterator.next();

            try
            {
            	for (int i = 0; i < MEI.effekAmount; i++) {
            		ResourceLocation location = new ResourceLocation(s + ":" + "effeks/effeks" + i + ".efk.json");
					List list = p_110549_1_.getAllResources(location);
					ResourceLocation locationHand = null;
					ResourceLocation locationHandThirdPerson = null;
					if (i % 4 == 0) { // every fourth effek has to have a hand effek with it. Don't question it.
						locationHand = new ResourceLocation(s + ":" + "effeks/effeks_hand_firstperson" + i + ".efk.json");
						// unlike the special 1st person hand location, this doesn't hold an in-code purpose, and is only there for standardizing's sake
						locationHandThirdPerson = new ResourceLocation(s + ":" + "effeks/effeks_hand_thirdperson" + i + ".efk.json");
						List listHand = p_110549_1_.getAllResources(locationHand);
						List listHandThirdPerson = p_110549_1_.getAllResources(locationHandThirdPerson);
						list.addAll(listHand);
						list.addAll(listHandThirdPerson);
					}
					Iterator iterator1 = list.iterator();
	                String lastLocation = "firstlocation";
	                int handEffek = 0; // 1 = world effek, 2 = first person hand effek, 3 = third person hand effek
	                while (iterator1.hasNext())
	                {
	                	handEffek++;
	                	LOGGER.info("Loading effek " + location.toString());
	                    IResource resource = (IResource)iterator1.next();
	                    InputStream stream = resource.getInputStream();
	    				byte[] bytes = new byte[stream.available()];
	    				stream.read(bytes);
	    				stream.close();
	    				// read json
	    				EffekseerEffect effect = new EffekseerEffect();
	    				JsonObject object = gson.fromJson(new String(bytes), JsonObject.class);
	    				if (!object.has("effect")) {
	    					LOGGER.warn("Cannot load effek " + location.toString() + " because the \"effect\" entry is missing from the json.");
	    					continue;
	    				}
	    				// load effect
	    				LoaderIndependentIdentifier location0 = new LoaderIndependentIdentifier(object.get("effect").getAsJsonPrimitive().getAsString());
	    				String namespace = location0.namespace();
	    				String path = location0.path();
	    				ResourceLocation location1 = new ResourceLocation(namespace + ":effeks/" + path);
	    				stream = getStream(p_110549_1_, location1);
	    				if (stream == null) {
	    					LOGGER.warn("Effek \"" + location.toString() + "\" failed to load, \"" + location1.toString() + "\" could not be found.");
	    					continue;
	    				}
	    				byte[] bytes1 = readFully(stream);
	    				if (!effect.load(bytes1, bytes1.length, 1)) {
	    					LOGGER.warn("Effek " + location1.toString() + " failed to load, no further info known.");
	    					continue;
	    				}
	    				{ // textures
	    					JsonObject texturesObject = object.has("textures") ? object.getAsJsonObject("textures") : new JsonObject();
	    					for (TextureType value : TextureType.values()) {
	    						for (int index = 0; index < effect.textureCount(value); index++) {
	    							String texturePath = effect.getTexturePath(index, value);
	    							if (texturesObject.has(texturePath))
	    								texturePath = texturesObject.get(texturePath).getAsJsonPrimitive().getAsString();
	    							LoaderIndependentIdentifier textureLocation = new LoaderIndependentIdentifier(texturePath);
	    							path = textureLocation.path();
	    							texturePath = namespace + ":effeks/" + path;
	    							stream = getStream(p_110549_1_, new ResourceLocation(texturePath));
	    							if (stream == null) {
	    								LOGGER.warn("Effek \"" + location.toString() + "\" failed to load, texture \"" + texturePath + "\" is missing.");
	    								continue;
	    							}
	    							bytes1 = readFully(stream);
	    							if (!effect.loadTexture(bytes1, bytes1.length, index, value)) {
	    								LOGGER.warn(texturePath + " seems to be an invalid png image.");
	    							}
	    						}
	    					}
	    				}
	    				{ // models
	    					JsonObject modelsObj = object.has("models") ? object.getAsJsonObject("models") : new JsonObject();
	    					for (int index = 0; index < effect.modelCount(); index++) {
	    						String texturePath = effect.getModelPath(index);
	    						if (modelsObj.has(texturePath)) texturePath = modelsObj.get(texturePath).getAsJsonPrimitive().getAsString();
	    						LoaderIndependentIdentifier textureLocation = new LoaderIndependentIdentifier(texturePath);
	    						path = textureLocation.path();
	    						texturePath = namespace + ":effeks/" + path;
	    						stream = getStream(p_110549_1_, new ResourceLocation(texturePath));
	    						if (stream == null) {
	    							LOGGER.warn("Effek \"" + location.toString() + "\" failed to load, model \"" + texturePath + "\" is missing.");
	    							continue;
	    						}
	    						bytes1 = readFully(stream);
	    						if (!effect.loadModel(bytes1, bytes1.length, index)) {
	    							LOGGER.warn(texturePath + " seems to be an invalid .efkmodel");
	    						}
	    					}
	    				}
	    				{ // curves
	    					JsonObject modelsObj = object.has("curves") ? object.getAsJsonObject("curves") : new JsonObject();
	    					for (int index = 0; index < effect.curveCount(); index++) {
	    						String texturePath = effect.getCurvePath(index);
	    						if (modelsObj.has(texturePath)) texturePath = modelsObj.get(texturePath).getAsJsonPrimitive().getAsString();
	    						LoaderIndependentIdentifier textureLocation = new LoaderIndependentIdentifier(texturePath);
	    						path = textureLocation.path();
	    						texturePath = namespace + ":effeks/" + path;
	    						stream = getStream(p_110549_1_, new ResourceLocation(texturePath));
	    						if (stream == null) {
	    							LOGGER.warn("Effek \"" + location.toString() + "\" failed to load, curve \"" + texturePath + "\" is missing.");
	    							continue;
	    						}
	    						bytes1 = readFully(stream);
	    						if (!effect.loadCurve(bytes1, bytes1.length, index)) {
	    							LOGGER.warn(texturePath + " seems to be an invalid [insert curve file type here].");
	    						}
	    					}
	    				}
	    				{ // materials
	    					JsonObject modelsObj = object.has("materials") ? object.getAsJsonObject("materials") : new JsonObject();
	    					for (int index = 0; index < effect.materialCount(); index++) {
	    						String texturePath = effect.getMaterialPath(index);
	    						if (modelsObj.has(texturePath)) texturePath = modelsObj.get(texturePath).getAsJsonPrimitive().getAsString();
	    						LoaderIndependentIdentifier textureLocation = new LoaderIndependentIdentifier(texturePath);
	    						path = textureLocation.path();
	    						texturePath = namespace + ":effeks/" + path;
	    						stream = getStream(p_110549_1_, new ResourceLocation(texturePath));
	    						if (stream == null) {
	    							LOGGER.warn("Effek \"" + location.toString() + "\" failed to load, material \"" + texturePath + "\" is missing.");
	    							continue;
	    						}
	    						bytes1 = readFully(stream);
	    						if (!effect.loadMaterial(bytes1, bytes1.length, index)) {
	    							LOGGER.warn(texturePath + " seems to be an invalid [insert material file type here].");
	    						}
	    					}
	    				}
	    				
	    				ResourceLocation location2World = new ResourceLocation(location.getResourceDomain() + ":" + location.getResourcePath().substring("effeks/".length(), location.getResourcePath().length() - ".efk.json".length()));
						ResourceLocation location2Hand = null;
						ResourceLocation location2ThirdPersonHand = null;
	    				if (handEffek > 1) { // 2 || 3
							location2Hand = new ResourceLocation(locationHand.getResourceDomain() + ":" + locationHand.getResourcePath().substring("effeks/".length(), locationHand.getResourcePath().length() - ".efk.json".length()));
							 location2ThirdPersonHand = new ResourceLocation(locationHandThirdPerson.getResourceDomain() + ":" + locationHandThirdPerson.getResourcePath().substring("effeks/".length(), locationHandThirdPerson.getResourcePath().length() - ".efk.json".length()));
						}

						ResourceLocation location2 = (handEffek == 1) ? location2World : (handEffek == 2) ? location2Hand : location2ThirdPersonHand;
	    				if (location2.toString().equals(lastLocation)) {
							LOGGER.info("Duplicate! Skipping.");
							continue;
	    				}
	    				lastLocation = location2.toString();
	    				
	    				// register Effek
	    				Effek effek = new Effek(
	    						new LoaderIndependentIdentifier(location2.toString()),
	    						(manager) -> new EffekEmitter(manager.createParticle(effect)),
	    						object.has("maxSpriteCount") ? object.getAsJsonPrimitive("maxSpriteCount").getAsInt() : 100,
	    						object.has("srgb") && object.getAsJsonPrimitive("srgb").getAsBoolean()
	    				);
	    				// allow mod devs to add custom things to their effeks, such as shaders
	    				if (effekConsumers.containsKey(location2.toString())) effekConsumers.get(location2.toString()).accept(effek);
	    				mapHandler.put(location2.toString(), effek);
	    				LOGGER.info("Successfully loaded effek " + location2.toString());
	                }
            	}
            }
            catch (IOException ioexception)
            {
                LOGGER.info("Finished loading resource domain " + s);
            }
            catch (Throwable err) 
            {
            	StringBuilder ex = new StringBuilder(err.getLocalizedMessage()).append("\n");
				for (StackTraceElement element : err.getStackTrace()) ex.append(element.toString()).append("\n");
				LOGGER.fatal(ex);
            }
        }

		LOGGER.info("Done loading Effekseer assets");
		mapHandler.setTimeSinceReload(0);
		mapHandler.markUnsafe(false);
	}
}
