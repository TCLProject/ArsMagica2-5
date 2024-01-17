/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package am2.worldgen.smartgen.generic.transformers;

import am2.worldgen.smartgen.reccomplexutils.IBlockState;
import am2.worldgen.smartgen.reccomplexutils.NBTStorable;
import am2.worldgen.smartgen.struct.info.StructureLoadContext;
import am2.worldgen.smartgen.struct.info.StructurePrepareContext;
import am2.worldgen.smartgen.struct.info.StructureSpawnContext;
import ivorius.ivtoolkit.tools.IvWorldData;
import net.minecraft.nbt.NBTBase;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

/**
 * Created by lukas on 25.05.14.
 */
public interface Transformer<S extends NBTStorable>
{
    String getDisplayString();

//    TableDataSource tableDataSource(TableNavigator navigator, TableDelegate delegate);

    S prepareInstanceData(StructurePrepareContext context);

    S loadInstanceData(StructureLoadContext context, NBTBase nbt);

    boolean skipGeneration(S instanceData, IBlockState state);

    void transform(S instanceData, Phase phase, StructureSpawnContext context, IvWorldData worldData, List<Pair<Transformer, NBTStorable>> transformers);

    boolean generatesInPhase(S instanceData, Phase phase);

    enum Phase
    {
        BEFORE,
        AFTER
    }
}
