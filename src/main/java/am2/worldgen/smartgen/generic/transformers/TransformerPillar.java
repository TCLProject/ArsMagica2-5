/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package am2.worldgen.smartgen.generic.transformers;

import am2.AMCore;
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
import ivorius.ivtoolkit.tools.MCRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTBase;
import net.minecraft.world.World;

import java.lang.reflect.Type;

/**
 * Created by lukas on 25.05.14.
 */
public class TransformerPillar extends TransformerSingleBlock<NBTNone>
{
    public BlockMatcher sourceMatcher;

    public IBlockState destState;

    public TransformerPillar()
    {
        this(BlockMatcher.of(AMCore.specialRegistry, Blocks.stone, 0), BlockStates.defaultState(Blocks.stone));
    }

    public TransformerPillar(String sourceExpression, IBlockState destState)
    {
        this.sourceMatcher = new BlockMatcher(AMCore.specialRegistry, sourceExpression);
        this.destState = destState;
    }

    @Override
    public boolean matches(NBTNone instanceData, IBlockState state)
    {
        return sourceMatcher.apply(state);
    }

    @Override
    public void transformBlock(NBTNone instanceData, Phase phase, StructureSpawnContext context, BlockCoord coord, IBlockState sourceState)
    {
        if (AMCore.specialRegistry.isSafe(destState.getBlock()))
        {
            // TODO Fix for partial generation
            World world = context.world;

            int y = coord.y;

            do
            {
                context.setBlock(coord.x, y--, coord.z, destState);

                Block block = world.getBlock(coord.x, y, coord.z);
                if (!(block.isReplaceable(world, coord.x, y, coord.z) || block.getMaterial() == Material.leaves || block.isFoliage(world, coord.x, y, coord.z)))
                    break;
            }
            while (y > 0);
        }
    }

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
    public String getDisplayString()
    {
        return "Pillar: " + sourceMatcher.getDisplayString() + "->" + destState.getBlock().getLocalizedName();
    }

//    @Override
//    public TableDataSource tableDataSource(TableNavigator navigator, TableDelegate delegate)
//    {
//        return new TableDataSourceBTPillar(this);
//    }

    @Override
    public boolean generatesInPhase(NBTNone instanceData, Phase phase)
    {
        return phase == Phase.BEFORE;
    }

    public static class Serializer implements JsonDeserializer<TransformerPillar>, JsonSerializer<TransformerPillar>
    {
        private MCRegistry registry;

        public Serializer(MCRegistry registry)
        {
            this.registry = registry;
        }

        @Override
        public TransformerPillar deserialize(JsonElement jsonElement, Type par2Type, JsonDeserializationContext context)
        {
            JsonObject jsonObject = JsonUtils.getJsonElementAsJsonObject(jsonElement, "transformerPillar");

            String expression = TransformerReplace.Serializer.readLegacyMatcher(jsonObject, "source", "sourceMetadata"); // Legacy
            if (expression == null)
                expression = JsonUtils.getJsonObjectStringFieldValueOrDefault(jsonObject, "sourceExpression", "");

            String destBlock = JsonUtils.getJsonObjectStringFieldValue(jsonObject, "dest");
            Block dest = registry.blockFromID(destBlock);
            IBlockState destState = dest != null ? BlockStates.fromMetadata(dest, JsonUtils.getJsonObjectIntegerFieldValue(jsonObject, "destMetadata")) : null;

            return new TransformerPillar(expression, destState);
        }

        @Override
        public JsonElement serialize(TransformerPillar transformer, Type par2Type, JsonSerializationContext context)
        {
            JsonObject jsonObject = new JsonObject();

            jsonObject.addProperty("sourceExpression", transformer.sourceMatcher.getExpression());

            jsonObject.addProperty("dest", registry.idFromBlock(transformer.destState.getBlock()));
            jsonObject.addProperty("destMetadata", BlockStates.getMetadata(transformer.destState));

            return jsonObject;
        }
    }
}
