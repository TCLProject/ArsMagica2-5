package net.tclproject.mysteriumlib.render.dae.hea3ven.colladamodel.client.model.lib;

import net.minecraft.util.Vec3;
import org.lwjgl.opengl.GL11;

public class Rotation extends Transform {
   private Vec3 vec;
   private double angle;
   private Animation animation;

   public Rotation(Vec3 vec, double angle) {
      this.vec = vec;
      this.angle = angle;
      this.animation = null;
   }

   public Vec3 getVec() {
      return this.vec;
   }

   public void apply() {
      GL11.glRotated(this.angle, this.vec.xCoord, this.vec.yCoord, this.vec.zCoord);
   }

   public double getAngle() {
      return this.angle;
   }

   public void setAngle(double angle) {
      this.angle = angle;
   }

   public void setAnimation(String paramName, Animation anim) {
      if (paramName.equals("ANGLE")) {
         this.animation = anim;
      }

   }

   public void applyAnimation(double time) {
      GL11.glRotated(this.animation == null ? this.angle : this.animation.getValue(time), this.vec.xCoord, this.vec.yCoord, this.vec.zCoord);
   }

   public double getAnimationLength() {
      return this.animation != null ? this.animation.getAnimationLength() : 0.0D;
   }
}
