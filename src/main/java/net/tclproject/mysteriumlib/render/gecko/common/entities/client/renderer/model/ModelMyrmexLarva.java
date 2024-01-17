package net.tclproject.mysteriumlib.render.gecko.common.entities.client.renderer.model;

import net.ilexiconn.llibrary.client.model.ModelAnimator;
import net.ilexiconn.llibrary.client.model.tools.AdvancedModelRenderer;
import net.ilexiconn.llibrary.server.animation.IAnimatedEntity;
import net.minecraft.entity.Entity;
import net.tclproject.mysteriumlib.render.gecko.common.entities.entity.specific.iceandfire.EntityMyrmexBase;
import net.tclproject.mysteriumlib.render.gecko.iceandfire.ModelUtils;

public class ModelMyrmexLarva extends ModelDragonBase {
   public AdvancedModelRenderer Body2;
   public AdvancedModelRenderer Body3;
   public AdvancedModelRenderer Body1;
   public AdvancedModelRenderer Body4;
   public AdvancedModelRenderer Body5;
   public AdvancedModelRenderer Body4_1;
   public AdvancedModelRenderer Body5_1;
   public AdvancedModelRenderer Body4_2;
   public AdvancedModelRenderer Neck1;
   public AdvancedModelRenderer HeadBase;
   private ModelAnimator animator;

   public ModelMyrmexLarva() {
      this.textureWidth = 128;
      this.textureHeight = 128;
      this.Body5 = new AdvancedModelRenderer(this, 82, 35);
      this.Body5.setRotationPoint(0.0F, -0.4F, 4.2F);
      this.Body5.addBox(-3.5F, -2.5F, -2.1F, 7, 8, 6, 0.0F);
      this.setRotateAngle(this.Body5, -0.045553092F, 0.0F, 0.0F);
      this.Body3 = new AdvancedModelRenderer(this, 36, 73);
      this.Body3.setRotationPoint(0.0F, 0.2F, 4.1F);
      this.Body3.addBox(-4.5F, -3.4F, -1.4F, 9, 9, 9, 0.0F);
      this.Body4_1 = new AdvancedModelRenderer(this, 58, 35);
      this.Body4_1.setRotationPoint(0.0F, 0.6F, 3.3F);
      this.Body4_1.addBox(-3.0F, -2.7F, -1.5F, 6, 7, 4, 0.0F);
      this.setRotateAngle(this.Body4_1, 0.045553092F, 0.0F, 0.0F);
      this.Neck1 = new AdvancedModelRenderer(this, 32, 22);
      this.Neck1.setRotationPoint(0.0F, 2.0F, -5.0F);
      this.Neck1.addBox(-2.5F, -2.0F, -3.5F, 5, 5, 4, 0.0F);
      this.setRotateAngle(this.Neck1, -1.6845918F, 0.0F, 0.0F);
      this.HeadBase = new AdvancedModelRenderer(this, 2, 2);
      this.HeadBase.setRotationPoint(0.0F, 1.2F, -3.3F);
      this.HeadBase.addBox(-4.0F, -2.51F, -8.1F, 8, 6, 8, 0.0F);
      this.setRotateAngle(this.HeadBase, 2.5953045F, 0.0F, 0.0F);
      this.Body4_2 = new AdvancedModelRenderer(this, 58, 35);
      this.Body4_2.setRotationPoint(0.0F, 0.8F, 4.3F);
      this.Body4_2.addBox(-3.0F, -2.7F, -1.5F, 6, 7, 4, 0.0F);
      this.setRotateAngle(this.Body4_2, -0.4553564F, 0.0F, 0.0F);
      this.Body1 = new AdvancedModelRenderer(this, 34, 47);
      this.Body1.setRotationPoint(0.0F, -0.7F, -1.0F);
      this.Body1.addBox(-3.5F, -2.1F, -6.3F, 7, 8, 9, 0.0F);
      this.setRotateAngle(this.Body1, 0.045553092F, 0.0F, 0.0F);
      this.Body4 = new AdvancedModelRenderer(this, 58, 35);
      this.Body4.setRotationPoint(0.0F, -0.4F, 7.3F);
      this.Body4.addBox(-3.0F, -2.7F, -1.5F, 6, 7, 4, 0.0F);
      this.setRotateAngle(this.Body4, 0.045553092F, 0.0F, 0.0F);
      this.Body5_1 = new AdvancedModelRenderer(this, 82, 35);
      this.Body5_1.setRotationPoint(0.0F, -0.4F, 4.2F);
      this.Body5_1.addBox(-3.5F, -2.5F, -2.1F, 7, 8, 6, 0.0F);
      this.setRotateAngle(this.Body5_1, -0.045553092F, 0.0F, 0.0F);
      this.Body2 = new AdvancedModelRenderer(this, 70, 53);
      this.Body2.setRotationPoint(0.0F, 19.0F, -6.0F);
      this.Body2.addBox(-3.0F, -2.7F, -0.1F, 6, 7, 4, 0.0F);
      this.setRotateAngle(this.Body2, -0.045553092F, 0.0F, 0.0F);
      this.Body4.addChild(this.Body5);
      this.Body2.addChild(this.Body3);
      this.Body5.addChild(this.Body4_1);
      this.Body1.addChild(this.Neck1);
      this.Neck1.addChild(this.HeadBase);
      this.Body5_1.addChild(this.Body4_2);
      this.Body2.addChild(this.Body1);
      this.Body3.addChild(this.Body4);
      this.Body4_1.addChild(this.Body5_1);
      this.animator = ModelAnimator.create();
      this.updateDefaultPose();
   }

   public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
      this.Body2.render(f5);
      this.animate((IAnimatedEntity)entity, f, f1, f2, f3, f4, f5);
   }

   public void animate(IAnimatedEntity entity, float f, float f1, float f2, float f3, float f4, float f5) {
      this.resetToDefaultPose();
      this.setRotationAngles(f, f1, f2, f3, f4, f5, (Entity)entity);
      this.animator.update(entity);
      this.animator.setAnimation(EntityMyrmexBase.ANIMATION_PUPA_WIGGLE);
      this.animator.startKeyframe(5);
      ModelUtils.rotate(this.animator, this.Body1, 0.0F, -15.0F, 0.0F);
      ModelUtils.rotate(this.animator, this.Body2, 0.0F, -15.0F, 0.0F);
      ModelUtils.rotate(this.animator, this.Body3, 0.0F, -15.0F, 0.0F);
      ModelUtils.rotate(this.animator, this.Body4, 0.0F, 15.0F, 0.0F);
      ModelUtils.rotate(this.animator, this.Body5, 0.0F, 15.0F, 0.0F);
      ModelUtils.rotate(this.animator, this.Body5_1, 0.0F, 15.0F, 0.0F);
      this.animator.endKeyframe();
      this.animator.startKeyframe(5);
      ModelUtils.rotate(this.animator, this.Body1, 0.0F, 15.0F, 0.0F);
      ModelUtils.rotate(this.animator, this.Body2, 0.0F, 15.0F, 0.0F);
      ModelUtils.rotate(this.animator, this.Body3, 0.0F, 15.0F, 0.0F);
      ModelUtils.rotate(this.animator, this.Body4, 0.0F, -15.0F, 0.0F);
      ModelUtils.rotate(this.animator, this.Body5, 0.0F, -15.0F, 0.0F);
      ModelUtils.rotate(this.animator, this.Body5_1, 0.0F, -15.0F, 0.0F);
      this.animator.endKeyframe();
      this.animator.startKeyframe(5);
      ModelUtils.rotate(this.animator, this.Body1, 0.0F, -15.0F, 0.0F);
      ModelUtils.rotate(this.animator, this.Body2, 0.0F, -15.0F, 0.0F);
      ModelUtils.rotate(this.animator, this.Body3, 0.0F, -15.0F, 0.0F);
      ModelUtils.rotate(this.animator, this.Body4, 0.0F, 15.0F, 0.0F);
      ModelUtils.rotate(this.animator, this.Body5, 0.0F, 15.0F, 0.0F);
      ModelUtils.rotate(this.animator, this.Body5_1, 0.0F, 15.0F, 0.0F);
      this.animator.endKeyframe();
      this.animator.resetKeyframe(5);
   }

   public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5, Entity entity) {
      this.resetToDefaultPose();
      float speed_idle = 0.025F;
      float degree_idle = 0.25F;
      AdvancedModelRenderer[] PARTS = new AdvancedModelRenderer[]{this.Body1, this.Body2, this.Body3, this.Body4, this.Body4_1, this.Body4_2, this.Body5, this.Body5_1};
      this.bob(this.Body2, speed_idle, degree_idle * 2.5F, true, f2, 1.0F);
      this.chainSwing(PARTS, speed_idle, degree_idle * 0.15F, 1.0D, f2, 1.0F);
      this.chainFlap(PARTS, speed_idle, degree_idle * 0.15F, 1.0D, f2, 1.0F);
   }
}
