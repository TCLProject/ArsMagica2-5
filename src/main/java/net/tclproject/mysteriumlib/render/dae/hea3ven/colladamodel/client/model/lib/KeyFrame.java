package net.tclproject.mysteriumlib.render.dae.hea3ven.colladamodel.client.model.lib;

public class KeyFrame {
   private double frame;
   private double value;
   private Interpolation interpolation;

   public KeyFrame(double frame, double value, Interpolation interpolation) {
      this.frame = frame;
      this.value = value;
      this.interpolation = interpolation;
   }

   public double getFrame() {
      return this.frame;
   }

   public void setFrame(double frame) {
      this.frame = frame;
   }

   public double getValue() {
      return this.value;
   }

   public void setValue(double value) {
      this.value = value;
   }

   public double interpolate(double time, KeyFrame nextFrame) {
      return this.interpolation.interpolate(time, this, nextFrame);
   }
}
