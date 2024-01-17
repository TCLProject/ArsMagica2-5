package net.tclproject.mysteriumlib.render.gecko.common.entities.client.renderer.model;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.tclproject.mysteriumlib.render.gecko.animation.model.AnimatedEntityModel;
import net.tclproject.mysteriumlib.render.gecko.animation.render.AnimatedModelRenderer;

public class DuckModel extends AnimatedEntityModel {
   private final AnimatedModelRenderer root;
   private final AnimatedModelRenderer main;
   private final AnimatedModelRenderer tail;
   private final AnimatedModelRenderer tailfeathers;
   private final AnimatedModelRenderer leg;
   private final AnimatedModelRenderer foot;
   private final AnimatedModelRenderer leg2;
   private final AnimatedModelRenderer foot2;
   private final AnimatedModelRenderer head;
   private final AnimatedModelRenderer beak;
   private final AnimatedModelRenderer wing;
   private final AnimatedModelRenderer wing2;

   public DuckModel() {
      this.textureWidth = 64;
      this.textureHeight = 64;
      this.root = new AnimatedModelRenderer(this);
      this.root.setRotationPoint(0.0F, 24.0F, 0.0F);
      this.main = new AnimatedModelRenderer(this);
      this.main.setRotationPoint(0.0F, -3.0F, 1.0F);
      this.root.addChild(this.main);
      this.main.cubeList.add(new AdvModelBox(this.main, 0, 0, -3.0F, -4.0F, -6.0F, 6, 5, 8, 0.0F, false));
      this.tail = new AnimatedModelRenderer(this);
      this.tail.setRotationPoint(0.0F, 0.25F, 0.0F);
      this.main.addChild(this.tail);
      this.setRotationAngle(this.tail, 0.829F, 0.0F, 0.0F);
      this.tail.cubeList.add(new AdvModelBox(this.tail, 22, 13, -2.0F, -2.2171F, 0.769F, 4, 4, 3, 0.0F, false));
      this.tailfeathers = new AnimatedModelRenderer(this);
      this.tailfeathers.setRotationPoint(0.5F, -2.75F, 0.5F);
      this.main.addChild(this.tailfeathers);
      this.setRotationAngle(this.tailfeathers, -0.3927F, 0.0F, 0.0F);
      this.tailfeathers.cubeList.add(new AdvModelBox(this.tailfeathers, 0, 13, -3.0F, -1.0F, 0.0F, 5, 1, 5, 0.0F, false));
      this.leg = new AnimatedModelRenderer(this);
      this.leg.setRotationPoint(-1.75F, 0.0F, 0.5F);
      this.main.addChild(this.leg);
      this.setRotationAngle(this.leg, -0.3054F, 0.2618F, 0.0F);
      this.leg.cubeList.add(new AdvModelBox(this.leg, 4, 4, -0.5F, 0.0F, -0.5F, 1, 3, 1, 0.0F, false));
      this.foot = new AnimatedModelRenderer(this);
      this.foot.setRotationPoint(0.0F, 2.8483F, 0.1117F);
      this.leg.addChild(this.foot);
      this.setRotationAngle(this.foot, 0.4363F, 0.0F, 0.0F);
      this.foot.cubeList.add(new AdvModelBox(this.foot, 0, 2, -1.5F, 0.0F, -2.0F, 3, 0, 2, 0.0F, false));
      this.leg2 = new AnimatedModelRenderer(this);
      this.leg2.setRotationPoint(1.75F, 0.0F, 0.5F);
      this.main.addChild(this.leg2);
      this.setRotationAngle(this.leg2, -0.3054F, -0.2618F, 0.0F);
      this.leg2.cubeList.add(new AdvModelBox(this.leg2, 0, 4, -0.5F, 0.0F, -0.5F, 1, 3, 1, 0.0F, false));
      this.foot2 = new AnimatedModelRenderer(this);
      this.foot2.setRotationPoint(0.0F, 2.8483F, 0.1117F);
      this.leg2.addChild(this.foot2);
      this.setRotationAngle(this.foot2, 0.4363F, 0.0F, 0.0F);
      this.foot2.cubeList.add(new AdvModelBox(this.foot2, 0, 0, -1.5F, 0.0F, -2.0F, 3, 0, 2, 0.0F, false));
      this.head = new AnimatedModelRenderer(this);
      this.head.setRotationPoint(0.0F, -4.2168F, -2.5699F);
      this.main.addChild(this.head);
      this.head.cubeList.add(new AdvModelBox(this.head, 20, 0, -2.0F, -3.6786F, -2.617F, 4, 4, 4, 0.0F, false));
      this.beak = new AnimatedModelRenderer(this);
      this.beak.setRotationPoint(0.0F, -0.7821F, -3.4644F);
      this.head.addChild(this.beak);
      this.beak.cubeList.add(new AdvModelBox(this.beak, 14, 24, -2.0F, -1.0F, -2.0F, 4, 2, 3, -0.1F, false));
      this.wing = new AnimatedModelRenderer(this);
      this.wing.setRotationPoint(-2.0F, -4.25F, -2.0F);
      this.main.addChild(this.wing);
      this.setRotationAngle(this.wing, 0.0F, 0.0F, 0.3491F);
      this.wing.cubeList.add(new AdvModelBox(this.wing, 0, 19, -1.0F, 0.0F, -1.5F, 1, 4, 6, 0.0F, false));
      this.wing2 = new AnimatedModelRenderer(this);
      this.wing2.setRotationPoint(2.25F, -4.25F, -2.0F);
      this.main.addChild(this.wing2);
      this.setRotationAngle(this.wing2, 0.0F, 0.0F, -0.3491F);
      this.wing2.cubeList.add(new AdvModelBox(this.wing2, 14, 14, -0.1611F, -0.0864F, -1.5F, 1, 4, 6, 0.0F, false));
      this.root.setModelRendererName("root");
      this.registerModelRenderer(this.root);
      this.main.setModelRendererName("main");
      this.registerModelRenderer(this.main);
      this.tail.setModelRendererName("tail");
      this.registerModelRenderer(this.tail);
      this.tailfeathers.setModelRendererName("tailfeathers");
      this.registerModelRenderer(this.tailfeathers);
      this.leg.setModelRendererName("leg");
      this.registerModelRenderer(this.leg);
      this.foot.setModelRendererName("foot");
      this.registerModelRenderer(this.foot);
      this.leg2.setModelRendererName("leg2");
      this.registerModelRenderer(this.leg2);
      this.foot2.setModelRendererName("foot2");
      this.registerModelRenderer(this.foot2);
      this.head.setModelRendererName("head");
      this.registerModelRenderer(this.head);
      this.beak.setModelRendererName("beak");
      this.registerModelRenderer(this.beak);
      this.wing.setModelRendererName("wing");
      this.registerModelRenderer(this.wing);
      this.wing2.setModelRendererName("wing2");
      this.registerModelRenderer(this.wing2);
      this.rootBones.add(this.root);
   }

   public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
      this.root.render(f5);
   }

   public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
      modelRenderer.rotateAngleX = x;
      modelRenderer.rotateAngleY = y;
      modelRenderer.rotateAngleZ = z;
   }

   public ResourceLocation getAnimationFileLocation() {
      return new ResourceLocation("arsmagica2:animations/duck.animations.json");
   }
}
