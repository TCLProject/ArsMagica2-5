package net.tclproject.mysteriumlib.render.gecko.common.entities.client.renderer.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;

public class StonelingModel extends ModelBase {
   private final ModelRenderer body;
   private final ModelRenderer arm_right;
   private final ModelRenderer arm_left;
   private final ModelRenderer leg_right;
   private final ModelRenderer leg_left;

   public StonelingModel() {
      this.textureWidth = 32;
      this.textureHeight = 32;
      this.body = new ModelRenderer(this);
      this.body.setRotationPoint(0.0F, 14.0F, 0.0F);
      ModelRenderer head = new ModelRenderer(this);
      head.setRotationPoint(0.0F, 0.0F, 0.0F);
      this.body.addChild(head);
      head.cubeList.add(new NewModelBox(head, -3.0F, -2.0F, -3.0F, 6, 8, 6, 0.0F, 0, 0));
      head.cubeList.add(new NewModelBox(head, -1.0F, -4.0F, -5.0F, 2, 4, 2, 0.0F, 8, 24));
      head.cubeList.add(new NewModelBox(head, -1.0F, 6.0F, -3.0F, 2, 2, 2, 0.0F, 16, 20));
      head.cubeList.add(new NewModelBox(head, -1.0F, -4.0F, 3.0F, 2, 4, 2, 0.0F, 0, 24));
      head.cubeList.add(new NewModelBox(head, -1.0F, -4.0F, -3.0F, 2, 2, 6, 0.0F, 16, 24));
      head.cubeList.add(new NewModelBox(head, -1.0F, -4.0F, -1.0F, 2, 2, 2, 0.0F, 24, 20));
      head.cubeList.add(new NewModelBox(head, -1.0F, 1.0F, -5.0F, 2, 2, 2, 0.0F, 18, 0));
      head.cubeList.add(new NewModelBox(head, -4.0F, -1.0F, -3.0F, 1, 2, 2, 0.0F, 0, 0));
      head.cubeList.add(new NewModelBox(head, 3.0F, -1.0F, -3.0F, 1, 2, 2, 0.0F, 0, 0));
      this.arm_right = new ModelRenderer(this);
      this.arm_right.setRotationPoint(-3.0F, 2.0F, 0.0F);
      this.setRotationAngle(this.arm_right, 3.1416F, 0.0F, 0.0F);
      this.body.addChild(this.arm_right);
      this.arm_right.cubeList.add(new NewModelBox(this.arm_right, -2.0F, 0.0F, -1.0F, 2, 8, 2, 0.0F, 0, 14));
      this.arm_left = new ModelRenderer(this);
      this.arm_left.setRotationPoint(3.0F, 2.0F, 0.0F);
      this.setRotationAngle(this.arm_left, 3.1416F, 0.0F, 0.0F);
      this.body.addChild(this.arm_left);
      this.arm_left.cubeList.add(new NewModelBox(this.arm_left, 0.0F, 0.0F, -1.0F, 2, 8, 2, 0.0F, 8, 14));
      this.leg_right = new ModelRenderer(this);
      this.leg_right.setRotationPoint(-2.0F, 4.0F, 0.0F);
      this.body.addChild(this.leg_right);
      this.leg_right.cubeList.add(new NewModelBox(this.leg_right, -1.0F, 2.0F, -1.0F, 2, 4, 2, 0.0F, 16, 14));
      this.leg_left = new ModelRenderer(this);
      this.leg_left.setRotationPoint(1.0F, 4.0F, 0.0F);
      this.body.addChild(this.leg_left);
      this.leg_left.cubeList.add(new NewModelBox(this.leg_left, 0.0F, 2.0F, -1.0F, 2, 4, 2, 0.0F, 24, 14));
   }

   public void setRotationAngles(float limbSwing, float limbSwingAmount, float par3, float par4, float par5, float par6, Entity stoneling) {
      this.leg_right.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * limbSwingAmount;
      this.leg_left.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + 3.1415927F) * limbSwingAmount;
      ItemStack carry = ((EntityLiving)stoneling).getHeldItem();
      if (carry == null && stoneling.riddenByEntity == null) {
         this.arm_right.rotateAngleX = 0.0F;
         this.arm_left.rotateAngleX = 0.0F;
      } else {
         this.arm_right.rotateAngleX = 3.1416F;
         this.arm_left.rotateAngleX = 3.1416F;
      }

   }

   public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
      this.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, ageInTicks, scale, entityIn);
      this.body.render(scale);
   }

   public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
      modelRenderer.rotateAngleX = x;
      modelRenderer.rotateAngleY = y;
      modelRenderer.rotateAngleZ = z;
   }
}
