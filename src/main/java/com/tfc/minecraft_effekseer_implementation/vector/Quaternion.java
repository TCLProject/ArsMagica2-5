package com.tfc.minecraft_effekseer_implementation.vector;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.tclproject.mysteriumlib.future.PortUtil;

public final class Quaternion {
   public static final Quaternion field_227060_a_ = new Quaternion(0.0F, 0.0F, 0.0F, 1.0F);
   private float field_227061_b_;
   private float field_227062_c_;
   private float field_227063_d_;
   private float field_227064_e_;

   public Quaternion(float p_i48100_1_, float p_i48100_2_, float p_i48100_3_, float p_i48100_4_) {
      this.field_227061_b_ = p_i48100_1_;
      this.field_227062_c_ = p_i48100_2_;
      this.field_227063_d_ = p_i48100_3_;
      this.field_227064_e_ = p_i48100_4_;
   }

   public Quaternion(Vector3f p_i48101_1_, float p_i48101_2_, boolean p_i48101_3_) {
      if (p_i48101_3_) {
         p_i48101_2_ *= ((float)Math.PI / 180F);
      }

      float f = func_214903_b(p_i48101_2_ / 2.0F);
      this.field_227061_b_ = p_i48101_1_.getX() * f;
      this.field_227062_c_ = p_i48101_1_.getY() * f;
      this.field_227063_d_ = p_i48101_1_.getZ() * f;
      this.field_227064_e_ = func_214904_a(p_i48101_2_ / 2.0F);
   }

   @SideOnly(Side.CLIENT)
   public Quaternion(float p_i48102_1_, float p_i48102_2_, float p_i48102_3_, boolean p_i48102_4_) {
      if (p_i48102_4_) {
         p_i48102_1_ *= ((float)Math.PI / 180F);
         p_i48102_2_ *= ((float)Math.PI / 180F);
         p_i48102_3_ *= ((float)Math.PI / 180F);
      }

      float f = func_214903_b(0.5F * p_i48102_1_);
      float f1 = func_214904_a(0.5F * p_i48102_1_);
      float f2 = func_214903_b(0.5F * p_i48102_2_);
      float f3 = func_214904_a(0.5F * p_i48102_2_);
      float f4 = func_214903_b(0.5F * p_i48102_3_);
      float f5 = func_214904_a(0.5F * p_i48102_3_);
      this.field_227061_b_ = f * f3 * f5 + f1 * f2 * f4;
      this.field_227062_c_ = f1 * f2 * f5 - f * f3 * f4;
      this.field_227063_d_ = f * f2 * f5 + f1 * f3 * f4;
      this.field_227064_e_ = f1 * f3 * f5 - f * f2 * f4;
   }

   public Quaternion(Quaternion p_i48103_1_) {
      this.field_227061_b_ = p_i48103_1_.field_227061_b_;
      this.field_227062_c_ = p_i48103_1_.field_227062_c_;
      this.field_227063_d_ = p_i48103_1_.field_227063_d_;
      this.field_227064_e_ = p_i48103_1_.field_227064_e_;
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else if (p_equals_1_ != null && this.getClass() == p_equals_1_.getClass()) {
         Quaternion quaternion = (Quaternion)p_equals_1_;
         if (Float.compare(quaternion.field_227061_b_, this.field_227061_b_) != 0) {
            return false;
         } else if (Float.compare(quaternion.field_227062_c_, this.field_227062_c_) != 0) {
            return false;
         } else if (Float.compare(quaternion.field_227063_d_, this.field_227063_d_) != 0) {
            return false;
         } else {
            return Float.compare(quaternion.field_227064_e_, this.field_227064_e_) == 0;
         }
      } else {
         return false;
      }
   }

   public int hashCode() {
      int i = Float.floatToIntBits(this.field_227061_b_);
      i = 31 * i + Float.floatToIntBits(this.field_227062_c_);
      i = 31 * i + Float.floatToIntBits(this.field_227063_d_);
      return 31 * i + Float.floatToIntBits(this.field_227064_e_);
   }

   public String toString() {
      StringBuilder stringbuilder = new StringBuilder();
      stringbuilder.append("Quaternion[").append(this.func_195894_d()).append(" + ");
      stringbuilder.append(this.func_195889_a()).append("i + ");
      stringbuilder.append(this.func_195891_b()).append("j + ");
      stringbuilder.append(this.func_195893_c()).append("k]");
      return stringbuilder.toString();
   }

   public float func_195889_a() {
      return this.field_227061_b_;
   }

   public float func_195891_b() {
      return this.field_227062_c_;
   }

   public float func_195893_c() {
      return this.field_227063_d_;
   }

   public float func_195894_d() {
      return this.field_227064_e_;
   }

   public void multiply(Quaternion p_195890_1_) {
      float f = this.func_195889_a();
      float f1 = this.func_195891_b();
      float f2 = this.func_195893_c();
      float f3 = this.func_195894_d();
      float f4 = p_195890_1_.func_195889_a();
      float f5 = p_195890_1_.func_195891_b();
      float f6 = p_195890_1_.func_195893_c();
      float f7 = p_195890_1_.func_195894_d();
      this.field_227061_b_ = f3 * f4 + f * f7 + f1 * f6 - f2 * f5;
      this.field_227062_c_ = f3 * f5 - f * f6 + f1 * f7 + f2 * f4;
      this.field_227063_d_ = f3 * f6 + f * f5 - f1 * f4 + f2 * f7;
      this.field_227064_e_ = f3 * f7 - f * f4 - f1 * f5 - f2 * f6;
   }

   @SideOnly(Side.CLIENT)
   public void multiply(float p_227065_1_) {
      this.field_227061_b_ *= p_227065_1_;
      this.field_227062_c_ *= p_227065_1_;
      this.field_227063_d_ *= p_227065_1_;
      this.field_227064_e_ *= p_227065_1_;
   }

   public void func_195892_e() {
      this.field_227061_b_ = -this.field_227061_b_;
      this.field_227062_c_ = -this.field_227062_c_;
      this.field_227063_d_ = -this.field_227063_d_;
   }

   @SideOnly(Side.CLIENT)
   public void set(float p_227066_1_, float p_227066_2_, float p_227066_3_, float p_227066_4_) {
      this.field_227061_b_ = p_227066_1_;
      this.field_227062_c_ = p_227066_2_;
      this.field_227063_d_ = p_227066_3_;
      this.field_227064_e_ = p_227066_4_;
   }

   private static float func_214904_a(float p_214904_0_) {
      return (float)Math.cos((double)p_214904_0_);
   }

   private static float func_214903_b(float p_214903_0_) {
      return (float)Math.sin((double)p_214903_0_);
   }

   @SideOnly(Side.CLIENT)
   public void func_227067_f_() {
      float f = this.func_195889_a() * this.func_195889_a() + this.func_195891_b() * this.func_195891_b() + this.func_195893_c() * this.func_195893_c() + this.func_195894_d() * this.func_195894_d();
      if (f > 1.0E-6F) {
         float f1 = PortUtil.func_226165_i_(f);
         this.field_227061_b_ *= f1;
         this.field_227062_c_ *= f1;
         this.field_227063_d_ *= f1;
         this.field_227064_e_ *= f1;
      } else {
         this.field_227061_b_ = 0.0F;
         this.field_227062_c_ = 0.0F;
         this.field_227063_d_ = 0.0F;
         this.field_227064_e_ = 0.0F;
      }

   }

   @SideOnly(Side.CLIENT)
   public Quaternion func_227068_g_() {
      return new Quaternion(this);
   }
}