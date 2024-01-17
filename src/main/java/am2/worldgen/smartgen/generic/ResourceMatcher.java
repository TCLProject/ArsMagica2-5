/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package am2.worldgen.smartgen.generic;

import am2.worldgen.smartgen.reccomplexutils.ExpressionCaches;
import am2.worldgen.smartgen.reccomplexutils.PrefixedTypeExpressionCache;
import am2.worldgen.smartgen.reccomplexutils.RCBoolAlgebra;
import am2.worldgen.smartgen.struct.info.StructureRegistry;
import net.minecraft.util.EnumChatFormatting;

/**
 * Created by lukas on 01.05.15.
 */
public class ResourceMatcher extends PrefixedTypeExpressionCache<Boolean>
{
    public static final String DOMAIN_PREFIX = "$";

    public ResourceMatcher(String expression)
    {
        super(RCBoolAlgebra.algebra(), true, EnumChatFormatting.GREEN + "Any Resource", expression);
        addType(new ResourceIDType(""));
        addType(new DomainType(DOMAIN_PREFIX));
    }

    public boolean apply(String resourceID, String domain)
    {
        return evaluate(resourceID, domain);
    }

    protected static class ResourceIDType extends ExpressionCaches.SimpleVariableType<Boolean>
    {
        public ResourceIDType(String prefix)
        {
            super(prefix);
        }

        @Override
        public Boolean evaluate(String var, Object... args)
        {
            return args[0].equals(var);
        }

        @Override
        public boolean isKnown(final String var, final Object... args)
        {
            return StructureRegistry.INSTANCE.hasStructure(var);
        }
    }

    protected static class DomainType extends ExpressionCaches.SimpleVariableType<Boolean>
    {
        public DomainType(String prefix)
        {
            super(prefix);
        }

        @Override
        public Boolean evaluate(String var, Object... args)
        {
            return args[1].equals(var);
        }

        @Override
        public boolean isKnown(final String var, final Object... args)
        {
            return true;
        }
    }
}
