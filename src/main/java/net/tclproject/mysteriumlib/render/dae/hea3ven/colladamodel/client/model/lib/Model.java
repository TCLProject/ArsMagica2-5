package net.tclproject.mysteriumlib.render.dae.hea3ven.colladamodel.client.model.lib;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import net.minecraft.client.renderer.Tessellator;

public class Model implements IModelAnimationCustom {
   private Map geometries = new HashMap();
   private double animationLength = -1.0D;

   public String getType() {
      return "dae";
   }

   public void addGeometry(Geometry geom) {
      this.geometries.put(geom.getName(), geom);
   }

   public Geometry getGeometry(String geomId) {
      return (Geometry)this.geometries.get(geomId);
   }

   public void renderAll() {
      Tessellator tessellator = Tessellator.instance;
      Iterator i$ = this.geometries.values().iterator();

      while(i$.hasNext()) {
         Geometry geom = (Geometry)i$.next();
         geom.render(tessellator);
      }

   }

   public void renderOnly(String... geometriesNames) {
      Tessellator tessellator = Tessellator.instance;
      String[] arr$ = geometriesNames;
      int len$ = geometriesNames.length;

      for(int i$ = 0; i$ < len$; ++i$) {
         String geometryName = arr$[i$];
         ((Geometry)this.geometries.get(geometryName)).render(tessellator);
      }

   }

   public void renderPart(String partName) {
      Tessellator tessellator = Tessellator.instance;
      ((Geometry)this.geometries.get(partName)).render(tessellator);
   }

   public void renderAllExcept(String... excludedGroupNames) {
      Set excludedSet = new HashSet(Arrays.asList(excludedGroupNames));
      Tessellator tessellator = Tessellator.instance;
      Iterator i$ = this.geometries.values().iterator();

      while(i$.hasNext()) {
         Geometry geometry = (Geometry)i$.next();
         if (!excludedSet.contains(geometry.getName())) {
            geometry.render(tessellator);
         }
      }

   }

   public void renderAnimationAll(double time) {
      Tessellator tessellator = Tessellator.instance;
      Iterator i$ = this.geometries.values().iterator();

      while(i$.hasNext()) {
         Geometry geom = (Geometry)i$.next();
         geom.renderAnimation(tessellator, time);
      }

   }

   public void renderAnimationOnly(double time, String... geometriesNames) {
      Tessellator tessellator = Tessellator.instance;
      String[] arr$ = geometriesNames;
      int len$ = geometriesNames.length;

      for(int i$ = 0; i$ < len$; ++i$) {
         String geometryName = arr$[i$];
         ((Geometry)this.geometries.get(geometryName)).renderAnimation(tessellator, time);
      }

   }

   public void renderAnimationPart(double time, String partName) {
      Tessellator tessellator = Tessellator.instance;
      ((Geometry)this.geometries.get(partName)).renderAnimation(tessellator, time);
   }

   public void renderAnimationAllExcept(double time, String... excludedGroupNames) {
      Set excludedSet = new HashSet(Arrays.asList(excludedGroupNames));
      Tessellator tessellator = Tessellator.instance;
      Iterator i$ = this.geometries.values().iterator();

      while(i$.hasNext()) {
         Geometry geometry = (Geometry)i$.next();
         if (!excludedSet.contains(geometry.getName())) {
            geometry.renderAnimation(tessellator, time);
         }
      }

   }

   public double getAnimationLength() {
      if (this.animationLength == -1.0D) {
         this.calculateAnimationLength();
      }

      return this.animationLength;
   }

   private void calculateAnimationLength() {
      this.animationLength = 0.0D;
      Iterator i$ = this.geometries.values().iterator();

      while(i$.hasNext()) {
         Geometry geom = (Geometry)i$.next();
         if (geom.getAnimationLength() > this.animationLength) {
            this.animationLength = geom.getAnimationLength();
         }
      }

   }
}
