package net.tclproject.mysteriumlib.render.dae.hea3ven.colladamodel.client.model.lib;

import net.minecraft.util.Vec3;
import org.lwjgl.opengl.GL11;

public class Scale extends Transform {
   private Vec3 vec;
   private Animation animationX;
   private Animation animationY;
   private Animation animationZ;

   public Scale(Vec3 vec) {
      this.vec = vec;
      this.animationX = null;
      this.animationY = null;
      this.animationZ = null;
   }

   public Vec3 getVec() {
      return this.vec;
   }

   public void setAnimation(String paramName, Animation anim) {
      if (paramName.equals("Z")) {
         this.animationX = anim;
      } else if (paramName.equals("Y")) {
         this.animationY = anim;
      } else if (paramName.equals("X")) {
         this.animationZ = anim;
      }

   }

   public void apply() {
      GL11.glScaled(this.vec.xCoord, this.vec.yCoord, this.vec.zCoord);
   }

   public void applyAnimation(double time) {
      GL11.glScaled(this.animationX == null ? this.vec.xCoord : this.animationX.getValue(time), this.animationY == null ? this.vec.yCoord : this.animationY.getValue(time), this.animationZ == null ? this.vec.zCoord : this.animationZ.getValue(time));
   }

   public double getAnimationLength() {
      double animationLength = 0.0D;
      if (this.animationX != null && this.animationX.getAnimationLength() > animationLength) {
         animationLength = this.animationX.getAnimationLength();
      }

      if (this.animationY != null && this.animationY.getAnimationLength() > animationLength) {
         animationLength = this.animationY.getAnimationLength();
      }

      if (this.animationZ != null && this.animationZ.getAnimationLength() > animationLength) {
         animationLength = this.animationZ.getAnimationLength();
      }

      return animationLength;
   }
}
