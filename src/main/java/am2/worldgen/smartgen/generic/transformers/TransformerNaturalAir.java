/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package am2.worldgen.smartgen.generic.transformers;

import am2.AMCore;
import am2.blocks.BlocksCommonProxy;
import am2.worldgen.smartgen.generic.BlockMatcher;
import am2.worldgen.smartgen.reccomplexutils.BlockStates;
import am2.worldgen.smartgen.reccomplexutils.IBlockState;
import am2.worldgen.smartgen.reccomplexutils.NBTNone;
import am2.worldgen.smartgen.reccomplexutils.json.JsonUtils;
import am2.worldgen.smartgen.struct.info.StructureLoadContext;
import am2.worldgen.smartgen.struct.info.StructurePrepareContext;
import am2.worldgen.smartgen.struct.info.StructureSpawnContext;
import com.google.gson.*;
import ivorius.ivtoolkit.blocks.BlockCoord;
import ivorius.ivtoolkit.math.IvVecMathHelper;
import ivorius.ivtoolkit.tools.MCRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTBase;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static am2.AMCore.rcpresent;

/**
 * Created by lukas on 25.05.14.
 */
public class TransformerNaturalAir extends TransformerSingleBlock<NBTNone>
{
    public static final double DEFAULT_NATURAL_EXPANSION_DISTANCE = 4.0;
    public static final double DEFAULT_NATURAL_EXPANSION_RANDOMIZATION = 10.0;

    public BlockMatcher sourceMatcher;
    public double naturalExpansionDistance;
    public double naturalExpansionRandomization;

//    private Object recTransformer;

    public TransformerNaturalAir()
    {
        this(BlockMatcher.of(AMCore.specialRegistry, BlocksCommonProxy.genericSpace, 1), DEFAULT_NATURAL_EXPANSION_DISTANCE, DEFAULT_NATURAL_EXPANSION_RANDOMIZATION);
    }

    public TransformerNaturalAir(String sourceMatcherExpression, double naturalExpansionDistance, double naturalExpansionRandomization)
    {
        this.sourceMatcher = new BlockMatcher(AMCore.specialRegistry, sourceMatcherExpression);
        this.naturalExpansionDistance = naturalExpansionDistance;
        this.naturalExpansionRandomization = naturalExpansionRandomization;
//        } else { // rc is present
//            try {
//                Class<?> clazz = Class.forName("ivorius.reccomplex.structures.generic.GenericStructureInfo");
//                Method method = clazz.getMethod("createDefaultStructure", new Class<?>[]{});
//                Object objectInstance = method.invoke(null, new Object[]{});
//                Field field = clazz.getField("transformers");
//                Object value = field.get(objectInstance);
//                List transformers = (List) value;
//
//                Class<?> clazzThisTransformer = Class.forName("ivorius.reccomplex.structures.generic.transformers.TransformerNaturalAir");
//                Method stringMethod = clazzThisTransformer.getMethod("getDisplayString", new Class<?>[]{});
//                for (Object transformer : transformers) {
//                    String name = (String)stringMethod.invoke(transformer, new Object[]{});
//                    if (name != null && name.startsWith("Natural Air:")) {
//                        // this is the needed transformer
//                        recTransformer = transformer;
//                        return;
//                    }
//                }
//            } catch (Exception e) {e.printStackTrace();}
//        }
    }

    @Override
    public boolean matches(NBTNone instanceData, IBlockState state)
    {
        return sourceMatcher.apply(state);
    }

    @Override
    public void transformBlock(NBTNone instanceData, Phase phase, StructureSpawnContext context, BlockCoord coord, IBlockState sourceState)
    {
        // TODO Fix for partial generation
        World world = context.world;
        Random random = context.random;

        BiomeGenBase biome = world.getBiomeGenForCoords(coord.x, coord.z);
        Block topBlock = biome.topBlock != null ? biome.topBlock : Blocks.air;
        Block fillerBlock = biome.fillerBlock != null ? biome.fillerBlock : Blocks.air;

        coord = coord.subtract(0, 4, 0);

        int currentY = coord.y;
        List<int[]> currentList = new ArrayList<>();
        List<int[]> nextList = new ArrayList<>();
        nextList.add(new int[]{coord.x, coord.z});

        int worldHeight = world.getHeight();
        while (nextList.size() > 0 && currentY < worldHeight)
        {
            List<int[]> cachedList = currentList;
            currentList = nextList;
            nextList = cachedList;

            while (currentList.size() > 0)
            {
                int[] currentPos = currentList.remove(0);
                int currentX = currentPos[0];
                int currentZ = currentPos[1];
                Block curBlock = world.getBlock(currentX, currentY, currentZ);

                boolean isFoliage = curBlock.isFoliage(world, currentX, currentY, currentZ) || curBlock.getMaterial() == Material.leaves || curBlock.getMaterial() == Material.plants || curBlock.getMaterial() == Material.wood;
                boolean isCommon = curBlock == Blocks.stone || curBlock == Blocks.dirt || curBlock == Blocks.sand || curBlock == Blocks.stained_hardened_clay || curBlock == Blocks.gravel;
                boolean replaceable = currentY == coord.y || curBlock == topBlock || curBlock == fillerBlock || curBlock.isReplaceable(world, currentX, currentY, currentZ)
                        || isCommon || isFoliage;

                if (replaceable)
                    context.setAirBlockWithDelay(currentX, currentY, currentZ);

                if (replaceable || curBlock.getMaterial() == Material.air)
                {
                    double distToOrigSQ = IvVecMathHelper.distanceSQ(new double[]{coord.x, coord.y, coord.z}, new double[]{currentX, currentY, currentZ});
                    double add = (random.nextDouble() - random.nextDouble()) * naturalExpansionRandomization;
                    distToOrigSQ += add < 0 ? -(add * add) : (add * add);

                    if (distToOrigSQ < naturalExpansionDistance * naturalExpansionDistance)
                    {
                        addIfNew(nextList, currentX, currentZ);
                        addIfNew(nextList, currentX - 1, currentZ);
                        addIfNew(nextList, currentX + 1, currentZ);
                        addIfNew(nextList, currentX, currentZ - 1);
                        addIfNew(nextList, currentX, currentZ + 1);
                    }
                }
            }

            currentY++;
        }
    }

    private void addIfNew(List<int[]> list, int... object)
    {
        if (!list.contains(object))
        {
            list.add(object);
        }
    }

    @Override
    public String getDisplayString()
    {
        return "Natural Air: " + sourceMatcher.getDisplayString();
    }

//    @Override
//    public TableDataSource tableDataSource(TableNavigator navigator, TableDelegate delegate)
//    {
//        return new TableDataSourceBTNaturalAir(this);
//    }

    @Override
    public NBTNone prepareInstanceData(StructurePrepareContext context)
    {
        return new NBTNone();
    }

    @Override
    public NBTNone loadInstanceData(StructureLoadContext context, NBTBase nbt)
    {
        return new NBTNone();
    }

    @Override
    public boolean generatesInPhase(NBTNone instanceData, Phase phase)
    {
        return phase == Phase.BEFORE;
    }

    public static class Serializer implements JsonDeserializer<TransformerNaturalAir>, JsonSerializer<TransformerNaturalAir>
    {
        private MCRegistry registry;

        public Serializer(MCRegistry registry)
        {
            this.registry = registry;
        }

        @Override
        public TransformerNaturalAir deserialize(JsonElement jsonElement, Type par2Type, JsonDeserializationContext context)
        {
            JsonObject jsonObject = JsonUtils.getJsonElementAsJsonObject(jsonElement, "transformerNatural");

            String expression = TransformerReplace.Serializer.readLegacyMatcher(jsonObject, "source", "sourceMetadata"); // Legacy
            if (expression == null)
                expression = JsonUtils.getJsonObjectStringFieldValueOrDefault(jsonObject, "sourceExpression", "");

            double naturalExpansionDistance = JsonUtils.getJsonObjectDoubleFieldValueOrDefault(jsonObject, "naturalExpansionDistance", DEFAULT_NATURAL_EXPANSION_DISTANCE);
            double naturalExpansionRandomization = JsonUtils.getJsonObjectDoubleFieldValueOrDefault(jsonObject, "naturalExpansionRandomization", DEFAULT_NATURAL_EXPANSION_RANDOMIZATION);

            return new TransformerNaturalAir(expression, naturalExpansionDistance, naturalExpansionRandomization);
        }

        @Override
        public JsonElement serialize(TransformerNaturalAir transformer, Type par2Type, JsonSerializationContext context)
        {
            JsonObject jsonObject = new JsonObject();

            jsonObject.addProperty("sourceExpression", transformer.sourceMatcher.getExpression());

            jsonObject.addProperty("naturalExpansionDistance", transformer.naturalExpansionDistance);
            jsonObject.addProperty("naturalExpansionRandomization", transformer.naturalExpansionRandomization);

            return jsonObject;
        }
    }
}
