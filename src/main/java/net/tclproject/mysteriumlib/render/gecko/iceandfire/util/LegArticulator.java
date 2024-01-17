package net.tclproject.mysteriumlib.render.gecko.iceandfire.util;

import net.ilexiconn.llibrary.client.model.tools.AdvancedModelRenderer;
import net.tclproject.mysteriumlib.render.gecko.common.entities.entity.specific.iceandfire.EntityDragonBase;

public final class LegArticulator {
   public static void articulateQuadruped(EntityDragonBase entity, LegSolverQuadruped legs, AdvancedModelRenderer body, AdvancedModelRenderer lowerBody, AdvancedModelRenderer neck, AdvancedModelRenderer backLeftThigh, AdvancedModelRenderer backLeftCalf, AdvancedModelRenderer[] backLeftFoot, AdvancedModelRenderer backRightThigh, AdvancedModelRenderer backRightCalf, AdvancedModelRenderer[] backRightFoot, AdvancedModelRenderer frontLeftThigh, AdvancedModelRenderer frontLeftCalf, AdvancedModelRenderer[] frontLeftFoot, AdvancedModelRenderer frontRightThigh, AdvancedModelRenderer frontRightCalf, AdvancedModelRenderer[] frontRightFoot, float rotBackThigh, float rotBackCalf, float rotBackFoot, float rotFrontThigh, float rotFrontCalf, float rotFrontFoot, float delta) {
      float heightBackLeft = legs.backLeft.getHeight(delta);
      float heightBackRight = legs.backRight.getHeight(delta);
      float heightFrontLeft = legs.frontLeft.getHeight(delta);
      float heightFrontRight = legs.frontRight.getHeight(delta);
      if (heightBackLeft > 0.0F || heightBackRight > 0.0F || heightFrontLeft > 0.0F || heightFrontRight > 0.0F) {
         float sc = getScale(entity);
         float backAvg = avg(heightBackLeft, heightBackRight);
         float frontAvg = avg(heightFrontLeft, heightFrontRight);
         float bodyLength = Math.abs(avg(legs.backLeft.forward, legs.backRight.forward) - avg(legs.frontLeft.forward, legs.frontRight.forward));
         float tilt = (float)(MathUtils.atan2_accurate((double)(bodyLength * sc), (double)(backAvg - frontAvg)) - 1.5707963267948966D);
         body.rotationPointY += 16.0F / sc * backAvg;
         body.rotateAngleX += tilt;
         lowerBody.rotateAngleX -= tilt;
         backLeftThigh.rotationPointY += 16.0F / sc * tilt;
         backRightThigh.rotationPointY += 16.0F / sc * tilt;
         frontLeftThigh.rotateAngleX -= tilt;
         frontRightThigh.rotateAngleX -= tilt;
         neck.rotateAngleX -= tilt;
         articulateLegPair(sc, heightBackLeft, heightBackRight, backAvg, -backAvg, backLeftThigh, backLeftCalf, backLeftFoot, backRightThigh, backRightCalf, backRightFoot, rotBackThigh, rotBackCalf, rotBackFoot);
         articulateLegPair(sc, heightFrontLeft, heightFrontRight, frontAvg, -frontAvg, frontLeftThigh, frontLeftCalf, frontLeftFoot, frontRightThigh, frontRightCalf, frontRightFoot, rotFrontThigh, rotFrontCalf, rotFrontFoot);
      }

   }

   private static void articulateLegPair(float sc, float heightLeft, float heightRight, float avg, float offsetY, AdvancedModelRenderer leftThigh, AdvancedModelRenderer leftCalf, AdvancedModelRenderer[] leftFoot, AdvancedModelRenderer rightThigh, AdvancedModelRenderer rightCalf, AdvancedModelRenderer[] rightFoot, float rotThigh, float rotCalf, float rotFoot) {
      float difLeft = Math.max(0.0F, heightRight - heightLeft);
      float difRight = Math.max(0.0F, heightLeft - heightRight);
      leftThigh.rotationPointY += 16.0F / sc * (Math.max(heightLeft, avg) + offsetY);
      rightThigh.rotationPointY += 16.0F / sc * (Math.max(heightRight, avg) + offsetY);
      leftThigh.rotateAngleX -= rotThigh * difLeft;
      leftCalf.rotateAngleX += rotCalf * difLeft;
      rightThigh.rotateAngleX -= rotThigh * difRight;
      rightCalf.rotateAngleX += rotCalf * difRight;
      AdvancedModelRenderer[] var16 = rightFoot;
      int var17 = rightFoot.length;

      int var18;
      AdvancedModelRenderer part;
      for(var18 = 0; var18 < var17; ++var18) {
         part = var16[var18];
         part.rotateAngleX -= rotFoot * Math.min(0.0F, heightRight - heightLeft);
      }

      var16 = leftFoot;
      var17 = leftFoot.length;

      for(var18 = 0; var18 < var17; ++var18) {
         part = var16[var18];
         part.rotateAngleX -= rotFoot * Math.min(0.0F, heightLeft - heightRight);
      }

   }

   private static float avg(float a, float b) {
      return (a + b) / 2.0F;
   }

   private static float getScale(EntityDragonBase entity) {
      return entity.getRenderSizeModifier() * 0.33F;
   }
}
