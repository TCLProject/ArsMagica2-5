package net.tclproject.mysteriumlib.render.gecko.iceandfire.animator;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.Iterator;
import net.ilexiconn.llibrary.LLibrary;
import net.ilexiconn.llibrary.client.model.tools.AdvancedModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import net.tclproject.mysteriumlib.render.gecko.common.entities.entity.specific.iceandfire.EntityDragonBase;
import net.tclproject.mysteriumlib.render.gecko.common.entities.entity.specific.iceandfire.EntityIceDragon;
import net.tclproject.mysteriumlib.render.gecko.iceandfire.util.EnumDragonAnimations;
import net.tclproject.mysteriumlib.render.gecko.iceandfire.util.IIceAndFireTabulaModelAnimator;
import net.tclproject.mysteriumlib.render.gecko.iceandfire.util.IceAndFireTabulaModel;
import net.tclproject.mysteriumlib.render.gecko.iceandfire.util.LegArticulator;

@SideOnly(Side.CLIENT)
public class IceDragonTabulaModelAnimator extends IceAndFireTabulaModelAnimator implements IIceAndFireTabulaModelAnimator {
   private IceAndFireTabulaModel[] walkPoses;
   private IceAndFireTabulaModel[] flyPoses;
   private IceAndFireTabulaModel[] swimPoses;
   private AdvancedModelRenderer[] neckParts;
   private AdvancedModelRenderer[] tailParts;
   private AdvancedModelRenderer[] tailPartsWBody;
   private AdvancedModelRenderer[] toesPartsL;
   private AdvancedModelRenderer[] toesPartsR;
   private AdvancedModelRenderer[] clawL;
   private AdvancedModelRenderer[] clawR;

   public IceDragonTabulaModelAnimator() {
      super(EnumDragonAnimations.GROUND_POSE.icedragon_model);
      this.walkPoses = new IceAndFireTabulaModel[]{EnumDragonAnimations.WALK1.icedragon_model, EnumDragonAnimations.WALK2.icedragon_model, EnumDragonAnimations.WALK3.icedragon_model, EnumDragonAnimations.WALK4.icedragon_model};
      this.flyPoses = new IceAndFireTabulaModel[]{EnumDragonAnimations.FLIGHT1.icedragon_model, EnumDragonAnimations.FLIGHT2.icedragon_model, EnumDragonAnimations.FLIGHT3.icedragon_model, EnumDragonAnimations.FLIGHT4.icedragon_model, EnumDragonAnimations.FLIGHT5.icedragon_model, EnumDragonAnimations.FLIGHT6.icedragon_model};
      this.swimPoses = new IceAndFireTabulaModel[]{EnumDragonAnimations.SWIM1.icedragon_model, EnumDragonAnimations.SWIM2.icedragon_model, EnumDragonAnimations.SWIM3.icedragon_model, EnumDragonAnimations.SWIM4.icedragon_model, EnumDragonAnimations.SWIM5.icedragon_model};
   }

   public void init(IceAndFireTabulaModel model) {
      this.neckParts = new AdvancedModelRenderer[]{model.getCube("Neck1"), model.getCube("Neck2"), model.getCube("Neck3"), model.getCube("Neck3"), model.getCube("Head")};
      this.tailParts = new AdvancedModelRenderer[]{model.getCube("Tail1"), model.getCube("Tail2"), model.getCube("Tail3"), model.getCube("Tail4")};
      this.tailPartsWBody = new AdvancedModelRenderer[]{model.getCube("BodyLower"), model.getCube("Tail1"), model.getCube("Tail2"), model.getCube("Tail3"), model.getCube("Tail4")};
      this.toesPartsL = new AdvancedModelRenderer[]{model.getCube("ToeL1"), model.getCube("ToeL2"), model.getCube("ToeL3")};
      this.toesPartsR = new AdvancedModelRenderer[]{model.getCube("ToeR1"), model.getCube("ToeR2"), model.getCube("ToeR3")};
      this.clawL = new AdvancedModelRenderer[]{model.getCube("ClawL")};
      this.clawR = new AdvancedModelRenderer[]{model.getCube("ClawR")};
   }

   public void setRotationAngles(IceAndFireTabulaModel model, Entity entity1, float limbSwing, float limbSwingAmount, float ageInTicks, float rotationYaw, float rotationPitch, float scale) {
      EntityIceDragon entity = null;
      if (entity1 instanceof EntityIceDragon) {
         entity = (EntityIceDragon)entity1;
      } else return;
      model.resetToDefaultPose();
      this.animate(model, entity, limbSwing, limbSwingAmount, ageInTicks, rotationYaw, rotationPitch, scale);
      if (entity.isFlying()) {
         ++entity.flyProgress;
      } else {
         entity.flyProgress = 0.0F;
      }

      boolean walking = !entity.isHovering() && !entity.isFlying() && entity.hoverProgress <= 0.0F && entity.flyProgress <= 0.0F;
      boolean swimming = entity.isInWater() && entity.swimProgress > 0;
      int currentIndex = walking ? entity.walkCycle / 10 : entity.flightCycle / 10;
      if (swimming) {
         currentIndex = entity.swimCycle / 10;
      }

      int prevIndex = currentIndex - 1;
      if (prevIndex < 0) {
         prevIndex = swimming ? 4 : (walking ? 3 : 5);
      }

      IceAndFireTabulaModel currentPosition = swimming ? this.swimPoses[currentIndex] : (walking ? this.walkPoses[currentIndex] : this.flyPoses[currentIndex]);
      IceAndFireTabulaModel prevPosition = swimming ? this.swimPoses[prevIndex] : (walking ? this.walkPoses[prevIndex] : this.flyPoses[prevIndex]);
      float delta = (float)(walking ? entity.walkCycle : entity.flightCycle) / 10.0F % 1.0F;
      if (swimming) {
         delta = (float)entity.swimCycle / 10.0F % 1.0F;
      }

      float deltaTicks = delta + LLibrary.PROXY.getPartialTicks() / 10.0F;
      if (delta == 0.0F) {
         deltaTicks = 0.0F;
      }

      Iterator var17 = model.getCubes().values().iterator();

      while(true) {
         AdvancedModelRenderer cube;
         AdvancedModelRenderer walkPart;
         float prevX;
         float prevY;
         float prevZ;
         float x;
         float y;
         float z;
         do {
            do {
               do {
                  do {
                     do {
                        do {
                           if (!var17.hasNext()) {
                              float speed_walk = 0.2F;
                              float speed_idle = entity.isSleeping() ? 0.025F : 0.05F;
                              float speed_fly = 0.2F;
                              prevX = 0.5F;
                              prevY = entity.isSleeping() ? 0.25F : 0.5F;
                              prevZ = 0.5F;
                              if (!walking) {
                                 model.bob(model.getCube("BodyUpper"), -speed_fly, prevZ * 5.0F, false, ageInTicks, 1.0F);
                                 model.walk(model.getCube("BodyUpper"), -speed_fly, prevZ * 0.1F, false, 0.0F, 0.0F, ageInTicks, 1.0F);
                                 model.chainWave(this.tailPartsWBody, speed_fly, prevZ * -0.1F, 0.0D, ageInTicks, 1.0F);
                                 model.chainWave(this.neckParts, speed_fly, prevZ * 0.2F, -4.0D, ageInTicks, 1.0F);
                                 model.chainWave(this.toesPartsL, speed_fly, prevZ * 0.2F, -2.0D, ageInTicks, 1.0F);
                                 model.chainWave(this.toesPartsR, speed_fly, prevZ * 0.2F, -2.0D, ageInTicks, 1.0F);
                                 model.walk(model.getCube("ThighR"), -speed_fly, prevZ * 0.1F, false, 0.0F, 0.0F, ageInTicks, 1.0F);
                                 model.walk(model.getCube("ThighL"), -speed_fly, prevZ * 0.1F, true, 0.0F, 0.0F, ageInTicks, 1.0F);
                              } else {
                                 model.bob(model.getCube("BodyUpper"), speed_walk * 2.0F, prevX * 1.7F, false, limbSwing, limbSwingAmount);
                                 model.bob(model.getCube("ThighR"), speed_walk, prevX * 1.7F, false, limbSwing, limbSwingAmount);
                                 model.bob(model.getCube("ThighL"), speed_walk, prevX * 1.7F, false, limbSwing, limbSwingAmount);
                                 model.chainSwing(this.tailParts, speed_walk, prevX * 0.25F, -2.0D, limbSwing, limbSwingAmount);
                                 model.chainWave(this.tailParts, speed_walk, prevX * 0.15F, 2.0D, limbSwing, limbSwingAmount);
                                 model.chainSwing(this.neckParts, speed_walk, prevX * 0.15F, 2.0D, limbSwing, limbSwingAmount);
                                 model.chainWave(this.neckParts, speed_walk, prevX * 0.05F, -2.0D, limbSwing, limbSwingAmount);
                                 model.chainSwing(this.tailParts, speed_idle, prevY * 0.25F, -2.0D, ageInTicks, 1.0F);
                                 model.chainWave(this.tailParts, speed_idle, prevY * 0.15F, -2.0D, ageInTicks, 1.0F);
                                 model.chainWave(this.neckParts, speed_idle, prevY * -0.15F, -3.0D, ageInTicks, 1.0F);
                                 model.walk(model.getCube("Neck1"), speed_idle, prevY * 0.05F, false, 0.0F, 0.0F, ageInTicks, 1.0F);
                              }

                              model.bob(model.getCube("BodyUpper"), speed_idle, prevY * 1.3F, false, ageInTicks, 1.0F);
                              model.bob(model.getCube("ThighR"), speed_idle, -prevY * 1.3F, false, ageInTicks, 1.0F);
                              model.bob(model.getCube("ThighL"), speed_idle, -prevY * 1.3F, false, ageInTicks, 1.0F);
                              model.bob(model.getCube("armR1"), speed_idle, -prevY * 1.3F, false, ageInTicks, 1.0F);
                              model.bob(model.getCube("armL1"), speed_idle, -prevY * 1.3F, false, ageInTicks, 1.0F);
                              if (entity.getAnimation() != EntityDragonBase.ANIMATION_SHAKEPREY || entity.getAnimation() != EntityDragonBase.ANIMATION_ROAR) {
                                 model.faceTarget(rotationYaw, rotationPitch, 4.0F, this.neckParts);
                              }

                              if (entity.isActuallyBreathingFire()) {
                                 x = 0.7F;
                                 y = 0.1F;
                                 model.chainFlap(this.neckParts, x, y, 2.0D, ageInTicks, 1.0F);
                                 model.chainSwing(this.neckParts, x * 0.65F, y * 0.1F, 1.0D, ageInTicks, 1.0F);
                              }

                              if (!entity.isModelDead()) {
                                 entity.turn_buffer.applyChainSwingBuffer(this.neckParts);
                                 entity.tail_buffer.applyChainSwingBuffer(this.tailPartsWBody);
                                 if (entity.flyProgress > 0.0F || entity.hoverProgress > 0.0F) {
                                    entity.roll_buffer.applyChainFlapBuffer(model.getCube("BodyUpper"));
                                    entity.pitch_buffer_body.applyChainWaveBuffer(model.getCube("BodyUpper"));
                                    entity.pitch_buffer.applyChainWaveBufferReverse(this.tailPartsWBody);
                                 }
                              }

                              if (entity.width >= 2.0F && entity.flyProgress == 0.0F && entity.hoverProgress == 0.0F) {
                                 LegArticulator.articulateQuadruped(entity, entity.legSolver, model.getCube("BodyUpper"), model.getCube("BodyLower"), model.getCube("Neck1"), model.getCube("ThighL"), model.getCube("LegL"), this.toesPartsL, model.getCube("ThighR"), model.getCube("LegR"), this.toesPartsR, model.getCube("armL1"), model.getCube("armL2"), this.clawL, model.getCube("armR1"), model.getCube("armR2"), this.clawR, 1.0F, 0.5F, 0.5F, -0.15F, -0.15F, 0.0F, 20.0F);
                              }

                              return;
                           }

                           cube = (AdvancedModelRenderer)var17.next();
                           this.genderMob(entity, cube);
                           if (!swimming && walking && entity.flyProgress <= 0.0F && entity.hoverProgress <= 0.0F && entity.modelDeadProgress <= 0.0F) {
                              walkPart = EnumDragonAnimations.GROUND_POSE.icedragon_model.getCube(cube.boxName);
                              if (prevPosition.getCube(cube.boxName) != null) {
                                 prevX = prevPosition.getCube(cube.boxName).rotateAngleX;
                                 prevY = prevPosition.getCube(cube.boxName).rotateAngleY;
                                 prevZ = prevPosition.getCube(cube.boxName).rotateAngleZ;
                                 x = currentPosition.getCube(cube.boxName).rotateAngleX;
                                 y = currentPosition.getCube(cube.boxName).rotateAngleY;
                                 z = currentPosition.getCube(cube.boxName).rotateAngleZ;
                                 if (!this.isHorn(cube) && (!this.isWing(model, cube) || entity.getAnimation() != EntityDragonBase.ANIMATION_WINGBLAST && entity.getAnimation() != EntityDragonBase.ANIMATION_EPIC_ROAR)) {
                                    this.addToRotateAngle(cube, limbSwingAmount, prevX + deltaTicks * this.distance(prevX, x), prevY + deltaTicks * this.distance(prevY, y), prevZ + deltaTicks * this.distance(prevZ, z));
                                 } else {
                                    this.addToRotateAngle(cube, limbSwingAmount, walkPart.rotateAngleX, walkPart.rotateAngleY, walkPart.rotateAngleZ);
                                 }
                              }
                           }

                           if (entity.modelDeadProgress > 0.0F) {
                              if (!this.isPartEqual(cube, EnumDragonAnimations.DEAD.icedragon_model.getCube(cube.boxName))) {
                                 this.transitionTo(cube, EnumDragonAnimations.DEAD.icedragon_model.getCube(cube.boxName), entity.modelDeadProgress, 20.0F, cube.boxName.equals("ThighR") || cube.boxName.equals("ThighL"));
                              }

                              if (cube.boxName.equals("BodyUpper")) {
                                 cube.rotationPointY += 0.35F * entity.modelDeadProgress;
                              }
                           }

                           if (entity.sleepProgress > 0.0F && !this.isPartEqual(cube, EnumDragonAnimations.SLEEPING_POSE.icedragon_model.getCube(cube.boxName))) {
                              this.transitionTo(cube, EnumDragonAnimations.SLEEPING_POSE.icedragon_model.getCube(cube.boxName), entity.sleepProgress, 20.0F, false);
                           }

                           if (entity.hoverProgress > 0.0F && !this.isPartEqual(cube, EnumDragonAnimations.HOVERING_POSE.icedragon_model.getCube(cube.boxName)) && !this.isWing(model, cube) && !cube.boxName.contains("Tail")) {
                              this.transitionTo(cube, EnumDragonAnimations.HOVERING_POSE.icedragon_model.getCube(cube.boxName), entity.hoverProgress, 20.0F, false);
                           }

                           if (entity.flyProgress > 0.0F && !this.isPartEqual(cube, EnumDragonAnimations.FLYING_POSE.icedragon_model.getCube(cube.boxName))) {
                              this.transitionTo(cube, EnumDragonAnimations.FLYING_POSE.icedragon_model.getCube(cube.boxName), entity.flyProgress - entity.diveProgress * 2.0F, 20.0F, false);
                           }

                           if (entity.sitProgress > 0.0F && !entity.isRiding() && !this.isPartEqual(cube, EnumDragonAnimations.SITTING_POSE.icedragon_model.getCube(cube.boxName))) {
                              this.transitionTo(cube, EnumDragonAnimations.SITTING_POSE.icedragon_model.getCube(cube.boxName), entity.sitProgress, 20.0F, false);
                           }

                           if (entity.ridingProgress > 0.0F && !this.isHorn(cube) && EnumDragonAnimations.SIT_ON_PLAYER_POSE.icedragon_model.getCube(cube.boxName) != null && !this.isPartEqual(cube, EnumDragonAnimations.SIT_ON_PLAYER_POSE.icedragon_model.getCube(cube.boxName))) {
                              this.transitionTo(cube, EnumDragonAnimations.SIT_ON_PLAYER_POSE.icedragon_model.getCube(cube.boxName), entity.ridingProgress, 20.0F, false);
                              if (cube.boxName.equals("BodyUpper")) {
                                 cube.rotationPointZ += (-12.0F - cube.rotationPointZ) / 20.0F * entity.ridingProgress;
                              }
                           }

                           if (entity.tackleProgress > 0.0F && !this.isPartEqual(EnumDragonAnimations.TACKLE.icedragon_model.getCube(cube.boxName), EnumDragonAnimations.FLYING_POSE.icedragon_model.getCube(cube.boxName)) && !this.isWing(model, cube)) {
                              this.transitionTo(cube, EnumDragonAnimations.TACKLE.icedragon_model.getCube(cube.boxName), entity.tackleProgress, 5.0F, false);
                           }

                           if (entity.diveProgress > 0.0F && !this.isPartEqual(cube, EnumDragonAnimations.DIVING_POSE.icedragon_model.getCube(cube.boxName))) {
                              this.transitionTo(cube, EnumDragonAnimations.DIVING_POSE.icedragon_model.getCube(cube.boxName), entity.diveProgress, 10.0F, false);
                           }

                           if ((float)entity.swimProgress > 0.0F && !this.isPartEqual(cube, EnumDragonAnimations.SWIM_POSE.icedragon_model.getCube(cube.boxName))) {
                              this.transitionTo(cube, EnumDragonAnimations.SWIM_POSE.icedragon_model.getCube(cube.boxName), (float)entity.swimProgress, 20.0F, false);
                           }

                           if (entity.fireBreathProgress > 0.0F && !this.isPartEqual(cube, EnumDragonAnimations.STREAM_BREATH.icedragon_model.getCube(cube.boxName)) && !this.isWing(model, cube) && !cube.boxName.contains("Finger")) {
                              if (entity.prevFireBreathProgress <= entity.fireBreathProgress) {
                                 this.transitionTo(cube, EnumDragonAnimations.BLAST_CHARGE3.icedragon_model.getCube(cube.boxName), MathHelper.clamp_float(entity.fireBreathProgress, 0.0F, 5.0F), 5.0F, false);
                              }

                              this.transitionTo(cube, EnumDragonAnimations.STREAM_BREATH.icedragon_model.getCube(cube.boxName), MathHelper.clamp_float(entity.fireBreathProgress - 5.0F, 0.0F, 5.0F), 5.0F, false);
                           }

                           if (!walking && !swimming) {
                              walkPart = EnumDragonAnimations.FLYING_POSE.icedragon_model.getCube(cube.boxName);
                              prevX = prevPosition.getCube(cube.boxName).rotateAngleX;
                              prevY = prevPosition.getCube(cube.boxName).rotateAngleY;
                              prevZ = prevPosition.getCube(cube.boxName).rotateAngleZ;
                              x = currentPosition.getCube(cube.boxName).rotateAngleX;
                              y = currentPosition.getCube(cube.boxName).rotateAngleY;
                              z = currentPosition.getCube(cube.boxName).rotateAngleZ;
                              if (x != walkPart.rotateAngleX || y != walkPart.rotateAngleY || z != walkPart.rotateAngleZ) {
                                 this.setRotateAngle(cube, 1.0F, prevX + deltaTicks * this.distance(prevX, x), prevY + deltaTicks * this.distance(prevY, y), prevZ + deltaTicks * this.distance(prevZ, z));
                              }
                           }
                        } while(!swimming);
                     } while(!(entity.flyProgress <= 0.0F));
                  } while(!(entity.hoverProgress <= 0.0F));
               } while(!(entity.modelDeadProgress <= 0.0F));

               walkPart = EnumDragonAnimations.SWIM_POSE.icedragon_model.getCube(cube.boxName);
            } while(prevPosition.getCube(cube.boxName) == null);

            prevX = prevPosition.getCube(cube.boxName).rotateAngleX;
            prevY = prevPosition.getCube(cube.boxName).rotateAngleY;
            prevZ = prevPosition.getCube(cube.boxName).rotateAngleZ;
            x = currentPosition.getCube(cube.boxName).rotateAngleX;
            y = currentPosition.getCube(cube.boxName).rotateAngleY;
            z = currentPosition.getCube(cube.boxName).rotateAngleZ;
         } while(x == walkPart.rotateAngleX && y == walkPart.rotateAngleY && z == walkPart.rotateAngleZ);

         this.setRotateAngle(cube, limbSwingAmount, prevX + deltaTicks * this.distance(prevX, x), prevY + deltaTicks * this.distance(prevY, y), prevZ + deltaTicks * this.distance(prevZ, z));
      }
   }

   private void genderMob(EntityIceDragon entity, AdvancedModelRenderer cube) {
      if (!entity.isMale()) {
         IceAndFireTabulaModel maleModel = EnumDragonAnimations.MALE.icedragon_model;
         IceAndFireTabulaModel femaleModel = EnumDragonAnimations.FEMALE.icedragon_model;
         float x = femaleModel.getCube(cube.boxName).rotateAngleX;
         float y = femaleModel.getCube(cube.boxName).rotateAngleY;
         float z = femaleModel.getCube(cube.boxName).rotateAngleZ;
         if (x != maleModel.getCube(cube.boxName).rotateAngleX || y != maleModel.getCube(cube.boxName).rotateAngleY || z != maleModel.getCube(cube.boxName).rotateAngleZ) {
            this.setRotateAngle(cube, 1.0F, x, y, z);
         }
      }

   }

   private boolean isWing(IceAndFireTabulaModel model, AdvancedModelRenderer modelRenderer) {
      return model.getCube("armL1") == modelRenderer || model.getCube("armR1") == modelRenderer || model.getCube("armL1").childModels.contains(modelRenderer) || model.getCube("armR1").childModels.contains(modelRenderer);
   }

   private boolean isHorn(AdvancedModelRenderer modelRenderer) {
      return modelRenderer.boxName.contains("Horn");
   }

   public void animate(IceAndFireTabulaModel model, EntityIceDragon entity, float limbSwing, float limbSwingAmount, float ageInTicks, float rotationYaw, float rotationPitch, float scale) {
      model.llibAnimator.update(entity);
      model.llibAnimator.setAnimation(EntityIceDragon.ANIMATION_FIRECHARGE);
      model.llibAnimator.startKeyframe(10);
      this.moveToPose(model, EnumDragonAnimations.BLAST_CHARGE1.icedragon_model);
      model.llibAnimator.endKeyframe();
      model.llibAnimator.startKeyframe(10);
      this.moveToPose(model, EnumDragonAnimations.BLAST_CHARGE2.icedragon_model);
      model.llibAnimator.endKeyframe();
      model.llibAnimator.startKeyframe(5);
      this.moveToPose(model, EnumDragonAnimations.BLAST_CHARGE3.icedragon_model);
      model.llibAnimator.endKeyframe();
      model.llibAnimator.resetKeyframe(5);
      model.llibAnimator.setAnimation(EntityIceDragon.ANIMATION_SPEAK);
      model.llibAnimator.startKeyframe(5);
      this.rotate(model.llibAnimator, model.getCube("Jaw"), 18.0F, 0.0F, 0.0F);
      model.llibAnimator.move(model.getCube("Jaw"), 0.0F, 0.0F, 0.2F);
      model.llibAnimator.endKeyframe();
      model.llibAnimator.setStaticKeyframe(5);
      model.llibAnimator.startKeyframe(5);
      this.rotate(model.llibAnimator, model.getCube("Jaw"), 18.0F, 0.0F, 0.0F);
      model.llibAnimator.move(model.getCube("Jaw"), 0.0F, 0.0F, 0.2F);
      model.llibAnimator.endKeyframe();
      model.llibAnimator.resetKeyframe(5);
      model.llibAnimator.setAnimation(EntityIceDragon.ANIMATION_BITE);
      model.llibAnimator.startKeyframe(10);
      this.moveToPose(model, EnumDragonAnimations.BITE1.icedragon_model);
      model.llibAnimator.endKeyframe();
      model.llibAnimator.startKeyframe(5);
      this.moveToPose(model, EnumDragonAnimations.BITE2.icedragon_model);
      model.llibAnimator.endKeyframe();
      model.llibAnimator.startKeyframe(5);
      this.moveToPose(model, EnumDragonAnimations.BITE3.icedragon_model);
      model.llibAnimator.endKeyframe();
      model.llibAnimator.resetKeyframe(10);
      model.llibAnimator.setAnimation(EntityIceDragon.ANIMATION_SHAKEPREY);
      model.llibAnimator.startKeyframe(15);
      this.moveToPose(model, EnumDragonAnimations.GRAB1.icedragon_model);
      model.llibAnimator.endKeyframe();
      model.llibAnimator.startKeyframe(10);
      this.moveToPose(model, EnumDragonAnimations.GRAB2.icedragon_model);
      model.llibAnimator.endKeyframe();
      model.llibAnimator.startKeyframe(10);
      this.moveToPose(model, EnumDragonAnimations.GRAB_SHAKE1.icedragon_model);
      model.llibAnimator.endKeyframe();
      model.llibAnimator.startKeyframe(10);
      this.moveToPose(model, EnumDragonAnimations.GRAB_SHAKE2.icedragon_model);
      model.llibAnimator.endKeyframe();
      model.llibAnimator.startKeyframe(10);
      this.moveToPose(model, EnumDragonAnimations.GRAB_SHAKE3.icedragon_model);
      model.llibAnimator.endKeyframe();
      model.llibAnimator.resetKeyframe(10);
      model.llibAnimator.setAnimation(EntityIceDragon.ANIMATION_TAILWHACK);
      model.llibAnimator.startKeyframe(10);
      this.moveToPose(model, EnumDragonAnimations.TAIL_WHIP1.icedragon_model);
      model.llibAnimator.endKeyframe();
      model.llibAnimator.startKeyframe(10);
      this.moveToPose(model, EnumDragonAnimations.TAIL_WHIP2.icedragon_model);
      model.llibAnimator.endKeyframe();
      model.llibAnimator.startKeyframe(10);
      this.moveToPose(model, EnumDragonAnimations.TAIL_WHIP3.icedragon_model);
      model.llibAnimator.endKeyframe();
      model.llibAnimator.resetKeyframe(10);
      model.llibAnimator.setAnimation(EntityIceDragon.ANIMATION_WINGBLAST);
      model.llibAnimator.startKeyframe(10);
      this.moveToPose(model, EnumDragonAnimations.WING_BLAST1.icedragon_model);
      model.llibAnimator.move(model.getCube("BodyUpper"), 0.0F, -4.0F, 0.0F);
      model.llibAnimator.endKeyframe();
      model.llibAnimator.startKeyframe(5);
      this.moveToPose(model, EnumDragonAnimations.WING_BLAST2.icedragon_model);
      model.llibAnimator.move(model.getCube("BodyUpper"), 0.0F, -4.0F, 0.0F);
      model.llibAnimator.endKeyframe();
      model.llibAnimator.startKeyframe(5);
      this.moveToPose(model, EnumDragonAnimations.WING_BLAST3.icedragon_model);
      model.llibAnimator.move(model.getCube("BodyUpper"), 0.0F, -4.0F, 0.0F);
      model.llibAnimator.endKeyframe();
      model.llibAnimator.startKeyframe(5);
      this.moveToPose(model, EnumDragonAnimations.WING_BLAST4.icedragon_model);
      model.llibAnimator.move(model.getCube("BodyUpper"), 0.0F, -4.0F, 0.0F);
      model.llibAnimator.endKeyframe();
      model.llibAnimator.startKeyframe(5);
      this.moveToPose(model, EnumDragonAnimations.WING_BLAST5.icedragon_model);
      model.llibAnimator.move(model.getCube("BodyUpper"), 0.0F, -4.0F, 0.0F);
      model.llibAnimator.endKeyframe();
      model.llibAnimator.startKeyframe(5);
      this.moveToPose(model, EnumDragonAnimations.WING_BLAST6.icedragon_model);
      model.llibAnimator.move(model.getCube("BodyUpper"), 0.0F, -4.0F, 0.0F);
      model.llibAnimator.endKeyframe();
      model.llibAnimator.startKeyframe(5);
      this.moveToPose(model, EnumDragonAnimations.WING_BLAST5.icedragon_model);
      model.llibAnimator.move(model.getCube("BodyUpper"), 0.0F, -4.0F, 0.0F);
      model.llibAnimator.endKeyframe();
      model.llibAnimator.resetKeyframe(10);
      model.llibAnimator.setAnimation(EntityIceDragon.ANIMATION_ROAR);
      model.llibAnimator.startKeyframe(10);
      this.moveToPose(model, EnumDragonAnimations.ROAR1.icedragon_model);
      model.llibAnimator.endKeyframe();
      model.llibAnimator.startKeyframe(10);
      this.moveToPose(model, EnumDragonAnimations.ROAR2.icedragon_model);
      model.llibAnimator.endKeyframe();
      model.llibAnimator.startKeyframe(10);
      this.moveToPose(model, EnumDragonAnimations.ROAR3.icedragon_model);
      model.llibAnimator.endKeyframe();
      model.llibAnimator.resetKeyframe(10);
      model.llibAnimator.setAnimation(EntityIceDragon.ANIMATION_EPIC_ROAR);
      model.llibAnimator.startKeyframe(10);
      this.moveToPose(model, EnumDragonAnimations.EPIC_ROAR1.icedragon_model);
      model.llibAnimator.move(model.getCube("BodyUpper"), 0.0F, -6.8F, 0.0F);
      model.llibAnimator.endKeyframe();
      model.llibAnimator.startKeyframe(10);
      this.moveToPose(model, EnumDragonAnimations.EPIC_ROAR2.icedragon_model);
      model.llibAnimator.move(model.getCube("BodyUpper"), 0.0F, -6.8F, 0.0F);
      model.llibAnimator.endKeyframe();
      model.llibAnimator.startKeyframe(10);
      this.moveToPose(model, EnumDragonAnimations.EPIC_ROAR3.icedragon_model);
      model.llibAnimator.move(model.getCube("BodyUpper"), 0.0F, -6.8F, 0.0F);
      model.llibAnimator.endKeyframe();
      model.llibAnimator.startKeyframe(10);
      this.moveToPose(model, EnumDragonAnimations.EPIC_ROAR2.icedragon_model);
      model.llibAnimator.move(model.getCube("BodyUpper"), 0.0F, -6.8F, 0.0F);
      model.llibAnimator.endKeyframe();
      model.llibAnimator.startKeyframe(10);
      this.moveToPose(model, EnumDragonAnimations.EPIC_ROAR3.icedragon_model);
      model.llibAnimator.move(model.getCube("BodyUpper"), 0.0F, -6.8F, 0.0F);
      model.llibAnimator.endKeyframe();
      model.llibAnimator.resetKeyframe(10);
   }
}
