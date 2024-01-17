package am2.items;

import am2.items.renderers.RenderItemBoxOfIllusions;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class ItemBoxOfIllusions extends ArsMagicaItem {

    @Override
    public String getItemStackDisplayName(ItemStack p_77653_1_)
    {
        return "§a" + ("" + StatCollector.translateToLocal(this.getUnlocalizedNameInefficiently(p_77653_1_) + ".name")).trim() + "§r";
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player){
        if (world.isRemote) RenderItemBoxOfIllusions.doRotations = true;
        return super.onItemRightClick(stack, world, player);
    }

    // courtesy of MPM
    public static void Copy(EntityLivingBase copied, EntityLivingBase entity){
        entity.worldObj = copied.worldObj;

        entity.deathTime = copied.deathTime;
        entity.distanceWalkedModified = copied.distanceWalkedModified;
        entity.prevDistanceWalkedModified = copied.distanceWalkedModified;
        entity.distanceWalkedOnStepModified = copied.distanceWalkedOnStepModified;

        entity.moveForward = copied.moveForward;
        entity.moveStrafing = copied.moveStrafing;
        entity.onGround = copied.onGround;
        entity.fallDistance = copied.fallDistance;
        entity.setJumping(copied.isAirBorne);
        entity.setSneaking(copied.isSneaking());

        entity.prevPosX = copied.prevPosX;
        entity.prevPosY = copied.prevPosY;
        entity.prevPosZ = copied.prevPosZ;

        entity.posX = copied.posX;
        entity.posY = copied.posY;
        entity.posZ = copied.posZ;
        entity.boundingBox.setBB(copied.boundingBox);

        entity.lastTickPosX = copied.lastTickPosX;
        entity.lastTickPosY = copied.lastTickPosY;
        entity.lastTickPosZ = copied.lastTickPosZ;

        entity.motionX = copied.motionX;
        entity.motionY = copied.motionY;
        entity.motionZ = copied.motionZ;

        entity.rotationYaw = copied.rotationYaw;
        entity.rotationPitch = copied.rotationPitch;
        entity.prevRotationYaw = copied.prevRotationYaw;
        entity.prevRotationPitch = copied.prevRotationPitch;
        entity.rotationYawHead = copied.rotationYawHead;
        entity.prevRotationYawHead = copied.prevRotationYawHead;
        entity.renderYawOffset = copied.renderYawOffset;
        entity.prevRenderYawOffset = copied.prevRenderYawOffset;
        entity.cameraPitch = copied.cameraPitch;
        entity.prevCameraPitch = copied.prevCameraPitch;

        entity.limbSwingAmount = copied.limbSwingAmount;
        entity.prevLimbSwingAmount = copied.prevLimbSwingAmount;
        entity.limbSwing = copied.limbSwing;

        entity.swingProgress = copied.swingProgress;
        entity.prevSwingProgress = copied.prevSwingProgress;
        entity.isSwingInProgress = copied.isSwingInProgress;
        entity.swingProgressInt = copied.swingProgressInt;

        entity.ticksExisted = copied.ticksExisted;

        entity.riddenByEntity = copied.riddenByEntity;

        if(entity instanceof EntityPlayer && copied instanceof EntityPlayer){
            EntityPlayer ePlayer = (EntityPlayer) entity;
            EntityPlayer cPlayer = (EntityPlayer) copied;

            ePlayer.cameraYaw = cPlayer.cameraYaw;
            ePlayer.prevCameraYaw = cPlayer.prevCameraYaw;

            ePlayer.field_71091_bM = cPlayer.field_71091_bM;
            ePlayer.field_71096_bN = cPlayer.field_71096_bN;
            ePlayer.field_71097_bO = cPlayer.field_71097_bO;
            ePlayer.field_71094_bP = cPlayer.field_71094_bP;
            ePlayer.field_71095_bQ = cPlayer.field_71095_bQ;
            ePlayer.field_71085_bR = cPlayer.field_71085_bR;
        }

        for(int i = 0; i < 5; i++){
            entity.setCurrentItemOrArmor(i, copied.getEquipmentInSlot(i));
        }

        if(entity instanceof EntityDragon){
            entity.rotationYaw += 180;
        }
    }
}
