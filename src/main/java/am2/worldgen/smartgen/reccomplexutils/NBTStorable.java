/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package am2.worldgen.smartgen.reccomplexutils;

import net.minecraft.nbt.NBTBase;

/**
 * Created by lukas on 30.03.15.
 */
public interface NBTStorable
{
    NBTBase writeToNBT();
}
