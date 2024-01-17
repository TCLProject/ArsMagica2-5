package net.tclproject.mysteriumlib.render.dae.hea3ven.colladamodel.client.model.lib;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.Vec3;

public class Face {
   private Vec3[] vertex = null;
   private Vec3[] vertexNormals = null;
   private Vec3[] vertexTexCoord = null;

   public void render(Tessellator tessellator) {
      tessellator.startDrawing(9);
      Vec3 faceNormal = this.calculateFaceNormal();
      tessellator.setNormal((float)(-faceNormal.xCoord), (float)(-faceNormal.yCoord), (float)(-faceNormal.zCoord));
      float averageU = 0.0F;
      float averageV = 0.0F;

      for(int i = 0; i < this.vertexTexCoord.length; ++i) {
         averageU = (float)((double)averageU + this.vertexTexCoord[i].xCoord);
         averageV = (float)((double)averageV + this.vertexTexCoord[i].yCoord);
      }

      averageU /= (float)this.vertexTexCoord.length;
      averageV /= (float)this.vertexTexCoord.length;

      for(int i = 0; i < this.vertex.length; ++i) {
         float offsetU = 5.0E-4F;
         float offsetV = 5.0E-4F;
         if (this.vertexTexCoord[i].xCoord > (double)averageU) {
            offsetU = -offsetU;
         }

         if (this.vertexTexCoord[i].yCoord > (double)averageV) {
            offsetV = -offsetV;
         }

         tessellator.addVertexWithUV(this.vertex[i].xCoord, this.vertex[i].yCoord, this.vertex[i].zCoord, this.vertexTexCoord[i].xCoord + (double)offsetU, 1.0D - this.vertexTexCoord[i].yCoord - (double)offsetV);
      }

      tessellator.draw();
   }

   private Vec3 calculateFaceNormal() {
      double sumX = 0.0D;
      double sumY = 0.0D;
      double sumZ = 0.0D;

      for(int i = 0; i < this.vertexNormals.length; ++i) {
         sumX += this.vertexNormals[i].xCoord;
         sumY += this.vertexNormals[i].yCoord;
         sumZ += this.vertexNormals[i].zCoord;
      }

      return Vec3.createVectorHelper(sumX / (double)this.vertexNormals.length, sumY / (double)this.vertexNormals.length, sumZ / (double)this.vertexNormals.length);
   }

   public void setVertex(Vec3[] vertex, Vec3[] normal, Vec3[] texCoords) {
      this.vertex = vertex;
      this.vertexNormals = normal;
      this.vertexTexCoord = texCoords;
   }
}
