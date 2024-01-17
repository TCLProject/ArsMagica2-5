package net.tclproject.mysteriumlib.render.gecko.common.entities.client.renderer.entity;

import com.google.common.collect.Maps;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.Map;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class RenderDragonBase extends RenderLiving {
   private Map LAYERED_TEXTURE_CACHE = Maps.newHashMap();
   private boolean fire;

   public RenderDragonBase(ModelBase model, boolean fire) {
      super(model, 0.8F);
      this.fire = fire;
   }

   protected void preRenderCallback(EntityLivingBase entity, float f) {
      this.shadowSize = ((EntityLiving)entity).getRenderSizeModifier() / 3.0F;
      GL11.glScalef(this.shadowSize, this.shadowSize, this.shadowSize);
      float f7 = entity.prevCameraPitch + (entity.cameraPitch - entity.prevCameraPitch) * f;
      GL11.glRotatef(f7, 1.0F, 0.0F, 0.0F);
   }

   protected ResourceLocation getEntityTexture(Entity entity) {
      return new ResourceLocation("arsmagica2:textures/model/entity/dragon_texture_2.png");
   }
}
