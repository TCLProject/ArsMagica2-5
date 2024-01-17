package com.tfc.minecraft_effekseer_implementation.vector;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.tclproject.mysteriumlib.future.PortUtil;

public final class Vector3f {
   public static Vector3f XN = new Vector3f(-1.0F, 0.0F, 0.0F);
   public static Vector3f XP = new Vector3f(1.0F, 0.0F, 0.0F);
   public static Vector3f YN = new Vector3f(0.0F, -1.0F, 0.0F);
   public static Vector3f YP = new Vector3f(0.0F, 1.0F, 0.0F);
   public static Vector3f ZN = new Vector3f(0.0F, 0.0F, -1.0F);
   public static Vector3f ZP = new Vector3f(0.0F, 0.0F, 1.0F);
   public float x;
   public float y;
   public float z;

   public Vector3f() {
   }

   public Vector3f(float p_i48098_1_, float p_i48098_2_, float p_i48098_3_) {
      this.x = p_i48098_1_;
      this.y = p_i48098_2_;
      this.z = p_i48098_3_;
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else if (p_equals_1_ != null && this.getClass() == p_equals_1_.getClass()) {
         Vector3f vector3f = (Vector3f)p_equals_1_;
         if (Float.compare(vector3f.x, this.x) != 0) {
            return false;
         } else if (Float.compare(vector3f.y, this.y) != 0) {
            return false;
         } else {
            return Float.compare(vector3f.z, this.z) == 0;
         }
      } else {
         return false;
      }
   }

   public int hashCode() {
      int i = Float.floatToIntBits(this.x);
      i = 31 * i + Float.floatToIntBits(this.y);
      return 31 * i + Float.floatToIntBits(this.z);
   }

   public float getX() {
      return this.x;
   }

   public float getY() {
      return this.y;
   }

   public float getZ() {
      return this.z;
   }

   @SideOnly(Side.CLIENT)
   public void func_195898_a(float p_195898_1_) {
      this.x *= p_195898_1_;
      this.y *= p_195898_1_;
      this.z *= p_195898_1_;
   }

   @SideOnly(Side.CLIENT)
   public void func_229192_b_(float p_229192_1_, float p_229192_2_, float p_229192_3_) {
      this.x *= p_229192_1_;
      this.y *= p_229192_2_;
      this.z *= p_229192_3_;
   }

   @SideOnly(Side.CLIENT)
   public void func_195901_a(float p_195901_1_, float p_195901_2_) {
      this.x = PortUtil.func_76131_a(this.x, p_195901_1_, p_195901_2_);
      this.y = PortUtil.func_76131_a(this.y, p_195901_1_, p_195901_2_);
      this.z = PortUtil.func_76131_a(this.z, p_195901_1_, p_195901_2_);
   }

   public void func_195905_a(float p_195905_1_, float p_195905_2_, float p_195905_3_) {
      this.x = p_195905_1_;
      this.y = p_195905_2_;
      this.z = p_195905_3_;
   }

   @SideOnly(Side.CLIENT)
   public void add(float p_195904_1_, float p_195904_2_, float p_195904_3_) {
      this.x += p_195904_1_;
      this.y += p_195904_2_;
      this.z += p_195904_3_;
   }

   @SideOnly(Side.CLIENT)
   public void add(Vector3f p_229189_1_) {
      this.x += p_229189_1_.x;
      this.y += p_229189_1_.y;
      this.z += p_229189_1_.z;
   }

   @SideOnly(Side.CLIENT)
   public void func_195897_a(Vector3f p_195897_1_) {
      this.x -= p_195897_1_.x;
      this.y -= p_195897_1_.y;
      this.z -= p_195897_1_.z;
   }

   @SideOnly(Side.CLIENT)
   public float func_195903_b(Vector3f p_195903_1_) {
      return this.x * p_195903_1_.x + this.y * p_195903_1_.y + this.z * p_195903_1_.z;
   }

   @SideOnly(Side.CLIENT)
   public boolean func_229194_d_() {
      float f = this.x * this.x + this.y * this.y + this.z * this.z;
      if ((double)f < 1.0E-5D) {
         return false;
      } else {
         float f1 = PortUtil.func_226165_i_(f);
         this.x *= f1;
         this.y *= f1;
         this.z *= f1;
         return true;
      }
   }

   @SideOnly(Side.CLIENT)
   public void func_195896_c(Vector3f p_195896_1_) {
      float f = this.x;
      float f1 = this.y;
      float f2 = this.z;
      float f3 = p_195896_1_.getX();
      float f4 = p_195896_1_.getY();
      float f5 = p_195896_1_.getZ();
      this.x = f1 * f5 - f2 * f4;
      this.y = f2 * f3 - f * f5;
      this.z = f * f4 - f1 * f3;
   }

   @SideOnly(Side.CLIENT)
   public void transform(Matrix3f p_229188_1_) {
      float f = this.x;
      float f1 = this.y;
      float f2 = this.z;
      this.x = p_229188_1_.m00 * f + p_229188_1_.m01 * f1 + p_229188_1_.m02 * f2;
      this.y = p_229188_1_.m10 * f + p_229188_1_.m11 * f1 + p_229188_1_.m12 * f2;
      this.z = p_229188_1_.m20 * f + p_229188_1_.m21 * f1 + p_229188_1_.m22 * f2;
   }

   public void transform(Quaternion p_214905_1_) {
      Quaternion quaternion = new Quaternion(p_214905_1_);
      quaternion.multiply(new Quaternion(this.getX(), this.getY(), this.getZ(), 0.0F));
      Quaternion quaternion1 = new Quaternion(p_214905_1_);
      quaternion1.func_195892_e();
      quaternion.multiply(quaternion1);
      this.func_195905_a(quaternion.func_195889_a(), quaternion.func_195891_b(), quaternion.func_195893_c());
   }

   @SideOnly(Side.CLIENT)
   public void func_229190_a_(Vector3f p_229190_1_, float p_229190_2_) {
      float f = 1.0F - p_229190_2_;
      this.x = this.x * f + p_229190_1_.x * p_229190_2_;
      this.y = this.y * f + p_229190_1_.y * p_229190_2_;
      this.z = this.z * f + p_229190_1_.z * p_229190_2_;
   }

   @SideOnly(Side.CLIENT)
   public Quaternion rotation(float p_229193_1_) {
      return new Quaternion(this, p_229193_1_, false);
   }

   @SideOnly(Side.CLIENT)
   public Quaternion rotationDegrees(float p_229187_1_) {
      return new Quaternion(this, p_229187_1_, true);
   }

   @SideOnly(Side.CLIENT)
   public Vector3f func_229195_e_() {
      return new Vector3f(this.x, this.y, this.z);
   }

   public String toString() {
      return "[" + this.x + ", " + this.y + ", " + this.z + "]";
   }

    // Forge start
    public Vector3f(float[] values) {
        set(values);
    }
    public void set(float[] values) {
        this.x = values[0];
        this.y = values[1];
        this.z = values[2];
    }
    public void setX(float x) { this.x = x; }
    public void setY(float y) { this.y = y; }
    public void setZ(float z) { this.z = z; }
}
