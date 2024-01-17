/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package am2.worldgen.smartgen.generic.presets;

import am2.worldgen.smartgen.generic.BiomeGenerationInfo;
import am2.worldgen.smartgen.reccomplexutils.ListPresets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by lukas on 26.02.15.
 */
public class BiomeMatcherPresets extends ListPresets<BiomeGenerationInfo>
{
    private static BiomeMatcherPresets instance;

    public static BiomeMatcherPresets instance()
    {
        return instance != null ? instance : (instance = new BiomeMatcherPresets());
    }

    @Override
    protected Gson createGson()
    {
        return new GsonBuilder().registerTypeAdapter(BiomeGenerationInfo.class, new BiomeGenerationInfo.Serializer()).create();
    }

    @Override
    protected Class<BiomeGenerationInfo[]> getType()
    {
        return BiomeGenerationInfo[].class;
    }
}
