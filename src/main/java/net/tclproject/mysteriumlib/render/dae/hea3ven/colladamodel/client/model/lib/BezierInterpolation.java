package net.tclproject.mysteriumlib.render.dae.hea3ven.colladamodel.client.model.lib;

public class BezierInterpolation implements Interpolation {
   public static double APPROXIMATION_EPSILON = 1.0E-9D;
   public static double VERYSMALL = 1.0E-20D;
   public static int MAXIMUM_ITERATIONS = 100;
   private double inTangent;
   private double outTangent;

   public BezierInterpolation(double outTangent, double inTangent) {
      this.outTangent = outTangent;
      this.inTangent = inTangent;
   }

   public double interpolate(double time, KeyFrame frame, KeyFrame nextFrame) {
      double s = this.approximateCubicBezierParameter(time, frame.getFrame(), frame.getFrame() / 3.0D + nextFrame.getFrame() * 2.0D / 3.0D, frame.getFrame() * 2.0D / 3.0D + nextFrame.getFrame() / 3.0D, nextFrame.getFrame());
      return this.bezierInterpolate(s, frame.getValue(), this.outTangent / 3.0D + frame.getValue(), nextFrame.getValue() - this.inTangent / 3.0D, nextFrame.getValue());
   }

   private double approximateCubicBezierParameter(double atX, double P0_X, double C0_X, double C1_X, double P1_X) {
      if (atX - P0_X < VERYSMALL) {
         return 0.0D;
      } else if (P1_X - atX < VERYSMALL) {
         return 1.0D;
      } else {
         long iterationStep = 0L;
         double u = 0.0D;

         double v;
         for(v = 1.0D; iterationStep < (long)MAXIMUM_ITERATIONS; ++iterationStep) {
            double a = (P0_X + C0_X) * 0.5D;
            double b = (C0_X + C1_X) * 0.5D;
            double c = (C1_X + P1_X) * 0.5D;
            double d = (a + b) * 0.5D;
            double e = (b + c) * 0.5D;
            double f = (d + e) * 0.5D;
            if (Math.abs(f - atX) < APPROXIMATION_EPSILON) {
               return this.clampToZeroOne((u + v) * 0.5D);
            }

            if (f < atX) {
               P0_X = f;
               C0_X = e;
               C1_X = c;
               u = (u + v) * 0.5D;
            } else {
               C0_X = a;
               C1_X = d;
               P1_X = f;
               v = (u + v) * 0.5D;
            }
         }

         return this.clampToZeroOne((u + v) * 0.5D);
      }
   }

   private double clampToZeroOne(double value) {
      if (value < 0.0D) {
         return 0.0D;
      } else {
         return value > 1.0D ? 1.0D : value;
      }
   }

   private double bezierInterpolate(double s, double p0, double c0, double c1, double p1) {
      return (double)((float)(Math.pow(1.0D - s, 3.0D) * p0 + 3.0D * Math.pow(1.0D - s, 2.0D) * s * c0 + 3.0D * (1.0D - s) * Math.pow(s, 2.0D) * c1 + Math.pow(s, 3.0D) * p1));
   }
}
