package com.tfc.minecraft_effekseer_implementation.common.api;

import com.tfc.minecraft_effekseer_implementation.MatrixStack;

public class EffekRenderInfo {

    public long lastFrame;
    public int ticksInHand;
    public boolean active;
    public MatrixStack mat;

    public EffekRenderInfo(int frame, int ticks, MatrixStack matrix, boolean isActive) {
       lastFrame = frame;
       ticksInHand = ticks;
       mat = matrix;
       active = isActive;
    }
}
