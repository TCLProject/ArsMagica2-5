package net.tclproject.mysteriumlib.render.gecko.common.entities.client.renderer.entity;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.tclproject.mysteriumlib.render.gecko.common.entities.client.renderer.model.EntModel;

public class EntRenderer extends RenderLiving {
   public EntRenderer() {
      super(new EntModel(), 0.6F);
   }

   protected ResourceLocation getEntityTexture(Entity entity) {
      return new ResourceLocation("arsmagica2:textures/entity/ent_texture.png");
   }
}
