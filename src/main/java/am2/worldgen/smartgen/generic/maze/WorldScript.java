/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package am2.worldgen.smartgen.generic.maze;

import am2.worldgen.smartgen.reccomplexutils.NBTStorable;
import am2.worldgen.smartgen.struct.info.StructureLoadContext;
import am2.worldgen.smartgen.struct.info.StructurePrepareContext;
import am2.worldgen.smartgen.struct.info.StructureSpawnContext;
import ivorius.ivtoolkit.blocks.BlockCoord;
import ivorius.ivtoolkit.tools.NBTCompoundObject;
import net.minecraft.nbt.NBTBase;
import net.minecraft.world.World;

/**
 * Created by lukas on 13.09.15.
 */
public interface WorldScript<S extends NBTStorable> extends NBTCompoundObject
{
    S prepareInstanceData(StructurePrepareContext context, BlockCoord coord, World world);

    S loadInstanceData(StructureLoadContext context, NBTBase nbt);

    void generate(StructureSpawnContext context, S instanceData, BlockCoord coord);

    String getDisplayString();

//    TableDataSource tableDataSource(TableNavigator navigator, TableDelegate tableDelegate);
}
