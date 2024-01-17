package net.tclproject.mysteriumlib.render.gecko.common.entities.client.renderer.entity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.tclproject.mysteriumlib.render.gecko.common.entities.entity.specific.ChickenEntity;

@SideOnly(Side.CLIENT)
public class ChickenRenderer extends RenderLiving {
   private static final ResourceLocation TEXTURE2 = new ResourceLocation("arsmagica2", "textures/entity/hen_golden.png");

   public ChickenRenderer(ModelBase model, float shadowSize) {
      super(model, shadowSize);
   }

   public ResourceLocation getEntityTexture(Entity entity) {
      return TEXTURE2;
   }

   protected float getDeathMaxRotation(EntityLivingBase entity) {
      return 0.0F;
   }

   protected float handleRotationFloat(EntityLivingBase livingBase, float partialTicks) {
      ChickenEntity entity = (ChickenEntity)livingBase;
      float f = entity.oFlap + (entity.field_70886_e - entity.oFlap) * partialTicks;
      float f1 = entity.oFlapSpeed + (entity.destPos - entity.oFlapSpeed) * partialTicks;
      return (MathHelper.sin(f) + 1.0F) * f1;
   }
}
