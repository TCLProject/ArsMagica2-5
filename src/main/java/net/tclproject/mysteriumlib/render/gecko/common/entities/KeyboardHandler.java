/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package net.tclproject.mysteriumlib.render.gecko.common.entities;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import net.minecraft.client.Minecraft;

public class KeyboardHandler
{
	public static boolean isForwardKeyDown = false;
	public static boolean isBackKeyDown = false;
	public static boolean isQDown = false;

	@SubscribeEvent
	public void onKeyPress(InputEvent.KeyInputEvent event)
	{
		Minecraft mc = Minecraft.getMinecraft();
		if (mc.thePlayer != null)
		{
			isForwardKeyDown = mc.gameSettings.keyBindForward.getIsKeyPressed();
			isBackKeyDown = mc.gameSettings.keyBindBack.getIsKeyPressed();
			isQDown = mc.gameSettings.keyBindDrop.getIsKeyPressed();
		}
	}
}
