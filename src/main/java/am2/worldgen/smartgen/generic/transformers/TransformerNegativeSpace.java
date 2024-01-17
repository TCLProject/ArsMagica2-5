/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package am2.worldgen.smartgen.generic.transformers;

import am2.AMCore;
import am2.blocks.BlocksCommonProxy;
import am2.worldgen.smartgen.generic.BlockMatcher;
import am2.worldgen.smartgen.reccomplexutils.IBlockState;
import am2.worldgen.smartgen.reccomplexutils.NBTNone;
import am2.worldgen.smartgen.reccomplexutils.NBTStorable;
import am2.worldgen.smartgen.reccomplexutils.json.JsonUtils;
import am2.worldgen.smartgen.struct.info.StructureLoadContext;
import am2.worldgen.smartgen.struct.info.StructurePrepareContext;
import am2.worldgen.smartgen.struct.info.StructureSpawnContext;
import com.google.gson.*;
import ivorius.ivtoolkit.tools.IvWorldData;
import ivorius.ivtoolkit.tools.MCRegistry;
import net.minecraft.nbt.NBTBase;
import org.apache.commons.lang3.tuple.Pair;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by lukas on 25.05.14.
 */
public class TransformerNegativeSpace implements Transformer<NBTNone>
{
    public BlockMatcher sourceMatcher;

    public TransformerNegativeSpace()
    {
        this(BlockMatcher.of(AMCore.specialRegistry, BlocksCommonProxy.genericSpace, 0));
    }

    public TransformerNegativeSpace(String sourceExpression)
    {
        this.sourceMatcher = new BlockMatcher(AMCore.specialRegistry, sourceExpression);
    }

    @Override
    public boolean skipGeneration(NBTNone instanceData, IBlockState state)
    {
        return sourceMatcher.apply(state);
    }

    @Override
    public void transform(NBTNone instanceData, Phase phase, StructureSpawnContext context, IvWorldData worldData, List<Pair<Transformer, NBTStorable>> transformers)
    {

    }

    @Override
    public String getDisplayString()
    {
        return "Space: " + sourceMatcher.getDisplayString();
    }

//    @Override
//    public TableDataSource tableDataSource(TableNavigator navigator, TableDelegate delegate)
//    {
//        return new TableDataSourceBTNegativeSpace(this);
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
        return false;
    }

    public static class Serializer implements JsonDeserializer<TransformerNegativeSpace>, JsonSerializer<TransformerNegativeSpace>
    {
        private MCRegistry registry;

        public Serializer(MCRegistry registry)
        {
            this.registry = registry;
        }

        @Override
        public TransformerNegativeSpace deserialize(JsonElement jsonElement, Type par2Type, JsonDeserializationContext context)
        {
            JsonObject jsonObject = JsonUtils.getJsonElementAsJsonObject(jsonElement, "transformerNegativeSpace");

            String expression = TransformerReplace.Serializer.readLegacyMatcher(jsonObject, "source", "sourceMetadata"); // Legacy
            if (expression == null)
                expression = JsonUtils.getJsonObjectStringFieldValueOrDefault(jsonObject, "sourceExpression", "");

            return new TransformerNegativeSpace(expression);
        }

        @Override
        public JsonElement serialize(TransformerNegativeSpace transformer, Type par2Type, JsonSerializationContext context)
        {
            JsonObject jsonObject = new JsonObject();

            jsonObject.addProperty("sourceExpression", transformer.sourceMatcher.getExpression());

            return jsonObject;
        }
    }
}
