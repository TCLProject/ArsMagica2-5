package net.tclproject.mysteriumlib.render.gecko.iceandfire;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.ilexiconn.llibrary.LLibrary;
import net.ilexiconn.llibrary.client.util.ClientUtils;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;
import net.tclproject.mysteriumlib.render.gecko.common.entities.entity.specific.iceandfire.IFlappable;

@SideOnly(Side.CLIENT)
public class IFChainBuffer {
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

   private boolean compareDouble(double a, double b) {
      double c = a - b;
      return Math.abs(c - 1.0D) <= 0.01D;
   }

   public void calculateChainSwingBuffer(float maxAngle, int bufferTime, float angleDecrement, float divisor, EntityLivingBase entity) {
      this.prevYawVariation = this.yawVariation;
      if (!this.compareDouble((double)entity.renderYawOffset, (double)entity.prevRenderYawOffset) && MathHelper.abs(this.yawVariation) < maxAngle) {
         this.yawVariation += MathHelper.clamp_float((entity.prevRenderYawOffset - entity.renderYawOffset) / divisor, -maxAngle, maxAngle);
      }

      if (this.yawVariation > 1.0F * angleDecrement) {
         if (this.yawTimer > bufferTime) {
            this.yawVariation -= angleDecrement;
            if (MathHelper.abs(this.yawVariation) < angleDecrement) {
               this.yawVariation = angleDecrement;
               this.yawTimer = 0;
            }
         } else {
            ++this.yawTimer;
         }
      } else if (this.yawVariation < -1.0F * angleDecrement) {
         if (this.yawTimer > bufferTime) {
            this.yawVariation += angleDecrement;
            if (MathHelper.abs(this.yawVariation) < angleDecrement) {
               this.yawVariation = angleDecrement;
               this.yawTimer = 0;
            }
         } else {
            ++this.yawTimer;
         }
      }

   }

   public void calculateChainPitchBuffer(float maxAngle, int bufferTime, float angleDecrement, float divisor, EntityLivingBase entity) {
      this.prevPitchVariation = entity.prevRotationPitch;
      this.pitchVariation = entity.rotationPitch;
   }

   public void calculateChainWaveBuffer(float maxAngle, int bufferTime, float angleDecrement, float divisor, EntityLivingBase entity) {
      this.prevPitchVariation = this.pitchVariation;
      if (!(Math.abs(entity.rotationPitch) > maxAngle)) {
         if (!this.compareDouble((double)entity.rotationPitch, (double)entity.prevRotationPitch) && MathHelper.abs(this.pitchVariation) < maxAngle) {
            this.pitchVariation += MathHelper.clamp_float((entity.prevRotationPitch - entity.rotationPitch) / divisor, -maxAngle, maxAngle);
         }

         if (this.pitchVariation > 1.0F * angleDecrement) {
            if (this.pitchTimer > bufferTime) {
               this.pitchVariation -= angleDecrement;
               if (MathHelper.abs(this.pitchVariation) < angleDecrement) {
                  this.pitchVariation = 0.0F;
                  this.pitchTimer = 0;
               }
            } else {
               ++this.pitchTimer;
            }
         } else if (this.pitchVariation < -1.0F * angleDecrement) {
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
   }

   public void calculateChainFlapBuffer(float maxAngle, int bufferTime, float angleDecrement, float divisor, EntityLivingBase entity) {
      this.prevYawVariation = this.yawVariation;
      if (!this.compareDouble((double)entity.renderYawOffset, (double)entity.prevRenderYawOffset) && MathHelper.abs(this.yawVariation) < maxAngle) {
         this.yawVariation += MathHelper.clamp_float((entity.prevRenderYawOffset - entity.renderYawOffset) / divisor, -maxAngle, maxAngle);
         if (entity instanceof IFlappable && (double)Math.abs(entity.prevRenderYawOffset - entity.renderYawOffset) > 15.0D) {
            ((IFlappable)entity).flapWings();
         }
      }

      if (this.yawVariation > 1.0F * angleDecrement) {
         if (this.yawTimer > bufferTime) {
            this.yawVariation -= angleDecrement;
            if (MathHelper.abs(this.yawVariation) < angleDecrement) {
               this.yawVariation = 0.0F;
               this.yawTimer = 0;
            }
         } else {
            ++this.yawTimer;
         }
      } else if (this.yawVariation < -1.0F * angleDecrement) {
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

   public void calculateChainSwingBuffer(float maxAngle, int bufferTime, float angleDecrement, EntityLivingBase entity) {
      this.calculateChainSwingBuffer(maxAngle, bufferTime, angleDecrement, 1.0F, entity);
   }

   public void calculateChainWaveBuffer(float maxAngle, int bufferTime, float angleDecrement, EntityLivingBase entity) {
      this.calculateChainWaveBuffer(maxAngle, bufferTime, angleDecrement, 1.0F, entity);
   }

   public void calculateChainFlapBuffer(float maxAngle, int bufferTime, float angleDecrement, EntityLivingBase entity) {
      this.calculateChainFlapBuffer(maxAngle, bufferTime, angleDecrement, 1.0F, entity);
   }

   public void applyChainSwingBuffer(ModelRenderer... boxes) {
      float rotateAmount = 0.017453292F * ClientUtils.interpolate(this.prevYawVariation, this.yawVariation, LLibrary.PROXY.getPartialTicks()) / (float)boxes.length;
      ModelRenderer[] var3 = boxes;
      int var4 = boxes.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         ModelRenderer box = var3[var5];
         box.rotateAngleY += rotateAmount;
      }

   }

   public void applyChainWaveBuffer(ModelRenderer... boxes) {
      float rotateAmount = 0.017453292F * ClientUtils.interpolate(this.prevPitchVariation, this.pitchVariation, LLibrary.PROXY.getPartialTicks()) / (float)boxes.length;
      ModelRenderer[] var3 = boxes;
      int var4 = boxes.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         ModelRenderer box = var3[var5];
         box.rotateAngleX += rotateAmount;
      }

   }

   public void applyChainFlapBuffer(ModelRenderer... boxes) {
      float rotateAmount = 0.017453292F * ClientUtils.interpolate(this.prevYawVariation, this.yawVariation, LLibrary.PROXY.getPartialTicks()) / (float)boxes.length;
      ModelRenderer[] var3 = boxes;
      int var4 = boxes.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         ModelRenderer box = var3[var5];
         box.rotateAngleZ += rotateAmount;
      }

   }

   public void applyChainFlapBufferReverse(ModelRenderer... boxes) {
      float rotateAmount = 0.017453292F * ClientUtils.interpolate(this.prevYawVariation, this.yawVariation, LLibrary.PROXY.getPartialTicks()) / (float)boxes.length;
      ModelRenderer[] var3 = boxes;
      int var4 = boxes.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         ModelRenderer box = var3[var5];
         box.rotateAngleZ -= rotateAmount * 0.5F;
      }

   }

   public void applyChainSwingBufferReverse(ModelRenderer... boxes) {
      float rotateAmount = 0.017453292F * ClientUtils.interpolate(this.prevYawVariation, this.yawVariation, LLibrary.PROXY.getPartialTicks()) / (float)boxes.length;
      ModelRenderer[] var3 = boxes;
      int var4 = boxes.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         ModelRenderer box = var3[var5];
         box.rotateAngleY -= rotateAmount;
      }

   }

   public void applyChainWaveBufferReverse(ModelRenderer... boxes) {
      float rotateAmount = 0.017453292F * ClientUtils.interpolate(this.prevPitchVariation, this.pitchVariation, LLibrary.PROXY.getPartialTicks()) / (float)boxes.length;
      ModelRenderer[] var3 = boxes;
      int var4 = boxes.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         ModelRenderer box = var3[var5];
         box.rotateAngleX -= rotateAmount;
      }

   }
}
