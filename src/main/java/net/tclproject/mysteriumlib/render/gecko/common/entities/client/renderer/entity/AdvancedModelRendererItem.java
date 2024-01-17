package net.tclproject.mysteriumlib.render.gecko.common.entities.client.renderer.entity;

import net.ilexiconn.llibrary.client.model.tools.AdvancedModelBase;
import net.ilexiconn.llibrary.client.model.tools.AdvancedModelRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import org.lwjgl.opengl.GL11;

public class AdvancedModelRendererItem extends AdvancedModelRenderer {
   public EntityLivingBase ent = null;

   public AdvancedModelRendererItem(AdvancedModelBase model) {
      super(model, (String)null);
   }

   public void render(float scale) {
      if (this.ent != null && this.ent.getHeldItem() != null) {
         if (!this.isHidden && this.showModel) {
            GL11.glPushMatrix();
            GL11.glTranslatef(this.offsetX, this.offsetY, this.offsetZ);
            GL11.glTranslatef(this.rotationPointX * scale, this.rotationPointY * scale, this.rotationPointZ * scale);
            if (this.rotateAngleZ != 0.0F) {
               GL11.glRotatef((float)Math.toDegrees((double)this.rotateAngleZ), 0.0F, 0.0F, 1.0F);
            }

            if (this.rotateAngleY != 0.0F) {
               GL11.glRotatef((float)Math.toDegrees((double)this.rotateAngleY), 0.0F, 1.0F, 0.0F);
            }

            if (this.rotateAngleX != 0.0F) {
               GL11.glRotatef((float)Math.toDegrees((double)this.rotateAngleX), 1.0F, 0.0F, 0.0F);
            }

            if (this.scaleX != 1.0F || this.scaleY != 1.0F || this.scaleZ != 1.0F) {
               GL11.glScalef(this.scaleX, this.scaleY, this.scaleZ);
            }

            if (!this.scaleChildren && (this.scaleX != 1.0F || this.scaleY != 1.0F || this.scaleZ != 1.0F)) {
               GL11.glPopMatrix();
               GL11.glPushMatrix();
               GL11.glTranslatef(this.offsetX, this.offsetY, this.offsetZ);
               GL11.glTranslatef(this.rotationPointX * scale, this.rotationPointY * scale, this.rotationPointZ * scale);
               if (this.rotateAngleZ != 0.0F) {
                  GL11.glRotatef((float)Math.toDegrees((double)this.rotateAngleZ), 0.0F, 0.0F, 1.0F);
               }

               if (this.rotateAngleY != 0.0F) {
                  GL11.glRotatef((float)Math.toDegrees((double)this.rotateAngleY), 0.0F, 1.0F, 0.0F);
               }

               if (this.rotateAngleX != 0.0F) {
                  GL11.glRotatef((float)Math.toDegrees((double)this.rotateAngleX), 1.0F, 0.0F, 0.0F);
               }
            }

            GL11.glScalef(0.5F, 0.5F, 0.5F);
            GL11.glTranslatef(0.0F, 0.2F, -0.4F);
            GL11.glRotatef(270.0F, 1.0F, 0.0F, 0.0F);
            GL11.glRotatef(-15.0F, 1.0F, 0.0F, 0.0F);
            GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
            RenderManager.instance.itemRenderer.renderItem(this.ent, this.ent.getHeldItem(), 0);
            ((AdvancedNPCRenderer)RenderManager.instance.getEntityRenderObject(this.ent)).bindEntityTexture(this.ent);
            GL11.glPopMatrix();
         }

      }
   }
}
