package com.tfc.minecraft_effekseer_implementation.vector;

public class Vector2f {
   public static final Vector2f field_189974_a = new Vector2f(0.0F, 0.0F);
   public static final Vector2f field_189975_b = new Vector2f(1.0F, 1.0F);
   public static final Vector2f field_189976_c = new Vector2f(1.0F, 0.0F);
   public static final Vector2f field_189977_d = new Vector2f(-1.0F, 0.0F);
   public static final Vector2f field_189978_e = new Vector2f(0.0F, 1.0F);
   public static final Vector2f field_189979_f = new Vector2f(0.0F, -1.0F);
   public static final Vector2f field_189980_g = new Vector2f(Float.MAX_VALUE, Float.MAX_VALUE);
   public static final Vector2f field_189981_h = new Vector2f(Float.MIN_VALUE, Float.MIN_VALUE);
   public final float field_189982_i;
   public final float field_189983_j;

   public Vector2f(float p_i47143_1_, float p_i47143_2_) {
      this.field_189982_i = p_i47143_1_;
      this.field_189983_j = p_i47143_2_;
   }

   public boolean func_201069_c(Vector2f p_201069_1_) {
      return this.field_189982_i == p_201069_1_.field_189982_i && this.field_189983_j == p_201069_1_.field_189983_j;
   }
}