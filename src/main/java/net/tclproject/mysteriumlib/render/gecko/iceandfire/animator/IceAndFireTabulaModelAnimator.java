package net.tclproject.mysteriumlib.render.gecko.iceandfire.animator;

import java.util.Iterator;
import net.ilexiconn.llibrary.client.model.ModelAnimator;
import net.ilexiconn.llibrary.client.model.tools.AdvancedModelRenderer;
import net.minecraft.util.MathHelper;
import net.tclproject.mysteriumlib.render.gecko.iceandfire.util.IceAndFireTabulaModel;
import net.tclproject.mysteriumlib.render.gecko.iceandfire.util.MathUtils;

public class IceAndFireTabulaModelAnimator {
   protected IceAndFireTabulaModel baseModel;

   public IceAndFireTabulaModelAnimator(IceAndFireTabulaModel baseModel) {
      this.baseModel = baseModel;
   }

   public void setRotateAngle(AdvancedModelRenderer model, float limbSwingAmount, float x, float y, float z) {
      model.rotateAngleX += limbSwingAmount * this.distance(model.rotateAngleX, x);
      model.rotateAngleY += limbSwingAmount * this.distance(model.rotateAngleY, y);
      model.rotateAngleZ += limbSwingAmount * this.distance(model.rotateAngleZ, z);
   }

   public void addToRotateAngle(AdvancedModelRenderer model, float limbSwingAmount, float x, float y, float z) {
      model.rotateAngleX += Math.min(limbSwingAmount * 2.0F, 1.0F) * this.distance(model.defaultRotationX, x);
      model.rotateAngleY += Math.min(limbSwingAmount * 2.0F, 1.0F) * this.distance(model.defaultRotationY, y);
      model.rotateAngleZ += Math.min(limbSwingAmount * 2.0F, 1.0F) * this.distance(model.defaultRotationZ, z);
   }

   public boolean isPartEqual(AdvancedModelRenderer original, AdvancedModelRenderer pose) {
      return pose != null && pose.rotateAngleX == original.defaultRotationX && pose.rotateAngleY == original.defaultRotationY && pose.rotateAngleZ == original.defaultRotationZ;
   }

   public boolean isPositionEqual(AdvancedModelRenderer original, AdvancedModelRenderer pose) {
      return pose.rotationPointX == original.defaultPositionX && pose.rotationPointY == original.defaultPositionY && pose.rotationPointZ == original.defaultPositionZ;
   }

   public void transitionTo(AdvancedModelRenderer from, AdvancedModelRenderer to, float timer, float maxTime, boolean oldFashioned) {
      if (oldFashioned) {
         from.rotateAngleX += (to.rotateAngleX - from.rotateAngleX) / maxTime * timer;
         from.rotateAngleY += (to.rotateAngleY - from.rotateAngleY) / maxTime * timer;
         from.rotateAngleZ += (to.rotateAngleZ - from.rotateAngleZ) / maxTime * timer;
      } else {
         this.transitionAngles(from, to, timer, maxTime);
      }

      from.rotationPointX += (to.rotationPointX - from.rotationPointX) / maxTime * timer;
      from.rotationPointY += (to.rotationPointY - from.rotationPointY) / maxTime * timer;
      from.rotationPointZ += (to.rotationPointZ - from.rotationPointZ) / maxTime * timer;
      from.offsetX += (to.offsetX - from.offsetX) / maxTime * timer;
      from.offsetY += (to.offsetY - from.offsetY) / maxTime * timer;
      from.offsetZ += (to.offsetZ - from.offsetZ) / maxTime * timer;
   }

   public void transitionAngles(AdvancedModelRenderer from, AdvancedModelRenderer to, float timer, float maxTime) {
      from.rotateAngleX += this.distance(from.rotateAngleX, to.rotateAngleX) / maxTime * timer;
      from.rotateAngleY += this.distance(from.rotateAngleY, to.rotateAngleY) / maxTime * timer;
      from.rotateAngleZ += this.distance(from.rotateAngleZ, to.rotateAngleZ) / maxTime * timer;
   }

   public float distance(float rotateAngleFrom, float rotateAngleTo) {
      return (float) MathUtils.atan2_accurate((double)MathHelper.sin(rotateAngleTo - rotateAngleFrom), (double)MathHelper.cos(rotateAngleTo - rotateAngleFrom));
   }

   public void rotate(ModelAnimator animator, AdvancedModelRenderer model, float x, float y, float z) {
      animator.rotate(model, (float)Math.toRadians((double)x), (float)Math.toRadians((double)y), (float)Math.toRadians((double)z));
   }

   public void moveToPose(IceAndFireTabulaModel model, IceAndFireTabulaModel modelTo) {
      Iterator var3 = model.getCubes().values().iterator();

      while(var3.hasNext()) {
         AdvancedModelRenderer cube = (AdvancedModelRenderer)var3.next();
         float toX;
         float toY;
         float toZ;
         if (!this.isPartEqual(this.baseModel.getCube(cube.boxName), modelTo.getCube(cube.boxName))) {
            toX = modelTo.getCube(cube.boxName).rotateAngleX;
            toY = modelTo.getCube(cube.boxName).rotateAngleY;
            toZ = modelTo.getCube(cube.boxName).rotateAngleZ;
            model.llibAnimator.rotate(cube, this.distance(cube.rotateAngleX, toX), this.distance(cube.rotateAngleY, toY), this.distance(cube.rotateAngleZ, toZ));
         }

         if (!this.isPositionEqual(this.baseModel.getCube(cube.boxName), modelTo.getCube(cube.boxName))) {
            toX = modelTo.getCube(cube.boxName).rotationPointX;
            toY = modelTo.getCube(cube.boxName).rotationPointY;
            toZ = modelTo.getCube(cube.boxName).rotationPointZ;
            model.llibAnimator.move(cube, this.distance(cube.rotationPointX, toX), this.distance(cube.rotationPointY, toY), this.distance(cube.rotationPointZ, toZ));
         }
      }

   }
}
