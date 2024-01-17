package net.tclproject.mysteriumlib.render.gecko.common.entities.client.renderer.model;

import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.tclproject.mysteriumlib.render.gecko.animation.model.AnimatedEntityModel;
import net.tclproject.mysteriumlib.render.gecko.animation.render.AnimatedModelRenderer;

public class PortalModel extends AnimatedEntityModel {
   private final AnimatedModelRenderer down;
   private final AnimatedModelRenderer left;
   private final AnimatedModelRenderer right;
   private final AnimatedModelRenderer up;
   private final AnimatedModelRenderer bb_main;

   public PortalModel() {
      this.textureWidth = 64;
      this.textureHeight = 64;
      this.down = new AnimatedModelRenderer(this);
      this.down.setRotationPoint(2.0F, 6.0F, 0.0F);
      this.down.cubeList.add(new AdvModelBox(this.down, 48, 13, -4.0F, 11.0F, 0.0F, 4, 3, 0, 0.0F, false));
      this.down.cubeList.add(new AdvModelBox(this.down, 48, 10, -7.0F, 8.0F, 0.0F, 4, 3, 0, 0.0F, false));
      this.down.cubeList.add(new AdvModelBox(this.down, 0, 39, -9.0F, -1.0F, 0.0F, 4, 9, 0, 0.0F, false));
      this.down.cubeList.add(new AdvModelBox(this.down, 48, 7, 0.0F, 11.0F, 0.0F, 4, 3, 0, 0.0F, false));
      this.down.cubeList.add(new AdvModelBox(this.down, 48, 4, 3.0F, 8.0F, 0.0F, 4, 3, 0, 0.0F, false));
      this.down.cubeList.add(new AdvModelBox(this.down, 22, 29, 4.0F, -3.0F, 0.0F, 4, 11, 0, 0.0F, false));
      this.down.cubeList.add(new AdvModelBox(this.down, 40, 1, -3.0F, 8.0F, 0.0F, 6, 3, 0, 0.0F, false));
      this.down.cubeList.add(new AdvModelBox(this.down, 12, 0, -5.0F, -1.0F, 0.0F, 9, 9, 0, 0.0F, false));
      this.left = new AnimatedModelRenderer(this);
      this.left.setRotationPoint(-9.0F, -8.0F, 0.0F);
      this.left.cubeList.add(new AdvModelBox(this.left, 36, 32, -1.0F, 4.0F, 0.0F, 4, 9, 0, 0.0F, false));
      this.left.cubeList.add(new AdvModelBox(this.left, 28, 40, -3.0F, 13.0F, 0.0F, 3, 9, 0, 0.0F, false));
      this.left.cubeList.add(new AdvModelBox(this.left, 46, 38, -5.0F, 13.0F, 0.0F, 2, 9, 0, 0.0F, false));
      this.left.cubeList.add(new AdvModelBox(this.left, 44, 29, -5.0F, 4.0F, 0.0F, 2, 9, 0, 0.0F, false));
      this.left.cubeList.add(new AdvModelBox(this.left, 36, 23, -7.0F, -5.0F, 0.0F, 4, 9, 0, 0.0F, false));
      this.left.cubeList.add(new AdvModelBox(this.left, 16, 19, 0.0F, -14.0F, 0.0F, 3, 18, 0, 0.0F, false));
      this.left.cubeList.add(new AdvModelBox(this.left, 22, 40, -3.0F, -5.0F, 0.0F, 3, 9, 0, 0.0F, false));
      this.left.cubeList.add(new AdvModelBox(this.left, 44, 20, -3.0F, 4.0F, 0.0F, 2, 9, 0, 0.0F, false));
      this.right = new AnimatedModelRenderer(this);
      this.right.setRotationPoint(8.0F, -9.0F, 0.0F);
      this.right.cubeList.add(new AdvModelBox(this.right, 8, 28, 0.0F, 1.0F, 0.0F, 4, 11, 0, 0.0F, false));
      this.right.cubeList.add(new AdvModelBox(this.right, 0, 28, 3.0F, -10.0F, 0.0F, 4, 11, 0, 0.0F, false));
      this.right.cubeList.add(new AdvModelBox(this.right, 42, 12, 4.0F, -18.0F, 0.0F, 3, 8, 0, 0.0F, false));
      this.right.cubeList.add(new AdvModelBox(this.right, 8, 39, 0.0F, -18.0F, 0.0F, 4, 8, 0, 0.0F, false));
      this.right.cubeList.add(new AdvModelBox(this.right, 42, 4, -3.0F, -11.0F, 0.0F, 3, 8, 0, 0.0F, false));
      this.right.cubeList.add(new AdvModelBox(this.right, 8, 47, -3.0F, -3.0F, 0.0F, 3, 6, 0, 0.0F, false));
      this.right.cubeList.add(new AdvModelBox(this.right, 30, 29, 0.0F, -10.0F, 0.0F, 3, 11, 0, 0.0F, false));
      this.up = new AnimatedModelRenderer(this);
      this.up.setRotationPoint(-7.0F, -22.0F, 0.0F);
      this.up.cubeList.add(new AdvModelBox(this.up, 34, 14, -8.0F, -9.0F, 0.0F, 4, 9, 0, 0.0F, false));
      this.up.cubeList.add(new AdvModelBox(this.up, 30, 9, -6.0F, -14.0F, 0.0F, 6, 5, 0, 0.0F, false));
      this.up.cubeList.add(new AdvModelBox(this.up, 46, 47, -1.0F, -18.0F, 0.0F, 4, 4, 0, 0.0F, false));
      this.up.cubeList.add(new AdvModelBox(this.up, 34, 41, 5.0F, -8.0F, 0.0F, 3, 8, 0, 0.0F, false));
      this.up.cubeList.add(new AdvModelBox(this.up, 16, 37, 3.0F, -18.0F, 0.0F, 3, 10, 0, 0.0F, false));
      this.up.cubeList.add(new AdvModelBox(this.up, 12, 9, -4.0F, -8.0F, 0.0F, 9, 8, 0, 0.0F, false));
      this.up.cubeList.add(new AdvModelBox(this.up, 40, 0, -4.0F, -9.0F, 0.0F, 7, 1, 0, 0.0F, false));
      this.up.cubeList.add(new AdvModelBox(this.up, 0, 48, 0.0F, -14.0F, 0.0F, 3, 5, 0, 0.0F, false));
      this.bb_main = new AnimatedModelRenderer(this);
      this.bb_main.setRotationPoint(0.0F, 24.0F, 0.0F);
      this.bb_main.cubeList.add(new AdvModelBox(this.bb_main, 30, 0, -14.0F, -46.0F, 0.0F, 5, 9, 0, 0.0F, false));
      this.bb_main.cubeList.add(new AdvModelBox(this.bb_main, 40, 41, 2.0F, -38.0F, 0.0F, 3, 8, 0, 0.0F, false));
      this.bb_main.cubeList.add(new AdvModelBox(this.bb_main, 18, 47, 0.0F, -38.0F, 0.0F, 2, 8, 0, 0.0F, false));
      this.bb_main.cubeList.add(new AdvModelBox(this.bb_main, 14, 47, 0.0F, -46.0F, 0.0F, 2, 8, 0, 0.0F, false));
      this.bb_main.cubeList.add(new AdvModelBox(this.bb_main, 0, 17, -6.0F, -21.0F, 0.0F, 12, 2, 0, 0.0F, false));
      this.bb_main.cubeList.add(new AdvModelBox(this.bb_main, 22, 19, -6.0F, -31.0F, 0.0F, 6, 10, 0, 0.0F, false));
      this.bb_main.cubeList.add(new AdvModelBox(this.bb_main, 0, 19, 0.0F, -30.0F, 0.0F, 8, 9, 0, 0.0F, false));
      this.bb_main.cubeList.add(new AdvModelBox(this.bb_main, 0, 0, -6.0F, -46.0F, 0.0F, 6, 15, 0, 0.0F, false));
      this.down.setModelRendererName("down");
      this.registerModelRenderer(this.down);
      this.left.setModelRendererName("left");
      this.registerModelRenderer(this.left);
      this.right.setModelRendererName("right");
      this.registerModelRenderer(this.right);
      this.up.setModelRendererName("up");
      this.registerModelRenderer(this.up);
      this.bb_main.setModelRendererName("bb_main");
      this.registerModelRenderer(this.bb_main);
      this.rootBones.add(this.bb_main);
   }

   public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
      this.down.render(f5);
      this.left.render(f5);
      this.right.render(f5);
      this.up.render(f5);
      this.bb_main.render(f5);
   }

   public void setRotationAngle(AnimatedModelRenderer AnimatedModelRenderer, float x, float y, float z) {
      AnimatedModelRenderer.rotateAngleX = x;
      AnimatedModelRenderer.rotateAngleY = y;
      AnimatedModelRenderer.rotateAngleZ = z;
   }

   public ResourceLocation getAnimationFileLocation() {
      return new ResourceLocation("arsmagica2:animations/portal.animations.json");
   }
}
