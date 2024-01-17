/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package am2.worldgen.smartgen.generic;

import am2.worldgen.smartgen.reccomplexutils.ExpressionCaches;
import am2.worldgen.smartgen.reccomplexutils.PrefixedTypeExpressionCache;
import am2.worldgen.smartgen.reccomplexutils.RCBoolAlgebra;
import am2.worldgen.smartgen.struct.info.StructureRegistry;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import cpw.mods.fml.common.Loader;
import ivorius.ivtoolkit.tools.IvGsonHelper;
import joptsimple.internal.Strings;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.BiomeDictionary;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by lukas on 19.09.14.
 */
public class DependencyMatcher extends PrefixedTypeExpressionCache<Boolean>
{
    public static final String MOD_PREFIX = "$";
    public static final String STRUCTURE_PREFIX = "#";

    public DependencyMatcher(String expression)
    {
        super(RCBoolAlgebra.algebra(), true, EnumChatFormatting.GREEN + "No Dependencies", expression);

        addType(new ModVariableType(MOD_PREFIX));
        addType(new StructureVariableType(STRUCTURE_PREFIX));
    }

    public static String ofMods(String... ids)
    {
        return ids.length > 0
                ? MOD_PREFIX + Strings.join(Arrays.asList(ids), " & " + MOD_PREFIX)
                : "";
    }

    public boolean apply()
    {
        return evaluate();
    }

    protected static class ModVariableType extends ExpressionCaches.SimpleVariableType<Boolean>
    {
        public ModVariableType(String prefix)
        {
            super(prefix);
        }

        @Override
        public Boolean evaluate(String var, Object... args)
        {
            return Loader.isModLoaded(var);
        }

        @Override
        public boolean isKnown(String var, Object... args)
        {
            return evaluate(var, args);
        }
    }

    protected static class StructureVariableType extends ExpressionCaches.SimpleVariableType<Boolean>
    {
        public StructureVariableType(String prefix)
        {
            super(prefix);
        }

        @Override
        public Boolean evaluate(String var, Object... args)
        {
            return StructureRegistry.INSTANCE.hasStructure(var);
        }

        @Override
        public boolean isKnown(String var, Object... args)
        {
            return evaluate(var, args);
        }
    }
}
