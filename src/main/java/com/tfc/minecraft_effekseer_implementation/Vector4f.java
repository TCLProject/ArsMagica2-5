package com.tfc.minecraft_effekseer_implementation;

import com.tfc.minecraft_effekseer_implementation.vector.Matrix4f;
import com.tfc.minecraft_effekseer_implementation.vector.Quaternion;
import com.tfc.minecraft_effekseer_implementation.vector.Vector3f;
import net.tclproject.mysteriumlib.future.PortUtil;

public class Vector4f {
   private float field_229368_a_;
   private float field_229369_b_;
   private float field_229370_c_;
   private float field_229371_d_;

   public Vector4f() {
   }

   public Vector4f(float p_i48096_1_, float p_i48096_2_, float p_i48096_3_, float p_i48096_4_) {
      this.field_229368_a_ = p_i48096_1_;
      this.field_229369_b_ = p_i48096_2_;
      this.field_229370_c_ = p_i48096_3_;
      this.field_229371_d_ = p_i48096_4_;
   }

   public Vector4f(Vector3f p_i226061_1_) {
      this(p_i226061_1_.getX(), p_i226061_1_.getY(), p_i226061_1_.getZ(), 1.0F);
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else if (p_equals_1_ != null && this.getClass() == p_equals_1_.getClass()) {
         Vector4f vector4f = (Vector4f)p_equals_1_;
         if (Float.compare(vector4f.field_229368_a_, this.field_229368_a_) != 0) {
            return false;
         } else if (Float.compare(vector4f.field_229369_b_, this.field_229369_b_) != 0) {
            return false;
         } else if (Float.compare(vector4f.field_229370_c_, this.field_229370_c_) != 0) {
            return false;
         } else {
            return Float.compare(vector4f.field_229371_d_, this.field_229371_d_) == 0;
         }
      } else {
         return false;
      }
   }

   public int hashCode() {
      int i = Float.floatToIntBits(this.field_229368_a_);
      i = 31 * i + Float.floatToIntBits(this.field_229369_b_);
      i = 31 * i + Float.floatToIntBits(this.field_229370_c_);
      return 31 * i + Float.floatToIntBits(this.field_229371_d_);
   }

   public float func_195910_a() {
      return this.field_229368_a_;
   }

   public float func_195913_b() {
      return this.field_229369_b_;
   }

   public float func_195914_c() {
      return this.field_229370_c_;
   }

   public float func_195915_d() {
      return this.field_229371_d_;
   }

   public void func_195909_a(Vector3f p_195909_1_) {
      this.field_229368_a_ *= p_195909_1_.getX();
      this.field_229369_b_ *= p_195909_1_.getY();
      this.field_229370_c_ *= p_195909_1_.getZ();
   }

   public void func_195911_a(float p_195911_1_, float p_195911_2_, float p_195911_3_, float p_195911_4_) {
      this.field_229368_a_ = p_195911_1_;
      this.field_229369_b_ = p_195911_2_;
      this.field_229370_c_ = p_195911_3_;
      this.field_229371_d_ = p_195911_4_;
   }

   public float func_229373_a_(Vector4f p_229373_1_) {
      return this.field_229368_a_ * p_229373_1_.field_229368_a_ + this.field_229369_b_ * p_229373_1_.field_229369_b_ + this.field_229370_c_ * p_229373_1_.field_229370_c_ + this.field_229371_d_ * p_229373_1_.field_229371_d_;
   }

   public boolean func_229374_e_() {
      float f = this.field_229368_a_ * this.field_229368_a_ + this.field_229369_b_ * this.field_229369_b_ + this.field_229370_c_ * this.field_229370_c_ + this.field_229371_d_ * this.field_229371_d_;
      if ((double)f < 1.0E-5D) {
         return false;
      } else {
         float f1 = PortUtil.func_226165_i_(f);
         this.field_229368_a_ *= f1;
         this.field_229369_b_ *= f1;
         this.field_229370_c_ *= f1;
         this.field_229371_d_ *= f1;
         return true;
      }
   }

   public void func_229372_a_(Matrix4f p_229372_1_) {
      float f = this.field_229368_a_;
      float f1 = this.field_229369_b_;
      float f2 = this.field_229370_c_;
      float f3 = this.field_229371_d_;
      this.field_229368_a_ = p_229372_1_.m00 * f + p_229372_1_.m01 * f1 + p_229372_1_.m02 * f2 + p_229372_1_.m03 * f3;
      this.field_229369_b_ = p_229372_1_.m10 * f + p_229372_1_.m11 * f1 + p_229372_1_.m12 * f2 + p_229372_1_.m13 * f3;
      this.field_229370_c_ = p_229372_1_.m20 * f + p_229372_1_.m21 * f1 + p_229372_1_.m22 * f2 + p_229372_1_.m23 * f3;
      this.field_229371_d_ = p_229372_1_.m30 * f + p_229372_1_.m31 * f1 + p_229372_1_.m32 * f2 + p_229372_1_.m33 * f3;
   }

   public void func_195912_a(Quaternion p_195912_1_) {
      Quaternion quaternion = new Quaternion(p_195912_1_);
      quaternion.multiply(new Quaternion(this.func_195910_a(), this.func_195913_b(), this.func_195914_c(), 0.0F));
      Quaternion quaternion1 = new Quaternion(p_195912_1_);
      quaternion1.func_195892_e();
      quaternion.multiply(quaternion1);
      this.func_195911_a(quaternion.func_195889_a(), quaternion.func_195891_b(), quaternion.func_195893_c(), this.func_195915_d());
   }

   public void func_229375_f_() {
      this.field_229368_a_ /= this.field_229371_d_;
      this.field_229369_b_ /= this.field_229371_d_;
      this.field_229370_c_ /= this.field_229371_d_;
      this.field_229371_d_ = 1.0F;
   }

   public String toString() {
      return "[" + this.field_229368_a_ + ", " + this.field_229369_b_ + ", " + this.field_229370_c_ + ", " + this.field_229371_d_ + "]";
   }

    // Forge start
    public void set(float[] values) {
        this.field_229368_a_ = values[0];
        this.field_229369_b_ = values[1];
        this.field_229370_c_ = values[2];
        this.field_229371_d_ = values[3];
    }
    public void setX(float x) { this.field_229368_a_ = x; }
    public void setY(float y) { this.field_229369_b_ = y; }
    public void setZ(float z) { this.field_229370_c_ = z; }
    public void setW(float z) { this.field_229371_d_ = z; }
}
