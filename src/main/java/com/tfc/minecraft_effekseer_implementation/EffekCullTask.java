package com.tfc.minecraft_effekseer_implementation;

import java.util.*;

import am2.AMCore;
import am2.items.IEffekItem;
import com.logisticscraft.occlusionculling.OcclusionCullingInstance;
import com.logisticscraft.occlusionculling.util.Vec3d;

import com.tfc.minecraft_effekseer_implementation.common.Effek;
import com.tfc.minecraft_effekseer_implementation.common.Effeks;
import com.tfc.minecraft_effekseer_implementation.common.api.EffekEmitter;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;

public class EffekCullTask implements Runnable {

        public boolean requestCull = false;

        private final OcclusionCullingInstance culling;
        private final Minecraft client = Minecraft.getMinecraft();
        private final int sleepDelay = 10;
        private final int hitboxLimit = 50;
        public long lastTime = 0;

        // reused preallocated vars
        private Vec3d lastPos = new Vec3d(0, 0, 0);
        private Vec3d aabbMin = new Vec3d(0, 0, 0);
        private Vec3d aabbMax = new Vec3d(0, 0, 0);

	    public EffekCullTask(OcclusionCullingInstance culling) {
            this.culling = culling;
        }

        @Override
        public void run() {
            while (client != null) { // not correct, but the running field is hidden
                try {
                    Thread.sleep(sleepDelay);

                    if (client.theWorld != null && client.thePlayer != null && client.thePlayer.ticksExisted > 10 && client.renderViewEntity != null) {
                        Vec3 cameraMC = getCameraPos();
//                        if(Config.debugMode) {
//                            cameraMC = getPositionEyes(client.thePlayer, 0);
//                        } else {
//                        }
                        if (requestCull || !(cameraMC.xCoord == lastPos.x && cameraMC.yCoord == lastPos.y && cameraMC.zCoord == lastPos.z)) {
                            long start = System.currentTimeMillis();
                            requestCull = false;
                            lastPos.set(cameraMC.xCoord, cameraMC.yCoord, cameraMC.zCoord);
                            Vec3d camera = lastPos;
                            culling.resetCache();
                            boolean noCulling = client.thePlayer.noClip || client.gameSettings.thirdPersonView != 0; // noClip is a 'spectator' check replacer (EtFuturum Requiem compat)

                            for (Map.Entry<String, double[]> entry : MEI.blockBoundEffeks.entrySet()) {
                                String effectInfo = entry.getKey();
                                double[] effectCoords = entry.getValue();
                                if (effectInfo != null) {
                                    Effek blockEffek = Effeks.get(effectInfo.split("!!!")[0]);
                                    EffekEmitter blockEmitter = null;
                                    if (blockEffek != null) {
                                        blockEmitter = blockEffek.getOrCreate(effectInfo.split("!!!")[1]);
                                        CullableEmitterWrapper cullable = CullableEmitterRegistry.getWrapper(blockEmitter);
                                        if (!cullable.isForcedVisible()) {
                                            if (noCulling) {
                                                cullable.setCulled(false);
                                                continue;
                                            }
                                            AxisAlignedBB boundingBox = AxisAlignedBB.getBoundingBox(effectCoords[0] - effectCoords[4], effectCoords[1] - effectCoords[5], effectCoords[2] - effectCoords[6], effectCoords[0] + effectCoords[7], effectCoords[1] + effectCoords[8], effectCoords[2] + effectCoords[9]);
                                            if (setBoxAndCheckLimits(cullable, boundingBox)) continue;
                                            boolean visible = culling.isAABBVisible(aabbMin, aabbMax, camera);
                                            cullable.setCulled(!visible);
                                        }
                                    }
                                }
                            }

                            for (int i = 0; i < am2.proxy.ClientProxy.mc.theWorld.loadedEntityList.size(); i++) { // entities client knows about, dimension check is unneeded
                                if (i >= am2.proxy.ClientProxy.mc.theWorld.loadedEntityList.size())
                                    break; // yes, this is needed. Do not question it.
                                Entity ent = (Entity) am2.proxy.ClientProxy.mc.theWorld.loadedEntityList.get(i);
                                boolean posVector = getPositionVector(ent).squareDistanceTo(cameraMC) > 128 * 128;
                                //general entity culling (3rd person effeks) start
                                if (ent instanceof EntityLivingBase) {
                                    ItemStack heldItem = ((EntityLivingBase) ent).getHeldItem();
                                    if (heldItem != null && heldItem.getItem() instanceof IEffekItem) {
                                        CullableEmitterWrapper cullableEnt = CullableEmitterRegistry.getWrapper(ent);
                                        if (!cullableEnt.isForcedVisible()) {
                                            if (noCulling) {
                                                cullableEnt.setCulled(false);
                                                continue;
                                            }
                                            if (posVector) {
                                                cullableEnt.setCulled(false); // If your entity view distance is larger than tracingDistance (128) just render it
                                                continue;
                                            }
                                            AxisAlignedBB boundingBox = ent instanceof IRenderBB ? ((IRenderBB) ent).getRenderBoundingBox() : ent.boundingBox.expand(0.5, 0.5, 0.5);
                                            if (setBoxAndCheckLimits(cullableEnt, boundingBox)) continue;
                                            boolean visible = culling.isAABBVisible(aabbMin, aabbMax, camera);
                                            cullableEnt.setCulled(!visible);
                                        }
                                    }
                                }
                                //general entity culling (3rd person effeks) end
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
                                            CullableEmitterWrapper cullable = CullableEmitterRegistry.getWrapper(entityEmitter);
                                            if (!cullable.isForcedVisible()) {
                                                if (noCulling) {
                                                    cullable.setCulled(false);
                                                    continue;
                                                }
                                                if(posVector) {
                                                    cullable.setCulled(false); // If your entity view distance is larger than tracingDistance (128) just render it
                                                    continue;
                                                }
                                                String[] splitString2 = splitStr[2].split("_");
                                                AxisAlignedBB boundingBox = ent instanceof IRenderBB ? ((IRenderBB) ent).getRenderBoundingBox() : ent.boundingBox.expand(Double.valueOf(splitString2[0]), Double.valueOf(splitString2[1]), Double.valueOf(splitString2[2]));
                                                if (setBoxAndCheckLimits(cullable, boundingBox)) continue;
                                                boolean visible = culling.isAABBVisible(aabbMin, aabbMax, camera);
                                                cullable.setCulled(!visible);
                                            }
                                        }
                                    }
                                }
                            }
                            lastTime = (System.currentTimeMillis()-start);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            System.out.println("Shutting down culling task!");
        }

        private boolean setBoxAndCheckLimits(CullableEmitterWrapper cullable, AxisAlignedBB boundingBox) {
            if(boundingBox.maxX - boundingBox.minX > hitboxLimit || boundingBox.maxY - boundingBox.minY > hitboxLimit || boundingBox.maxZ - boundingBox.minZ > hitboxLimit) {
                cullable.setCulled(false); // To big to bother to cull
                return true;
            }
            aabbMin.set(boundingBox.minX, boundingBox.minY, boundingBox.minZ);
            aabbMax.set(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ);
            return false;
        }

        public static Vec3 getPositionVector(Entity e)
        {
            return Vec3.createVectorHelper(e.posX, e.posY, e.posZ);
        }

        @SideOnly(Side.CLIENT)
        public Vec3 getPositionEyes(Entity e, float partialTicks)
        {
            if (partialTicks == 1.0F)
            {
                return Vec3.createVectorHelper(e.posX, e.posY + (double)e.getEyeHeight(), e.posZ);
            }
            else
            {
                double d0 = e.prevPosX + (e.posX - e.prevPosX) * (double)partialTicks;
                double d1 = e.prevPosY + (e.posY - e.prevPosY) * (double)partialTicks + (double)e.getEyeHeight();
                double d2 = e.prevPosZ + (e.posZ - e.prevPosZ) * (double)partialTicks;
                return Vec3.createVectorHelper(d0, d1, d2);
            }
        }

        // 1.7.x doesn't know where the heck the camera is either
        private Vec3 getCameraPos() {
            return getPositionEyes(client.renderViewEntity, 0);
            // doesnt work correctly
//        Entity entity = client.getRenderViewEntity();
//        float f = entity.getEyeHeight();
//        double d0 = entity.posX;
//        double d1 = entity.posY + f;
//        double d2 = entity.posZ;
//        double d3 = 4.0F;
//        float f1 = entity.rotationYaw;
//        float f2 = entity.rotationPitch;
//        if (client.gameSettings.thirdPersonView == 2)
//            f2 += 180.0F;
//        double d4 = (-MathHelper.sin(f1 / 180.0F * 3.1415927F) * MathHelper.cos(f2 / 180.0F * 3.1415927F)) * d3;
//        double d5 = (MathHelper.cos(f1 / 180.0F * 3.1415927F) * MathHelper.cos(f2 / 180.0F * 3.1415927F)) * d3;
//        double d6 = -MathHelper.sin(f2 / 180.0F * 3.1415927F) * d3;
//        for (int i = 0; i < 8; i++) {
//            float f3 = ((i & 0x1) * 2 - 1);
//            float f4 = ((i >> 1 & 0x1) * 2 - 1);
//            float f5 = ((i >> 2 & 0x1) * 2 - 1);
//            f3 *= 0.1F;
//            f4 *= 0.1F;
//            f5 *= 0.1F;
//            MovingObjectPosition movingobjectposition = client.theWorld.rayTraceBlocks(
//                    new Vec3(d0 + f3, d1 + f4, d2 + f5),
//                    new Vec3(d0 - d4 + f3 + f5, d1 - d6 + f4, d2 - d5 + f5));
//            if (movingobjectposition != null) {
//                double d7 = movingobjectposition.hitVec.distanceTo(new Vec3(d0, d1, d2));
//                if (d7 < d3)
//                    d3 = d7;
//            }
//        }
//        float pitchRadian = f2 * (3.1415927F / 180); // X rotation
//        float yawRadian   = f1   * (3.1415927F / 180); // Y rotation
//        double newPosX = d0 - d3 *  MathHelper.sin( yawRadian ) * MathHelper.cos( pitchRadian );
//        double newPosY = d1 - d3 * -MathHelper.sin( pitchRadian );
//        double newPosZ = d2 - d3 *  MathHelper.cos( yawRadian ) * MathHelper.cos( pitchRadian );
//        Vec3 vec = new Vec3(newPosX, newPosY, newPosZ);
//        System.out.println(newPosX + " " + newPosY + " " + newPosZ);
//        return vec;
        }
}
