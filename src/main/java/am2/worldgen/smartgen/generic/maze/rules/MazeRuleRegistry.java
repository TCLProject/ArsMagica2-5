/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package am2.worldgen.smartgen.generic.maze.rules;

import am2.worldgen.smartgen.reccomplexutils.NBTStringTypeRegistry;

/**
 * Created by lukas on 21.03.16.
 */
public class MazeRuleRegistry extends NBTStringTypeRegistry<MazeRule>
{
    public static final MazeRuleRegistry INSTANCE = new MazeRuleRegistry("rule", "type");

    public MazeRuleRegistry(String objectKey, String typeKey)
    {
        super(objectKey, typeKey);
    }

    public MazeRuleRegistry()
    {
    }
}
