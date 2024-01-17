package com.tfc.minecraft_effekseer_implementation;

import am2.AMCore;
import am2.items.IEffekItem;
import com.tfc.minecraft_effekseer_implementation.common.Effek;
import com.tfc.minecraft_effekseer_implementation.common.Effeks;
import com.tfc.minecraft_effekseer_implementation.common.api.EffekEmitter;
import com.tfc.minecraft_effekseer_implementation.loader.EffekseerMCAssetLoader;
import com.tfc.minecraft_effekseer_implementation.vector.Matrix4f;
import com.tfc.minecraft_effekseer_implementation.vector.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.culling.Frustrum;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import org.lwjgl.BufferUtils;
import org.lwjgl.input.Keyboard;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent.KeyInputEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClientProxy {
	
	public static KeyBinding keyBindToggleRendering;

    public void register() 
    {
	   if (ClientProxy.keyBindToggleRendering == null) {
		   ClientProxy.keyBindToggleRendering = new KeyBinding("Toggle Rendering Effects", Keyboard.KEY_NONE, "key.categories.misc");
           ClientRegistry.registerKeyBinding(ClientProxy.keyBindToggleRendering);
           KeyBinding.resetKeyBindingArrayAndHash();
       }

		if (Effek.widthGetter.get() == null) {
			Effek.widthGetter.set(() -> Minecraft.getMinecraft().displayWidth);
			Effek.heightGetter.set(() -> Minecraft.getMinecraft().displayHeight);
		}

		IReloadableResourceManager manager = (IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager();
		manager.registerReloadListener(EffekseerMCAssetLoader.INSTANCE);

		am2.proxy.ClientProxy.mc = Minecraft.getMinecraft();
    }

	public void registerKeyHandelers() {
		MinecraftForge.EVENT_BUS.register(this);
	    FMLCommonHandler.instance().bus().register(this);
	}
	
	@SideOnly(Side.CLIENT)
	@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
	public void onKeyInputEvent(KeyInputEvent event)
	{
		if (ClientProxy.keyBindToggleRendering.isPressed()) {
			MEI.renderingEnabled = !MEI.renderingEnabled;
		}
	}

//	public static Map<EffekEmitter, int[]> effekStat = new HashMap<EffekEmitter, int[]>(); // the int array is {currentPlayTime, maxPlayTime}
//
//	public static void register(EffekEmitter e, int maxPlayTime) {
//		if (!effekStat.containsKey(e)) effekStat.put(e, new int[]{0, maxPlayTime, 0});
//		else effekStat.put(e, new int[]{effekStat.get(e)[0], maxPlayTime, 0});
//	}

	public static Effek effekInHand = null; // the current (first person) effek on display in hand
	public static Effek thirdPersonEffekInHand = null; // the current (third person) effek on display in hand

    @SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void renderWorldLast(final RenderWorldLastEvent event) {
		MEI mei = MEI.instance;
		if (!mei.refreshed) {
//			am2.proxy.ClientProxy.mc.refreshResources();
			EffekseerMCAssetLoader.INSTANCE.onResourceManagerReload(am2.proxy.ClientProxy.mc.getResourceManager());
			mei.refreshed = true;
		}
		if(MEI.renderingEnabled && mei.refreshed && am2.proxy.ClientProxy.mc.thePlayer != null && am2.proxy.ClientProxy.mc.thePlayer.ticksExisted > 18) {
			am2.proxy.ClientProxy.mapHandler.setTimeSinceReload(Effeks.getTimeSinceReload() + 1);
			mei.ticks++;
			Effek effek = Effeks.get("arsmagica2:effeks0");
			EffekEmitter emitter = null;

			GL11.glPushMatrix();
			am2.proxy.ClientProxy.mat.push();

//			System.out.println(this.mc.renderViewEntity.posX);
// 			mat.rotate(Vector3f.YP.rotationDegrees((activerenderinfo.getYaw() + 180F) * 0.9F));
//			mat.rotate(Vector3f.YP.rotationDegrees(activerenderinfo.getYaw()));


			if (am2.proxy.ClientProxy.mc.gameSettings.thirdPersonView > 0) {
				double d7 = (double) (mei.thirdPersonDistanceTemp + (mei.thirdPersonDistance - mei.thirdPersonDistanceTemp) * event.partialTicks);
				float f2;
				float f6;

				EntityLivingBase entitylivingbase = am2.proxy.ClientProxy.mc.renderViewEntity;
				float f1 = entitylivingbase.yOffset - 1.62F;
				double d0 = entitylivingbase.prevPosX + (entitylivingbase.posX - entitylivingbase.prevPosX) * (double)event.partialTicks;
				double d1 = entitylivingbase.prevPosY + (entitylivingbase.posY - entitylivingbase.prevPosY) * (double)event.partialTicks - (double)f1;
				double d2 = entitylivingbase.prevPosZ + (entitylivingbase.posZ - entitylivingbase.prevPosZ) * (double)event.partialTicks;
				f6 = entitylivingbase.rotationYaw;
				f2 = entitylivingbase.rotationPitch;

				if (am2.proxy.ClientProxy.mc.gameSettings.thirdPersonView == 2) {
					f2 += 180.0F;
				}

				double d3 = (double) (-MathHelper.sin(f6 / 180.0F * (float) Math.PI) * MathHelper.cos(f2 / 180.0F * (float) Math.PI)) * d7;
				double d4 = (double) (MathHelper.cos(f6 / 180.0F * (float) Math.PI) * MathHelper.cos(f2 / 180.0F * (float) Math.PI)) * d7;
				double d5 = (double) (-MathHelper.sin(f2 / 180.0F * (float) Math.PI)) * d7;

				for (int k = 0; k < 8; ++k) {
					float f3 = (float) ((k & 1) * 2 - 1);
					float f4 = (float) ((k >> 1 & 1) * 2 - 1);
					float f5 = (float) ((k >> 2 & 1) * 2 - 1);
					f3 *= 0.1F;
					f4 *= 0.1F;
					f5 *= 0.1F;
					MovingObjectPosition movingobjectposition = am2.proxy.ClientProxy.mc.theWorld.rayTraceBlocks(Vec3.createVectorHelper(d0 + (double) f3, d1 + (double) f4, d2 + (double) f5), Vec3.createVectorHelper(d0 - d3 + (double) f3 + (double) f5, d1 - d5 + (double) f4, d2 - d4 + (double) f5));

					if (movingobjectposition != null) {
						double d6 = movingobjectposition.hitVec.distanceTo(Vec3.createVectorHelper(d0, d1, d2));

						if (d6 < d7) {
							d7 = d6;
						}
					}
				}

				if (am2.proxy.ClientProxy.mc.gameSettings.thirdPersonView == 2) {
					am2.proxy.ClientProxy.mat.rotate(Vector3f.YP.rotationDegrees(180.0F));
				}

				am2.proxy.ClientProxy.mat.rotate(Vector3f.XP.rotationDegrees(entitylivingbase.rotationPitch - f2));
				am2.proxy.ClientProxy.mat.rotate(Vector3f.YP.rotationDegrees(entitylivingbase.rotationYaw - f6));
				am2.proxy.ClientProxy.mat.translate(0.0F, 0.0F, (float) (-d7));
				am2.proxy.ClientProxy.mat.rotate(Vector3f.YP.rotationDegrees(f6 - entitylivingbase.rotationYaw));
				am2.proxy.ClientProxy.mat.rotate(Vector3f.XP.rotationDegrees(f2 - entitylivingbase.rotationPitch));
			}

			am2.proxy.ClientProxy.activerenderinfo.update((EntityLivingBase) (am2.proxy.ClientProxy.mc.renderViewEntity == null ? am2.proxy.ClientProxy.mc.thePlayer : am2.proxy.ClientProxy.mc.renderViewEntity), event.partialTicks);
			CameraSetup cameraSetup = new CameraSetup(am2.proxy.ClientProxy.activerenderinfo.getYaw(), am2.proxy.ClientProxy.activerenderinfo.getPitch(), 0);
			am2.proxy.ClientProxy.activerenderinfo.setAnglesInternal(cameraSetup.getYaw(), cameraSetup.getPitch());

			am2.proxy.ClientProxy.mat.rotate(Vector3f.XP.rotationDegrees(am2.proxy.ClientProxy.activerenderinfo.getPitch()));

			am2.proxy.ClientProxy.mat.rotate(Vector3f.ZP.rotationDegrees(cameraSetup.getRoll()));

			double truePosX = am2.proxy.ClientProxy.activerenderinfo.getProjectedView().getX();
			double truePosY = am2.proxy.ClientProxy.activerenderinfo.getProjectedView().getY();
			double truePosZ = am2.proxy.ClientProxy.activerenderinfo.getProjectedView().getZ();

//			System.out.println(truePosZ + " , " + RenderManager.renderPosZ + " , " + RenderManager.instance.viewerPosZ + " , " + RenderManager.instance.playerViewX);
//			System.out.println(activerenderinfo.getPitch());

			// testing code
//			if (effek != null) {
////				float transformNeeded = 0.5f;
////				float yawRounded = (activerenderinfo.getYaw() + 180.0F) % 360;
////				float[] goodValues = {-225f, -45f, 135f, 315f}; // these are the values at which we want to apply 0 transform (0.5 at -135, 45, 255, etc)
////
////				float distance = Math.abs(goodValues[0] - yawRounded); // get which of the good numbers is closest
////				int idx = 0;
////				for(int c = 1; c < goodValues.length; c++){
////					float cdistance = Math.abs(goodValues[c] - yawRounded);
////					if(cdistance < distance){
////						idx = c;
////						distance = cdistance;
////					}
////				}
////				float multiplier = Math.abs(goodValues[idx] - yawRounded) / 90; // the closest out of the good values - yawRounded, and how far it is (90 is furthest, at which point 0.5 transform)
////				transformNeeded *= multiplier;
//
////				float[] badValues = {-360, -180, 0, 180, 360, -270, -90, 90, 270}; // second round of transformation: the last 4 values need transforming (-, -), the first five (+, +)
////				// transformNeeded2 is 0.25
////				float distance2 = Math.abs(badValues[0] - yawRounded); // get which of the bad numbers is closest
////				int idx2 = 0;
////				for(int c = 1; c < badValues.length; c++){
////					float cdistance = Math.abs(badValues[c] - yawRounded);
////					if(cdistance < distance2){
////						idx2 = c;
////						distance2 = cdistance;
////					}
////				}
////				float transformNeeded2 = idx2 > 4 ? -1f : 1f;
////				transformNeeded2 *= ((-0.00555555555 * Math.abs(badValues[idx2] - yawRounded)) + 1); // *= multiplier, how close it is on a range of 45 (zero transform) to 0 (max 0.25/-0.25 transform)
//////				float multiplier = Math.abs( - yawRounded) / 90; // the closest out of the good values - yawRounded, and how far it is (90 is furthest, at which point 0.5 transform)
////				transformNeeded *= multiplier;
////
////				transformNeeded2 += idx2 > 4 ? 0.7 : -0.7;
////				transformNeeded +=  idx2 > 4 ? -0.07 : 0.07;
//				double vfxLocationX = 0;
//				double vfxLocationZ = 0;
////				double[] transformNeeded = {0,0};
//				double[] transformNeeded = MEI.getPointOnCircle(MathHelper.wrapAngleTo180_float(am2.proxy.ClientProxy.mc.thePlayer.rotationYaw) + 180F, truePosX, truePosZ, 0.5, 0.5);
////				double[] transformNeeded2 = getPointOnCircle(MathHelper.wrapAngleTo180_float(this.mc.thePlayer.rotationYaw) + 90F, vfxLocationX, vfxLocationZ + 1, -0.5, 0.5);
////				transformNeeded[0] += transformNeeded2[0];
////				transformNeeded[1] += transformNeeded2[1];
//
//				emitter = effek.getOrCreate("test:test" + 1);
//				emitter.setPosition(vfxLocationX + transformNeeded[0] + -truePosX + 0.5, 10 + -truePosY + 0.5, vfxLocationZ + transformNeeded[1] + -truePosZ + 0.5);
//				if (mei.ticks > 10) {
//					mei.ticks = 0;
//					effek.delete(emitter);
//				}
//			}

			// here be the code that determines what and where to render

			EntityLivingBase entitylivingbase = Minecraft.getMinecraft().renderViewEntity;
			double x = entitylivingbase.lastTickPosX + (entitylivingbase.posX - entitylivingbase.lastTickPosX) * Minecraft.getMinecraft().timer.renderPartialTicks;
			double y = entitylivingbase.lastTickPosY + (entitylivingbase.posY - entitylivingbase.lastTickPosY) * Minecraft.getMinecraft().timer.renderPartialTicks;
			double z = entitylivingbase.lastTickPosZ + (entitylivingbase.posZ - entitylivingbase.lastTickPosZ) * Minecraft.getMinecraft().timer.renderPartialTicks;
			Frustrum frustrum = new Frustrum();
			frustrum.setPosition(x, y, z);

			for (Map.Entry<String, double[]> entry : MEI.blockBoundEffeks.entrySet()) {
				String effectInfo = entry.getKey();
				double[] effectCoords = entry.getValue();
				if (effectInfo != null) {
					Effek blockEffek = Effeks.get(effectInfo.split("!!!")[0]);
					EffekEmitter blockEmitter = null;
					if (blockEffek != null) {
						blockEmitter = blockEffek.getOrCreate(effectInfo.split("!!!")[1]);
						if (am2.proxy.ClientProxy.mc.thePlayer.worldObj.provider.dimensionId == effectCoords[3]) {
							if (mei.getDistanceFrom(effectCoords[0], effectCoords[1], effectCoords[2], entitylivingbase.posX, entitylivingbase.posY, entitylivingbase.posZ) < AMCore.config.getMaxRenderDistanceSq()) {
								if (frustrum.isBoundingBoxInFrustum(AxisAlignedBB.getBoundingBox(effectCoords[0] - effectCoords[4], effectCoords[1] - effectCoords[5], effectCoords[2] - effectCoords[6], effectCoords[0] + effectCoords[7], effectCoords[1] + effectCoords[8], effectCoords[2] + effectCoords[9])) && !CullableEmitterRegistry.getWrapper(blockEmitter).isCulled()) {
									blockEmitter.setVisible(true);
									blockEmitter.setPaused(false);
									double[] realCoords = mei.getRealEmitterCoordinates(effectCoords[0], effectCoords[1], effectCoords[2]);
									blockEmitter.setPosition(realCoords[0], realCoords[1], realCoords[2]);
								} else {
									blockEmitter.setVisible(false);
									blockEmitter.setPaused(true);
								}
							} else {
								blockEffek.delete(blockEmitter);
							}
						} else {
							blockEffek.delete(blockEmitter);
						}
					}
				}
			}

			// none of this works because transformation matrices don't work
//			List<EffekEmitter> toRemove = new ArrayList<EffekEmitter>();
//			for (Map.Entry<EffekEmitter, int[]> pair : effekStat.entrySet()) { // tick resetting emitters
//				pair.getValue()[0]++;
//				if (pair.getValue()[0] >= pair.getValue()[1] - 1) {
//					pair.getKey().setPlayProgress(0);
//					pair.getValue()[0] = 0;
//				}
//				if (pair.getValue()[2] == 0) { // this is an effek that is being updated
//				} else { // (or not)
//					pair.getKey().setVisible(false);
//					pair.getKey().setPaused(true);
//					toRemove.add(pair.getKey()); // prevents doubling (two entries for same emitter)
//				}
//				pair.getValue()[2]++;
//			}
//			for (EffekEmitter ee : toRemove) effekStat.remove(ee);
//			for (Map.Entry<EffekEmitter, int[]> pair : effekStat.entrySet()) {
//				int[] value = pair.getValue();
//				if (value[0] >= (value[1] - 1)) {
//					pair.getKey().setPlayProgress(0);
//					value[0] = 0;
//				}
//				effekStat.put(pair.getKey(), new int[]{value[0], value[1] + 1});
//			}

			for (int i = 0; i < am2.proxy.ClientProxy.mc.theWorld.loadedEntityList.size(); i++) { // entities client knows about, dimension check is unneeded
				if (i >= am2.proxy.ClientProxy.mc.theWorld.loadedEntityList.size())
					break; // yes, this is needed. Do not question it.
				Entity ent = (Entity) am2.proxy.ClientProxy.mc.theWorld.loadedEntityList.get(i);
				// none of this works because transformation matrices don't work
//				boolean noThirdPerson = (ent == am2.proxy.ClientProxy.mc.thePlayer && am2.proxy.ClientProxy.mc.gameSettings.thirdPersonView == 0); // do not render the player's own 3rd person effek when they're in 1st person
//				boolean maxRenderDistanceSatisfied = false;
//				boolean maxRenderDistanceSatisfiedCalc = false;
//				boolean boundingBoxSatisfied = false;
//				/* 3rd person hand effeks START */
//				if (ent instanceof EntityLivingBase && !noThirdPerson) {
//					EntityLivingBase elb = (EntityLivingBase)ent;
//					ItemStack heldItem = elb.getHeldItem();
//					if (heldItem != null && heldItem.getItem() instanceof IEffekItem) {
//						IEffekItem heldEffekItem = ((IEffekItem)heldItem.getItem());
//						Effek inHand = heldEffekItem.getDisplayedEffekTP(elb, heldItem); // manual non-item 3rd person hand effeks can be done manually
//						if (inHand != null) {
//							float[] scaleValues = heldEffekItem.getScaleValuesTP(elb, heldItem);
//							float[] translationValues = heldEffekItem.getTranslationValuesTP(elb, heldItem);
//							float[] rotationValues = heldEffekItem.getRotationValuesTP(elb, heldItem);
//							int reloadTime = heldEffekItem.getEffekDurationTP(elb, heldItem);
//							EffekEmitter thirdPersonEmitter = inHand.getOrCreate("thirdpersonhand:" + elb.getEntityId()); // not persistent across reloads but we don't need that
//							register(thirdPersonEmitter, reloadTime); // ??????? This not optimal but needed
//							maxRenderDistanceSatisfied = mei.getDistanceFrom(ent.posX, ent.posY, ent.posZ, entitylivingbase.posX, entitylivingbase.posY, entitylivingbase.posZ) < AMCore.config.getMaxRenderDistanceSq();
//							maxRenderDistanceSatisfiedCalc = true;
//							// not a thing we can do; if it's needed to only play an effek once, set reloadTime > actual animation length, it results in auto-destruction
////							if (reloadTime != -1) {
////								if (!heldItem.hasTagCompound()) {
////									NBTTagCompound stackTag = new NBTTagCompound();
////									stackTag.setInteger("effekTSR", 0);
////									heldItem.setTagCompound(stackTag);
////								}
////								int timeSinceReload = heldItem.getTagCompound().getInteger("effekTSR"); // if no key stored, returns 0 (precisely what we need)
////								if (timeSinceReload + 1 > reloadTime) {
////									timeSinceReload = 0;
////									thirdPersonEmitter.setPlayProgress(0);
////								}
////								heldItem.getTagCompound().setInteger("effekTSR", timeSinceReload + 1);
////							}
////							GL11.glEnable(GL12.GL_RESCALE_NORMAL); // not needed here, only reloading helps (?)
//							if (maxRenderDistanceSatisfied) {
//								boundingBoxSatisfied = frustrum.isBoundingBoxInFrustum(ent instanceof IRenderBB ? ((IRenderBB) ent).getRenderBoundingBox() : ent.boundingBox.expand(0.5, 0.5, 0.5)) && !CullableEmitterRegistry.getWrapper(ent).isCulled(); // can't check if the emitter is culled, reason being, 3rd person effeks aren't culled in cullTask. And, no 3rd-person-hand emitter is going to be extraordinarily big as to require special treatment
//								if (boundingBoxSatisfied) {
//									thirdPersonEmitter.setVisible(true);
//									thirdPersonEmitter.setPaused(false);
//									double[] realCoords = mei.getRealEmitterCoordinates(ent.posX, ent.posY, ent.posZ);
//									thirdPersonEmitter.setPosition(realCoords[0], realCoords[1], realCoords[2]);
//									thirdPersonEmitter.applyMatrixChanges(scaleValues[0] - 0.78f, scaleValues[1] - 0.78f, scaleValues[2] - 0.78f, rotationValues[0], rotationValues[1] + 15, rotationValues[2], translationValues[0], translationValues[1] - 0.75f, translationValues[2] - 0.4f);
//								} else {
//									thirdPersonEmitter.setVisible(false); // if it's outside frustum, we do not want it to reset by deleting it, otherwise it would look stupid
//									thirdPersonEmitter.setPaused(true);
//								}
//							} else {
//								inHand.delete(thirdPersonEmitter);
//							}
//						}
//					}
//				}
				/* 3rd person hand effeks END */
				List<String> effectInfo = null;
				if (MEI.cachedBoundEffeks.containsKey(ent)) effectInfo = MEI.cachedBoundEffeks.get(ent);
				else {
					effectInfo = MEI.entityBoundEffeks.get(ent.getUniqueID().toString());
					MEI.cachedBoundEffeks.put(ent, effectInfo);
				}
				if (effectInfo != null) {
					for (String thisEffectInfo : effectInfo) {
						String[] splitStr = thisEffectInfo.split("!!!");
						Effek entityEffek = Effeks.get(splitStr[0]);
						EffekEmitter entityEmitter = null;
						if (entityEffek != null) {
							entityEmitter = entityEffek.getOrCreate(splitStr[1]);
							if (mei.getDistanceFrom(ent.posX, ent.posY, ent.posZ, entitylivingbase.posX, entitylivingbase.posY, entitylivingbase.posZ) < AMCore.config.getMaxRenderDistanceSq()) {
								String[] splitString2 = splitStr[2].split("_");
								// no 'boundingBoxSatisfiedCalc' variable, reason: calculation is done here taking into account expansion values from strings; may have different result from previous calculation with 0.5 hardcoded expansion values
								if (frustrum.isBoundingBoxInFrustum(ent instanceof IRenderBB ? ((IRenderBB) ent).getRenderBoundingBox() : ent.boundingBox.expand(Double.valueOf(splitString2[0]), Double.valueOf(splitString2[1]), Double.valueOf(splitString2[2]))) && !CullableEmitterRegistry.getWrapper(entityEmitter).isCulled()) {
									entityEmitter.setVisible(true);
									entityEmitter.setPaused(false);
									double[] realCoords = mei.getRealEmitterCoordinates(ent.posX, ent.posY, ent.posZ);
									entityEmitter.setPosition(realCoords[0], realCoords[1], realCoords[2]);
									int rotateMode = Integer.valueOf(splitStr[3]);
									if (rotateMode != 0) { // rotatable. If this is 1, 2 or 3, the program assumes you have *all* the necessary dynamic inputs in the effek for rotation.
										if (rotateMode == 2 || rotateMode == 3)
											entityEmitter.setDynamicInput(0, (float) Math.toRadians(ent.rotationPitch));
										else entityEmitter.setDynamicInput(0, 0);
										if (rotateMode == 1 || rotateMode == 3)
											entityEmitter.setDynamicInput(1, (float) Math.toRadians(-ent.rotationYaw));
										else entityEmitter.setDynamicInput(1, 0);
										entityEmitter.setDynamicInput(2, 0); // no roll
									}
								} else {
									entityEmitter.setVisible(false); // see comment on identical line above, as to why this doesn't 'delete'
									entityEmitter.setPaused(true);
								}
							} else {
								entityEffek.delete(entityEmitter);
							}
						}
					}
				}
			}

	//		Effek effek = Effeks.get("arsmagica2:example");
	//		if (effek != null) {
	//			EffekEmitter emitter = effek.getOrCreate("test:test");
	//			emitter.setVisible(false);
	//			for (Entity allEntity : Minecraft.getInstance().world.getAllEntities()) {
	//				if (allEntity instanceof FishingBobberEntity) {
	//					emitter.emitter.setVisibility(true);
	//					emitter.emitter.move(
	//							(float) MathHelper.lerp(Minecraft.getInstance().getRenderPartialTicks(), (float) allEntity.lastTickPosX, allEntity.getPosX()) - 0.5f,
	//							(float) MathHelper.lerp(Minecraft.getInstance().getRenderPartialTicks(), (float) allEntity.lastTickPosY, allEntity.getPosY()) - 0.5f,
	//							(float) MathHelper.lerp(Minecraft.getInstance().getRenderPartialTicks(), (float) allEntity.lastTickPosZ, allEntity.getPosZ()) - 0.5f
	//					);
	//				}
	//				if (allEntity instanceof ArmorStandEntity) {
	//					ResourceLocation location = new ResourceLocation("modid:"+allEntity.getUniqueID().toString());
	//					EffekEmitter emitter1 = effek.getOrCreate(location.toString());
	//					emitter1.setPosition(allEntity.getPosX(), allEntity.getPosY() + allEntity.getEyeHeight(), allEntity.getPosZ());
	//					if (!allEntity.isAlive()) effek.delete(emitter1);
	//				}
	//			}
	//		}
	//		effek = Effeks.get("example:aura");
	//		if (effek != null)
	//			for (int x = 0; x < 16; x++) {
	//				for (int y = 0; y < 16; y++) {
	//					EffekEmitter emitter = effek.getOrCreate("test:x" + x + "y" + y + "z0");
	//					if (emitter != null) emitter.setPosition(x, y + 16, 0);
	//					effek.delete(emitter);
	//				}
	//			}

			float diff = 1;
			if (MEI.lastFrame != -1) {
				long currentTime = System.currentTimeMillis();
				diff = (Math.abs(currentTime - MEI.lastFrame) / 1000f) * 60;
			}

			// to prevent abnormally long hangups to do with effects catching up. this has a downside:
			// if on a server, effects are not synced with other players if they're continuously played.
			// however, this is not an issue, as for anything that relies on those kinds of things, custom scripted effects and stages can solve these problems.
			diff = Math.min(1.5f, diff);

			MEI.lastFrame = System.currentTimeMillis();

//			System.out.println((activerenderinfo.getYaw() + 180.0F) % 360 + "," + activerenderinfo.getPitch());

			// in case the above won't work, try the below
//			System.out.println(-Minecraft.getMinecraft().renderViewEntity.posX + "," +
//			-Minecraft.getMinecraft().renderViewEntity.posY + "," +
//			-Minecraft.getMinecraft().renderViewEntity.posZ);
//

//			GL11.glTranslated(
//					-x,
//					-y,
//					-z
//			);

//			mat.translate(-x, -y, -z);

//			mat.translate(-Minecraft.getMinecraft().renderViewEntity.posX, -Minecraft.getMinecraft().renderViewEntity.posY, -Minecraft.getMinecraft().renderViewEntity.posZ);
//			mat.translate(0.5f, 0.5f, 0.5f);
//			mat.translate(-Minecraft.getMinecraft().renderViewEntity.posX, -Minecraft.getMinecraft().renderViewEntity.posY, -Minecraft.getMinecraft().renderViewEntity.posZ);

//			GL11.glTranslatef(0.5f, 0.5f, 0.5f);
			Matrix4f matrix = am2.proxy.ClientProxy.mat.getLast().getMatrix();
			float[][] cameraMatrix = MEI.matrixToArray(matrix);
			GL11.glDisable(GL11.GL_CULL_FACE);
			GL11.glDisable(GL11.GL_ALPHA_TEST);
			GL11.glDisable(GL11.GL_DEPTH_TEST);

			am2.proxy.ClientProxy.mat.pop();

	//		matrix = Minecraft.getMinecraft().entityRenderer.theShaderGroup.projectionMatrix;
	//		float[][] projectionMatrix = matrixToArray(matrix);

			// if the above won't work
			FloatBuffer b2 = BufferUtils.createFloatBuffer(16);
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
	//		if (event.renderer.getParticleFrameBuffer() != null)
	//			event.renderer.getParticleFrameBuffer().copyDepthFrom(Minecraft.getMinecraft().getFramebuffer());
	//		RenderState.PARTICLES_TARGET.setupRenderState();
			Effeks.forEach((name, effect) -> effect.draw(cameraMatrix, projectionMatrix, finalDiff));
	//		RenderState.PARTICLES_TARGET.clearRenderState();
			GL11.glEnable(GL11.GL_CULL_FACE);
			GL11.glEnable(GL11.GL_DEPTH_TEST);
			GL11.glEnable(GL11.GL_ALPHA_TEST);
			GL11.glPopMatrix();
		}
	}
}
