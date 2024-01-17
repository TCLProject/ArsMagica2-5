package net.tclproject.mysteriumlib.asm.fixes;

import am2.AMCore;
import am2.AMEventHandler;
import am2.LogHelper;
import am2.affinity.AffinityHelper;
import am2.api.spell.enums.Affinity;
import am2.armor.BoundArmor;
import am2.blocks.liquid.BlockLiquidEssence;
import am2.bosses.AM2Boss;
import am2.bosses.EntityWaterGuardian;
import am2.buffs.BuffList;
import am2.entities.EntityHallucination;
import am2.entities.renderers.RenderPlayerSpecial;
import am2.items.*;
import am2.items.renderers.SpellScrollRenderer;
import am2.network.*;
import am2.worldgen.dynamic.DynamicBossWorldProvider;
import com.tfc.minecraft_effekseer_implementation.*;
import com.tfc.minecraft_effekseer_implementation.common.Effek;
import com.tfc.minecraft_effekseer_implementation.common.Effeks;
import com.tfc.minecraft_effekseer_implementation.common.api.EffekEmitter;
import com.tfc.minecraft_effekseer_implementation.common.api.EffekRenderInfo;
import com.tfc.minecraft_effekseer_implementation.vector.Matrix4f;
import com.tfc.minecraft_effekseer_implementation.vector.Vector3f;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.EventBus;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ivorius.ivtoolkit.IvToolkitCoreContainer;
import ivorius.ivtoolkit.tools.IvWorldData;
import ivorius.ivtoolkit.tools.MCRegistry;
import net.ilexiconn.llibrary.client.event.RenderArmEvent;
import net.ilexiconn.llibrary.server.core.patcher.LLibraryHooks;
import net.ilexiconn.llibrary.server.util.EnumHandSide;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelMagmaCube;
import net.minecraft.client.model.ModelSkeleton;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.culling.Frustrum;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderFish;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityTracker;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.play.client.C0EPacketClickWindow;
import net.minecraft.realms.RealmsBridge;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;
import net.tclproject.mysteriumlib.asm.annotations.EnumReturnSetting;
import net.tclproject.mysteriumlib.asm.annotations.MFix;
import net.tclproject.mysteriumlib.asm.annotations.ReturnedValue;
import net.tclproject.mysteriumlib.asm.common.CustomLoadingPlugin;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.nio.FloatBuffer;
import java.util.*;

import static am2.blocks.liquid.BlockLiquidEssence.liquidEssenceMaterial;
import static am2.entities.renderers.RenderPlayerSpecial.getEntityTextureFromOutside;
import static am2.items.renderers.SpellScrollRenderer.renderFirstPersonArm;
import static org.lwjgl.BufferUtils.createFloatBuffer;

public class MysteriumPatchesFixesMagicka{

	public static List<int[]> providingRedstone = new ArrayList<int[]>();
	static int staffSlotTo = -1, staffSlotColumnTo = -1, staffSlotFrom = -1, staffSlotColumnFrom = -1;
	static int armorSlotTo = -1, armorSlotColumnTo = -1;
	static int spellSlotFrom = -1, spellSlotColumnFrom = -1;
	static boolean craftingStaffsPossible = false, craftingSpellsPossible = false, craftingArmorPossible = false;

	public static int countdownToChangeBack = -1;

	public static boolean isPlayerEthereal(EntityPlayer entityPlayer) {
		if (entityPlayer == null) return false;
		if (entityPlayer.inventory == null) return false;
		if (entityPlayer.inventory.armorInventory[0] != null) {
			if (ItemSoulspike.bootsHaveEtherealTag(entityPlayer.inventory.armorInventory[0])) {return true;}
		}
		return false;
	}


	// TCLProject Vs. Boss Farmers, Episode 6
	private static List<String> technologicalMods = new ArrayList<String>();

	public static List<String> getTechnologicalModsList() { return technologicalMods; }
	public static boolean addTechModToList(String modid) { return technologicalMods.add(modid); }
	public static boolean isItemTech(ItemStack item) { return technologicalMods.contains(getModId(item)); }

	public static String getModId(Item item) {
		GameRegistry.UniqueIdentifier id = GameRegistry.findUniqueIdentifierFor(item);
		return id == null || id.modId.equals("") ? "minecraft" : id.modId;
	}

	public static String getFullId(Item item) {
		GameRegistry.UniqueIdentifier id = GameRegistry.findUniqueIdentifierFor(item);
		if (id == null) return null;
		return id.modId.equals("") ? "minecraft:" + id.name : id.modId + ":" + id.name;
	}

	public static String getModId(ItemStack key) {
		return getModId(key.getItem());
	}

	// TCLProject Vs. Boss Farmers, Episode 5
	@MFix(returnSetting = EnumReturnSetting.ON_TRUE, booleanAlwaysReturned = false)
	public static boolean setBlock(World world, int p_147465_1_, int p_147465_2_, int p_147465_3_, Block p_147465_4_, int p_147465_5_, int p_147465_6_)
	{
		if (world.provider instanceof DynamicBossWorldProvider) return true;
		return false;
	}

	// TCLProject Vs. Boss Farmers, Episode 4
	@MFix(returnSetting = EnumReturnSetting.ON_TRUE, booleanAlwaysReturned = false)
	public static boolean canPlayerEdit(EntityPlayer ep, int p_82247_1_, int p_82247_2_, int p_82247_3_, int p_82247_4_, ItemStack p_82247_5_)
	{
		if (ep.worldObj.provider instanceof DynamicBossWorldProvider) return true;
		return false;
	}

	@MFix // Temporary fix
	public static void getProviderType(DimensionManager dm_null, int id)
	{
		if (!DimensionManager.isDimensionRegistered(id))
		{
//			throw new IllegalArgumentException(String.format("Could not get provider type for dimension %d, does not exist", dim));
			DimensionManager.registerProviderType(id, DynamicBossWorldProvider.class, false);
			DimensionManager.registerDimension(id, id);
			AMDataWriter writer = new AMDataWriter();
			writer.add(id);
			AMNetHandler.INSTANCE.sendPacketToAllClients(AMPacketIDs.SYNCDIMENSIONSTOCLIENT, writer.generate());
		}
	}

//	private static Set<Long> loadingChunks = com.google.common.collect.Sets.newHashSet();
//
//	@MFix(returnSetting = EnumReturnSetting.ON_NOT_NULL)
//	public static Chunk loadChunk(ChunkProviderServer chunkProviderServer, int par1, int par2, Runnable runnable) {
//		if (chunkProviderServer.worldObj.provider instanceof DynamicBossWorldProvider) {
//			long k = ChunkCoordIntPair.chunkXZ2Int(par1, par2);
//			boolean added = loadingChunks.add(k);
//			if (!added)
//			{
////				cpw.mods.fml.common.FMLLog.bigWarning("-- DynamicWorld -- There is an attempt to load a chunk (%d,%d) in dimension %d that is already being loaded. ABORTING in an attempt to fix!");
//				return new Chunk(chunkProviderServer.worldObj, par1, par2);
//			}
//		}
//		return null;
//	}
//
//	@MFix
//	public static void originalLoadChunk(ChunkProviderServer chunkProviderServer, int p_73158_1_, int p_73158_2_) {
//		if (chunkProviderServer.worldObj.provider instanceof DynamicBossWorldProvider) {
//			long k = ChunkCoordIntPair.chunkXZ2Int(p_73158_1_, p_73158_2_);
//			loadingChunks.remove(k);
//		}
//	}
//	// doesn't alter rendering of normal TEs, only facilitates rendering for particle TEs by setting visibility and position
//	@MFix
//	@SideOnly(Side.CLIENT)
//	public static void renderTileEntity(TileEntityRendererDispatcher tileERD, TileEntity tile, float p_147544_2_)
//	{
//		if (tile instanceof TileEntityParticle) {
//			if (tile.getDistanceFrom(tileERD.field_147560_j, tileERD.field_147561_k, tileERD.field_147558_l) < AMCore.config.getMaxRenderDistanceSq()) {
//				TileEntityParticle tileP = (TileEntityParticle)tile;
//				if (tileP.getEmitter() != null) {
//					tileP.getEmitter().setPosition(tile.xCoord + -Minecraft.getMinecraft().renderViewEntity.posX + 0.5, tile.yCoord + -Minecraft.getMinecraft().renderViewEntity.posY + 1, tile.zCoord + -Minecraft.getMinecraft().renderViewEntity.posZ);
////					tileP.getEmitter().setPosition(tile.xCoord + 0.5, tile.yCoord + 1, tile.zCoord);
//					tileP.setVisible();
//				}
//				// this is called in the original method
////				tileERD.renderTileEntityAt(tile, (double) tile.xCoord - tileERD.staticPlayerX, (double) tile.yCoord - tileERD.staticPlayerY, (double) tile.zCoord - tileERD.staticPlayerZ, p_147544_2_);
//			}
//		}
//	}

	// entity ID to render info for 3rd person
//	private static Map<Integer, EffekRenderInfo> thirdPersonRenderers = new HashMap<Integer, EffekRenderInfo>();

//	private static float arm_rotateAngleX, arm_rotateAngleY, arm_rotateAngleZ;
//	private static float body_rotateAngleX, body_rotateAngleY, body_rotateAngleZ;
//
//	// this is *NOT* a long-term solution
//	// this is a crude solution to "see if it works"
//	@MFix(insertOnExit = true)
//	@SideOnly(Side.CLIENT)
//	public static void setRotationAngles(ModelBiped mb, float p_78087_1_, float p_78087_2_, float p_78087_3_, float p_78087_4_, float p_78087_5_, float p_78087_6_, Entity p_78087_7_)
//	{
//		if (p_78087_7_ == am2.proxy.ClientProxy.mc.thePlayer) {
//			arm_rotateAngleX = mb.bipedRightArm.rotateAngleX;
//			arm_rotateAngleY = mb.bipedRightArm.rotateAngleY;
//			arm_rotateAngleZ = mb.bipedRightArm.rotateAngleZ;
//
//			body_rotateAngleX = mb.bipedBody.rotateAngleX;
//			body_rotateAngleY = mb.bipedBody.rotateAngleY;
//			body_rotateAngleZ = mb.bipedBody.rotateAngleZ;
//		}
//	}
//
//	private static float interpolateRotation(float p_77034_1_, float p_77034_2_, float p_77034_3_)
//	{
//		float f3;
//
//		for (f3 = p_77034_2_ - p_77034_1_; f3 < -180.0F; f3 += 360.0F)
//		{
//			;
//		}
//
//		while (f3 >= 180.0F)
//		{
//			f3 -= 360.0F;
//		}
//
//		return p_77034_1_ + p_77034_3_ * f3;
//	}
//
////	@MFix(returnSetting = EnumReturnSetting.ON_TRUE)
////	@SideOnly(Side.CLIENT)
////	public static boolean render(ModelBiped md, Entity p_78088_1_, float p_78088_2_, float p_78088_3_, float p_78088_4_, float p_78088_5_, float p_78088_6_, float p_78088_7_)
////	{
////		if (am2.proxy.ClientProxy.mc.thePlayer != null && am2.proxy.ClientProxy.mc.thePlayer.getHeldItem() != null
////				&& am2.proxy.ClientProxy.mc.thePlayer.getHeldItem().getItem() instanceof IEffekItem) {
////			if (p_78088_1_ == am2.proxy.ClientProxy.mc.thePlayer) {
////				md.setRotationAngles(p_78088_2_, p_78088_3_, p_78088_4_, p_78088_5_, p_78088_6_, p_78088_7_, p_78088_1_);
//////				System.out.println(((EntityLivingBase) p_78088_1_).rotationYawHead - ((EntityLivingBase) p_78088_1_).renderYawOffset - 42);
////				float f2 = interpolateRotation(am2.proxy.ClientProxy.mc.thePlayer.prevRotationYawHead, am2.proxy.ClientProxy.mc.thePlayer.rotationYawHead, Minecraft.getMinecraft().timer.renderPartialTicks);
////
//////			md.bipedLeftLeg.rotationPointX = md.bipedLeftLeg.rotationPointY = md.bipedLeftLeg.rotationPointZ = 0;
////			am2.proxy.ClientProxy.mc.thePlayer.renderYawOffset = am2.proxy.ClientProxy.mc.thePlayer.rotationYawHead;
////			GL11.glRotatef(4, 0, -1, 0);
////			GL11.glRotatef((f2 % 360) - am2.proxy.ClientProxy.mc.thePlayer.renderYawOffset % 360, 0, -1, 0);
////
//////			md.bipedLeftLeg.rotateAngleY = 20;
//////			md.bipedBody.rotateAngleY = 20;
////				if (md.isChild) {
////					float f6 = 2.0F;
////					GL11.glPushMatrix();
////					GL11.glScalef(1.5F / f6, 1.5F / f6, 1.5F / f6);
////					GL11.glTranslatef(0.0F, 16.0F * p_78088_7_, 0.0F);
////					md.bipedHead.render(p_78088_7_);
////					GL11.glPopMatrix();
////					GL11.glPushMatrix();
////					GL11.glScalef(1.0F / f6, 1.0F / f6, 1.0F / f6);
////					GL11.glTranslatef(0.0F, 24.0F * p_78088_7_, 0.0F);
////					md.bipedBody.render(p_78088_7_);
////					md.bipedRightArm.render(p_78088_7_);
////					md.bipedLeftArm.render(p_78088_7_);
////					md.bipedRightLeg.render(p_78088_7_);
////					md.bipedLeftLeg.render(p_78088_7_);
////					md.bipedHeadwear.render(p_78088_7_);
////					GL11.glPopMatrix();
////				} else {
////					md.bipedHead.render(p_78088_7_);
////					md.bipedBody.render(p_78088_7_);
////					md.bipedRightArm.render(p_78088_7_);
////					md.bipedLeftArm.render(p_78088_7_);
////					md.bipedRightLeg.render(p_78088_7_);
////					md.bipedLeftLeg.render(p_78088_7_);
////					md.bipedHeadwear.render(p_78088_7_);
////				}
////				return true;
////			}
////		}
////		return false;
////	}
//
//	// EQUIPPED render type (3rd person item)
//	@MFix(insertOnExit = true)
//	@SideOnly(Side.CLIENT)
//	public static void renderItem(ItemRenderer ir, EntityLivingBase ent, ItemStack stack, int pass)
//	{
////		if (true) return;
//		float p_78440_1_ = Minecraft.getMinecraft().timer.renderPartialTicks;
//		if (ent.getSwingProgress(p_78440_1_) > 0.01) return; // TODO. I do not want to be dealing with this right now.
//		float[] scaleValues = {0.35F, 0.28F, 0.45F};
//		float[] translationValues = {0.2F, 0.4F, 0.0F};
//		float[] rotationValues = {0F, 0F, 0F};
//		int reloadTime = 65;
//		if (am2.proxy.ClientProxy.mc.thePlayer != null && am2.proxy.ClientProxy.mc.thePlayer.getHeldItem() != null
//				&& am2.proxy.ClientProxy.mc.thePlayer.getHeldItem().getItem() instanceof IEffekItem) {
//			ItemStack heldItem = am2.proxy.ClientProxy.mc.thePlayer.getHeldItem();
//			IEffekItem heldEffekItem = ((IEffekItem)heldItem.getItem());
//			ClientProxy.thirdPersonEffekInHand = heldEffekItem.getDisplayedEffekTP(am2.proxy.ClientProxy.mc.thePlayer, heldItem);
//			scaleValues = heldEffekItem.getScaleValuesTP(am2.proxy.ClientProxy.mc.thePlayer, heldItem);
//			translationValues = heldEffekItem.getTranslationValuesTP(am2.proxy.ClientProxy.mc.thePlayer, heldItem);
//			rotationValues = heldEffekItem.getRotationValuesTP(am2.proxy.ClientProxy.mc.thePlayer, heldItem);
//			reloadTime = heldEffekItem.getEffekDurationTP(am2.proxy.ClientProxy.mc.thePlayer, heldItem);
//		} else {
//			ClientProxy.thirdPersonEffekInHand = null;
//		}
//		if (ClientProxy.thirdPersonEffekInHand != null) {
//			MEI mei = MEI.instance;
//			if (!mei.refreshed) {
//				am2.proxy.ClientProxy.mc.refreshResources();
//				mei.refreshed = true;
//			}
//			if(MEI.renderingEnabled && mei.refreshed && am2.proxy.ClientProxy.mc.thePlayer != null && am2.proxy.ClientProxy.mc.thePlayer.ticksExisted > 18 && am2.proxy.ClientProxy.mc.gameSettings.thirdPersonView != 0) {
////				am2.proxy.ClientProxy.mapHandler.setTimeSinceReload(Effeks.getTimeSinceReload() + 1);
//				if (reloadTime != -1) mei.ticksHandTP++;
//				EffekEmitter emitter = null;
//
//				GL11.glPushMatrix();
//				am2.proxy.ClientProxy.matHandTP.push();
//
//				double d7 = (double) (mei.thirdPersonDistanceTemp + (mei.thirdPersonDistance - mei.thirdPersonDistanceTemp) * p_78440_1_);
//				float f2;
//				float f6;
//
//				EntityLivingBase entitylivingbase = am2.proxy.ClientProxy.mc.renderViewEntity;
//				float f1 = entitylivingbase.yOffset - 1.62F;
//				double d0 = entitylivingbase.prevPosX + (entitylivingbase.posX - entitylivingbase.prevPosX) * (double)p_78440_1_;
//				double d1 = entitylivingbase.prevPosY + (entitylivingbase.posY - entitylivingbase.prevPosY) * (double)p_78440_1_ - (double)f1;
//				double d2 = entitylivingbase.prevPosZ + (entitylivingbase.posZ - entitylivingbase.prevPosZ) * (double)p_78440_1_;
//				f6 = entitylivingbase.rotationYaw;
//				f2 = entitylivingbase.rotationPitch;
//
//				if (am2.proxy.ClientProxy.mc.gameSettings.thirdPersonView == 2) {
//					f2 += 180.0F;
//				}
//
//				double d3 = (double) (-MathHelper.sin(f6 / 180.0F * (float) Math.PI) * MathHelper.cos(f2 / 180.0F * (float) Math.PI)) * d7;
//				double d4 = (double) (MathHelper.cos(f6 / 180.0F * (float) Math.PI) * MathHelper.cos(f2 / 180.0F * (float) Math.PI)) * d7;
//				double d5 = (double) (-MathHelper.sin(f2 / 180.0F * (float) Math.PI)) * d7;
//
//				for (int k = 0; k < 8; ++k) {
//					float f3 = (float) ((k & 1) * 2 - 1);
//					float f4 = (float) ((k >> 1 & 1) * 2 - 1);
//					float f5 = (float) ((k >> 2 & 1) * 2 - 1);
//					f3 *= 0.1F;
//					f4 *= 0.1F;
//					f5 *= 0.1F;
//					MovingObjectPosition movingobjectposition = am2.proxy.ClientProxy.mc.theWorld.rayTraceBlocks(Vec3.createVectorHelper(d0 + (double) f3, d1 + (double) f4, d2 + (double) f5), Vec3.createVectorHelper(d0 - d3 + (double) f3 + (double) f5, d1 - d5 + (double) f4, d2 - d4 + (double) f5));
//
//					if (movingobjectposition != null) {
//						double d6 = movingobjectposition.hitVec.distanceTo(Vec3.createVectorHelper(d0, d1, d2));
//
//						if (d6 < d7) {
//							d7 = d6;
//						}
//					}
//				}
//
////				am2.proxy.ClientProxy.matHandTP.rotate(Vector3f.XP.rotationDegrees(entitylivingbase.rotationPitch - f2));
////				am2.proxy.ClientProxy.matHandTP.rotate(Vector3f.YP.rotationDegrees(entitylivingbase.rotationYaw - f6));
//				am2.proxy.ClientProxy.matHandTP.translate(0.0F, 0.0F, (float) (-d7));
////				am2.proxy.ClientProxy.matHandTP.rotate(Vector3f.YP.rotationDegrees(f6 - entitylivingbase.rotationYaw));
////				am2.proxy.ClientProxy.matHandTP.rotate(Vector3f.XP.rotationDegrees(f2 - entitylivingbase.rotationPitch));
//
//				//			System.out.println(this.mc.renderViewEntity.posX);
//				// 			matHand.rotate(Vector3f.YP.rotationDegrees((activerenderinfo.getYaw() + 180F) * 0.9F));
//
//				am2.proxy.ClientProxy.activerenderinfo.update((EntityLivingBase) (am2.proxy.ClientProxy.mc.renderViewEntity == null ? am2.proxy.ClientProxy.mc.thePlayer : am2.proxy.ClientProxy.mc.renderViewEntity), p_78440_1_);
//				CameraSetup cameraSetup = new CameraSetup(am2.proxy.ClientProxy.activerenderinfo.getYaw(), am2.proxy.ClientProxy.activerenderinfo.getPitch(), 0);
//				am2.proxy.ClientProxy.activerenderinfo.setAnglesInternal(cameraSetup.getYaw(), cameraSetup.getPitch());
////				am2.proxy.ClientProxy.matHand.rotate(Vector3f.XP.rotationDegrees(am2.proxy.ClientProxy.activerenderinfo.getYaw()));
//
//				am2.proxy.ClientProxy.matHandTP.rotate(Vector3f.XP.rotationDegrees(am2.proxy.ClientProxy.mc.gameSettings.thirdPersonView == 2 ? -am2.proxy.ClientProxy.activerenderinfo.getPitch() : am2.proxy.ClientProxy.activerenderinfo.getPitch()));
////
////				am2.proxy.ClientProxy.matHand.rotate(Vector3f.ZP.rotationDegrees(cameraSetup.getRoll()));
//
//
////				double truePosX = am2.proxy.ClientProxy.activerenderinfo.getProjectedView().getX();
////				double truePosY = am2.proxy.ClientProxy.activerenderinfo.getProjectedView().getY();
////				double truePosZ = am2.proxy.ClientProxy.activerenderinfo.getProjectedView().getZ();
//
////				float f1 = ir.prevEquippedProgress + (ir.equippedProgress - ir.prevEquippedProgress) * p_78440_1_;
//				EntityClientPlayerMP entityclientplayermp = am2.proxy.ClientProxy.mc.thePlayer;
////				float f2 = entityclientplayermp.prevRotationPitch + (entityclientplayermp.rotationPitch - entityclientplayermp.prevRotationPitch) * p_78440_1_;
////				am2.proxy.ClientProxy.matHand.rotate(Vector3f.XP.rotationDegrees(f2));
////				am2.proxy.ClientProxy.matHand.rotate(Vector3f.YP.rotationDegrees(entityclientplayermp.prevRotationYaw + (entityclientplayermp.rotationYaw - entityclientplayermp.prevRotationYaw) * p_78440_1_));
////				RenderHelper.enableStandardItemLighting();
//				EntityPlayerSP entityplayersp = (EntityPlayerSP)entityclientplayermp;
//				float f3 = entityplayersp.prevRenderArmPitch + (entityplayersp.renderArmPitch - entityplayersp.prevRenderArmPitch) * p_78440_1_;
//				float f4 = entityplayersp.prevRenderArmYaw + (entityplayersp.renderArmYaw - entityplayersp.prevRenderArmYaw) * p_78440_1_;
////				am2.proxy.ClientProxy.matHand.rotate(Vector3f.XP.rotationDegrees((entityclientplayermp.rotationPitch - f3) * 0.1F));
////				am2.proxy.ClientProxy.matHand.rotate(Vector3f.YP.rotationDegrees((entityclientplayermp.rotationYaw - f4) * 0.1F));
////				GL11.glPushMatrix();
//				float f12 = 0.8F;
//				float f7 = entityclientplayermp.getSwingProgress(p_78440_1_);
//				float f8 = MathHelper.sin(f7 * (float)Math.PI);
////				float f6 = MathHelper.sin(MathHelper.sqrt_float(f7) * (float)Math.PI);
////				am2.proxy.ClientProxy.matHandTP.translate(-f6 * 0.3F, MathHelper.sin(MathHelper.sqrt_float(f7) * (float)Math.PI * 2.0F) * 0.4F, -f8 * 0.4F);
//				am2.proxy.ClientProxy.matHandTP.translate(0.8F * f12, -0.75F * f12 - (1.0F - 1) * 0.6F, -0.9F * f12);
////				am2.proxy.ClientProxy.matHandTP.rotate(Vector3f.YP.rotationDegrees(45.0F));
//				if (am2.proxy.ClientProxy.mc.gameSettings.thirdPersonView == 2) { // from the front
////					GL11.glRotatef(199, 1, 0, 0);
//					// .75 (-90) to 1.075 (0)
//					float rotationCorrection = interpolateRotation(ent.rotationYawHead, ent.prevRotationYawHead, p_78440_1_) - interpolateRotation(ent.renderYawOffset, ent.prevRenderYawOffset, p_78440_1_) - 42;
//					am2.proxy.ClientProxy.matHandTP.translate(0, -0.17, 1.075 + (rotationCorrection * 0.0036));
////					System.out.println(ent.rotationYawHead - ent.renderYawOffset - 42);
//					//					GL11.glRotatef(ent.rotationYawHead - ent.renderYawOffset - 42, 0, 1f, 0);
////					am2.proxy.ClientProxy.matHandTP.rotate(Vector3f.XP.rotationDegrees(30));
//
//					am2.proxy.ClientProxy.matHandTP.rotate(Vector3f.YP.rotationDegrees(rotationCorrection));
////					am2.proxy.ClientProxy.matHandTP.scale(1, 1, 2f);
//
////					double truePosX = am2.proxy.ClientProxy.activerenderinfo.getProjectedView().getX();
////					double truePosZ = am2.proxy.ClientProxy.activerenderinfo.getProjectedView().getZ();
////
////					double[] transformNeeded = MEI.getPointOnCircle(MathHelper.wrapAngleTo180_float(am2.proxy.ClientProxy.mc.thePlayer.rotationYaw) + 180F, truePosX, truePosZ, 0.5, 0.5);
////					am2.proxy.ClientProxy.matHandTP.translate(transformNeeded[0], 0, transformNeeded[1]);
////					EntityLivingBase p_76986_1_ = ent;
////					float p_76986_9_ = p_78440_1_; // partial ticks
////					float f211 = interpolateRotation(p_76986_1_.prevRenderYawOffset, p_76986_1_.renderYawOffset, p_76986_9_);
////					float f311 = interpolateRotation(p_76986_1_.prevRotationYawHead, p_76986_1_.rotationYawHead, p_76986_9_);
////					float f411;
////
////					if (p_76986_1_.isRiding() && p_76986_1_.ridingEntity instanceof EntityLivingBase)
////					{
////						EntityLivingBase entitylivingbase1 = (EntityLivingBase)p_76986_1_.ridingEntity;
////						f211 = interpolateRotation(entitylivingbase1.prevRenderYawOffset, entitylivingbase1.renderYawOffset, p_76986_9_);
////						f411 = MathHelper.wrapAngleTo180_float(f311 - f211);
////
////						if (f411 < -85.0F)
////						{
////							f411 = -85.0F;
////						}
////
////						if (f411 >= 85.0F)
////						{
////							f411 = 85.0F;
////						}
////
////						f211 = f311 - f411;
////
////						if (f411 * f411 > 2500.0F)
////						{
////							f211 += f411 * 0.2F;
////						}
////					}
////
//////					am2.proxy.ClientProxy.matHandTP.rotate(Vector3f.YP.rotationDegrees(180.0F - f211));
//////					System.out.println(am2.proxy.ClientProxy.activerenderinfo.getYaw() );
//////					am2.proxy.ClientProxy.matHandTP.rotate(Vector3f.YN.rotationDegrees(am2.proxy.ClientProxy.activerenderinfo.getYaw() % 180));
////					float f511 = 0.0625F;
//////					GL11.glScalef(-1.0F, -1.0F, 1.0F);
//////					am2.proxy.ClientProxy.matHandTP.translate(0.0F, -24.0F * f511 - 0.0078125F, 0.0F);
//////					System.out.println("34234");
////					this.renderModel(p_76986_1_, f711, f611, f411, f311 - f211, f1311, f511);
//					// END
//				} else { // (== 1) from the back
//					am2.proxy.ClientProxy.matHandTP.translate(0, -0.17, 0.25);
//				}
////				am2.proxy.ClientProxy.matHand.rotate(Vector3f.ZP.rotationDegrees(15F));
//				GL11.glEnable(GL12.GL_RESCALE_NORMAL);
//
////				f7 = entityclientplayermp.getSwingProgress(p_78440_1_);
////				f8 = MathHelper.sin(f7 * f7 * (float)Math.PI);
////				f6 = MathHelper.sin(MathHelper.sqrt_float(f7) * (float)Math.PI);
//				//start swing rotation
////				float f1333 = 0.8F;
////				float f111 = ir.prevEquippedProgress + (ir.equippedProgress - ir.prevEquippedProgress) * p_78440_1_;
////				float f555 = entityclientplayermp.getSwingProgress(p_78440_1_);
////				float f666 = MathHelper.sin(f555 * (float)Math.PI);
////				float f777 = MathHelper.sin(MathHelper.sqrt_float(f555) * (float)Math.PI);
////				//am2.proxy.ClientProxy.matHand.translate(-f777 * 0.4F, MathHelper.sin(MathHelper.sqrt_float(f555) * (float)Math.PI * 2.0F) * 0.2F, -f666 * 0.2F);
////				//am2.proxy.ClientProxy.matHand.translate(0.7F * f1333, -0.65F * f1333 - (1.0F - f111) * 0.6F, -0.9F * f1333);
////				f555 = entityclientplayermp.getSwingProgress(p_78440_1_);
////				f666 = MathHelper.sin(f555 * f555 * (float)Math.PI);
////				f777 = MathHelper.sin(MathHelper.sqrt_float(f555) * (float)Math.PI);
////				am2.proxy.ClientProxy.matHandTP.rotate(Vector3f.YP.rotationDegrees(-f666 * 20.0F));
////				am2.proxy.ClientProxy.matHandTP.rotate(Vector3f.ZP.rotationDegrees(-f777 * 20.0F));
////				am2.proxy.ClientProxy.matHandTP.rotate(Vector3f.XP.rotationDegrees(-f777 * 80.0F));
//				//end swing rotation
//
////				am2.proxy.ClientProxy.matHand.rotate(Vector3f.YP.rotationDegrees(f6 * 70.0F));
////				am2.proxy.ClientProxy.matHand.rotate(Vector3f.ZP.rotationDegrees(-f8 * 20.0F));
////				am2.proxy.ClientProxy.matHand.translate(-1.0F, 3.6F, 3.5F);
////				am2.proxy.ClientProxy.matHand.rotate(Vector3f.XP.rotationDegrees(120.0F));
////				am2.proxy.ClientProxy.matHand.rotate(Vector3f.XP.rotationDegrees(200.0F));
////				am2.proxy.ClientProxy.matHand.rotate(Vector3f.YP.rotationDegrees(-135.0F));
//				am2.proxy.ClientProxy.matHandTP.scale(scaleValues[0], scaleValues[1], scaleValues[2]);
//				am2.proxy.ClientProxy.matHandTP.rotate(Vector3f.XP.rotationDegrees(rotationValues[0]));
//				am2.proxy.ClientProxy.matHandTP.rotate(Vector3f.YP.rotationDegrees(rotationValues[1]));
//				am2.proxy.ClientProxy.matHandTP.rotate(Vector3f.ZP.rotationDegrees(rotationValues[2]));
//				am2.proxy.ClientProxy.matHandTP.translate(translationValues[0], translationValues[1], translationValues[2]);
////				double[] transformNeeded = {0,0};
////				double[] transformNeeded = MEI.getPointOnCircle(MathHelper.wrapAngleTo180_float(am2.proxy.ClientProxy.mc.thePlayer.rotationYaw) + 180F, truePosX, truePosZ, 0.5, 0.5);
//
//				emitter = ClientProxy.thirdPersonEffekInHand.getOrCreate("localthirdperson:hand" + 1);
//				emitter.setPosition(0, 0, 0);
//				if (reloadTime != -1 && mei.ticksHandTP > reloadTime) {
//					mei.ticksHandTP = 0;
//					emitter.setPlayProgress(0);
////					System.out.println("111");
////					EffekseerMCAssetLoader.INSTANCE.onResourceManagerReload(Minecraft.getMinecraft().getResourceManager());
////					ClientProxy.effekInHand.delete(emitter);
//				}
//
//				// breaks here, as this isn't 3rd person code
////				EntityLivingBase entitylivingbase = Minecraft.getMinecraft().renderViewEntity;
////				double x = entitylivingbase.lastTickPosX + (entitylivingbase.posX - entitylivingbase.lastTickPosX) * Minecraft.getMinecraft().timer.renderPartialTicks;
////				double y = entitylivingbase.lastTickPosY + (entitylivingbase.posY - entitylivingbase.lastTickPosY) * Minecraft.getMinecraft().timer.renderPartialTicks;
////				double z = entitylivingbase.lastTickPosZ + (entitylivingbase.posZ - entitylivingbase.lastTickPosZ) * Minecraft.getMinecraft().timer.renderPartialTicks;
////				Frustrum frustrum = new Frustrum();
////				frustrum.setPosition(x, y, z);
//
//				float diff = 1;
//				if (MEI.lastFrameHandTP != -1) {
//					long currentTime = System.currentTimeMillis();
//					diff = (Math.abs(currentTime - MEI.lastFrameHandTP) / 1000f) * 60;
//				}
//
//				diff = Math.min(1.5f, diff);
//
//				MEI.lastFrameHandTP = System.currentTimeMillis();
//
//				Matrix4f matrix = am2.proxy.ClientProxy.matHandTP.getLast().getMatrix();
//				float[][] cameraMatrix = MEI.matrixToArray(matrix);
////				GL11.glDisable(GL11.GL_CULL_FACE);
////				GL11.glDisable(GL11.GL_ALPHA_TEST);
////				GL11.glDisable(GL11.GL_DEPTH_TEST);
//
//				am2.proxy.ClientProxy.matHandTP.pop();
//
//				//		matrix = Minecraft.getMinecraft().entityRenderer.theShaderGroup.projectionMatrix;
//				//		float[][] projectionMatrix = matrixToArray(matrix);
//
//				// if the above won't work
//				FloatBuffer b2 = createFloatBuffer(16);
//				GL11.glGetFloat(GL11.GL_PROJECTION_MATRIX, b2);
//				float[] matrix2arr = new float[] {
//						b2.get(), b2.get(), b2.get(), b2.get(),
//						b2.get(), b2.get(), b2.get(), b2.get(),
//						b2.get(), b2.get(), b2.get(), b2.get(),
//						b2.get(), b2.get(), b2.get(), b2.get()};
//				float[] actualMatrix2arr = new float[] {
//						matrix2arr[0], matrix2arr[1], matrix2arr[2], matrix2arr[3],
//						matrix2arr[4], matrix2arr[5], matrix2arr[6], matrix2arr[7],
//						matrix2arr[8], matrix2arr[9], matrix2arr[10], matrix2arr[14],
//						matrix2arr[12], matrix2arr[13], matrix2arr[11], matrix2arr[15]
//
//				};
//
//				Matrix4f matrix2 = new Matrix4f(actualMatrix2arr);
//				float[][] projectionMatrix = MEI.matrixToArray(matrix2);
//				final float finalDiff = diff;
//				Effeks.forEachThirdPersonHand((name, effect) -> effect.draw(cameraMatrix, projectionMatrix, finalDiff));
//				GL11.glEnable(GL11.GL_CULL_FACE);
//				GL11.glEnable(GL11.GL_DEPTH_TEST);
//				GL11.glEnable(GL11.GL_ALPHA_TEST);
//				GL11.glPopMatrix();
//			}
//		}
//	}

	private static FloatBuffer setColorBuffer(double p_74517_0_, double p_74517_2_, double p_74517_4_, double p_74517_6_)
	{
		return setColorBuffer((float)p_74517_0_, (float)p_74517_2_, (float)p_74517_4_, (float)p_74517_6_);
	}
	private static FloatBuffer colorBuffer = GLAllocation.createDirectFloatBuffer(16);
	private static final Vec3 field_82884_b = Vec3.createVectorHelper(0.20000000298023224D, 1.0D, -0.699999988079071D).normalize();
	private static final Vec3 field_82885_c = Vec3.createVectorHelper(-0.20000000298023224D, 1.0D, 0.699999988079071D).normalize();

	private static FloatBuffer setColorBuffer(float p_74521_0_, float p_74521_1_, float p_74521_2_, float p_74521_3_)
	{
		colorBuffer.clear();
		colorBuffer.put(p_74521_0_).put(p_74521_1_).put(p_74521_2_).put(p_74521_3_);
		colorBuffer.flip();
		return colorBuffer;
	}

	private static void enableCustomShading() {
//	    GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_LIGHT0);
		GL11.glEnable(GL11.GL_LIGHT1);
//	    GL11.glEnable(GL11.GL_COLOR_MATERIAL);
		GL11.glColorMaterial(GL11.GL_FRONT_AND_BACK, GL11.GL_AMBIENT_AND_DIFFUSE);
		float f = 0.4F;
		float f1 = 0.6F;
		float f2 = 0.0F;
		GL11.glLight(GL11.GL_LIGHT0, GL11.GL_POSITION, setColorBuffer(field_82884_b.xCoord, field_82884_b.yCoord, field_82884_b.zCoord, 0.0D));
		GL11.glLight(GL11.GL_LIGHT0, GL11.GL_DIFFUSE, setColorBuffer(f1, f1, f1, 1.0F));
		GL11.glLight(GL11.GL_LIGHT0, GL11.GL_AMBIENT, setColorBuffer(0.0F, 0.0F, 0.0F, 1.0F));
		GL11.glLight(GL11.GL_LIGHT0, GL11.GL_SPECULAR, setColorBuffer(f2, f2, f2, 1.0F));
		GL11.glLight(GL11.GL_LIGHT1, GL11.GL_POSITION, setColorBuffer(field_82885_c.xCoord, field_82885_c.yCoord, field_82885_c.zCoord, 0.0D));
		GL11.glLight(GL11.GL_LIGHT1, GL11.GL_DIFFUSE, setColorBuffer(f1, f1, f1, 1.0F));
		GL11.glLight(GL11.GL_LIGHT1, GL11.GL_AMBIENT, setColorBuffer(0.0F, 0.0F, 0.0F, 1.0F));
		GL11.glLight(GL11.GL_LIGHT1, GL11.GL_SPECULAR, setColorBuffer(f2, f2, f2, 1.0F));
		GL11.glShadeModel(GL11.GL_FLAT);
		GL11.glLightModel(GL11.GL_LIGHT_MODEL_AMBIENT, setColorBuffer(f, f, f, 1.0F));
	}

	public static boolean insideLoop = false;

	@MFix(targetMethod = "renderItem", returnSetting = EnumReturnSetting.ON_TRUE)
	@SideOnly(Side.CLIENT)
	public static boolean renderItemInFirstPersonStart(ItemRenderer ir, EntityLivingBase p_78443_1_, ItemStack p_78443_2_, int p_78443_3_, IItemRenderer.ItemRenderType type)
	{
		if (insideLoop) return false;
		boolean doNotContinueExecuting = false;
		if (am2.proxy.ClientProxy.mc.thePlayer != null && am2.proxy.ClientProxy.mc.thePlayer.getHeldItem() != null
				&& am2.proxy.ClientProxy.mc.thePlayer.getHeldItem().getItem() instanceof IEffekItem) {
			ItemStack heldItem = am2.proxy.ClientProxy.mc.thePlayer.getHeldItem();
			IEffekItem heldEffekItem = ((IEffekItem) heldItem.getItem());
			if (heldEffekItem.getDisplayedEffek(am2.proxy.ClientProxy.mc.thePlayer, heldItem) != null
					|| heldEffekItem.useHandRenderAlways(am2.proxy.ClientProxy.mc.thePlayer, heldItem)) {
				switch (heldEffekItem.getHandRenderType(am2.proxy.ClientProxy.mc.thePlayer, heldItem)) {
					case RENDER_ITEM:
						return false; // continue rendering the item
					case RENDER_DEFAULT_HAND:
						GL11.glPushMatrix();
						float f8;
						float f9;
						float f10;
						float f13;
						Render render;
						RenderPlayer renderplayer;
						float f5;
						float f6;
						float f7;
						float p_78440_1_ = am2.proxy.ClientProxy.mc.timer.renderPartialTicks;
						f13 = 0.8F;
						float f1 = ir.prevEquippedProgress + (ir.equippedProgress - ir.prevEquippedProgress) * p_78440_1_;
						EntityClientPlayerMP entityclientplayermp = am2.proxy.ClientProxy.mc.thePlayer;
						f5 = entityclientplayermp.getSwingProgress(p_78440_1_);
						f6 = MathHelper.sin(f5 * (float)Math.PI);
						f7 = MathHelper.sin(MathHelper.sqrt_float(f5) * (float)Math.PI);
						GL11.glTranslatef(-f7 * 0.3F, MathHelper.sin(MathHelper.sqrt_float(f5) * (float)Math.PI * 2.0F) * 0.4F, -f6 * 0.4F);
						GL11.glTranslatef(0.8F * f13, -0.75F * f13 - (1.0F - f1) * 0.6F, -0.9F * f13);
						GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
						GL11.glEnable(GL12.GL_RESCALE_NORMAL);
						f5 = entityclientplayermp.getSwingProgress(p_78440_1_);
						f6 = MathHelper.sin(f5 * f5 * (float)Math.PI);
						f7 = MathHelper.sin(MathHelper.sqrt_float(f5) * (float)Math.PI);
						GL11.glRotatef(f7 * 70.0F, 0.0F, 1.0F, 0.0F);
						GL11.glRotatef(-f6 * 20.0F, 0.0F, 0.0F, 1.0F);
						am2.proxy.ClientProxy.mc.getTextureManager().bindTexture(entityclientplayermp.getLocationSkin());
						GL11.glTranslatef(-4.15F, 3.20F, 2.0F);
						GL11.glRotatef(119.0F, 0.0F, 0.0F, 1.0F);
						GL11.glRotatef(160.0F, 1.0F, 0.0F, 0.0F);
						GL11.glRotatef(-150.0F, 0.0F, 1.0F, 0.0F);
						GL11.glScalef(1.0F, 1.0F, 1.0F);
						GL11.glTranslatef(5.6F, 0.0F, 0.0F);
						render = RenderManager.instance.getEntityRenderObject(am2.proxy.ClientProxy.mc.thePlayer);
						renderplayer = (RenderPlayer)render;
						f10 = 2.3F;
						GL11.glScalef(f10, f10, f10);
						renderplayer.renderFirstPersonArm(am2.proxy.ClientProxy.mc.thePlayer);
						GL11.glPopMatrix();
						doNotContinueExecuting = false;
						break;
					case RENDER_SPELL_HAND:
//						float scale = 3f;
//						GL11.glPushMatrix();
//						GL11.glEnable(32826 /*GL_RESCALE_NORMAL_EXT*/);
//						GL11.glEnable(3042);
//						GL11.glEnable(GL11.GL_BLEND);
//						GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
//						// -------
//						if (am2.proxy.ClientProxy.mc.thePlayer.getItemInUseCount() > 0){
//							GL11.glRotatef(-130, 0, 1, 0);
//							GL11.glTranslatef(-1f, 0.2f, -2.5f);
//						}
//
////						GL11.glScalef(scale, scale, scale);
////						GL11.glTranslatef(0f, 0.6f, 1.1f);
////						GL11.glRotatef(-15, 0, 1, 0);
//						if (playerModelMap.get(am2.proxy.ClientProxy.mc.thePlayer.getCommandSenderName()) != null && playerModelMap.get(am2.proxy.ClientProxy.mc.thePlayer.getCommandSenderName()).startsWith("maid")) {
//							Minecraft.getMinecraft().renderEngine.bindTexture(getEntityTextureFromOutside(am2.proxy.ClientProxy.mc.thePlayer));
//							renderFirstPersonArm(am2.proxy.ClientProxy.mc.thePlayer, getEntityTextureFromOutside(am2.proxy.ClientProxy.mc.thePlayer));
//						} else {
//							Minecraft.getMinecraft().renderEngine.bindTexture(am2.proxy.ClientProxy.mc.thePlayer.getLocationSkin());
//							renderFirstPersonArm(am2.proxy.ClientProxy.mc.thePlayer, am2.proxy.ClientProxy.mc.thePlayer.getLocationSkin());
//						}
//						// -------
//						GL11.glDisable(32826 /*GL_RESCALE_NORMAL_EXT*/);
//						GL11.glDisable(GL11.GL_BLEND);
//						GL11.glDisable(3042);
//						GL11.glPopMatrix();
						GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
						SpellScrollRenderer.instance.renderEffect(Affinity.FIRE, true, RenderBlocks.getInstance(), am2.proxy.ClientProxy.mc.thePlayer, false);
//						renderItemInFirstPersonEnd(ir, am2.proxy.ClientProxy.mc.timer.renderPartialTicks);
//						RenderHelper.disableStandardItemLighting();
						doNotContinueExecuting = false;
						break;
				}
			}
		}
		return doNotContinueExecuting;
	}

	@MFix(insertOnExit = true, targetMethod = "renderItemInFirstPerson")
	@SideOnly(Side.CLIENT)
	public static void renderItemInFirstPersonEnd(ItemRenderer ir, float p_78440_1_)
	{
//		ClientProxy.effekInHand = Effeks.get("arsmagica2:effeks_hand_firstperson0");
		float[] scaleValues = {0.35F, 0.28F, 0.45F};
		float[] translationValues = {0.2F, 0.4F, 0.0F};
		float[] rotationValues = {0F, 0F, 0F};
		int reloadTime = 65;
		boolean bobEffek = true;
		int doSwingOffset = 1;
		if (am2.proxy.ClientProxy.mc.thePlayer != null && am2.proxy.ClientProxy.mc.thePlayer.getHeldItem() != null
				&& am2.proxy.ClientProxy.mc.thePlayer.getHeldItem().getItem() instanceof IEffekItem) {
			ItemStack heldItem = am2.proxy.ClientProxy.mc.thePlayer.getHeldItem();
			IEffekItem heldEffekItem = ((IEffekItem)heldItem.getItem());
			ClientProxy.effekInHand = heldEffekItem.getDisplayedEffek(am2.proxy.ClientProxy.mc.thePlayer, heldItem);
			scaleValues = heldEffekItem.getScaleValues(am2.proxy.ClientProxy.mc.thePlayer, heldItem);
			translationValues = heldEffekItem.getTranslationValues(am2.proxy.ClientProxy.mc.thePlayer, heldItem);
			rotationValues = heldEffekItem.getRotationValues(am2.proxy.ClientProxy.mc.thePlayer, heldItem);
			reloadTime = heldEffekItem.getEffekDuration(am2.proxy.ClientProxy.mc.thePlayer, heldItem);
			bobEffek = heldEffekItem.bobEffek(am2.proxy.ClientProxy.mc.thePlayer, heldItem);
			doSwingOffset = heldEffekItem.doSwingOffset(am2.proxy.ClientProxy.mc.thePlayer, heldItem);
		} else {
			ClientProxy.effekInHand = null;
		}
		if (ClientProxy.effekInHand != null) {
			MEI mei = MEI.instance;
			if (!mei.refreshed) {
				am2.proxy.ClientProxy.mc.refreshResources();
				mei.refreshed = true;
			}
			if(MEI.renderingEnabled && mei.refreshed && am2.proxy.ClientProxy.mc.thePlayer != null && am2.proxy.ClientProxy.mc.thePlayer.ticksExisted > 18 && am2.proxy.ClientProxy.mc.gameSettings.thirdPersonView == 0) {
//				am2.proxy.ClientProxy.mapHandler.setTimeSinceReload(Effeks.getTimeSinceReload() + 1);
				if (reloadTime != -1) mei.ticksHand++;
				EffekEmitter emitter = null;

				GL11.glPushMatrix();
				am2.proxy.ClientProxy.matHand.push();

	//			System.out.println(this.mc.renderViewEntity.posX);
	// 			matHand.rotate(Vector3f.YP.rotationDegrees((activerenderinfo.getYaw() + 180F) * 0.9F));

				am2.proxy.ClientProxy.activerenderinfo.update((EntityLivingBase) (am2.proxy.ClientProxy.mc.renderViewEntity == null ? am2.proxy.ClientProxy.mc.thePlayer : am2.proxy.ClientProxy.mc.renderViewEntity), p_78440_1_);
				CameraSetup cameraSetup = new CameraSetup(am2.proxy.ClientProxy.activerenderinfo.getYaw(), am2.proxy.ClientProxy.activerenderinfo.getPitch(), 0);
				am2.proxy.ClientProxy.activerenderinfo.setAnglesInternal(cameraSetup.getYaw(), cameraSetup.getPitch());
//				am2.proxy.ClientProxy.matHand.rotate(Vector3f.XP.rotationDegrees(am2.proxy.ClientProxy.activerenderinfo.getYaw()));

//				am2.proxy.ClientProxy.matHand.rotate(Vector3f.XP.rotationDegrees(am2.proxy.ClientProxy.activerenderinfo.getPitch()));
//
//				am2.proxy.ClientProxy.matHand.rotate(Vector3f.ZP.rotationDegrees(cameraSetup.getRoll()));


//				double truePosX = am2.proxy.ClientProxy.activerenderinfo.getProjectedView().getX();
//				double truePosY = am2.proxy.ClientProxy.activerenderinfo.getProjectedView().getY();
//				double truePosZ = am2.proxy.ClientProxy.activerenderinfo.getProjectedView().getZ();

//				float f1 = ir.prevEquippedProgress + (ir.equippedProgress - ir.prevEquippedProgress) * p_78440_1_;
				EntityClientPlayerMP entityclientplayermp = am2.proxy.ClientProxy.mc.thePlayer;
				float f2 = entityclientplayermp.prevRotationPitch + (entityclientplayermp.rotationPitch - entityclientplayermp.prevRotationPitch) * p_78440_1_;
//				am2.proxy.ClientProxy.matHand.rotate(Vector3f.XP.rotationDegrees(f2));
//				am2.proxy.ClientProxy.matHand.rotate(Vector3f.YP.rotationDegrees(entityclientplayermp.prevRotationYaw + (entityclientplayermp.rotationYaw - entityclientplayermp.prevRotationYaw) * p_78440_1_));
//				RenderHelper.enableStandardItemLighting();
				EntityPlayerSP entityplayersp = (EntityPlayerSP)entityclientplayermp;
				float f3 = entityplayersp.prevRenderArmPitch + (entityplayersp.renderArmPitch - entityplayersp.prevRenderArmPitch) * p_78440_1_;
				float f4 = entityplayersp.prevRenderArmYaw + (entityplayersp.renderArmYaw - entityplayersp.prevRenderArmYaw) * p_78440_1_;
//				am2.proxy.ClientProxy.matHand.rotate(Vector3f.XP.rotationDegrees((entityclientplayermp.rotationPitch - f3) * 0.1F));
//				am2.proxy.ClientProxy.matHand.rotate(Vector3f.YP.rotationDegrees((entityclientplayermp.rotationYaw - f4) * 0.1F));
//				GL11.glPushMatrix();
				float f12 = 0.8F;
				float f7 = entityclientplayermp.getSwingProgress(p_78440_1_);
				float f8 = MathHelper.sin(f7 * (float)Math.PI);
				float f6 = MathHelper.sin(MathHelper.sqrt_float(f7) * (float)Math.PI);
				am2.proxy.ClientProxy.matHand.translate(-f6 * 0.3F, MathHelper.sin(MathHelper.sqrt_float(f7) * (float)Math.PI * 2.0F) * 0.4F, -f8 * 0.4F);
				am2.proxy.ClientProxy.matHand.translate(0.8F * f12, -0.75F * f12 - (1.0F - 1) * 0.6F, -0.9F * f12);
				am2.proxy.ClientProxy.matHand.rotate(Vector3f.YP.rotationDegrees(45.0F));
//				am2.proxy.ClientProxy.matHand.rotate(Vector3f.ZP.rotationDegrees(15F));
				GL11.glEnable(GL12.GL_RESCALE_NORMAL);
				f7 = entityclientplayermp.getSwingProgress(p_78440_1_);
//				f8 = MathHelper.sin(f7 * f7 * (float)Math.PI);
//				f6 = MathHelper.sin(MathHelper.sqrt_float(f7) * (float)Math.PI);
				if (bobEffek) {
					EntityPlayer entityplayer = (EntityPlayer)am2.proxy.ClientProxy.mc.renderViewEntity;
					float f11 = entityplayer.distanceWalkedModified - entityplayer.prevDistanceWalkedModified;
					float f22 = -(entityplayer.distanceWalkedModified + f11 * p_78440_1_);
					float f33 = entityplayer.prevCameraYaw + (entityplayer.cameraYaw - entityplayer.prevCameraYaw) * p_78440_1_;
					float f44 = entityplayer.prevCameraPitch + (entityplayer.cameraPitch - entityplayer.prevCameraPitch) * p_78440_1_;
					am2.proxy.ClientProxy.matHand.translate(-(MathHelper.sin(f22 * (float)Math.PI) * f33 * 0.5F), -Math.abs(MathHelper.cos(f22 * (float)Math.PI) * f33), 0.0F);
					am2.proxy.ClientProxy.matHand.rotate(Vector3f.ZP.rotationDegrees(MathHelper.sin(f22 * (float)Math.PI) * f33 * 3.0F));
					am2.proxy.ClientProxy.matHand.rotate(Vector3f.XP.rotationDegrees(Math.abs(MathHelper.cos(f22 * (float)Math.PI - 0.2F) * f33) * 5.0F));
					am2.proxy.ClientProxy.matHand.rotate(Vector3f.XP.rotationDegrees(f44));
				}
				//start swing rotation
				float f1333 = 0.8F;
				float f111 = ir.prevEquippedProgress + (ir.equippedProgress - ir.prevEquippedProgress) * p_78440_1_;
				float f555 = entityclientplayermp.getSwingProgress(p_78440_1_);
				float f666 = MathHelper.sin(f555 * (float)Math.PI);
				float f777 = MathHelper.sin(MathHelper.sqrt_float(f555) * (float)Math.PI);
				//am2.proxy.ClientProxy.matHand.translate(-f777 * 0.4F, MathHelper.sin(MathHelper.sqrt_float(f555) * (float)Math.PI * 2.0F) * 0.2F, -f666 * 0.2F);
				//am2.proxy.ClientProxy.matHand.translate(0.7F * f1333, -0.65F * f1333 - (1.0F - f111) * 0.6F, -0.9F * f1333);
				f555 = entityclientplayermp.getSwingProgress(p_78440_1_);
				f666 = MathHelper.sin(f555 * f555 * (float)Math.PI);
				f777 = MathHelper.sin(MathHelper.sqrt_float(f555) * (float)Math.PI);
				am2.proxy.ClientProxy.matHand.rotate(Vector3f.YP.rotationDegrees(-f666 * 20.0F));
				am2.proxy.ClientProxy.matHand.rotate(Vector3f.ZP.rotationDegrees(-f777 * 20.0F));
				am2.proxy.ClientProxy.matHand.rotate(Vector3f.XP.rotationDegrees(-f777 * 80.0F));
				if (f555 > 0) {
					if (doSwingOffset == 1) am2.proxy.ClientProxy.matHand.translate(0.25F, 0, -0.5F); // offset
					else if (doSwingOffset == 2) am2.proxy.ClientProxy.matHand.scale(0F, 0F, 0F); // hide
				}
				//end swing rotation

//				am2.proxy.ClientProxy.matHand.rotate(Vector3f.YP.rotationDegrees(f6 * 70.0F));
//				am2.proxy.ClientProxy.matHand.rotate(Vector3f.ZP.rotationDegrees(-f8 * 20.0F));
//				am2.proxy.ClientProxy.matHand.translate(-1.0F, 3.6F, 3.5F);
//				am2.proxy.ClientProxy.matHand.rotate(Vector3f.XP.rotationDegrees(120.0F));
//				am2.proxy.ClientProxy.matHand.rotate(Vector3f.XP.rotationDegrees(200.0F));
//				am2.proxy.ClientProxy.matHand.rotate(Vector3f.YP.rotationDegrees(-135.0F));
				am2.proxy.ClientProxy.matHand.scale(scaleValues[0], scaleValues[1], scaleValues[2]); // this works now
				am2.proxy.ClientProxy.matHand.rotate(Vector3f.XP.rotationDegrees(rotationValues[0]));
				am2.proxy.ClientProxy.matHand.rotate(Vector3f.YP.rotationDegrees(rotationValues[1]));
				am2.proxy.ClientProxy.matHand.rotate(Vector3f.ZP.rotationDegrees(rotationValues[2]));
				am2.proxy.ClientProxy.matHand.translate(translationValues[0], translationValues[1], translationValues[2]);
//				double[] transformNeeded = {0,0};
//				double[] transformNeeded = MEI.getPointOnCircle(MathHelper.wrapAngleTo180_float(am2.proxy.ClientProxy.mc.thePlayer.rotationYaw) + 180F, truePosX, truePosZ, 0.5, 0.5);

				emitter = ClientProxy.effekInHand.getOrCreate("localfirstperson:hand" + 1);
				emitter.setPosition(0, 0, 0);
				if (reloadTime != -1 && mei.ticksHand > reloadTime) {
					mei.ticksHand = 0;
					emitter.setPlayProgress(0);
//					System.out.println("111");
//					EffekseerMCAssetLoader.INSTANCE.onResourceManagerReload(Minecraft.getMinecraft().getResourceManager());
//					ClientProxy.effekInHand.delete(emitter);
				}

				EntityLivingBase entitylivingbase = Minecraft.getMinecraft().renderViewEntity;
				double x = entitylivingbase.lastTickPosX + (entitylivingbase.posX - entitylivingbase.lastTickPosX) * Minecraft.getMinecraft().timer.renderPartialTicks;
				double y = entitylivingbase.lastTickPosY + (entitylivingbase.posY - entitylivingbase.lastTickPosY) * Minecraft.getMinecraft().timer.renderPartialTicks;
				double z = entitylivingbase.lastTickPosZ + (entitylivingbase.posZ - entitylivingbase.lastTickPosZ) * Minecraft.getMinecraft().timer.renderPartialTicks;
				Frustrum frustrum = new Frustrum();
				frustrum.setPosition(x, y, z);

				float diff = 1;
				if (MEI.lastFrameHand != -1) {
					long currentTime = System.currentTimeMillis();
					diff = (Math.abs(currentTime - MEI.lastFrameHand) / 1000f) * 60;
				}

				diff = Math.min(1.5f, diff);

				MEI.lastFrameHand = System.currentTimeMillis();

				Matrix4f matrix = am2.proxy.ClientProxy.matHand.getLast().getMatrix();
				float[][] cameraMatrix = MEI.matrixToArray(matrix);
//				GL11.glDisable(GL11.GL_CULL_FACE);
//				GL11.glDisable(GL11.GL_ALPHA_TEST);
//				GL11.glDisable(GL11.GL_DEPTH_TEST);

				am2.proxy.ClientProxy.matHand.pop();

				//		matrix = Minecraft.getMinecraft().entityRenderer.theShaderGroup.projectionMatrix;
				//		float[][] projectionMatrix = matrixToArray(matrix);

				// if the above won't work
				FloatBuffer b2 = createFloatBuffer(16);
				GL11.glGetFloat(GL11.GL_PROJECTION_MATRIX, b2);
				float[] matrix2arr = new float[] {
						b2.get(), b2.get(), b2.get(), b2.get(),
						b2.get(), b2.get(), b2.get(), b2.get(),
						b2.get(), b2.get(), b2.get(), b2.get(),
						b2.get(), b2.get(), b2.get(), b2.get()};
				float[] actualMatrix2arr = new float[] {
						matrix2arr[0], matrix2arr[1], matrix2arr[2], matrix2arr[3],
						matrix2arr[4], matrix2arr[5], matrix2arr[6], matrix2arr[7],
						matrix2arr[8], matrix2arr[9], matrix2arr[10], matrix2arr[14],
						matrix2arr[12], matrix2arr[13], matrix2arr[11], matrix2arr[15]

				};

				Matrix4f matrix2 = new Matrix4f(actualMatrix2arr);
				float[][] projectionMatrix = MEI.matrixToArray(matrix2);
				final float finalDiff = diff;
				Effeks.forEachHand((name, effect) -> effect.draw(cameraMatrix, projectionMatrix, finalDiff));
				GL11.glEnable(GL11.GL_CULL_FACE);
				GL11.glEnable(GL11.GL_DEPTH_TEST);
				GL11.glEnable(GL11.GL_ALPHA_TEST);
				GL11.glPopMatrix();
			}
		}
	}

	// Undo LLibrary's shenanigans, fixing the transparent hand issue (why, LLibrary, why?!)
	@SideOnly(Side.CLIENT)
	@MFix(returnSetting = EnumReturnSetting.ALWAYS)
	public static void renderArm(LLibraryHooks hooks, RenderPlayer renderPlayer, EntityPlayer player, EnumHandSide side) {
		GL11.glColor3f(1.0F, 1.0F, 1.0F);
		renderPlayer.modelBipedMain.onGround = 0.0F;
		renderPlayer.modelBipedMain.setRotationAngles(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F, player);
		if (!MinecraftForge.EVENT_BUS.post(new RenderArmEvent.Pre((AbstractClientPlayer)player, renderPlayer, renderPlayer.modelBipedMain, side))) {
//			GL11.glEnable(3042); This is GL11.GL_BLEND. Why are you turning on blend here? Hands do not have transparency (at least, they didn't, until LLibrary).
			renderPlayer.modelBipedMain.bipedRightArm.render(0.0625F);
//			GL11.glDisable(3042);
			MinecraftForge.EVENT_BUS.post(new RenderArmEvent.Post((AbstractClientPlayer)player, renderPlayer, renderPlayer.modelBipedMain, side));
		}
	}

	@MFix(returnSetting = EnumReturnSetting.ALWAYS)
	public static void applyIDFixTag(IvWorldData ivWorldData, NBTTagCompound compound, MCRegistry registry, NBTTagCompound fixTag) {
		String type = fixTag.getString("type");
		byte var5 = -1;
		switch(type.hashCode()) {
			case 3242771:
				if (type.equals("item")) {
					var5 = 0;
				}
				break;
			case 93832333:
				if (type.equals("block")) {
					var5 = 1;
				}
		}

		String dest;
		String stringID;
		switch(var5) {
			case 0:
				dest = fixTag.getString("tagDest");
				stringID = fixTag.getString("itemID");
				Item item = registry.itemFromID(stringID);
				if (item != null) {
					compound.setInteger(dest, Item.getIdFromItem(item));
				} else { // stop log spam. Other than that, this overwrite should have no effect whatsoever
//					IvToolkitCoreContainer.logger.warn("Failed to fix item tag from structure with ID '" + stringID + "'");
				}

				registry.modifyItemStackCompound(compound, stringID);
				break;
			case 1:
				dest = fixTag.getString("tagDest");
				stringID = fixTag.getString("blockID");
				Block block = registry.blockFromID(stringID);
				if (block != null) {
					compound.setInteger(dest, Block.getIdFromBlock(block));
				} else {
					IvToolkitCoreContainer.logger.warn("Failed to fix block tag from structure with ID '" + stringID + "'");
				}
				break;
			default:
				IvToolkitCoreContainer.logger.warn("Unrecognized ID fix tag in structure with type '" + type + "'");
		}
	}

	private static boolean func_851_exec = false;
	private static boolean removePlayerFromTrackers_exec = false;
	private static boolean updateTrackedEntities_exec = false;
	private static boolean removeEntityFromAllTracking_exec = false;
	private static boolean addEntityToTracker_exec = false;

	// fix crash START

	@MFix(returnSetting = EnumReturnSetting.ON_TRUE)
	public static boolean func_85172_a(EntityTracker tracker, EntityPlayerMP p_85172_1_, Chunk p_85172_2_)
	{
		if (!func_851_exec) {
			func_851_exec = true;
			try {
				tracker.func_85172_a(p_85172_1_, p_85172_2_);
			} catch (ConcurrentModificationException cme) {
				// get rid of this unreasonable crash
			}
			return true;
		} else {
			func_851_exec = false;
			return false;
		}
	}

	@MFix(returnSetting = EnumReturnSetting.ON_TRUE)
	public static boolean removePlayerFromTrackers(EntityTracker tracker, EntityPlayerMP p_85172_1_)
	{
		if (!removePlayerFromTrackers_exec) {
			removePlayerFromTrackers_exec = true;
			try {
				tracker.removePlayerFromTrackers(p_85172_1_);
			} catch (ConcurrentModificationException cme) {
				// get rid of this unreasonable crash
			}
			return true;
		} else {
			removePlayerFromTrackers_exec = false;
			return false;
		}
	}

	@MFix(returnSetting = EnumReturnSetting.ON_TRUE)
	public static boolean updateTrackedEntities(EntityTracker tracker)
	{
		if (!updateTrackedEntities_exec) {
			updateTrackedEntities_exec = true;
			try {
				tracker.updateTrackedEntities();
			} catch (ConcurrentModificationException cme) {
				// get rid of this unreasonable crash
			}
			return true;
		} else {
			updateTrackedEntities_exec = false;
			return false;
		}
	}

	@MFix(returnSetting = EnumReturnSetting.ON_TRUE)
	public static boolean removeEntityFromAllTrackingPlayers(EntityTracker tracker, Entity e)
	{
		if (!removeEntityFromAllTracking_exec) {
			removeEntityFromAllTracking_exec = true;
			try {
				tracker.removeEntityFromAllTrackingPlayers(e);
			} catch (ConcurrentModificationException cme) {
				// get rid of this unreasonable crash
			}
			return true;
		} else {
			removeEntityFromAllTracking_exec = false;
			return false;
		}
	}

	@MFix(returnSetting = EnumReturnSetting.ON_TRUE)
	public static boolean addEntityToTracker(EntityTracker tracker, Entity e)
	{
		if (!addEntityToTracker_exec) {
			addEntityToTracker_exec = true;
			try {
				tracker.addEntityToTracker(e);
			} catch (ConcurrentModificationException cme) {
				// get rid of this unreasonable crash
			}
			return true;
		} else {
			addEntityToTracker_exec = false;
			return false;
		}
	}

	// fix crash END

	// IEntitySelector impl. is useless because that'd make it incompatible in this case
	@MFix(returnSetting = EnumReturnSetting.ALWAYS, insertOnExit = true)
	public static List selectEntitiesWithinAABB(World wrld, Class p_82733_1_, AxisAlignedBB p_82733_2_, IEntitySelector p_82733_3_, @ReturnedValue List returnedValue)
	{
		ArrayList toReturn = new ArrayList();
		for (int i = 0; i < returnedValue.size(); i++) {
			if (i >= returnedValue.size()) break; // because we now know how volatile entity lists are.
			if (!(returnedValue.get(i) instanceof EntityPlayer)) {
				toReturn.add(returnedValue.get(i));
			} else { // if player
				if (!isPlayerEthereal((EntityPlayer)returnedValue.get(i))) toReturn.add(returnedValue.get(i));
			}
		}
		return toReturn;
	}

	@MFix(returnSetting = EnumReturnSetting.ALWAYS, insertOnExit = true)
	public static EntityPlayer getClosestPlayer(World world, double p_72977_1_, double p_72977_3_, double p_72977_5_, double p_72977_7_, @ReturnedValue EntityPlayer returnedValue)
	{
		return isPlayerEthereal(returnedValue) ? null : returnedValue;
	}

	// same as last method: prevent ethereal players from being returned
	@MFix(returnSetting = EnumReturnSetting.ALWAYS, insertOnExit = true)
	public static List getEntitiesWithinAABBExcludingEntity(World world, Entity p_94576_1_, AxisAlignedBB p_94576_2_, IEntitySelector p_94576_3_, @ReturnedValue List returnedValue)
	{
		ArrayList toReturn = new ArrayList();
		for (int i = 0; i < returnedValue.size(); i++) {
			if (i >= returnedValue.size()) break; // because we now know how volatile entity lists are.
			if (!(returnedValue.get(i) instanceof EntityPlayer)) {
				toReturn.add(returnedValue.get(i));
			} else { // if player
				if (!isPlayerEthereal((EntityPlayer)returnedValue.get(i))) toReturn.add(returnedValue.get(i));
			}
		}
		return toReturn;
	}

	public static boolean updatingRenderWorld = false;
	public static boolean orientingCamera = false;

	@MFix
	public static void updateRenderer(WorldRenderer wr, EntityLivingBase p_147892_1_) { updatingRenderWorld = true; }

	@MFix(insertOnExit = true, targetMethod = "updateRenderer")
	public static void updateRendererEnd(WorldRenderer wr, EntityLivingBase p_147892_1_) { updatingRenderWorld = false; }

	@MFix
	public static void orientCamera(EntityRenderer er, float p_78467_1_) { orientingCamera = true; }

	@MFix(insertOnExit = true, targetMethod = "orientCamera")
	public static void orientCameraEnd(EntityRenderer er, float p_78467_1_) { orientingCamera = false; }

	@MFix(returnSetting = EnumReturnSetting.ALWAYS)
	public static double distanceTo(Vec3 thisVec, Vec3 p_72438_1_)
	{
		if (FMLCommonHandler.instance() != null && FMLCommonHandler.instance().getMinecraftServerInstance() != null) {
			if (!FMLCommonHandler.instance().getMinecraftServerInstance().isDedicatedServer()) {
				if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
					if (isEtherealMinecraft()) return Double.MAX_VALUE;
				}
			} // just in case
		}
		double d0 = p_72438_1_.xCoord - thisVec.xCoord;
		double d1 = p_72438_1_.yCoord - thisVec.yCoord;
		double d2 = p_72438_1_.zCoord - thisVec.zCoord;
		return (double) MathHelper.sqrt_double(d0 * d0 + d1 * d1 + d2 * d2);
	}

	@SideOnly(Side.CLIENT) // method isn't clientside technically but we only need this patch clientside
	public static boolean isEtherealMinecraft() {
		if (Minecraft.getMinecraft() != null && Minecraft.getMinecraft().thePlayer != null && Minecraft.getMinecraft().renderViewEntity != null) {
			if (orientingCamera && isPlayerEthereal(Minecraft.getMinecraft().thePlayer) && Minecraft.getMinecraft().renderViewEntity == Minecraft.getMinecraft().thePlayer) return true;
		}
		return false;
	}

	@MFix(returnSetting = EnumReturnSetting.ON_TRUE, anotherMethodReturned = "renderPlayerSpecial")
	@SideOnly(Side.CLIENT)
	public static boolean getEntityRenderObject(RenderManager rm, Entity ent)
	{
		if (ent instanceof EntityPlayer) {
			if (playerModelMap.get(((EntityPlayer)ent).getCommandSenderName()) != null && playerModelMap.get(((EntityPlayer)ent).getCommandSenderName()).startsWith("maid")) return true;
		}
		return false;
	}

	@SideOnly(Side.CLIENT)
	public static Render renderPlayerSpecial(RenderManager rm, Entity ent) {
		return new RenderPlayerSpecial();
	}

	// hacky fix for foamfix (foamfix, why must you do this?!)
	@MFix(returnSetting = EnumReturnSetting.ON_TRUE)
	public static boolean register(EventBus eb, Object target)
	{
		if (target.getClass().getName().contains("Ears")) {
			LogHelper.warn("--------------------------!!!--------------------------");
			LogHelper.warn("The Ears mod transformer was found and disabled. This most likely means you're using FoamFix's hacky 1.8+ skin support.");
			LogHelper.warn("Please disable it in FoamFix's config and use (preferably, as its solution is much more compatible and elegant) EtFuturumRequiem, or (if need be) SkinPort instead.");
			LogHelper.warn("--------------------------!!!--------------------------");
			return true; // Do not register the Ears transformer, for it is evil.
		}
		return false; // continue as normal
	}

	//	@cpw.mods.fml.common.Optional.Method(modid="FoamFixCore")
//	@MFix(returnSetting = EnumReturnSetting.ALWAYS) // foamfix compat doesn't work this way
//	public static boolean applyEarsPatch(pl.asie.foamfix.bugfixmod.coremod.BugfixModClassTransformer transformer) {
//		return false;
//	}

//	@MFix(returnSetting = EnumReturnSetting.ON_TRUE)
//	@SideOnly(Side.CLIENT)
//	public static boolean renderItemInFirstPerson(ItemRenderer ir, float p_78440_1_)
//	{
//		if (shouldNotUseNormalRender) return true;
//		return false;
//	}

	public static Map<String, String> playerModelMap = new HashMap<String, String>();

	@MFix(returnSetting = EnumReturnSetting.ALWAYS, insertOnExit = true)
	public static boolean isEntityInsideOpaqueBlock(Entity thisEntity, @ReturnedValue boolean returnedValue)
	{
		if (thisEntity.noClip) return false;
		return returnedValue;
	}

	@MFix(returnSetting = EnumReturnSetting.ALWAYS)
	public static boolean canBePushed(EntityLivingBase elb)
	{
		if (elb instanceof EntityPlayer) {
			if (isPlayerEthereal((EntityPlayer) elb)) return false;
		}
		return !elb.isDead;
	}

	@MFix(returnSetting = EnumReturnSetting.ALWAYS)
	public static boolean canBeCollidedWith(EntityLivingBase elb)
	{
		if (elb instanceof EntityPlayer) {
			if (isPlayerEthereal((EntityPlayer) elb)) return false;
		}
		return !elb.isDead;
	}

	@MFix(returnSetting = EnumReturnSetting.ALWAYS)
	public static boolean isOnLadder(EntityLivingBase elb)
	{
		if (elb instanceof EntityPlayer) {
			if (isPlayerEthereal((EntityPlayer) elb)) return false;
		}
		return isOnLadderCalc(elb);
	}

	public static boolean isOnLadderCalc(EntityLivingBase elb)
	{
		int i = MathHelper.floor_double(elb.posX);
		int j = MathHelper.floor_double(elb.boundingBox.minY);
		int k = MathHelper.floor_double(elb.posZ);
		Block block = elb.worldObj.getBlock(i, j, k);
		return ForgeHooks.isLivingOnLadder(block, elb.worldObj, i, j, k, elb);
	}

	@MFix(returnSetting = EnumReturnSetting.ON_TRUE, anotherMethodReturned = "returnFalse")
	public static boolean handleWaterMovement(Entity thisEntity)
	{
		if (thisEntity instanceof EntityPlayer) {
			if (isPlayerEthereal((EntityPlayer) thisEntity)) return true; // return false from the original method
		}
		if (thisEntity instanceof AM2Boss && !(thisEntity instanceof EntityWaterGuardian)) return true;
		return false; // continue normal execution
	}

	@MFix(returnSetting = EnumReturnSetting.ON_TRUE)
	public static boolean processClickWindow(NetHandlerPlayServer nhps, C0EPacketClickWindow p_147351_1_) {
		if (isPlayerEthereal(nhps.playerEntity)) {
			nhps.playerEntity.func_143004_u();
			if (nhps.playerEntity.openContainer.windowId == p_147351_1_.func_149548_c() && nhps.playerEntity.openContainer.isPlayerNotUsingContainer(nhps.playerEntity)) {
				ArrayList<ItemStack> arraylist = new ArrayList<>();
				for (int i = 0; i < nhps.playerEntity.openContainer.inventorySlots.size(); ++i)
				{
					arraylist.add(((Slot)nhps.playerEntity.openContainer.inventorySlots.get(i)).getStack());
				}
				nhps.playerEntity.sendContainerAndContentsToPlayer(nhps.playerEntity.openContainer, arraylist);
				return true;
			}
		}
		return false;
	}

	public static boolean returnFalse(Entity thisEntity) { return false; }

	@MFix(returnSetting = EnumReturnSetting.ON_TRUE)
	public static boolean applyEntityCollision(Entity thisEntity, Entity argumentPassed)
	{
		if (argumentPassed instanceof EntityPlayer) {
			if (isPlayerEthereal((EntityPlayer) argumentPassed)) return true; // stop execution
		}
		return false; // continue execution
	}

	@MFix(returnSetting = EnumReturnSetting.ON_TRUE, anotherMethodReturned = "returnMinusOne")
	public static boolean getRenderType(Block block) { // this is alright, we don't want to change overridden classes so allthatextends isn't necessary
//		if (updatingRenderWorld && isPlayerEthereal(Minecraft.getMinecraft().thePlayer)) return true;
		return false;
	}

//	@MFix(returnSetting = EnumReturnSetting.ON_TRUE)
//	@SideOnly(Side.CLIENT)
//	public static boolean playSound(SoundManager sm, ISound p_148611_1_)
//	{
//		if (Minecraft.getMinecraft() != null) {
//			if (Minecraft.getMinecraft().thePlayer != null && (AMPacketProcessorClient.deaf > 0)) return true;
//		}
//		return false; // play the sound
//	} // doesn't work because forge screwed up mappings

	@MFix(returnSetting = EnumReturnSetting.ON_TRUE)
	@SideOnly(Side.CLIENT)
	public static boolean renderParticles(EffectRenderer er, Entity p_78874_1_, float p_78874_2_)
	{
		if (Minecraft.getMinecraft() != null) {
			if (AMPacketProcessorClient.cloaking > 0) return true;
		}
		return false; // render as usual
	}

	public static int returnMinusOne(Block block) { return -1; }

	@MFix(returnSetting = EnumReturnSetting.ALWAYS)
	@SideOnly(Side.CLIENT)
	public static boolean isInvisibleToPlayer(Entity e, EntityPlayer p_98034_1_){
		return (p_98034_1_.isPotionActive(BuffList.trueSight.id) || isPlayerEthereal(p_98034_1_)) ? false : e.isInvisible();
	}

	@MFix(returnSetting = EnumReturnSetting.ON_TRUE)
	public static boolean setDamage(Item item, ItemStack stack, int damage)
	{
		if (stack != null) {
			if (stack.getItem() instanceof ItemSoulspike && damage != 66 && damage != 0) return true;
		}
		return false;
	}

	// item handling: etherium (floating up) and cognitive dust (slowly floating up)
	@MFix
	public static void onUpdate(EntityItem ei)
	{
		if (ei.getEntityItem() != null) {
			if (ei.getEntityItem().getItem() instanceof ItemOre && ei.getEntityItem().getItemDamage() == ItemsCommonProxy.itemOre.META_COGNITIVEDUST) {
				ei.moveEntity(ei.motionX, ei.motionY, ei.motionZ);
				ei.motionY *= 0.0500000011920929D;
				ei.motionY += 0.025D;
			}
			if (ei.worldObj.isMaterialInBB(ei.boundingBox.expand(-0.10000000149011612D, -0.4000000059604645D, -0.10000000149011612D), liquidEssenceMaterial)) {
				double d0 = ei.posY;
				ei.moveEntity(ei.motionX, ei.motionY, ei.motionZ);
				ei.motionX *= 0.500000011920929D;
				ei.motionY *= 0.0500000011920929D;
				ei.motionZ *= 0.500000011920929D;
				ei.motionY += 0.05D;

				if (ei.isCollidedHorizontally && ei.isOffsetPositionInLiquid(ei.motionX, ei.motionY + 0.6000000238418579D - ei.posY + d0, ei.motionZ)) {
					ei.motionY = 0.30000001192092896D;
				}
				ei.fallDistance = 0;
			}
		}
	}

	@MFix(returnSetting = EnumReturnSetting.ALWAYS)
	public static void switchToRealms(RealmsBridge rb, GuiScreen p_switchToRealms_1_)
	{
		return; // annoying realms crash disabler
	}

	private static Map<String, Integer> tileticks = new HashMap<>(); // for keeping track of ticks, not power

	@MFix(returnSetting = EnumReturnSetting.ALWAYS)
	public static boolean hasWorldObj(TileEntity te) // very roundabout way to slow down tileentity ticks. The TE won't update if hasWorldObj in it returns false. it's only used in rendering chests otherwise, so there's no harm in overwriting this function.
	{
		if (te.getWorldObj() != null) {
			String thistile = te.xCoord + "_" + te.yCoord + "_" + te.zCoord + "_" + te.getWorldObj().provider.dimensionId;
			if (AMEventHandler.slowedTiles.containsKey(thistile)) {
				if (!tileticks.containsKey(thistile)) tileticks.put(thistile, 1);
				else tileticks.put(thistile, tileticks.get(thistile)+1);

				if (tileticks.get(thistile) > 1000) tileticks.put(thistile, 1);

				if (tileticks.get(thistile) % AMEventHandler.slowedTiles.get(thistile) != 0) {
					return false; // returns false from the original method
				}
			}
		}
		return te.getWorldObj() != null; // do not overwrite default behavior
	}

	@SideOnly(Side.CLIENT)
	@MFix(returnSetting = EnumReturnSetting.ON_TRUE)
	public static boolean setLivingAnimations(ModelMagmaCube mmc, EntityLivingBase elb, float p_78086_2_, float p_78086_3_, float p_78086_4_)
	{
		if (elb instanceof EntityHallucination) return true;
		return false;
	}

	@SideOnly(Side.CLIENT)
	@MFix(returnSetting = EnumReturnSetting.ON_TRUE)
	public static boolean setLivingAnimations(ModelSkeleton mmc, EntityLivingBase elb, float p_78086_2_, float p_78086_3_, float p_78086_4_)
	{
		if (elb instanceof EntityHallucination) return true;
		return false;
	}

	public static long servertickrate = 50L; // changing
	public static long servertickratedefault = 50L; // default

	public static float clienttickrate = 20; // stored in ticks as opposed to milis
	public static float clienttickratedefault = 20;

	// a few of the following methods are courtesy of Guichaguri (TickrateChanger mod)

	public static void changeTickrate(float ticksPerSecond) {
		if (AMCore.config.isGlobalTimeManipulationEnabled()) {
			changeServerTickrate(ticksPerSecond);
			changeClientTickratePublic(ticksPerSecond);
		}
	}

	public static void changeClientTickratePublic(float ticksPerSecond) {
		MinecraftServer server = MinecraftServer.getServer();
		if((server != null) && (server.getConfigurationManager() != null)) { // Is a server or singleplayer
			for(EntityPlayer p : (List<EntityPlayer>)server.getConfigurationManager().playerEntityList) {
				changeClientTickratePublic(p, ticksPerSecond);
			}
		} else { // Is in menu or a player connected in a server. We can say this is client.
			changeClientTickratePublic(null, ticksPerSecond);
		}
	}

	public static void changeClientTickratePublic(EntityPlayer player, float ticksPerSecond) {
		if((player == null) || (player.worldObj.isRemote)) { // Client
			if(FMLCommonHandler.instance().getSide() != Side.CLIENT) return;
			if((player != null) && (player != Minecraft.getMinecraft().thePlayer)) return;
			changeClientTickrate(ticksPerSecond);
		} else { // Server
			AMCore.NETWORK.sendTo(new TickrateMessage(ticksPerSecond), (EntityPlayerMP)player);
		}
	}

	private static final MethodHandle isDrawingGet = createIsDrawingGet();

	private static MethodHandle createIsDrawingGet() {
		try {
			Minecraft.getMinecraft(); // roundabout way to check for client
			return createIsDrawingGetDelegate();
		} catch (RuntimeException e) {
		} catch (NoClassDefFoundError e) {
		} catch (Exception e) {
		}
		return null;
	}

	@SideOnly(Side.CLIENT)
	private static MethodHandle createIsDrawingGetDelegate() {
		try {
			Field field = CustomLoadingPlugin.isObfuscated() ? Tessellator.class.getDeclaredField("field_78415_z") : Tessellator.class.getDeclaredField("isDrawing");
			field.setAccessible(true);
			return MethodHandles.publicLookup().unreflectGetter(field);
		} catch (Exception e) {
			LogHelper.error("Did not find tesselator isDrawing field! Only report this error if you're seeing it on a client!");
			return null;
		}
	}

	// Completely removes the "already tessellating" error
	@SideOnly(Side.CLIENT)
	@MFix(returnSetting = EnumReturnSetting.ON_TRUE)
	public static boolean startDrawing(Tessellator tsl, int p_78371_1_)
	{
		boolean isDrawing = false;
		try {
			isDrawing = (boolean) isDrawingGet.invokeExact((Tessellator) tsl);
		} catch (Throwable e) {
			throw new RuntimeException("Could not invoke isDrawing field! Only report this error if you're seeing it on a client!", e);
		}
		if (isDrawing)
		{
			return true;
		}
		return false;
	}

	// Completely removes the "already tessellating" error - part 2, electric boogaloo
	@SideOnly(Side.CLIENT)
	@MFix(returnSetting = EnumReturnSetting.ON_TRUE)
	public static boolean draw(Tessellator tsl) {
		boolean isDrawing = false;
		try {
			isDrawing = (boolean) isDrawingGet.invokeExact((Tessellator) tsl);
		} catch (Throwable e) {
			throw new RuntimeException("Could not invoke isDrawing field! Only report this error if you're seeing it on a client!", e);
		}
		if (!isDrawing)
		{
			return true;
		}
		return false;
	}

	private static Field timerField = null;
	@SideOnly(Side.CLIENT)
	public static void changeClientTickrate(float newtickrate) {
		clienttickrate = newtickrate; // store it in case we need to access it later
		Minecraft mc = Minecraft.getMinecraft();
		if(mc == null) return;
		try {
			if(timerField == null) {
				for(Field fld : mc.getClass().getDeclaredFields()) {
					if(fld.getType() == net.minecraft.util.Timer.class) { // have to type out the whole class, otherwise it assumes it's the java.util timer
						timerField = fld;
						timerField.setAccessible(true);
						break;
					}
				}
			}
			timerField.set(mc, new net.minecraft.util.Timer(clienttickrate));
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}

	public static void changeServerTickrate(float newtickrate) {
		servertickrate = (long)(1000L / newtickrate); // 1000 milis in a second. 20 ticks a second. = 50 milis per tick.
	}

	@MFix(returnSetting = EnumReturnSetting.ON_TRUE, booleanAlwaysReturned = true)
	public static boolean isBlockIndirectlyGettingPowered(World world, int x, int y, int z)
	{
		int theID = world.provider.dimensionId;
		boolean toReturn = false;
		int counter = 0;
		for (int[] redstoneProvider : providingRedstone) {
			if (redstoneProvider[0] == theID && redstoneProvider[1] == x && redstoneProvider[2] == y && redstoneProvider[3] == z) {
				toReturn = true;
				break;
			}
			counter++;
		}

		if (toReturn) {
			int newValue = providingRedstone.get(counter)[4] - 1;
			if (newValue <= 0) {
				providingRedstone.remove(counter);
			} else{
				providingRedstone.add(new int[]{theID, x, y, z, newValue});
				providingRedstone.remove(counter);
			}

			world.getBlock(x, y, z).onNeighborBlockChange(world, x, y, z, Blocks.stonebrick);
			return true;
		}
		return false;
	}

	@MFix(returnSetting = EnumReturnSetting.ON_TRUE, anotherMethodReturned = "isInsideWater")
	public static boolean isInsideOfMaterial(Entity e, Material p_70055_1_)
	{
		if (e instanceof EntityPlayer && p_70055_1_.isLiquid()) {
			if (AffinityHelper.isNotInWaterActually.contains((EntityPlayer)e)) {
				return true;
			}
		}
		return false;
	}

	@MFix
	public static void updateTick(BlockDynamicLiquid bdl, World p_149674_1_, int p_149674_2_, int p_149674_3_, int p_149674_4_, Random p_149674_5_){
		if (bdl.getMaterial() == Material.lava && p_149674_1_.getBlock(p_149674_2_, p_149674_3_ - 1, p_149674_4_) instanceof BlockLiquidEssence) {
			p_149674_1_.setBlock(p_149674_2_, p_149674_3_ - 1, p_149674_4_, Blocks.stained_glass, 11, 3);
			func_149799_m(p_149674_1_, p_149674_2_, p_149674_3_ - 1, p_149674_4_);
			return;
		}
	}

	protected static void func_149799_m(World p_149799_1_, int p_149799_2_, int p_149799_3_, int p_149799_4_) {
		p_149799_1_.playSoundEffect((double)((float)p_149799_2_ + 0.5F), (double)((float)p_149799_3_ + 0.5F), (double)((float)p_149799_4_ + 0.5F), "random.fizz", 0.5F, 2.6F + (p_149799_1_.rand.nextFloat() - p_149799_1_.rand.nextFloat()) * 0.8F);

		for (int l = 0; l < 8; ++l) {
			p_149799_1_.spawnParticle("largesmoke", (double)p_149799_2_ + Math.random(), (double)p_149799_3_ + 1.2D, (double)p_149799_4_ + Math.random(), 0.0D, 0.0D, 0.0D);
		}
	}

		@MFix(returnSetting = EnumReturnSetting.ON_TRUE, anotherMethodReturned = "isInsideWater")
	public static boolean isInWater(Entity e) {
		if (e instanceof EntityPlayer) {
			if (AffinityHelper.isNotInWaterActually.contains((EntityPlayer)e)) {
				return true;
			}
		}
		return false;
	}

	@MFix(returnSetting = EnumReturnSetting.ON_TRUE)
	public static boolean velocityToAddToEntity(BlockLiquid block, World p_149640_1_, int p_149640_2_, int p_149640_3_, int p_149640_4_, Entity e, Vec3 p_149640_6_) {
		if (e instanceof EntityPlayer) {
			if (AffinityHelper.isNotInWaterActually.contains((EntityPlayer)e)) {
				return true;
			}
		}
		if (e instanceof AM2Boss && !(e instanceof EntityWaterGuardian)) return true;
		return false;
	}

	private static final ResourceLocation rodtexture1 = new ResourceLocation("arsmagica2", "textures/items/particles/particleswitharcanerod.png");
	private static final ResourceLocation rodtexture2 = new ResourceLocation("arsmagica2", "textures/items/particles/particleswithinfernalrod.png");

	@SideOnly(Side.CLIENT)
	@MFix(returnSetting = EnumReturnSetting.ON_TRUE, anotherMethodReturned = "getEntityTextureResult")
	public static boolean getEntityTexture(RenderFish r, EntityFishHook e) {
		if (Minecraft.getMinecraft().thePlayer != null) {
			if (Minecraft.getMinecraft().thePlayer.getHeldItem() != null) {
				Item heldItem = Minecraft.getMinecraft().thePlayer.getHeldItem().getItem();
				if (heldItem instanceof ItemArcaneFishingRod || heldItem instanceof ItemInfernalFishingRod) {
					return true;
				}
			}
		}
		return false;
	}

	@SideOnly(Side.CLIENT) // other methods of preventing flickering don't work
	@MFix(returnSetting = EnumReturnSetting.ON_TRUE)
	public static boolean doRenderShadowAndFire(Render r, Entity ent, double p_76979_2_, double p_76979_4_, double p_76979_6_, float p_76979_8_, float p_76979_9_)
	{
		if (ent instanceof EntityFishHook) return true;
		return false;
	}

	@SideOnly(Side.CLIENT)
	public static ResourceLocation getEntityTextureResult(RenderFish r, EntityFishHook e) {
		if (Minecraft.getMinecraft().thePlayer.getHeldItem().getItem() instanceof ItemArcaneFishingRod) {
			return rodtexture1;
		} else {
			return rodtexture2;
		}
	}

	public static boolean isInsideWater(Entity e, Material p_70055_1_) {
		return false;
	}

	public static boolean isInsideWater(Entity e) {
		return false;
	}

	@MFix(returnSetting = EnumReturnSetting.ON_TRUE, anotherMethodReturned = "findMatchingRecipeResult")
	public static boolean findMatchingRecipe(CraftingManager cm, InventoryCrafting p_82787_1_, World p_82787_2_) {
		craftingStaffsPossible = false;
		craftingSpellsPossible = false;
		craftingArmorPossible = false;
		int craftingCompsStaffs = 0, craftingCompsSpells = 0, craftingCompsArmor = 0;
		for (int i = 0; i<3; i++) {
			for (int j = 0; j<3; j++) {
				if (p_82787_1_.getStackInRowAndColumn(i,j) != null) {
					if (p_82787_1_.getStackInRowAndColumn(i,j).getItem() instanceof ItemSpellStaff) {
						if (!((ItemSpellStaff)p_82787_1_.getStackInRowAndColumn(i,j).getItem()).isMagiTechStaff()) {
							craftingCompsStaffs++;
							if (staffSlotTo == -1) {
								staffSlotTo = i;
								staffSlotColumnTo = j;
							} else {
								staffSlotFrom = i;
								staffSlotColumnFrom = j;
							}
						}
					} else if (p_82787_1_.getStackInRowAndColumn(i,j).getItem() instanceof SpellBase) {
						craftingCompsSpells++;
						spellSlotFrom = i;
						spellSlotColumnFrom = j;
					} else if (p_82787_1_.getStackInRowAndColumn(i,j).getItem() instanceof BoundArmor) {
						craftingCompsArmor++;
						armorSlotTo = i;
						armorSlotColumnTo = j;
					}
				}
			}
		}

		if (craftingCompsSpells == 1 && craftingCompsStaffs == 1) {
			craftingSpellsPossible = true;
		} else if (craftingCompsStaffs == 2) {
			craftingStaffsPossible = true;
		} else if (craftingCompsSpells == 1 && craftingCompsArmor == 1) {
			craftingArmorPossible = true;
		}

		if (craftingStaffsPossible || craftingSpellsPossible || craftingArmorPossible) {
			return true;
		} else {
			staffSlotTo = -1;
			staffSlotColumnTo = -1;
			spellSlotFrom = -1;
			spellSlotColumnFrom = -1;
			staffSlotFrom = -1;
			staffSlotColumnFrom = -1;
			armorSlotColumnTo = -1;
			armorSlotTo = -1;
		}
		return false;
	}

	public static ItemStack findMatchingRecipeResult(CraftingManager cm, InventoryCrafting p_82787_1_, World p_82787_2_) {

		if (craftingStaffsPossible){
			ItemStack result = ItemSpellStaff.copyChargeFrom(
					p_82787_1_.getStackInRowAndColumn(staffSlotTo, staffSlotColumnTo).copy(),
					p_82787_1_.getStackInRowAndColumn(staffSlotFrom, staffSlotColumnFrom));
			staffSlotTo = -1;
			staffSlotFrom = -1;
			staffSlotColumnTo = -1;
			staffSlotColumnFrom = -1;
			return result;
		} else if (craftingSpellsPossible) {
			ItemStack result = ItemSpellStaff.setSpellScroll(
					p_82787_1_.getStackInRowAndColumn(staffSlotTo, staffSlotColumnTo).copy(),
					p_82787_1_.getStackInRowAndColumn(spellSlotFrom, spellSlotColumnFrom));
			staffSlotTo = -1;
			staffSlotColumnTo = -1;
			spellSlotFrom = -1;
			spellSlotColumnFrom = -1;
			return result;
		} else { // if crafting armor possible
			ItemStack result = BoundArmor.setSpell(
					p_82787_1_.getStackInRowAndColumn(armorSlotTo, armorSlotColumnTo).copy(),
					p_82787_1_.getStackInRowAndColumn(spellSlotFrom, spellSlotColumnFrom));
			armorSlotTo = -1;
			armorSlotColumnTo = -1;
			spellSlotFrom = -1;
			spellSlotColumnFrom = -1;
			return result;
		}
	}

}
