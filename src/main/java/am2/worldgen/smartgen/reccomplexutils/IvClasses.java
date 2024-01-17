/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package am2.worldgen.smartgen.reccomplexutils;

import am2.AMCore;

/**
 * Created by lukas on 21.03.16.
 */
public class IvClasses
{
    public static <T> T instantiate(Class<T> clazz)
    {
        T t = null;

        try
        {
            t = clazz.newInstance();
        }
        catch (InstantiationException | IllegalAccessException e)
        {
            AMCore.logger.error(e);
        }

        return t;
    }
}
