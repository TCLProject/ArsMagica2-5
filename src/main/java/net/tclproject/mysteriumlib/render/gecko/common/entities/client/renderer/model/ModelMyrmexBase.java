package net.tclproject.mysteriumlib.render.gecko.common.entities.client.renderer.model;

import net.ilexiconn.llibrary.server.util.EnumHandSide;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public abstract class ModelMyrmexBase extends ModelDragonBase {
   private static final ModelMyrmexLarva LARVA_MODEL = new ModelMyrmexLarva();
   private static final ModelMyrmexPupa PUPA_MODEL = new ModelMyrmexPupa();

   public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
      this.renderAdult(entity, f, f1, f2, f3, f4, f5);
   }

   public void postRenderArm(float scale, EnumHandSide side) {
      ModelRenderer[] var3 = this.getHeadParts();
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         ModelRenderer renderer = var3[var5];
         renderer.postRender(scale);
      }

   }

   public abstract ModelRenderer[] getHeadParts();

   public abstract void renderAdult(Entity var1, float var2, float var3, float var4, float var5, float var6, float var7);
}
