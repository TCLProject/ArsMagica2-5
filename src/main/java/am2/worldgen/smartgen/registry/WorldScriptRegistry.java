/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package am2.worldgen.smartgen.registry;

import am2.worldgen.smartgen.generic.maze.WorldScript;
import am2.worldgen.smartgen.reccomplexutils.NBTStringTypeRegistry;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import java.util.Set;

/**
 * Created by lukas on 14.09.15.
 */
public class WorldScriptRegistry extends NBTStringTypeRegistry<WorldScript>
{
    public static final WorldScriptRegistry INSTANCE = new WorldScriptRegistry("script", "id");

    public WorldScriptRegistry(String objectKey, String typeKey)
    {
        super(objectKey, typeKey);
    }

    public WorldScriptRegistry()
    {
    }
}
