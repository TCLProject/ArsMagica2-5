package net.tclproject.mysteriumlib.render.gecko.common.entities.client.renderer.model;

import net.minecraft.client.model.ModelBase;
import net.tclproject.mysteriumlib.render.gecko.common.entities.client.renderer.entity.AdvancedNPCRenderer;

public class MoreAdvancedModelRenderer extends AdvancedNPCRenderer implements Cloneable {
   public MoreAdvancedModelRenderer(ModelBase model, float shadowSize) {
      super(model, shadowSize);
   }

   public Object clone() throws CloneNotSupportedException {
      return super.clone();
   }
}
