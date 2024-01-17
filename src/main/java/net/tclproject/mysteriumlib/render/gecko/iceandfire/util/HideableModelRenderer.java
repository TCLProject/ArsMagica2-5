package net.tclproject.mysteriumlib.render.gecko.iceandfire.util;

import java.util.Iterator;
import net.ilexiconn.llibrary.client.model.tools.AdvancedModelBase;
import net.ilexiconn.llibrary.client.model.tools.AdvancedModelRenderer;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.Tessellator;

public class HideableModelRenderer extends AdvancedModelRenderer {
   public boolean invisible;
   private int displayList;
   private boolean compiled;

   public HideableModelRenderer(AdvancedModelBase model, String name) {
      super(model, name);
   }

   public HideableModelRenderer(AdvancedModelBase model, int i, int i1) {
      super(model, i, i1);
   }

   public void render(float scale) {
      if (!this.isHidden && this.showModel) {
         GlStateManager.pushMatrix();
         if (!this.compiled && !this.invisible) {
            this.compileDisplayList(scale);
         }

         GlStateManager.translate(this.offsetX, this.offsetY, this.offsetZ);
         GlStateManager.translate(this.rotationPointX * scale, this.rotationPointY * scale, this.rotationPointZ * scale);
         if (this.rotateAngleZ != 0.0F) {
            GlStateManager.rotate((float)Math.toDegrees((double)this.rotateAngleZ), 0.0F, 0.0F, 1.0F);
         }

         if (this.rotateAngleY != 0.0F) {
            GlStateManager.rotate((float)Math.toDegrees((double)this.rotateAngleY), 0.0F, 1.0F, 0.0F);
         }

         if (this.rotateAngleX != 0.0F) {
            GlStateManager.rotate((float)Math.toDegrees((double)this.rotateAngleX), 1.0F, 0.0F, 0.0F);
         }

         if (this.scaleX != 1.0F || this.scaleY != 1.0F || this.scaleZ != 1.0F) {
            GlStateManager.scale(this.scaleX, this.scaleY, this.scaleZ);
         }

         if (!this.invisible) {
            GlStateManager.callList(this.displayList);
         }

         if (!this.scaleChildren && (this.scaleX != 1.0F || this.scaleY != 1.0F || this.scaleZ != 1.0F)) {
            GlStateManager.popMatrix();
            GlStateManager.pushMatrix();
            GlStateManager.translate(this.offsetX, this.offsetY, this.offsetZ);
            GlStateManager.translate(this.rotationPointX * scale, this.rotationPointY * scale, this.rotationPointZ * scale);
            if (this.rotateAngleZ != 0.0F) {
               GlStateManager.rotate((float)Math.toDegrees((double)this.rotateAngleZ), 0.0F, 0.0F, 1.0F);
            }

            if (this.rotateAngleY != 0.0F) {
               GlStateManager.rotate((float)Math.toDegrees((double)this.rotateAngleY), 0.0F, 1.0F, 0.0F);
            }

            if (this.rotateAngleX != 0.0F) {
               GlStateManager.rotate((float)Math.toDegrees((double)this.rotateAngleX), 1.0F, 0.0F, 0.0F);
            }
         }

         if (this.childModels != null) {
            Iterator var2 = this.childModels.iterator();

            while(var2.hasNext()) {
               Object childModel = var2.next();
               ((ModelRenderer)childModel).render(scale);
            }
         }

         GlStateManager.popMatrix();
      }

   }

   public void compileDisplayList(float scale) {
      this.displayList = GLAllocation.generateDisplayLists(1);
      GlStateManager.glNewList(this.displayList, 4864);
      Tessellator tessellator = Tessellator.instance;
      Iterator var3 = this.cubeList.iterator();

      while(var3.hasNext()) {
         Object box = var3.next();
         ((ModelBox)box).render(tessellator, scale);
      }

      GlStateManager.glEndList();
      this.compiled = true;
   }
}
