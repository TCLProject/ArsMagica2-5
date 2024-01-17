package net.tclproject.mysteriumlib.render.gecko.common.entities.client.renderer.entity;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.tclproject.mysteriumlib.render.gecko.common.entities.client.renderer.model.ModelMyrmexPupa;

public class RenderPupa extends RenderLiving {
   public RenderPupa() {
      super(new ModelMyrmexPupa(), 0.6F);
   }

   protected ResourceLocation getEntityTexture(Entity entity) {
      return new ResourceLocation("arsmagica2", "textures/model/entity/myrmex/myrmex_jungle_pupa.png");
   }
}
