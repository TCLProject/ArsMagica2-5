package com.tfc.minecraft_effekseer_implementation;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Direction;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import com.tfc.minecraft_effekseer_implementation.vector.*;
import net.tclproject.mysteriumlib.future.PortUtil;

@SideOnly(Side.CLIENT)
public class ActiveRenderInfo {
    private boolean valid;
    private Entity renderViewEntity;
    private final Vector3f look = new Vector3f(0.0F, 0.0F, 1.0F);
    private final Vector3f up = new Vector3f(0.0F, 1.0F, 0.0F);
    private final Vector3f left = new Vector3f(1.0F, 0.0F, 0.0F);
    private float pitch;
    private float yaw;
    private final Quaternion rotation = new Quaternion(0.0F, 0.0F, 0.0F, 1.0F);
    private boolean thirdPerson;
    private boolean thirdPersonReverse;
    private Vector3f pos = new Vector3f(0f, 0f, 0f);
    private float height;
    private float previousHeight;

    public void update(EntityLivingBase renderViewEntity, float partialTicks) {
        this.valid = true;
        this.renderViewEntity = renderViewEntity;
//        this.setPosition(renderViewEntity.posX, renderViewEntity.posY, renderViewEntity.posZ);
        this.thirdPerson = Minecraft.getMinecraft().gameSettings.thirdPersonView != 0;
        this.thirdPersonReverse = Minecraft.getMinecraft().gameSettings.thirdPersonView == 2;
        this.setDirection(renderViewEntity.rotationYaw, renderViewEntity.rotationPitch);
        this.setPosition(PortUtil.lerp((double)partialTicks, renderViewEntity.prevPosX, renderViewEntity.posX), PortUtil.lerp((double)partialTicks, renderViewEntity.prevPosY, renderViewEntity.posY) + (double)PortUtil.lerp(partialTicks, this.previousHeight, this.height), PortUtil.lerp((double)partialTicks, renderViewEntity.prevPosZ, renderViewEntity.posZ));
//        if (thirdPerson) {
//            if (thirdPersonReverse) {
//                this.setDirection(-this.pitch, this.yaw + 180.0F);
//            }

//            this.movePosition(-this.calcCameraDistance(4.0D), 0.0D, 0.0D);
//        }
    }

    public void interpolateHeight() {
        if (this.renderViewEntity != null) {
            this.previousHeight = this.height;
            this.height += (this.renderViewEntity.getEyeHeight() - this.height) * 0.5F;
        }

    }

    /**
     * Checks for collision of the third person camera and returns the distance
     */
    private double calcCameraDistance(double startingDistance) {
        MovingObjectPosition raytraceresult = getRaytraceBlock(Minecraft.getMinecraft().thePlayer);
        if (raytraceresult != null) {
            double d0 = raytraceresult.hitVec.distanceTo(Vec3.createVectorHelper(this.pos.x, this.pos.y, this.pos.z));
            if (d0 < startingDistance) {
                startingDistance = d0;
            }
        }
        return startingDistance;
    }

    public static MovingObjectPosition getRaytraceBlock(EntityPlayer p) {
        float scaleFactor = 1.0F;
        float rotPitch = p.prevRotationPitch + (p.rotationPitch - p.prevRotationPitch) * scaleFactor;
        float rotYaw = p.prevRotationYaw + (p.rotationYaw - p.prevRotationYaw) * scaleFactor;
        double testX = p.prevPosX + (p.posX - p.prevPosX) * scaleFactor;
        double testY = p.prevPosY + (p.posY - p.prevPosY) * scaleFactor + 1.62D - p.yOffset;//1.62 is player eye height
        double testZ = p.prevPosZ + (p.posZ - p.prevPosZ) * scaleFactor;
        Vec3 testVector = Vec3.createVectorHelper(testX, testY, testZ);
        float var14 = MathHelper.cos(-rotYaw * 0.017453292F - (float)Math.PI);
        float var15 = MathHelper.sin(-rotYaw * 0.017453292F - (float)Math.PI);
        float var16 = -MathHelper.cos(-rotPitch * 0.017453292F);
        float vectorY = MathHelper.sin(-rotPitch * 0.017453292F);
        float vectorX = var15 * var16;
        float vectorZ = var14 * var16;
        double reachLength = 5.0D;
        Vec3 testVectorFar = testVector.addVector(vectorX * reachLength, vectorY * reachLength, vectorZ * reachLength);
        return p.worldObj.rayTraceBlocks(testVector, testVectorFar, false);
    }

    /**
     * Moves the render position relative to the view direction, for third person camera
     */
    protected void movePosition(double distanceOffset, double verticalOffset, double horizontalOffset) {
        double d0 = (double)this.look.getX() * distanceOffset + (double)this.up.getX() * verticalOffset + (double)this.left.getX() * horizontalOffset;
        double d1 = (double)this.look.getY() * distanceOffset + (double)this.up.getY() * verticalOffset + (double)this.left.getY() * horizontalOffset;
        double d2 = (double)this.look.getZ() * distanceOffset + (double)this.up.getZ() * verticalOffset + (double)this.left.getZ() * horizontalOffset;
        this.setPosition(new Vector3f((float)(this.pos.x + d0), (float)(this.pos.y + d1), (float)(this.pos.z + d2)));
    }

    protected void setDirection(float pitchIn, float yawIn) {
        this.pitch = yawIn;
        this.yaw = pitchIn;
        this.rotation.set(0.0F, 0.0F, 0.0F, 1.0F);
        this.rotation.multiply(Vector3f.YP.rotationDegrees(-pitchIn));
        this.rotation.multiply(Vector3f.XP.rotationDegrees(yawIn));
        this.look.set(new float[] {0.0F, 0.0F, 1.0F});
        this.look.transform(this.rotation);
        this.up.set(new float[] {0.0F, 1.0F, 0.0F});
        this.up.transform(this.rotation);
        this.left.set(new float[] {1.0F, 0.0F, 0.0F});
        this.left.transform(this.rotation);
    }

    /**
     * Sets the position and blockpos of the active render
     */
    protected void setPosition(double x, double y, double z) {
        this.setPosition(new Vector3f((float)x, (float)y, (float)z));
    }

    protected void setPosition(Vector3f posIn) {
        this.pos = posIn;
    }

    public Vector3f getProjectedView() {
        return this.pos;
    }

    public float getPitch() {
        return this.pitch;
    }

    public float getYaw() {
        return this.yaw;
    }

    public Quaternion getRotation() {
        return this.rotation;
    }

    public Entity getRenderViewEntity() {
        return this.renderViewEntity;
    }

    public boolean isValid() {
        return this.valid;
    }

    public boolean isThirdPerson() {
        return this.thirdPerson;
    }

    public final Vector3f getViewVector() {
        return this.look;
    }

    public final Vector3f getUpVector() {
        return this.up;
    }

    public void clear() {
        this.renderViewEntity = null;
        this.valid = false;
    }

    public void setAnglesInternal(float yaw, float pitch) {
        this.yaw = yaw;
        this.pitch = pitch;
    }
}
