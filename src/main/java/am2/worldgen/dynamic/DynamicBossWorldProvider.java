package am2.worldgen.dynamic;

import am2.AMCore;
import am2.bosses.*;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.util.Vec3;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldProviderEnd;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.WorldChunkManagerHell;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.common.DimensionManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Map;

public class DynamicBossWorldProvider extends WorldProvider {

    private Vec3 fogColor = null;

    public void registerWorldChunkManager()
    {
        this.worldChunkMgr = new DynamicBossWorldChunkManager(BiomeGenBase.sky, 0.0F, this);
        this.hasNoSky = true;
        loadFogColorIfExists();
    }

    @Override
    public float calculateCelestialAngle(long p_76563_1_, float p_76563_3_)
    {
        return 0.5F;
    }

    public void setDimension(int dim) {
        this.dimensionId = dim;
        super.setDimension(dim);
    }

    public void setFogColorDynamically(AM2Boss boss) {
        Vec3 fogColorToSave = null;
        if (boss instanceof EntityWaterGuardian) {
            fogColorToSave = Vec3.createVectorHelper(0.0f, 0.5f, 0.99f);
        } else if (boss instanceof EntityEarthGuardian) {
            fogColorToSave = Vec3.createVectorHelper(0.75f, 0.75f, 0.75f);
        } else if (boss instanceof EntityAirGuardian) {
            fogColorToSave = Vec3.createVectorHelper(0.22f, 0.99f, 0.99f);
        } else if (boss instanceof EntityArcaneGuardian) {
            fogColorToSave = Vec3.createVectorHelper(0.70f, 0.23f, 0.93f);
        } else if (boss instanceof EntityNatureGuardian) {
            fogColorToSave = Vec3.createVectorHelper(0.24f, 0.55f, 0.22f);
        } else if (boss instanceof EntityLifeGuardian) {
            fogColorToSave = Vec3.createVectorHelper(0.29f, 0.89f, 0.5f);
        } else if (boss instanceof EntityWinterGuardian) {
            fogColorToSave = Vec3.createVectorHelper(0.95f, 0.95f, 0.95f);
        } else if (boss instanceof EntityFireGuardian) {
            fogColorToSave = Vec3.createVectorHelper(0.98f, 0.11f, 0.19f);
        } else if (boss instanceof EntityLightningGuardian) {
            fogColorToSave = Vec3.createVectorHelper(0.12f, 0.56f, 0.99f);
        } else if (boss instanceof EntityEnderGuardian) {
            fogColorToSave = Vec3.createVectorHelper(0.0f, 0.0f, 0.0f);
        }
        fogColor = fogColorToSave;
        File save = new File(new File(DimensionManager.getCurrentSaveRootDirectory(), this.getSaveFolder()), "FogColor.txt");
        try {
            save.createNewFile();
            PrintWriter pw = new PrintWriter(save);
            pw.println(fogColorToSave.xCoord + "," + fogColorToSave.yCoord + "," + fogColorToSave.zCoord);
            pw.close();
        } catch (Exception e) {
            throw new RuntimeException("Failed to save world information file, symptom of a larger issue!");
        }
    }

    public void loadFogColorIfExists() {
        File load = new File(new File(DimensionManager.getCurrentSaveRootDirectory(), this.getSaveFolder()), "FogColor.txt");
        try {
            BufferedReader br = new BufferedReader(new FileReader(load));
            String line = br.readLine();
            br.close();
            String[] fogColors = line.split(",");
            fogColor = Vec3.createVectorHelper(Float.parseFloat(fogColors[0]), Float.parseFloat(fogColors[1]), Float.parseFloat(fogColors[2]));
        } catch (Exception e) {
            AMCore.logger.info("Fog colors were not retrieved. Using default values.");
            fogColor = Vec3.createVectorHelper(0.0f, 0.0f, 0.0f);
        }
    }

    @Override
    public String getDimensionName()
    {
        return "Guardian Domain " + this.dimensionId;
    }

    public String getWelcomeMessage() {
        return "Entering the Elemental Planes";
    }

    public String getDepartMessage() {
        return "Leaving the Elemental Planes";
    }

    public String getSaveFolder() {
        try {
            return this.dimensionId == 0 ? "" : "GuardianDomains/DIM" + this.dimensionId;
        } catch (Exception var2) {
            return "";
        }
    }

    @Override
    public IChunkProvider createChunkGenerator()
    {
        return new EmptyChunkProvider(this.worldObj);
    }

    // TODO:  rain when water guardian fight + snow when winter
    @Override
    @SideOnly(Side.CLIENT)
    public Vec3 getFogColor(float p_76562_1_, float p_76562_2_)
    {
        return fogColor;
    } // r g b??

    @Override
    public float getCloudHeight()
    {
        return -10;
    }

    @Override
    protected void generateLightBrightnessTable()
    {
        for (int i = 0; i <= 15; ++i)
        {
            this.lightBrightnessTable[i] = 1F;
        }
    }

    @Override
    public boolean canCoordinateBeSpawn(int x, int z)
    {
        return false;
    }

    @Override
    public boolean isSurfaceWorld()
    {
        return false;
    }

    @Override
    public boolean canRespawnHere()
    {
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean doesXZShowFog(int x, int z)
    {
        return false;
    }

    @Override
    public float[] calcSunriseSunsetColors(float par1, float par2)
    {
        return new float[] { 0, 0, 0, 0 };
    }
}
