/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package am2.worldgen.smartgen.struct.info;

import am2.worldgen.smartgen.generic.GenericStructureInfo;
import am2.worldgen.smartgen.generic.gentypes.StructureGenerationInfo;
import am2.worldgen.smartgen.reccomplexutils.NBTStorable;
import net.minecraft.nbt.NBTBase;

import java.util.List;

/**
 * Created by lukas on 24.05.14.
 */
public interface StructureInfo<S extends NBTStorable>
{
    void generate(StructureSpawnContext context, S instanceData);

    S prepareInstanceData(StructurePrepareContext context);

    S loadInstanceData(StructureLoadContext context, NBTBase nbt);

    <I extends StructureGenerationInfo> List<I> generationInfos(Class<I> clazz);

    StructureGenerationInfo generationInfo(String id);

    int[] structureBoundingBox();

    boolean isRotatable();

    boolean isMirrorable();

    GenericStructureInfo copyAsGenericStructureInfo();

    boolean areDependenciesResolved();
}
