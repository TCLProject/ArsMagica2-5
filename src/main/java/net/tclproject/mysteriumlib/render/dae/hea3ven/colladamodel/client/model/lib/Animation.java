package net.tclproject.mysteriumlib.render.dae.hea3ven.colladamodel.client.model.lib;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class Animation {
   private List frames = new LinkedList();

   public void addKeyFrame(KeyFrame keyFrame) {
      this.frames.add(keyFrame);
   }

   public double getValue(double time) {
      KeyFrame prevFrame = null;
      KeyFrame nextFrame = null;

      for(Iterator i = this.frames.iterator(); i.hasNext(); prevFrame = nextFrame) {
         nextFrame = (KeyFrame)i.next();
         if (time <= nextFrame.getFrame()) {
            break;
         }
      }

      if (prevFrame == null) {
         return nextFrame.getValue();
      } else {
         return prevFrame == nextFrame ? nextFrame.getValue() : prevFrame.interpolate(time, nextFrame);
      }
   }

   public double getAnimationLength() {
      return ((KeyFrame)this.frames.get(this.frames.size() - 1)).getFrame();
   }
}
