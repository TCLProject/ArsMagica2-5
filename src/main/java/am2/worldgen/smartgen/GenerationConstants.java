package am2.worldgen.smartgen;

import am2.AMCore;
import am2.worldgen.smartgen.generic.BiomeMatcher;
import am2.worldgen.smartgen.generic.DimensionMatcher;
import am2.worldgen.smartgen.generic.ResourceMatcher;
import am2.worldgen.smartgen.reccomplexutils.ExpressionCache;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.biome.BiomeGenBase;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;

public class GenerationConstants {

    public static Pair<String, Float> customArtifactTag = Pair.of("", 0.0f);
    public static Pair<String, Float> customBookTag = Pair.of("", 0.0f);

    public static float minDistToSpawnForGeneration = 250;
    public static boolean avoidOverlappingGeneration = true;
    public static boolean honorStructureGenerationOption = true;

    public static int baseVillageSpawnWeight = 10; // Vanilla average is about 10 - if you want to fully replace vanilla structures in villages, crank this up to something big

    public static String commandPrefix = "#m#";

    public static boolean savePlayerCache = true;
    public static boolean notifyAdminOnBlockCommands = false;

    public static boolean lightweightMode = true;
    public static boolean hideRedundantNegativeSpace = true; //Only show the edges of negative space blocks? (Improves performance in big builds)

    // Keyboard may (?) be clientside only, better not risk it and have this only where it's needed
//    public static int[] blockSelectorModifierKeys = new int[]{Keyboard.KEY_LCONTROL, Keyboard.KEY_RCONTROL}; //The key to be held when you want to make a secondary selection with block selectors

    private static ResourceMatcher structureLoadMatcher = new ResourceMatcher("");
    private static ResourceMatcher structureGenerationMatcher = new ResourceMatcher("");

    private static ResourceMatcher inventoryGeneratorLoadMatcher = new ResourceMatcher("");
    private static ResourceMatcher inventoryGeneratorGenerationMatcher = new ResourceMatcher("");

    private static BiomeMatcher universalBiomeMatcher = new BiomeMatcher("");
    private static DimensionMatcher universalDimensionMatcher = new DimensionMatcher("");

    public static float mazePlacementReversesPerRoom = 10; // Maximum number of reverses per room the maze generator can do. A higher number results in a better generation success rate, but may freeze the server temporarily. default: 10

    public static void loadConfig()
    {
        structureLoadMatcher.setExpression(""); // Resource Expression that will be applied to each loading structure, determining if it should be loaded.
        logExpressionException(structureLoadMatcher, "structureLoadMatcher", AMCore.logger);
        structureGenerationMatcher.setExpression(""); // Resource Expression that will be applied to each loading structure, determining if it should be set to 'active'
        logExpressionException(structureGenerationMatcher, "structureGenerationMatcher", AMCore.logger);

        inventoryGeneratorLoadMatcher.setExpression(""); // Resource Expression that will be applied to each loading inventory generator, determining if it should be loaded.
        logExpressionException(inventoryGeneratorLoadMatcher, "inventoryGeneratorLoadMatcher", AMCore.logger);
        inventoryGeneratorGenerationMatcher.setExpression(""); // Resource Expression that will be applied to each loading inventory generator, determining if it should be set to 'active'.
        logExpressionException(inventoryGeneratorGenerationMatcher, "inventoryGeneratorGenerationMatcher", AMCore.logger);

        universalBiomeMatcher.setExpression(""); // Biome Expression that will be checked for every single structure. Use this if you want to blacklist / whitelist specific biomes that shouldn't have structures.
        logExpressionException(universalBiomeMatcher, "universalBiomeMatcher", AMCore.logger);

        universalDimensionMatcher.setExpression(""); // Dimension Expression that will be checked for every single structure. Use this if you want to blacklist / whitelist specific dimensions that shouldn't have structures.
        logExpressionException(universalDimensionMatcher, "universalDimensionMatcher", AMCore.logger);

        customArtifactTag = Pair.of(
                "", // Custom Inventory Generator to override when an artifact generation tag fires.
                0.0f // Chance to use the customArtifactTag when an artifact generation tag fires.
        );
        customBookTag = Pair.of(
                "", // Custom Inventory Generator to override when a book generation tag fires.
                0.0f // "Chance to use the customArtifactTag when a book generation tag fires.
        );
    }

    private static void logExpressionException(ExpressionCache<?> cache, String name, Logger logger)
    {
        if (cache.getParseException() != null)
            logger.error("Error in expression '" + name + "'", cache.getParseException());
    }

    public static boolean isLightweightMode()
    {
        return lightweightMode;
    }

    public static boolean shouldStructureLoad(String id, String domain)
    {
        return structureLoadMatcher.apply(id, domain);
    }

    public static boolean shouldStructureGenerate(String id, String domain)
    {
        return structureGenerationMatcher.apply(id, domain);
    }

    public static boolean shouldInventoryGeneratorLoad(String id, String domain)
    {
        return inventoryGeneratorLoadMatcher.apply(id, domain);
    }

    public static boolean shouldInventoryGeneratorGenerate(String id, String domain)
    {
        return inventoryGeneratorGenerationMatcher.apply(id, domain);
    }

    public static boolean isGenerationEnabled(BiomeGenBase biome)
    {
        return !universalBiomeMatcher.isExpressionValid() || universalBiomeMatcher.apply(biome);
    }

    public static boolean isGenerationEnabled(WorldProvider provider)
    {
        return !universalDimensionMatcher.isExpressionValid() || universalDimensionMatcher.apply(provider);
    }
}
