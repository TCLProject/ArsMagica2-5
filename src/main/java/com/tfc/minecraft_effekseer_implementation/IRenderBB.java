package com.tfc.minecraft_effekseer_implementation;

import net.minecraft.util.AxisAlignedBB;

// Entities that have a custom bounding box for Effek render (their effeks are much bigger than they are), must implement this.
public interface IRenderBB {
    public AxisAlignedBB getRenderBoundingBox();
}
