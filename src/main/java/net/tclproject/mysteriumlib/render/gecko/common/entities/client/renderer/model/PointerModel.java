package net.tclproject.mysteriumlib.render.gecko.common.entities.client.renderer.model;

import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.tclproject.mysteriumlib.render.gecko.animation.model.AnimatedEntityModel;
import net.tclproject.mysteriumlib.render.gecko.animation.render.AnimatedModelRenderer;

public class PointerModel extends AnimatedEntityModel {
   private final AnimatedModelRenderer bone;
   private final AnimatedModelRenderer cube_r1;
   private final AnimatedModelRenderer cube_r2;
   private final AnimatedModelRenderer cube_r3;
   private final AnimatedModelRenderer cube_r4;

   public PointerModel() {
      this.textureWidth = 64;
      this.textureHeight = 64;
      this.bone = new AnimatedModelRenderer(this);
      this.bone.setRotationPoint(0.0F, 24.0F, 0.0F);
      this.bone.cubeList.add(new AdvModelBox(this.bone, 0, 0, -1.0F, -38.0F, -1.0F, 2, 38, 2, 0.0F, false));
      this.bone.cubeList.add(new AdvModelBox(this.bone, 16, 12, -0.5F, -39.0F, -0.5F, 1, 3, 1, 0.0F, false));
      this.cube_r1 = new AnimatedModelRenderer(this);
      this.cube_r1.setRotationPoint(-3.5F, -31.5F, -1.5F);
      this.bone.addChild(this.cube_r1);
      this.setRotationAngle(this.cube_r1, 0.0F, 0.1309F, 0.0F);
      this.cube_r1.cubeList.add(new AdvModelBox(this.cube_r1, 8, 1, -0.5F, -2.0F, 0.0F, 1, 4, 1, 0.0F, false));
      this.cube_r2 = new AnimatedModelRenderer(this);
      this.cube_r2.setRotationPoint(2.5F, -23.0F, -1.5F);
      this.bone.addChild(this.cube_r2);
      this.setRotationAngle(this.cube_r2, 0.0F, 2.9758F, 0.0F);
      this.cube_r2.cubeList.add(new AdvModelBox(this.cube_r2, 8, 0, -1.0F, -2.0F, -0.75F, 7, 5, 1, 0.0F, false));
      this.cube_r2.cubeList.add(new AdvModelBox(this.cube_r2, 8, 12, 6.0F, -1.5F, -0.75F, 1, 4, 1, 0.0F, false));
      this.cube_r2.cubeList.add(new AdvModelBox(this.cube_r2, 15, 16, 7.0F, -0.5F, -0.75F, 1, 2, 1, 0.0F, false));
      this.cube_r2.cubeList.add(new AdvModelBox(this.cube_r2, 12, 18, 8.0F, 0.0F, -0.75F, 1, 1, 1, 0.0F, false));
      this.cube_r3 = new AnimatedModelRenderer(this);
      this.cube_r3.setRotationPoint(3.25F, -31.5F, -1.5F);
      this.bone.addChild(this.cube_r3);
      this.setRotationAngle(this.cube_r3, 0.0F, 0.096F, 0.0F);
      this.cube_r3.cubeList.add(new AdvModelBox(this.cube_r3, 18, 18, 2.75F, -0.5F, -0.7F, 1, 1, 1, 0.0F, false));
      this.cube_r3.cubeList.add(new AdvModelBox(this.cube_r3, 8, 17, 1.75F, -1.0F, -0.7F, 1, 2, 1, 0.0F, false));
      this.cube_r3.cubeList.add(new AdvModelBox(this.cube_r3, 12, 12, 0.75F, -2.0F, -0.7F, 1, 4, 1, 0.0F, false));
      this.cube_r3.cubeList.add(new AdvModelBox(this.cube_r3, 9, 6, -6.25F, -2.5F, -0.7F, 7, 5, 1, 0.0F, false));
      this.cube_r4 = new AnimatedModelRenderer(this);
      this.cube_r4.setRotationPoint(4.4968F, -20.5F, -2.0485F);
      this.bone.addChild(this.cube_r4);
      this.setRotationAngle(this.cube_r4, 0.0F, -0.2182F, 0.0F);
      this.cube_r4.cubeList.add(new AdvModelBox(this.cube_r4, 22, 7, -1.0F, -4.0F, 0.75F, 1, 4, 1, 0.0F, false));
      this.bone.setModelRendererName("bone");
      this.registerModelRenderer(this.bone);
      this.cube_r1.setModelRendererName("cube_r1");
      this.registerModelRenderer(this.cube_r1);
      this.cube_r2.setModelRendererName("cube_r2");
      this.registerModelRenderer(this.cube_r2);
      this.cube_r3.setModelRendererName("cube_r3");
      this.registerModelRenderer(this.cube_r3);
      this.cube_r4.setModelRendererName("cube_r4");
      this.registerModelRenderer(this.cube_r4);
      this.rootBones.add(this.bone);
   }

   public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
      this.bone.render(f5);
   }

   public void setRotationAngle(AnimatedModelRenderer AnimatedModelRenderer, float x, float y, float z) {
      AnimatedModelRenderer.rotateAngleX = x;
      AnimatedModelRenderer.rotateAngleY = y;
      AnimatedModelRenderer.rotateAngleZ = z;
   }

   public ResourceLocation getAnimationFileLocation() {
      return new ResourceLocation("arsmagica2:animations/empty.animations.json");
   }
}
