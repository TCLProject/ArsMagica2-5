package net.tclproject.mysteriumlib.render.dae.hea3ven.colladamodel.client.model.lib;


import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.AdvancedModelLoader;

public class ModelManager implements IResourceManagerReloadListener {

    private static Map<ResourceLocation, IModelAnimationCustom> models = new HashMap<ResourceLocation, IModelAnimationCustom>();

    public ModelManager() {
    }

    @Override
    public void onResourceManagerReload(IResourceManager var1) {
        for (ResourceLocation resource : models.keySet()) {
            models.put(resource, (IModelAnimationCustom) AdvancedModelLoader
                    .loadModel(resource));
        }
    }

    public IModelAnimationCustom getModel(ResourceLocation resource) {
        if (!models.containsKey(resource))
            models.put(resource, (IModelAnimationCustom) AdvancedModelLoader
                    .loadModel(resource));
        return models.get(resource);
    }

}