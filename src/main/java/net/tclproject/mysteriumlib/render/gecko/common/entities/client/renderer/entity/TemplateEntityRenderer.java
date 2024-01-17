package net.tclproject.mysteriumlib.render.gecko.common.entities.client.renderer.entity;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.tclproject.mysteriumlib.render.gecko.animation.model.AnimatedEntityModel;

public class TemplateEntityRenderer extends RenderLiving {
   public String textureName;

   public TemplateEntityRenderer(AnimatedEntityModel model, float shadow, String textureName) {
      super(model, shadow);
      this.textureName = textureName;
   }

   protected ResourceLocation getEntityTexture(Entity entity) {
      return new ResourceLocation("arsmagica2:textures/entity/" + this.textureName + ".png");
   }
}
