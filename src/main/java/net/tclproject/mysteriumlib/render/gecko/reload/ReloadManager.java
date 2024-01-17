package net.tclproject.mysteriumlib.render.gecko.reload;

import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.tclproject.mysteriumlib.render.gecko.animation.controller.AnimationController;

import java.util.ArrayList;
import java.util.List;

public class ReloadManager
{
	private static List<IResourceManagerReloadListener> registeredModels = new ArrayList<>();
	private static List<AnimationController> registeredAnimationControllers = new ArrayList<>();


	public static void registerModel(IResourceManagerReloadListener model)
	{
		registeredModels.add(model);
	}

	public static void registerAnimationController(AnimationController controller)
	{
		registeredAnimationControllers.add(controller);
	}

	public static List<IResourceManagerReloadListener> getRegisteredModels()
	{
		return registeredModels;
	}

	public static List<AnimationController> getRegisteredAnimationControllers()
	{
		return registeredAnimationControllers;
	}
}
