package net.tclproject.mysteriumlib.render.dae.hea3ven.colladamodel.client.model.lib;

public class LinearInterpolation implements Interpolation {
   public double interpolate(double time, KeyFrame frame, KeyFrame nextFrame) {
      double s = (time - frame.getFrame()) / (nextFrame.getFrame() - frame.getFrame());
      return frame.getValue() + (nextFrame.getValue() - frame.getValue()) * s;
   }
}
