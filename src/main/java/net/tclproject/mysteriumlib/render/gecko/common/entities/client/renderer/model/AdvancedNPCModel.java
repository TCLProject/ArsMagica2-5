package net.tclproject.mysteriumlib.render.gecko.common.entities.client.renderer.model;

import net.ilexiconn.llibrary.client.model.ModelAnimator;
import net.ilexiconn.llibrary.client.model.tools.AdvancedModelBase;
import net.ilexiconn.llibrary.client.model.tools.AdvancedModelRenderer;
import net.ilexiconn.llibrary.server.animation.IAnimatedEntity;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.tclproject.mysteriumlib.render.gecko.common.entities.client.renderer.entity.AdvancedModelRendererItem;
import net.tclproject.mysteriumlib.render.gecko.common.entities.entity.AdvancedNPCEntity;

public class AdvancedNPCModel extends AdvancedModelBase {
   public final AdvancedModelRenderer model;
   public final AdvancedModelRenderer head;
   public final AdvancedModelRenderer headwear;
   public final AdvancedModelRenderer body;
   public final AdvancedModelRenderer jacket;
   public final AdvancedModelRenderer left_arm;
   public final AdvancedModelRenderer left_arm_2;
   public final AdvancedModelRenderer right_arm;
   public final AdvancedModelRenderer right_arm_2;
   public final AdvancedModelRenderer left_leg;
   public final AdvancedModelRenderer left_leg_2;
   public final AdvancedModelRenderer right_leg;
   public final AdvancedModelRenderer right_leg_2;
   public final AdvancedModelRenderer item;
   public AdvancedModelRenderer[] theDefaultPose;
   private ModelAnimator animator = ModelAnimator.create();

   public AdvancedNPCModel() {
      this.textureWidth = 64;
      this.textureHeight = 64;
      this.model = new AdvancedModelRenderer(this);
      this.model.setRotationPoint(0.0F, 12.0F, 0.0F);
      this.body = new AdvancedModelRenderer(this);
      this.body.setRotationPoint(0.0F, 1.0F, 2.0F);
      this.model.addChild(this.body);
      this.body.cubeList.add(new ModelBox(this.body, 16, 16, -4.0F, -13.0F, -4.0F, 8, 12, 4, 0.0F));
      this.jacket = new AdvancedModelRenderer(this);
      this.jacket.setRotationPoint(0.0F, -13.0F, -2.0F);
      this.body.addChild(this.jacket);
      this.jacket.cubeList.add(new ModelBox(this.jacket, 16, 32, -4.0F, 0.0F, -2.0F, 8, 12, 4, 0.25F));
      this.head = new AdvancedModelRenderer(this);
      this.head.setRotationPoint(0.0F, -13.0F, -2.0F);
      this.body.addChild(this.head);
      this.head.cubeList.add(new ModelBox(this.head, 0, 0, -4.0F, -8.0F, -4.0F, 8, 8, 8, 0.0F));
      this.headwear = new AdvancedModelRenderer(this);
      this.headwear.setRotationPoint(0.0F, 0.0F, 0.0F);
      this.head.addChild(this.headwear);
      this.headwear.cubeList.add(new ModelBox(this.headwear, 32, 0, -4.0F, -8.0F, -4.0F, 8, 8, 8, 0.5F));
      this.right_arm = new AdvancedModelRenderer(this);
      this.right_arm.setRotationPoint(-6.0F, -11.0F, -2.0F);
      this.body.addChild(this.right_arm);
      this.right_arm.cubeList.add(new ModelBox(this.right_arm, 40, 16, -2.0F, -2.0F, -2.0F, 4, 7, 4, 0.0F));
      this.right_arm.cubeList.add(new ModelBox(this.right_arm, 40, 32, -2.0F, -2.0F, -2.0F, 4, 7, 4, 0.25F));
      this.right_arm_2 = new AdvancedModelRenderer(this);
      this.right_arm_2.setRotationPoint(0.0F, 5.0F, 2.0F);
      this.right_arm.addChild(this.right_arm_2);
      this.right_arm_2.cubeList.add(new ModelBox(this.right_arm_2, 40, 23, -2.0F, 0.0F, -4.0F, 4, 5, 4, 0.0F));
      this.right_arm_2.cubeList.add(new ModelBox(this.right_arm_2, 40, 38, -2.0F, 0.0F, -4.0F, 4, 5, 4, 0.25F));
      this.left_arm = new AdvancedModelRenderer(this);
      this.left_arm.setRotationPoint(6.0F, -11.0F, -2.0F);
      this.body.addChild(this.left_arm);
      this.left_arm.cubeList.add(new ModelBox(this.left_arm, 32, 48, -2.0F, -2.0F, -2.0F, 4, 7, 4, 0.0F));
      this.left_arm.cubeList.add(new ModelBox(this.left_arm, 48, 48, -2.0F, -2.0F, -2.0F, 4, 7, 4, 0.25F));
      this.left_arm_2 = new AdvancedModelRenderer(this);
      this.left_arm_2.setRotationPoint(0.0F, 5.0F, 2.0F);
      this.left_arm.addChild(this.left_arm_2);
      this.left_arm_2.cubeList.add(new ModelBox(this.left_arm_2, 32, 55, -2.0F, 0.0F, -4.0F, 4, 5, 4, 0.0F));
      this.left_arm_2.cubeList.add(new ModelBox(this.left_arm_2, 48, 55, -2.0F, 0.0F, -4.0F, 4, 5, 4, 0.25F));
      this.left_leg = new AdvancedModelRenderer(this);
      this.left_leg.setRotationPoint(2.0F, 0.0F, 0.0F);
      this.model.addChild(this.left_leg);
      this.left_leg.cubeList.add(new ModelBox(this.left_leg, 16, 48, -2.0F, 0.0F, -2.0F, 4, 6, 4, 0.0F));
      this.left_leg.cubeList.add(new ModelBox(this.left_leg, 0, 48, -2.0F, 0.0F, -2.0F, 4, 6, 4, 0.25F));
      this.left_leg_2 = new AdvancedModelRenderer(this);
      this.left_leg_2.setRotationPoint(0.0F, 6.0F, -2.0F);
      this.left_leg.addChild(this.left_leg_2);
      this.left_leg_2.cubeList.add(new ModelBox(this.left_leg_2, 16, 54, -2.0F, 0.0F, 0.0F, 4, 6, 4, 0.0F));
      this.left_leg_2.cubeList.add(new ModelBox(this.left_leg_2, 0, 54, -2.0F, 0.0F, 0.0F, 4, 6, 4, 0.25F));
      this.right_leg = new AdvancedModelRenderer(this);
      this.right_leg.setRotationPoint(-2.0F, 0.0F, 0.0F);
      this.model.addChild(this.right_leg);
      this.right_leg.cubeList.add(new ModelBox(this.right_leg, 0, 16, -2.0F, 0.0F, -2.0F, 4, 6, 4, 0.0F));
      this.right_leg.cubeList.add(new ModelBox(this.right_leg, 0, 32, -2.0F, 0.0F, -2.0F, 4, 6, 4, 0.25F));
      this.right_leg_2 = new AdvancedModelRenderer(this);
      this.right_leg_2.setRotationPoint(0.0F, 6.0F, -2.0F);
      this.right_leg.addChild(this.right_leg_2);
      this.right_leg_2.cubeList.add(new ModelBox(this.right_leg_2, 0, 22, -2.0F, 0.0F, 0.0F, 4, 6, 4, 0.0F));
      this.right_leg_2.cubeList.add(new ModelBox(this.right_leg_2, 0, 37, -2.0F, 0.0F, 0.0F, 4, 6, 4, 0.25F));
      this.item = new AdvancedModelRendererItem(this);
      this.right_arm_2.addChild(this.item);
      this.setDefaultRotation(this.model);
      this.setDefaultRotation(this.head);
      this.setDefaultRotation(this.headwear);
      this.setDefaultRotation(this.body);
      this.setDefaultRotation(this.jacket);
      this.setDefaultRotation(this.right_arm);
      this.setDefaultRotation(this.right_arm_2);
      this.setDefaultRotation(this.left_arm);
      this.setDefaultRotation(this.left_arm_2);
      this.setDefaultRotation(this.right_leg);
      this.setDefaultRotation(this.right_leg_2);
      this.setDefaultRotation(this.left_leg);
      this.setDefaultRotation(this.left_leg_2);
      this.setDefaultRotation(this.item);
      Object[] objs = this.boxList.stream().filter((modelRenderer) -> {
         return modelRenderer instanceof AdvancedModelRenderer;
      }).toArray();
      this.theDefaultPose = new AdvancedModelRenderer[objs.length];

      for(int i = 0; i < objs.length; ++i) {
         this.theDefaultPose[i] = (AdvancedModelRenderer)((AdvancedModelRenderer)objs[i]);
      }

      this.updateDefaultPose();
   }

   public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
      this.animate((IAnimatedEntity)entity, f, f1, f2, f3, f4, f5);
      ((AdvancedModelRendererItem)this.item).ent = (EntityLivingBase)entity;
      this.model.render(f5);
   }

   public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
      modelRenderer.rotateAngleX = x;
      modelRenderer.rotateAngleY = y;
      modelRenderer.rotateAngleZ = z;
   }

   public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5, Entity entity) {
      AdvancedNPCEntity npcEntity = (AdvancedNPCEntity)entity;
      this.resetToDefaultPose();
      float globalSpeed = 0.25F;
      float globalHeight = 1.0F;
      float globalDegree = 1.0F;
      this.bob(this.model, 1.0F * globalSpeed, 1.0F * globalHeight, false, f, f1);
      this.walk(this.left_leg, 0.5F * globalSpeed, globalDegree, false, 0.0F, 0.0F, f, f1);
      this.walk(this.right_leg, 0.5F * globalSpeed, globalDegree, true, 0.0F, 0.0F, f, f1);
      this.walk(this.left_leg_2, 0.5F * globalSpeed, globalDegree, false, 0.0F, 1.0F, f, f1);
      this.walk(this.right_leg_2, 0.5F * globalSpeed, globalDegree, true, 0.0F, -1.0F, f, f1);
      this.walk(this.left_arm, 0.5F * globalSpeed, globalDegree, true, 0.0F, 0.0F, f, f1);
      this.walk(this.right_arm, 0.5F * globalSpeed, globalDegree, false, 0.0F, 0.0F, f, f1);
      this.walk(this.left_arm_2, 0.5F * globalSpeed, 0.5F * globalDegree, false, 0.0F, -0.5F, f, f1);
      this.walk(this.right_arm_2, 0.5F * globalSpeed, 0.5F * globalDegree, true, 0.0F, 0.5F, f, f1);
   }

   public void animate(IAnimatedEntity entity, float f, float f1, float f2, float f3, float f4, float f5) {
      AdvancedNPCEntity npc = (AdvancedNPCEntity)entity;
      this.animator.update(npc);
      this.setRotationAngles(f, f1, f2, f3, f4, f5, (Entity)entity);
      if (entity.getAnimation() == AdvancedNPCEntity.HAMMER_SWING_ANIMATION) {
         if (npc.getAnimState() != 1) {
            this.right_arm.rotateAngleX = -0.4F;
            this.right_arm_2.rotateAngleX = -0.2F;
            this.left_arm.rotateAngleX = -0.4F;
            this.left_leg.rotateAngleZ = -0.2F;
            this.right_leg.rotateAngleZ = 0.2F;
            this.updateDefaultPose();
            npc.setAnimState(1);
         }

         this.animator.setAnimation(AdvancedNPCEntity.HAMMER_SWING_ANIMATION);
         this.animator.startKeyframe(30);
         this.animator.rotate(this.right_arm, -2.0F, 0.0F, 0.0F);
         this.animator.rotate(this.body, -0.1F, 0.0F, 0.0F);
         this.animator.rotate(this.right_arm_2, -0.5F, 0.0F, 0.0F);
         this.animator.endKeyframe();
         this.animator.setStaticKeyframe(3);
         this.animator.resetKeyframe(7);
      }

      if (entity.getAnimation() == AdvancedNPCEntity.RESET_TO_STANDING_ANIMATION) {
         if (npc.getAnimState() != 0) {
            this.updateToTheDefaultPose();
            npc.setAnimState(0);
         }

         this.animator.setAnimation(AdvancedNPCEntity.RESET_TO_STANDING_ANIMATION);
         this.animator.resetKeyframe(7);
      }

      if (entity.getAnimation() == AdvancedNPCEntity.KNOCK_ANIMATION) {
         this.animator.setAnimation(AdvancedNPCEntity.KNOCK_ANIMATION);
         this.animator.startKeyframe(10);
         this.animator.rotate(this.right_arm, -1.6F, 0.0F, 0.0F);
         this.animator.rotate(this.right_arm_2, -0.5F, 0.0F, 0.0F);
         this.animator.endKeyframe();
         this.animator.setStaticKeyframe(10);
         this.animator.startKeyframe(10);
         this.animator.endKeyframe();
         this.animator.setStaticKeyframe(30);
         this.animator.resetKeyframe(10);
      }

   }

   public void updateDefaultPose(AdvancedModelRenderer renderer, AdvancedModelRenderer def) {
      renderer.defaultRotationX = def.rotateAngleX;
      renderer.defaultRotationY = def.rotateAngleY;
      renderer.defaultRotationZ = def.rotateAngleZ;
      renderer.defaultOffsetX = def.offsetX;
      renderer.defaultOffsetY = def.offsetY;
      renderer.defaultOffsetZ = def.offsetZ;
      renderer.defaultPositionX = def.rotationPointX;
      renderer.defaultPositionY = def.rotationPointY;
      renderer.defaultPositionZ = def.rotationPointZ;
   }

   public void updateToTheDefaultPose() {
      Object[] objs = this.boxList.stream().filter((modelRenderer) -> {
         return modelRenderer instanceof AdvancedModelRenderer;
      }).toArray();

      for(int i = 0; i < objs.length; ++i) {
         AdvancedModelRenderer advancedModelRenderer = (AdvancedModelRenderer)objs[i];
         this.updateDefaultPose(advancedModelRenderer, this.theDefaultPose[i]);
      }

   }

   public void setDefaultRotation(AdvancedModelRenderer renderer) {
      renderer.rotateAngleX = 0.0F;
      renderer.rotateAngleY = 0.0F;
      renderer.rotateAngleZ = 0.0F;
   }
}
