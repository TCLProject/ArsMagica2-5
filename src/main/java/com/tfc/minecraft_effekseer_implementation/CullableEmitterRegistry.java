package com.tfc.minecraft_effekseer_implementation;

import com.google.common.collect.MapMaker;
import com.tfc.minecraft_effekseer_implementation.common.api.EffekEmitter;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.chunk.Chunk;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

public class CullableEmitterRegistry {
//    public static Map<Entity, CullableEntityWrapper> entityWrappers = new HashMap<>();
//    public static Map<TileEntity, CullableEntityWrapper> tileWrappers = new HashMap<>();
    private static ConcurrentMap<EffekEmitter, CullableEmitterWrapper> effekWrappers = new MapMaker().weakKeys().concurrencyLevel(3).makeMap();
    private static ConcurrentMap<Entity, CullableEmitterWrapper> entityWrappers = new MapMaker().weakKeys().concurrencyLevel(3).makeMap();

    public static CullableEmitterWrapper getWrapper(EffekEmitter e) {
        if (!effekWrappers.containsKey(e)) effekWrappers.put(e, new CullableEmitterWrapper(e));
        return effekWrappers.get(e);
    }

    public static CullableEmitterWrapper getWrapper(Entity e) {
        if (!entityWrappers.containsKey(e)) entityWrappers.put(e, new CullableEmitterWrapper(e));
        return entityWrappers.get(e);
    }
}
