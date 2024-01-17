package net.tclproject.mysteriumlib.render.gecko.common.entities.client.renderer.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class StonelingRenderer extends RenderLiving {
   public StonelingRenderer(ModelBase model, float shadowSize) {
      super(model, shadowSize);
   }

   public ResourceLocation getEntityTexture(Entity entity) {
      return new ResourceLocation("arsmagica2", "textures/entity/stoneling/basalt.png");
   }

   protected void renderEquippedItems(EntityLivingBase p_77029_1_, float p_77029_2_) {
      GL11.glColor3f(1.0F, 1.0F, 1.0F);
      super.renderEquippedItems(p_77029_1_, p_77029_2_);
      ItemStack stack = p_77029_1_.getHeldItem();
      if (stack != null) {
         boolean isBlock = stack.getItem() instanceof ItemBlock;
         GL11.glPushMatrix();
         GL11.glTranslatef(0.0F, 0.3F, 0.0F);
         if (!isBlock) {
            GL11.glRotatef(80.0F, 1.0F, 0.0F, 0.0F);
            GL11.glRotatef(130.0F, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(10.0F, 0.0F, 0.0F, 1.0F);
            GL11.glTranslatef(0.0F, -0.3F, 0.0F);
            GL11.glTranslatef(0.3F, 0.0F, 0.0F);
            GL11.glScalef(0.725F, 0.725F, 0.725F);
         } else {
            GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
         }

         GL11.glScalef(0.725F, 0.725F, 0.725F);
         Minecraft mc = Minecraft.getMinecraft();
         this.renderManager.itemRenderer.renderItem(p_77029_1_, stack, 0);
         GL11.glPopMatrix();
      }

   }

   protected float getDeathMaxRotation(EntityLivingBase entity) {
      return 0.0F;
   }
}
