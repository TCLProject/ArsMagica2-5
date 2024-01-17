package net.tclproject.mysteriumlib.render.gecko.common.entities.client.renderer.entity;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.tclproject.mysteriumlib.render.gecko.common.entities.client.renderer.model.FairyModel;

public class FairyRenderer extends RenderLiving {
   public FairyRenderer() {
      super(new FairyModel(), 0.6F);
   }

   protected ResourceLocation getEntityTexture(Entity entity) {
      return new ResourceLocation("arsmagica2:textures/entity/winter_pixie.png");
   }
}
