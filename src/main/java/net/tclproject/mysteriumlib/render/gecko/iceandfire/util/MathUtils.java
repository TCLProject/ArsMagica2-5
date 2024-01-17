package net.tclproject.mysteriumlib.render.gecko.iceandfire.util;

public class MathUtils {
   private static final double coeff_1 = 0.7853981633974483D;
   private static final double coeff_2 = 2.356194490192345D;

   public static final double atan2_accurate(double y, double x) {
      double r;
      if (y < 0.0D) {
         y = -y;
         if (x > 0.0D) {
            r = (x - y) / (x + y);
            return -(0.1963D * r * r * r - 0.9817D * r + 0.7853981633974483D);
         } else {
            r = (x + y) / (y - x);
            return -(0.1963D * r * r * r - 0.9817D * r + 2.356194490192345D);
         }
      } else {
         if (y == 0.0D) {
            y = 1.0E-25D;
         }

         if (x > 0.0D) {
            r = (x - y) / (x + y);
            return 0.1963D * r * r * r - 0.9817D * r + 0.7853981633974483D;
         } else {
            r = (x + y) / (y - x);
            return 0.1963D * r * r * r - 0.9817D * r + 2.356194490192345D;
         }
      }
   }
}
