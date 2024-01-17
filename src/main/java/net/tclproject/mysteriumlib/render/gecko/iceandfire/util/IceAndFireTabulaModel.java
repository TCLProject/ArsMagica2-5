package net.tclproject.mysteriumlib.render.gecko.iceandfire.util;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.ilexiconn.llibrary.client.model.ModelAnimator;
import net.ilexiconn.llibrary.client.model.tabula.container.TabulaCubeContainer;
import net.ilexiconn.llibrary.client.model.tabula.container.TabulaCubeGroupContainer;
import net.ilexiconn.llibrary.client.model.tabula.container.TabulaModelContainer;
import net.ilexiconn.llibrary.client.model.tools.AdvancedModelBase;
import net.ilexiconn.llibrary.client.model.tools.AdvancedModelRenderer;
import net.minecraft.entity.Entity;

@SideOnly(Side.CLIENT)
public class IceAndFireTabulaModel extends AdvancedModelBase {
   public ModelAnimator llibAnimator;
   protected Map cubes;
   protected List rootBoxes;
   protected IIceAndFireTabulaModelAnimator tabulaAnimator;
   protected Map identifierMap;
   protected double[] scale;
   protected boolean init;

   public IceAndFireTabulaModel(TabulaModelContainer container, IIceAndFireTabulaModelAnimator tabulaAnimator) {
      this.cubes = new HashMap();
      this.rootBoxes = new ArrayList();
      this.identifierMap = new HashMap();
      this.init = false;
      this.textureWidth = container.getTextureWidth();
      this.textureHeight = container.getTextureHeight();
      this.tabulaAnimator = tabulaAnimator;
      Iterator var3 = container.getCubes().iterator();

      while(var3.hasNext()) {
         TabulaCubeContainer cube = (TabulaCubeContainer)var3.next();
         this.parseCube(cube, (AdvancedModelRenderer)null);
      }

      container.getCubeGroups().forEach(this::parseCubeGroup);
      this.updateDefaultPose();
      this.scale = container.getScale();
      this.llibAnimator = ModelAnimator.create();
   }

   public IceAndFireTabulaModel(TabulaModelContainer container) {
      this(container, (IIceAndFireTabulaModelAnimator)null);
   }

   private void parseCubeGroup(TabulaCubeGroupContainer container) {
      Iterator var2 = container.getCubes().iterator();

      while(var2.hasNext()) {
         TabulaCubeContainer cube = (TabulaCubeContainer)var2.next();
         this.parseCube(cube, (AdvancedModelRenderer)null);
      }

      container.getCubeGroups().forEach(this::parseCubeGroup);
   }

   private void parseCube(TabulaCubeContainer cube, AdvancedModelRenderer parent) {
      AdvancedModelRenderer box = this.createBox(cube);
      this.cubes.put(cube.getName(), box);
      this.identifierMap.put(cube.getIdentifier(), box);
      if (parent != null) {
         parent.addChild(box);
      } else {
         this.rootBoxes.add(box);
      }

      Iterator var4 = cube.getChildren().iterator();

      while(var4.hasNext()) {
         TabulaCubeContainer child = (TabulaCubeContainer)var4.next();
         this.parseCube(child, box);
      }

   }

   private AdvancedModelRenderer createBox(TabulaCubeContainer cube) {
      int[] textureOffset = cube.getTextureOffset();
      double[] position = cube.getPosition();
      double[] rotation = cube.getRotation();
      double[] offset = cube.getOffset();
      int[] dimensions = cube.getDimensions();
      AdvancedModelRenderer box = new AdvancedModelRenderer(this, cube.getName());
      box.setTextureOffset(textureOffset[0], textureOffset[1]);
      box.mirror = cube.isTextureMirrorEnabled();
      box.setRotationPoint((float)position[0], (float)position[1], (float)position[2]);
      box.addBox((float)offset[0], (float)offset[1], (float)offset[2], dimensions[0], dimensions[1], dimensions[2], 0.0F);
      box.rotateAngleX = (float)Math.toRadians(rotation[0]);
      box.rotateAngleY = (float)Math.toRadians(rotation[1]);
      box.rotateAngleZ = (float)Math.toRadians(rotation[2]);
      return box;
   }

   public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float rotationYaw, float rotationPitch, float scale) {
      GlStateManager.pushMatrix();
      GlStateManager.scale(this.scale[0], this.scale[1], this.scale[2]);
      Iterator var8 = this.rootBoxes.iterator();

      while(var8.hasNext()) {
         AdvancedModelRenderer box = (AdvancedModelRenderer)var8.next();
         box.render(scale);
      }

      GlStateManager.popMatrix();
   }

   public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float rotationYaw, float rotationPitch, float scale, Entity entity) {
      if (this.tabulaAnimator != null) {
         if (!this.init) {
            this.tabulaAnimator.init(this);
            this.init = true;
         }

         this.tabulaAnimator.setRotationAngles(this, entity, limbSwing, limbSwingAmount, ageInTicks, rotationYaw, rotationPitch, scale);
      }

   }

   public AdvancedModelRenderer getCube(String name) {
      return (AdvancedModelRenderer)this.cubes.get(name);
   }

   public AdvancedModelRenderer getCubeByIdentifier(String identifier) {
      return (AdvancedModelRenderer)this.identifierMap.get(identifier);
   }

   public Map getCubes() {
      return this.cubes;
   }
}
