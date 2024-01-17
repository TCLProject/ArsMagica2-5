package net.tclproject.mysteriumlib.render.dae.hea3ven.colladamodel.client.model.lib;

import net.minecraft.util.Vec3;

public class ColladaSource {
   private String id;
   private float[] float_data;
   private String[] string_data;
   private String[] params;
   private int count;
   private int stride;

   public String getId() {
      return this.id;
   }

   public void setId(String id) {
      this.id = id;
   }

   public float[] getFloatData() {
      return this.float_data;
   }

   public void setData(float[] data) {
      this.float_data = data;
   }

   public String[] getStringData() {
      return this.string_data;
   }

   public void setData(String[] data) {
      this.string_data = data;
   }

   public int getCount() {
      return this.count;
   }

   public void setCount(int count) {
      this.count = count;
   }

   public int getStride() {
      return this.stride;
   }

   public void setStride(int stride) {
      this.stride = stride;
   }

   public String[] getParams() {
      return this.params;
   }

   public void setParams(String[] params) {
      this.params = params;
   }

   private int getParamOffset(String param) {
      for(int i = 0; i < this.params.length; ++i) {
         if (this.params[i].equals(param)) {
            return i;
         }
      }

      return 0;
   }

   public float getFloat(String param, Integer index) {
      return this.float_data[index * this.stride + this.getParamOffset(param)];
   }

   public float getDouble(String param, Integer index) {
      return this.float_data[index * this.stride + this.getParamOffset(param)];
   }

   public float getFloat(Integer paramOffset, Integer index) {
      return this.float_data[index * this.stride + paramOffset];
   }

   public float getDouble(Integer paramOffset, Integer index) {
      return this.float_data[index * this.stride + paramOffset];
   }

   public Vec3 getVec3(Integer index, String param1, String param2, String param3) {
      return this.getStride() == 3 ? Vec3.createVectorHelper((double)this.getDouble(param1, index), (double)this.getDouble(param2, index), (double)this.getDouble(param3, index)) : null;
   }

   public Vec3 getVec2(Integer index, String param1, String param2) {
      return Vec3.createVectorHelper((double)this.getDouble(param1, index), (double)this.getDouble(param2, index), 0.0D);
   }

   public String getString(String param, Integer index) {
      return this.getString(this.getParamOffset(param), index);
   }

   public String getString(Integer paramOffset, Integer index) {
      return this.string_data[index * this.stride + paramOffset];
   }
}
