package am2.worldgen.dynamic;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.WorldChunkManager;
import net.minecraft.world.biome.WorldChunkManagerHell;

public class DynamicBossWorldChunkManager extends WorldChunkManagerHell {

    public WorldProvider wp;

    public DynamicBossWorldChunkManager(BiomeGenBase p_i45374_1_, float p_i45374_2_, WorldProvider worldProvider) {
        super(p_i45374_1_, p_i45374_2_);
        wp = worldProvider;
    }

    @SideOnly(Side.CLIENT)
    public float getTemperatureAtHeight(float p_76939_1_, int p_76939_2_)
    {
        return wp.getFogColor(1, 1).xCoord == 0.95f ? 0.1F : p_76939_1_; // dynamic snow for winter guardian
    }
}
