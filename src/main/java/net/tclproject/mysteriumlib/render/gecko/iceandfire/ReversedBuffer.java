package net.tclproject.mysteriumlib.render.gecko.iceandfire;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.ilexiconn.llibrary.LLibrary;
import net.ilexiconn.llibrary.client.util.ClientUtils;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;

@SideOnly(Side.CLIENT)
public class ReversedBuffer {
   private int yawTimer;
   private float yawVariation;
   private int pitchTimer;
   private float pitchVariation;
   private float prevYawVariation;
   private float prevPitchVariation;

   public void resetRotations() {
      this.yawVariation = 0.0F;
      this.pitchVariation = 0.0F;
      this.prevYawVariation = 0.0F;
      this.prevPitchVariation = 0.0F;
   }

   public void calculateChainSwingBuffer(float maxAngle, int bufferTime, float angleDecrement, float divisor, EntityLivingBase entity) {
      this.prevYawVariation = this.yawVariation;
      if (entity.renderYawOffset != entity.prevRenderYawOffset && MathHelper.abs(this.yawVariation) < maxAngle) {
         this.yawVariation += (entity.prevRenderYawOffset - entity.renderYawOffset) / divisor;
      }

      if (this.yawVariation > 0.7F * angleDecrement) {
         if (this.yawTimer > bufferTime) {
            this.yawVariation -= angleDecrement;
            if (MathHelper.abs(this.yawVariation) < angleDecrement) {
               this.yawVariation = 0.0F;
               this.yawTimer = 0;
            }
         } else {
            ++this.yawTimer;
         }
      } else if (this.yawVariation < -0.7F * angleDecrement) {
         if (this.yawTimer > bufferTime) {
            this.yawVariation += angleDecrement;
            if (MathHelper.abs(this.yawVariation) < angleDecrement) {
               this.yawVariation = 0.0F;
               this.yawTimer = 0;
            }
         } else {
            ++this.yawTimer;
         }
      }

   }

   public void calculateChainWaveBuffer(float maxAngle, int bufferTime, float angleDecrement, float divisor, EntityLivingBase entity) {
      this.prevPitchVariation = this.pitchVariation;
      if (entity.rotationPitch != entity.prevRotationPitch && MathHelper.abs(this.pitchVariation) < maxAngle) {
         this.pitchVariation += (entity.prevRotationPitch - entity.rotationPitch) / divisor;
      }

      if (this.pitchVariation > 0.7F * angleDecrement) {
         if (this.pitchTimer > bufferTime) {
            this.pitchVariation -= angleDecrement;
            if (MathHelper.abs(this.pitchVariation) < angleDecrement) {
               this.pitchVariation = 0.0F;
               this.pitchTimer = 0;
            }
         } else {
            ++this.pitchTimer;
         }
      } else if (this.pitchVariation < -0.7F * angleDecrement) {
         if (this.pitchTimer > bufferTime) {
            this.pitchVariation += angleDecrement;
            if (MathHelper.abs(this.pitchVariation) < angleDecrement) {
               this.pitchVariation = 0.0F;
               this.pitchTimer = 0;
            }
         } else {
            ++this.pitchTimer;
         }
      }

   }

   public void calculateChainSwingBuffer(float maxAngle, int bufferTime, float angleDecrement, EntityLivingBase entity) {
      this.calculateChainSwingBuffer(maxAngle, bufferTime, angleDecrement, 1.0F, entity);
   }

   public void calculateChainWaveBuffer(float maxAngle, int bufferTime, float angleDecrement, EntityLivingBase entity) {
      this.calculateChainWaveBuffer(maxAngle, bufferTime, angleDecrement, 1.0F, entity);
   }

   public void applyChainSwingBuffer(ModelRenderer... boxes) {
      float rotateAmount = 0.017453292F * ClientUtils.interpolate(this.prevYawVariation, this.yawVariation, LLibrary.PROXY.getPartialTicks()) / (float)boxes.length;
      ModelRenderer[] var3 = boxes;
      int var4 = boxes.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         ModelRenderer box = var3[var5];
         box.rotateAngleY -= rotateAmount;
      }

   }

   public void applyChainWaveBuffer(ModelRenderer... boxes) {
      float rotateAmount = 0.017453292F * ClientUtils.interpolate(this.prevPitchVariation, this.pitchVariation, LLibrary.PROXY.getPartialTicks()) / (float)boxes.length;
      ModelRenderer[] var3 = boxes;
      int var4 = boxes.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         ModelRenderer box = var3[var5];
         box.rotateAngleX -= rotateAmount;
      }

   }
}
