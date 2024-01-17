package net.tclproject.mysteriumlib.render.gecko.common.entities.client.renderer.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

public class HorseRenderer extends RenderLiving {
   private static final ResourceLocation TEXTURE2 = new ResourceLocation("arsmagica2", "textures/entity/draft_horse_white.png");

   public HorseRenderer(ModelBase model, float shadowSize) {
      super(model, shadowSize);
   }

   public ResourceLocation getEntityTexture(Entity entity) {
      return TEXTURE2;
   }

   protected float getDeathMaxRotation(EntityLivingBase entity) {
      return 0.0F;
   }
}
