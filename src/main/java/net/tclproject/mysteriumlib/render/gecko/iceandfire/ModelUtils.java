package net.tclproject.mysteriumlib.render.gecko.iceandfire;

import java.util.Iterator;
import java.util.List;
import net.ilexiconn.llibrary.client.model.ModelAnimator;
import net.ilexiconn.llibrary.client.model.tools.AdvancedModelRenderer;
import net.minecraft.client.model.ModelRenderer;

public class ModelUtils {
   public static void renderAll(List boxList) {
      Iterator var1 = boxList.iterator();

      while(var1.hasNext()) {
         Object element = var1.next();
         if (element instanceof AdvancedModelRenderer) {
            AdvancedModelRenderer box = (AdvancedModelRenderer)element;
            if (box.getParent() == null) {
               box.render(0.0625F);
            }
         }
      }

   }

   public static void animateOrRotate(ModelAnimator ModelAnimator, boolean animate, AdvancedModelRenderer box, float x, float y, float z) {
      if (animate) {
         ModelAnimator.rotate(box, x == box.rotateAngleX ? 0.0F : x, y == box.rotateAngleY ? 0.0F : y, z == box.rotateAngleZ ? 0.0F : z);
      } else {
         setRotateAngle(box, x, y, z);
      }

   }

   public static void animateOrRotateIgnore(ModelAnimator ModelAnimator, boolean animate, AdvancedModelRenderer box, float x, float y, float z, boolean ignoreX, boolean ignoreY, boolean ignoreZ) {
      if (animate) {
         ModelAnimator.rotate(box, ignoreX ? 0.0F : x, ignoreY ? y : 0.0F, ignoreZ ? z : 0.0F);
      } else {
         setRotateAngle(box, x, y, z);
      }

   }

   private static void setRotateAngle(AdvancedModelRenderer model, float x, float y, float z) {
      model.rotateAngleX = x;
      model.rotateAngleY = y;
      model.rotateAngleZ = z;
   }

   public static void rotate(ModelAnimator ModelAnimator, ModelRenderer box, float x, float y, float z) {
      ModelAnimator.rotate(box, (float)Math.toRadians((double)x), (float)Math.toRadians((double)y), (float)Math.toRadians((double)z));
   }

   public static void rotateFrom(ModelAnimator ModelAnimator, ModelRenderer box, float x, float y, float z) {
      ModelAnimator.rotate(box, (float)Math.toRadians((double)x) - box.rotateAngleX, (float)Math.toRadians((double)y) - box.rotateAngleY, (float)Math.toRadians((double)z) - box.rotateAngleZ);
   }

   public static void rotateFromRadians(ModelAnimator ModelAnimator, ModelRenderer box, float x, float y, float z) {
      ModelAnimator.rotate(box, x - box.rotateAngleX, y - box.rotateAngleY, z - box.rotateAngleZ);
   }
}
