package com.tfc.minecraft_effekseer_implementation;

import java.util.*;

import am2.customdata.CustomWorldData;
import com.tfc.minecraft_effekseer_implementation.vector.Matrix4f;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.Entity;
import net.minecraft.util.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.tfc.effekseer4j.EffekseerEffect;
import com.tfc.effekseer4j.enums.TextureType;
import com.tfc.minecraft_effekseer_implementation.common.Effek;
import com.tfc.minecraft_effekseer_implementation.common.LoaderIndependentIdentifier;
import com.tfc.minecraft_effekseer_implementation.loader.EffekseerMCAssetLoader;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.command.ICommand;

public class MEI {
	public static final Logger LOGGER = LogManager.getLogger();

	public static boolean renderingEnabled = true;
	public static MEI instance;

	public boolean refreshed;

	// the max amout of effeks that will be scanned on startup. Do NOT in any circumstance make it lower.
	// If the 256 is not enough, you can make it higher to accommodate for any additional effects you may require.
	// That can be done with a simple mod, as the variable is public. In that case, it is highly recommended to
	// add to the variable (do THIS -> (MEI.effekAmount += newAmount)) rather than set it (do NOT do this -> (MEI.effekAmount = newAmount))
	public static int effekAmount = 256;

    public static void preInit(FMLPreInitializationEvent event)
    {
		if (event.getSide() == Side.CLIENT) am2.proxy.ClientProxy.proxy.registerKeyHandelers();
    }

    public static void init(FMLInitializationEvent event) {
		if (LoaderIndependentIdentifier.rlConstructor1.get() == null) {
			LoaderIndependentIdentifier.rlConstructor1.set(ResourceLocation::new);
			LoaderIndependentIdentifier.rlConstructor2.set(ResourceLocation::new);
		}
		Networking.registerPackets();

		if (!FMLCommonHandler.instance().getSide().isClient()) return;
		am2.proxy.ClientProxy.proxy.register();
	}

	@SideOnly(Side.CLIENT)
	public static void loadEffeksFromWorldVars() { // gets called each time a player joins a world, including dimension transfers
		Map<String, String> blockEffeks = CustomWorldData.getWorldVarsStartingWith(Minecraft.getMinecraft().theWorld, "CLIENT_BLOCKFX_");
    	Map<String, String> entityEffeks = CustomWorldData.getWorldVarsStartingWith(Minecraft.getMinecraft().theWorld, "CLIENT_ENTITYFX_");
    	for (Map.Entry<String,String> effek : blockEffeks.entrySet()) {
			if (!("REMOVED".equalsIgnoreCase(effek.getValue()))) {
				String key = effek.getKey().replace("CLIENT_BLOCKFX_", "");
				String[] values = effek.getValue().split("-");
				addBlockBoundEffect(key.split("!!!")[0], key.split("!!!")[1], Double.valueOf(values[0]), Double.valueOf(values[1]), Double.valueOf(values[2]),
						Integer.valueOf(values[3]), Double.valueOf(values[4]), Double.valueOf(values[5]), Double.valueOf(values[6]), Double.valueOf(values[7]), Double.valueOf(values[8]), Double.valueOf(values[9]));
			}
		}

		for (Map.Entry<String,String> effek : entityEffeks.entrySet()) {
			if (!("REMOVED".equalsIgnoreCase(effek.getValue()))) {
				String entityUUID = effek.getKey().replace("CLIENT_ENTITYFX_", "");
				String[] values = effek.getValue().split("!!!");
				String[] secondaryValues = values[2].split("_");
				addEntityBoundEffect(values[0], values[1], entityUUID, Double.valueOf(secondaryValues[0]), Double.valueOf(secondaryValues[1]), Double.valueOf(secondaryValues[2]), Integer.valueOf(values[3]));
			}
		}
	}

	public static long lastFrame = -1;
	public static long lastFrameHand = -1;
	public static long lastFrameHandTP = -1;

	public static float nearest(float minus, float plus, float pick) {
		return Math.abs(minus - pick) < Math.abs(plus - pick) ? minus : plus;
	}

	public int ticks = 0;
	public int ticksHand = 0;
	public int ticksHandTP = 0;

	// needless to say, this is not bound to a 'block'. It is bound to any arbitrary coordinate in the world, which may correspond to a block.
	// removing effeks when the block is broken, etc, should be handled by the block itself.
	public static HashMap<String, double[]> blockBoundEffeks = new HashMap<>(); //  "effekname!!!emittername", {x, y, z, dim, bbminusx, bbminusy, bbminusz, bbplusx, bbplusy, bbplusz}
	// bound to the position of an entity
	public static HashMap<String, List<String>> entityBoundEffeks = new HashMap<>(); //  "ENTITY-UUID-4534-5435-3124", list of effects in format "effekname!!!emittername!!!xExpand_yExpand_zExpand!!!rotatable"
	// to reduce entity.getUniqueID.toString() usage to a minimum due to performance reasons
	public static HashMap<Entity, List<String>> cachedBoundEffeks = new HashMap<>(); //  entity object, list of effects in format "effekname!!!emittername!!!xExpand_yExpand_zExpand!!!rotatable"

	// it is up to the requester (caller of this/other methods here) to make the emitter name unique
	public static void addBlockBoundEffect(String effekName, String emitterName, double x, double y, double z, int dim, double bbminusx, double bbminusy, double bbminusz, double bbplusx, double bbplusy, double bbplusz) {
		blockBoundEffeks.put(effekName + "!!!" + emitterName, new double[]{x, y, z, dim, bbminusx, bbminusy, bbminusz, bbplusx, bbplusy, bbplusz});
		CustomWorldData.requestWorldVar(dim, "CLIENT_BLOCKFX_" + effekName + "!!!" + emitterName, x+"-"+y+"-"+z+"-"+dim+"-"+bbminusx+"-"+bbminusy+"-"+bbminusz+"-"+bbplusx+"-"+bbplusy+"-"+bbplusz, false);
	}

	public static void addBlockBoundEffect(String effekName, String emitterName, double x, double y, double z, int dim, double radius) {
		blockBoundEffeks.put(effekName + "!!!" + emitterName, new double[]{x, y, z, dim, radius, radius, radius, radius, radius, radius});
		CustomWorldData.requestWorldVar(dim, "CLIENT_BLOCKFX_" + effekName + "!!!" + emitterName, x+"-"+y+"-"+z+"-"+dim+"-"+radius+"-"+radius+"-"+radius+"-"+radius+"-"+radius+"-"+radius, false);
	}

	public static void removeBlockBoundEffect(String effekName, String emitterName, int dim) {
		blockBoundEffeks.remove(effekName + "!!!" + emitterName);
		CustomWorldData.requestWorldVar(dim, "CLIENT_BLOCKFX_" + effekName + "!!!" + emitterName, "REMOVED", false);
	}

	// rotate: 0 = none, 1 = yaw only, 2 = pitch only, 3 = yaw and pitch
	public static void addEntityBoundEffect(String effekName, String emitterName, Entity entity, double xExpand, double yExpand, double zExpand, int rotate) {
		addEntityBoundEffect(effekName, emitterName, entity.getUniqueID().toString(), xExpand, yExpand, zExpand, rotate);
	}

	public static void removeAllEntityBoundEffects(Entity entity) {
		removeAllEntityBoundEffects(entity.getUniqueID().toString());
	}

	public static void removeEntityBoundEffect(String effekName, String emitterName, Entity entity, double xExpand, double yExpand, double zExpand, int rotate) {
		removeEntityBoundEffect(effekName, emitterName, entity.getUniqueID().toString(), xExpand, yExpand, zExpand, rotate);
	}

	// rotate: 0 = none, 1 = yaw only, 2 = pitch only, 3 = yaw and pitch
	@SideOnly(Side.CLIENT)
	public static void addEntityBoundEffect(String effekName, String emitterName, String entityUUID, double xExpand, double yExpand, double zExpand, int rotate) {
		List<String> newList =  new ArrayList<String>();
		newList.addAll(entityBoundEffeks.get(entityUUID));
		String effekString = effekName + "!!!" + emitterName + "!!!" + xExpand + "_" + yExpand + "_" + zExpand + "!!!" + rotate;
		newList.add(effekString);
		entityBoundEffeks.put(entityUUID, newList);
		cachedBoundEffeks.clear();
		CustomWorldData.requestWorldVar(Minecraft.getMinecraft().theWorld, "CLIENT_ENTITYFX_" + entityUUID, effekString, true);
	}

	@SideOnly(Side.CLIENT)
	public static void removeAllEntityBoundEffects(String entityUUID) {
		entityBoundEffeks.remove(entityUUID);
		cachedBoundEffeks.clear();
		CustomWorldData.requestWorldVar(Minecraft.getMinecraft().theWorld, "CLIENT_ENTITYFX_" + entityUUID, "REMOVED", true);
	}

	@SideOnly(Side.CLIENT)
	public static void removeEntityBoundEffect(String effekName, String emitterName, String entityUUID, double xExpand, double yExpand, double zExpand, int rotate) {
		entityBoundEffeks.get(entityUUID).remove(effekName + "!!!" + emitterName + "!!!" + xExpand + "_" + yExpand + "_" + zExpand + "!!!" + rotate);
		cachedBoundEffeks.clear();
		CustomWorldData.requestWorldVar(Minecraft.getMinecraft().theWorld, "CLIENT_ENTITYFX_" + entityUUID, "REMOVED", true);
	}

	public double getDistanceFrom(double thisXCoord, double thisYCoord, double thisZCoord, double p_145835_1_, double p_145835_3_, double p_145835_5_)
	{
		double d3 = (double)thisXCoord + 0.5D - p_145835_1_;
		double d4 = (double)thisYCoord + 0.5D - p_145835_3_;
		double d5 = (double)thisZCoord + 0.5D - p_145835_5_;
		return d3 * d3 + d4 * d4 + d5 * d5;
	}

	public float thirdPersonDistanceTemp = 4.0F;
	public float thirdPersonDistance = 4.0F;

	// Gets the real (non-game) coordinates the emitter has to be set to in order to display at a specific fake (in-game) coordinate
	public double[] getRealEmitterCoordinates(double posX, double posY, double posZ) {
		double vfxLocationX = posX;
		double vfxLocationZ = posZ;
		double[] transformNeeded = getPointOnCircle(MathHelper.wrapAngleTo180_float(am2.proxy.ClientProxy.mc.thePlayer.rotationYaw) + 180F, Minecraft.getMinecraft().renderViewEntity.posX, Minecraft.getMinecraft().renderViewEntity.posZ, 0.5 + vfxLocationX, 0.5 + vfxLocationZ);
		return new double[]{vfxLocationX + transformNeeded[0] + -Minecraft.getMinecraft().renderViewEntity.posX + 0.5, posY + -Minecraft.getMinecraft().renderViewEntity.posY + 0.5, vfxLocationZ + transformNeeded[1] + -Minecraft.getMinecraft().renderViewEntity.posZ + 0.5};
	}

	// The circles inputted into this function will always go through the point (x, y) but otherwise have an arbitrary radius and center
	// Then, a second point is plotted. This second point is always on the circle, offset from (x, y) at some arbitrary angle.
	// We calculate the coordinates of this second point and return it
	// This entire function could be one line of code but I made it 8 because it's beneficial for me to visualise what it happening. The added performance
	// impact is not noticeabe;.
	public static double[] getPointOnCircle(double angle, double centerX, double centerY, double pointThatTouchesOriginX, double pointThatTouchesOriginY) {
		// Convert angle to radians
		double angleRad = -Math.toRadians(angle);
		// translate the entire circle to be centered on the origin, find the new point the circle passes through (assuming the player, the center, is at 0,0)
		double x = pointThatTouchesOriginX - centerX;
		double y = pointThatTouchesOriginY - centerY;
		// rotate this point around the origin
		double x2 = x*Math.cos(angleRad) - y*Math.sin(angleRad);
		double y2 = x*Math.sin(angleRad) + y*Math.cos(angleRad);
		// translate point back to where it used to be
		double x3 = x2 + centerX;
		double y3 = y2 + centerY;

		// Return point as a Java array
		return new double[]{x3, y3};
	}


	public static void printEffectInfo(EffekseerEffect effect) {
		System.out.println("Effect info:");
		System.out.println(" curveCount: " + effect.curveCount());
		for (int index = 0; index < effect.curveCount(); index++) System.out.println("  curve"+index+": " + effect.getCurvePath(index));
		System.out.println(" materialCount: " + effect.materialCount());
		for (int index = 0; index < effect.materialCount(); index++) System.out.println("  material"+index+": " + effect.getMaterialPath(index));
		System.out.println(" modelCount: " + effect.modelCount());
		for (int index = 0; index < effect.modelCount(); index++) System.out.println("  model"+index+": " + effect.getModelPath(index));
		System.out.println(" textureCount: " + effect.textureCount());
		for (TextureType value : TextureType.values()) {
			System.out.println("  textureCount"+value.toString()+":"+effect.textureCount(value));
			for (int index = 0; index < effect.textureCount(value); index++) System.out.println("   model"+index+": " + effect.getTexturePath(index, value));
		}
		System.out.println(" isLoaded: " + effect.isLoaded());
		System.out.println(" minTerm: " + effect.minTerm());
		System.out.println(" maxTerm: " + effect.maxTerm());
	}
	
	public static float[][] matrixToArray(Matrix4f matrix) {
		return new float[][]{
				{matrix.m00, matrix.m01, matrix.m02, matrix.m02},
				{matrix.m10, matrix.m11, matrix.m12, matrix.m13},
				{matrix.m20, matrix.m21, matrix.m22, matrix.m23},
				{matrix.m30, matrix.m31, matrix.m32, matrix.m33}
		};
	}
}
