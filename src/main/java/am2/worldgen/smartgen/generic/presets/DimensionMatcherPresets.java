/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package am2.worldgen.smartgen.generic.presets;

import am2.worldgen.smartgen.generic.DimensionGenerationInfo;
import am2.worldgen.smartgen.reccomplexutils.ListPresets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by lukas on 26.02.15.
 */
public class DimensionMatcherPresets extends ListPresets<DimensionGenerationInfo>
{
    private static DimensionMatcherPresets instance;

    public static DimensionMatcherPresets instance()
    {
        return instance != null ? instance : (instance = new DimensionMatcherPresets());
    }

    @Override
    protected Gson createGson()
    {
        return new GsonBuilder().registerTypeAdapter(DimensionGenerationInfo.class, new DimensionGenerationInfo.Serializer()).create();
    }

    @Override
    protected Class<DimensionGenerationInfo[]> getType()
    {
        return DimensionGenerationInfo[].class;
    }
}
