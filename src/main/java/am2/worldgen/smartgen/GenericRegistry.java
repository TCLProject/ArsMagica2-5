package am2.worldgen.smartgen;

import am2.AMCore;
import am2.blocks.BlocksCommonProxy;
import am2.worldgen.smartgen.generic.*;
import am2.worldgen.smartgen.generic.blocks.BlockGenericSolid;
import am2.worldgen.smartgen.generic.blocks.BlockGenericSpace;
import am2.worldgen.smartgen.generic.blocks.ItemBlockGeneric;
import am2.worldgen.smartgen.generic.gentypes.*;
import am2.worldgen.smartgen.generic.maze.WorldScriptMazeGenerator;
import am2.worldgen.smartgen.generic.maze.rules.MazeRuleRegistry;
import am2.worldgen.smartgen.generic.maze.rules.saved.MazeRuleConnect;
import am2.worldgen.smartgen.generic.maze.rules.saved.MazeRuleConnectAll;
import am2.worldgen.smartgen.generic.presets.BiomeMatcherPresets;
import am2.worldgen.smartgen.generic.presets.DimensionMatcherPresets;
import am2.worldgen.smartgen.generic.presets.WeightedBlockStatePresets;
import am2.worldgen.smartgen.generic.transformers.*;
import am2.worldgen.smartgen.reccomplexutils.BlockStates;
import am2.worldgen.smartgen.reccomplexutils.FMLUtils;
import am2.worldgen.smartgen.reccomplexutils.json.SerializableStringTypeRegistry;
import am2.worldgen.smartgen.registry.WorldScriptRegistry;
import am2.worldgen.smartgen.schematics.OperationGenerateSchematic;
import am2.worldgen.smartgen.struct.OperationGenerateStructure;
import am2.worldgen.smartgen.struct.OperationMoveStructure;
import am2.worldgen.smartgen.struct.OperationRegistry;
import am2.worldgen.smartgen.struct.info.StructureRegistry;
import am2.worldgen.smartgen.struct.inventory.ItemCollectionSaveHandler;
import am2.worldgen.smartgen.struct.inventory.RCInventoryGenerators;
import am2.worldgen.smartgen.struct.templates.PoemLoader;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import ivorius.ivtoolkit.tools.MCRegistry;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.BiomeDictionary;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static am2.AMCore.fileTypeRegistry;
import static am2.AMCore.specialRegistry;
import static am2.blocks.BlocksCommonProxy.genericSolid;
import static am2.blocks.BlocksCommonProxy.genericSpace;

public class GenericRegistry {

    public static void preInit(FMLPreInitializationEvent event, AMCore mod)
    {
        genericSpace = new BlockGenericSpace().setBlockName("negativeSpace").setBlockTextureName("arsmagica2:generic_air");
        genericSpace.setCreativeTab(BlocksCommonProxy.blockTab);
        register(genericSpace, ItemBlockGeneric.class, "generic_space");
        AMCore.cremapper.registerLegacyIDs(genericSpace, true, "negativeSpace");

        genericSolid = new BlockGenericSolid().setBlockName("naturalFloor").setBlockTextureName("arsmagica2:generic_solid");
        genericSolid.setCreativeTab(BlocksCommonProxy.blockTab);
        register(genericSolid, ItemBlockGeneric.class, "generic_solid");
        AMCore.cremapper.registerLegacyIDs(genericSolid, true, "naturalFloor");

        registerDimensionPresets();
        registerBiomePresets();
        registerBlockStatePresets();
    }

    public static void load(FMLInitializationEvent event, AMCore mod)
    {
        MCRegistry mcRegistry = AMCore.specialRegistry;

        fileTypeRegistry.put(StructureSaveHandler.FILE_SUFFIX, StructureSaveHandler.INSTANCE);
        fileTypeRegistry.put(ItemCollectionSaveHandler.FILE_SUFFIX, ItemCollectionSaveHandler.INSTANCE);
        fileTypeRegistry.put(PoemLoader.FILE_SUFFIX, new PoemLoader());
        fileTypeRegistry.put(CategoryLoader.FILE_SUFFIX, new CategoryLoader());

        WorldScriptRegistry.INSTANCE.register("mazeGen", WorldScriptMazeGenerator.class);

        SerializableStringTypeRegistry<Transformer> transformerRegistry = StructureRegistry.INSTANCE.getTransformerRegistry();
        transformerRegistry.registerType("natural", TransformerNatural.class, new TransformerNatural.Serializer(mcRegistry));
        transformerRegistry.registerType("naturalAir", TransformerNaturalAir.class, new TransformerNaturalAir.Serializer(mcRegistry));
        transformerRegistry.registerType("pillar", TransformerPillar.class, new TransformerPillar.Serializer(mcRegistry));
        transformerRegistry.registerType("replaceAll", TransformerReplaceAll.class, new TransformerReplaceAll.Serializer(mcRegistry));
        transformerRegistry.registerType("replace", TransformerReplace.class, new TransformerReplace.Serializer(mcRegistry));
        transformerRegistry.registerType("ruins", TransformerRuins.class, new TransformerRuins.Serializer(mcRegistry));
        transformerRegistry.registerType("negativeSpace", TransformerNegativeSpace.class, new TransformerNegativeSpace.Serializer(mcRegistry));

        SerializableStringTypeRegistry<StructureGenerationInfo> genInfoRegistry = StructureRegistry.INSTANCE.getStructureGenerationInfoRegistry();
        genInfoRegistry.registerType("natural", NaturalGenerationInfo.class, new NaturalGenerationInfo.Serializer());
        genInfoRegistry.registerType("structureList", StructureListGenerationInfo.class, new StructureListGenerationInfo.Serializer());
        genInfoRegistry.registerType("mazeComponent", MazeGenerationInfo.class, new MazeGenerationInfo.Serializer());
        genInfoRegistry.registerType("static", StaticGenerationInfo.class, new StaticGenerationInfo.Serializer());
        genInfoRegistry.registerType("vanilla", VanillaStructureGenerationInfo.class, new VanillaStructureGenerationInfo.Serializer());

        MazeRuleRegistry.INSTANCE.register("connect", MazeRuleConnect.class);
        MazeRuleRegistry.INSTANCE.register("connectall", MazeRuleConnectAll.class);

        OperationRegistry.register("strucGen", OperationGenerateStructure.class);
        OperationRegistry.register("schemGen", OperationGenerateSchematic.class);
        OperationRegistry.register("strucMove", OperationMoveStructure.class);

//        GameRegistry.registerWorldGenerator(new WorldGenStructures(), 50);
        RCInventoryGenerators.registerVanillaInventoryGenerators();
//        MapGenStructureIO.func_143031_a(GenericVillagePiece.class, "RcGSP");
//        VillagerRegistry.instance().registerVillageCreationHandler(new GenericVillageCreationHandler("DesertHut"));

//        RCBlockRendering.negativeSpaceRenderID = RenderingRegistry.getNextAvailableRenderId();
    }

    public static void register(Item item, String id)
    {
        specialRegistry.register(FMLUtils.addPrefix(id), item);
    }

    public static void register(Block block, String id)
    {
        GameRegistry.registerBlock(block, id);
        specialRegistry.register(FMLUtils.addPrefix(id), block);
        specialRegistry.register(FMLUtils.addPrefix(id), new ItemBlock(block));
    }

    public static void register(Block block, Class<? extends ItemBlock> itemClass, String id, Object... itemArgs)
    {
        GameRegistry.registerBlock(block, itemClass, id, itemArgs);
        specialRegistry.register(FMLUtils.addPrefix(id), block);
        Item item = FMLUtils.constructItem(block, itemClass, itemArgs);
        if (item != null) specialRegistry.register(FMLUtils.addPrefix(id), item);
    }

    public static void register(Class<? extends TileEntity> tileEntity, String id, String...alternatives)
    {
        specialRegistry.register(id, tileEntity);
        for (String aid : alternatives) specialRegistry.register(aid, tileEntity);
    }

    protected static void registerDimensionPresets()
    {
        DimensionMatcherPresets.instance().register("clear");

        DimensionMatcherPresets.instance().register("overworld",
                new DimensionGenerationInfo(DimensionMatcher.ofTypes(DimensionDictionary.UNCATEGORIZED), null),
                new DimensionGenerationInfo(DimensionMatcher.ofTypes(DimensionDictionary.NO_TOP_LIMIT, DimensionDictionary.BOTTOM_LIMIT, DimensionDictionary.INFINITE, DimensionDictionary.EARTH), null)
        );
        DimensionMatcherPresets.instance().setDefault("overworld");

        DimensionMatcherPresets.instance().register("anyplanet",
                new DimensionGenerationInfo(DimensionMatcher.ofTypes(DimensionDictionary.UNCATEGORIZED), null),
                new DimensionGenerationInfo(DimensionMatcher.ofTypes(DimensionDictionary.NO_TOP_LIMIT, DimensionDictionary.BOTTOM_LIMIT, DimensionDictionary.INFINITE), null)
        );

        DimensionMatcherPresets.instance().register("nether",
                new DimensionGenerationInfo(DimensionMatcher.ofTypes(DimensionDictionary.HELL, DimensionDictionary.TOP_LIMIT, DimensionDictionary.BOTTOM_LIMIT), null)
        );

        DimensionMatcherPresets.instance().register("end",
                new DimensionGenerationInfo(DimensionMatcher.ofTypes(DimensionDictionary.ENDER, DimensionDictionary.NO_TOP_LIMIT, DimensionDictionary.NO_BOTTOM_LIMIT), null)
        );
    }

    protected static void registerBiomePresets()
    {
        BiomeMatcherPresets.instance().register("clear");

        BiomeMatcherPresets.instance().register("overworld",
                new BiomeGenerationInfo(BiomeMatcher.ofTypes(BiomeDictionary.Type.WATER), 0.0),
                new BiomeGenerationInfo(BiomeMatcher.ofTypes(BiomeDictionary.Type.PLAINS), null),
                new BiomeGenerationInfo(BiomeMatcher.ofTypes(BiomeDictionary.Type.FOREST), null),
                new BiomeGenerationInfo(BiomeMatcher.ofTypes(BiomeDictionary.Type.MOUNTAIN), null),
                new BiomeGenerationInfo(BiomeMatcher.ofTypes(BiomeDictionary.Type.HILLS), null),
                new BiomeGenerationInfo(BiomeMatcher.ofTypes(BiomeDictionary.Type.SWAMP), null),
                new BiomeGenerationInfo(BiomeMatcher.ofTypes(BiomeDictionary.Type.SANDY), null),
                new BiomeGenerationInfo(BiomeMatcher.ofTypes(BiomeDictionary.Type.MESA), null),
                new BiomeGenerationInfo(BiomeMatcher.ofTypes(BiomeDictionary.Type.SAVANNA), null),
                new BiomeGenerationInfo(BiomeMatcher.ofTypes(BiomeDictionary.Type.WASTELAND), null),
                new BiomeGenerationInfo(BiomeMatcher.ofTypes(BiomeDictionary.Type.MUSHROOM), null),
                new BiomeGenerationInfo(BiomeMatcher.ofTypes(BiomeDictionary.Type.JUNGLE), null));
        BiomeMatcherPresets.instance().setDefault("overworld");

        BiomeMatcherPresets.instance().register("underground",
                new BiomeGenerationInfo(BiomeMatcher.ofTypes(BiomeDictionary.Type.PLAINS), null),
                new BiomeGenerationInfo(BiomeMatcher.ofTypes(BiomeDictionary.Type.FOREST), null),
                new BiomeGenerationInfo(BiomeMatcher.ofTypes(BiomeDictionary.Type.MOUNTAIN), null),
                new BiomeGenerationInfo(BiomeMatcher.ofTypes(BiomeDictionary.Type.HILLS), null),
                new BiomeGenerationInfo(BiomeMatcher.ofTypes(BiomeDictionary.Type.SWAMP), null),
                new BiomeGenerationInfo(BiomeMatcher.ofTypes(BiomeDictionary.Type.SANDY), null),
                new BiomeGenerationInfo(BiomeMatcher.ofTypes(BiomeDictionary.Type.MESA), null),
                new BiomeGenerationInfo(BiomeMatcher.ofTypes(BiomeDictionary.Type.SAVANNA), null),
                new BiomeGenerationInfo(BiomeMatcher.ofTypes(BiomeDictionary.Type.RIVER), null),
                new BiomeGenerationInfo(BiomeMatcher.ofTypes(BiomeDictionary.Type.OCEAN), null),
                new BiomeGenerationInfo(BiomeMatcher.ofTypes(BiomeDictionary.Type.WASTELAND), null),
                new BiomeGenerationInfo(BiomeMatcher.ofTypes(BiomeDictionary.Type.MUSHROOM), null),
                new BiomeGenerationInfo(BiomeMatcher.ofTypes(BiomeDictionary.Type.JUNGLE), null));

        BiomeMatcherPresets.instance().register("ocean",
                new BiomeGenerationInfo(BiomeMatcher.ofTypes(BiomeDictionary.Type.OCEAN, BiomeDictionary.Type.SNOWY), 0.0),
                new BiomeGenerationInfo(BiomeMatcher.ofTypes(BiomeDictionary.Type.OCEAN), null));
    }

    protected static void registerBlockStatePresets()
    {
        WeightedBlockStatePresets.instance().register("clear");

        WeightedBlockStatePresets.instance().register("allWool",  IntStream.range(0, 16).mapToObj(i -> new WeightedBlockState(null, BlockStates.fromMetadata(Blocks.wool, i), "")).collect(Collectors.toList()));
        WeightedBlockStatePresets.instance().setDefault("allWool");
    }
}
