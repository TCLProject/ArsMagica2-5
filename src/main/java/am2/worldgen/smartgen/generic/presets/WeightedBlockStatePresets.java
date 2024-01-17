/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package am2.worldgen.smartgen.generic.presets;

import am2.worldgen.smartgen.generic.WeightedBlockState;
import am2.worldgen.smartgen.reccomplexutils.ListPresets;
import com.google.gson.Gson;

/**
 * Created by lukas on 03.03.15.
 */
public class WeightedBlockStatePresets extends ListPresets<WeightedBlockState>
{
    private static WeightedBlockStatePresets instance;

    public static WeightedBlockStatePresets instance()
    {
        return instance != null ? instance : (instance = new WeightedBlockStatePresets());
    }


    @Override
    protected Gson createGson()
    {
        return WeightedBlockState.getGson();
    }

    @Override
    protected Class<WeightedBlockState[]> getType()
    {
        return WeightedBlockState[].class;
    }
}
