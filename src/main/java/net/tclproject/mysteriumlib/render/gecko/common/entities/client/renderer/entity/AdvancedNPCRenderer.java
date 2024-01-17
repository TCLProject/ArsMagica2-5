package net.tclproject.mysteriumlib.render.gecko.common.entities.client.renderer.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

public class AdvancedNPCRenderer extends RenderLiving {
   private static final ResourceLocation TEXTURE = new ResourceLocation("arsmagica2", "textures/models/maid0.png");

   public AdvancedNPCRenderer(ModelBase model, float shadowSize) {
      super(model, shadowSize);
   }

   public ResourceLocation getEntityTexture(Entity entity) {
      return TEXTURE;
   }

   public void bindEntityTexture(Entity p_110777_1_) {
      super.bindEntityTexture(p_110777_1_);
   }

   protected float getDeathMaxRotation(EntityLivingBase entity) {
      return 0.0F;
   }
}
