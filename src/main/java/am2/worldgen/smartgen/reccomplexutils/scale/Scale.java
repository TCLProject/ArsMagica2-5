/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package am2.worldgen.smartgen.reccomplexutils.scale;

/**
 * Created by lukas on 01.09.15.
 */
public interface Scale
{
    float in(float val);

    float out(float val);
}
