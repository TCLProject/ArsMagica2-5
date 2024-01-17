package am2.worldgen.smartgen;


import am2.AMCore;
import am2.worldgen.smartgen.generic.gentypes.NaturalGenerationInfo;
import am2.worldgen.smartgen.generic.gentypes.StaticGenerationInfo;
import am2.worldgen.smartgen.struct.info.StructureInfo;
import am2.worldgen.smartgen.struct.info.StructureInfos;
import am2.worldgen.smartgen.struct.info.StructureRegistry;
import cpw.mods.fml.common.IWorldGenerator;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.IChunkProvider;
import net.tclproject.mysteriumlib.asm.core.MMiscUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Random;

/**
 * Created by lukas on 24.05.14.
 *
 * The RecurrentComplex codebase has at large been copied and refactored into Ars Magica 2.5, with IvToolkit remaining a hard dependency.
 * The reasoning behind this is currently as follows:
 * - It's the only significantly non-broken, working implementation of custom structures for 1.7.10.
 * - I need custom structures and I'm not reinventing the bicycle by writing my own custom structure gen lib, which would likely contain many issues.
 * - If I make RecComplex a hard dependency, I'm likely to receive a flood of complaints about "I don't like the default reccomplex structures and I'm too dumb to disable them!!"
 * - Moreover, if I make it a hard dependency, it will be much harder to modify it to my needs (doing so will require coremodding, which will also mess with non-AM2.5 use(r)s of RecComplex)
 * - The file size additions are not significant considering no assets are copied (1-2MB maximum).
 * - The license allows for it (MIT).
 */
public class WorldGenStructures implements IWorldGenerator
{
    @Override
    public void generate(Random random, final int chunkX, final int chunkZ, final World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider)
    {
        boolean worldWantsStructures = world.getWorldInfo().isMapFeaturesEnabled();
        StructureGenerationData data = StructureGenerationData.get(world);

        generatePartialStructuresInChunk(random, chunkX, chunkZ, world, chunkGenerator, chunkProvider);

        if (worldWantsStructures)
        {
            BiomeGenBase biomeGen = world.getBiomeGenForCoords(chunkX * 16, chunkZ * 16);
            ChunkCoordinates spawnPos = world.getSpawnPoint();

            for (Pair<StructureInfo, StaticGenerationInfo> pair : StructureRegistry.INSTANCE.getStaticStructuresAt(chunkX, chunkZ, world, spawnPos))
            {
                AMCore.logger.trace(String.format("Spawning static structure at x = %d, z = %d", chunkX << 4, chunkZ << 4));

                StaticGenerationInfo staticGenInfo = pair.getRight();
                StructureInfo structureInfo = pair.getLeft();
                String structureName = StructureRegistry.INSTANCE.structureID(structureInfo);

                int strucX = staticGenInfo.getPositionX(spawnPos);
                int strucZ = staticGenInfo.getPositionZ(spawnPos);

                StructureGenerator.randomInstantly(world, random, structureInfo, staticGenInfo.ySelector, strucX, strucZ, false, structureName);
            }

            if (data.checkChunk(new ChunkCoordIntPair(chunkX, chunkZ)))
            {
                boolean mayGenerate = mayGenerateIn(biomeGen, world.provider);

                if (world.provider.dimensionId == 0)
                {
                    double distToSpawn = distanceSQ(new double[]{chunkX * 16 + 8, chunkZ * 16 + 8}, new double[]{spawnPos.posX, spawnPos.posZ});
                    mayGenerate &= distToSpawn >= GenerationConstants.minDistToSpawnForGeneration * GenerationConstants.minDistToSpawnForGeneration; // 300 = minDistToSpawn
                }

                if (mayGenerate)
                {
                    StructureSelector structureSelector = StructureRegistry.INSTANCE.getStructureSelector(biomeGen, world.provider);
                    List<Pair<StructureInfo, NaturalGenerationInfo>> generated = structureSelector.generatedStructures(random, chunkX, chunkZ, world, chunkGenerator, chunkProvider);
                    for (Pair<StructureInfo, NaturalGenerationInfo> pair : generated)
                    {
                        StructureInfo structureInfo = pair.getLeft();
                        NaturalGenerationInfo naturalGenInfo = pair.getRight();
                        String structureName = StructureRegistry.INSTANCE.structureID(structureInfo);

                        int genX = chunkX * 16 + random.nextInt(16);
                        int genZ = chunkZ * 16 + random.nextInt(16);
                        if (AMCore.config.isVerboseStructureGenEnabled()) System.out.println("Ars Magica: Generated structure at " + genX + " x, " + genZ + " z.");

                        // checking for biome crossovers, for example, half of the structure being in a river
                        if (world.getBiomeGenForCoords(chunkX * 16, chunkZ * 16) != world.getBiomeGenForCoords((chunkX * 16)+7, (chunkZ * 16)+7)) return;
                        if (world.getBiomeGenForCoords(chunkX * 16, chunkZ * 16) != world.getBiomeGenForCoords((chunkX * 16)+15, (chunkZ * 16)+15)) return;

                        if (!naturalGenInfo.hasLimitations() || naturalGenInfo.getLimitations().areResolved(world, structureName)) {
                            StructureGenerator.randomInstantly(world, random, structureInfo, naturalGenInfo.ySelector, genX, genZ, true, structureName);
                        }
                    }
                }
            }
        }
    }

    private static boolean mayGenerateIn(BiomeGenBase biomeGen, WorldProvider provider) {
        // TODO: Add custom logic?
        return true;
    }

    public static double distanceSQ(double[] pos1, double[] pos2)
    {
        double distanceSQ = 0.0;

        for (int i = 0; i < pos1.length; i++)
            distanceSQ += (pos1[i] - pos2[i]) * (pos1[i] - pos2[i]);

        return distanceSQ;
    }

    public static void generatePartialStructuresInChunk(Random random, final int chunkX, final int chunkZ, final World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider)
    {
        StructureGenerationData data = StructureGenerationData.get(world);

        for (StructureGenerationData.Entry entry : data.getEntriesAt(new ChunkCoordIntPair(chunkX, chunkZ), true))
        {
            StructureInfo structureInfo = StructureRegistry.INSTANCE.getStructure(entry.getStructureID());

            if (structureInfo != null)
            {
                StructureGenerator.partially(structureInfo, world, random, entry.lowerCoord, entry.transform, StructureInfos.chunkBoundingBox(chunkX, chunkZ), 0, entry.getStructureID(), entry.instanceData, entry.firstTime);

                if (entry.firstTime)
                {
                    entry.firstTime = false;
                    data.markDirty();
                }
            }
        }
    }
}