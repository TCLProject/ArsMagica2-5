package net.tclproject.mysteriumlib.render.gecko.iceandfire.util;

public final class LegSolverQuadruped extends LegSolver {
   public final Leg backLeft;
   public final Leg backRight;
   public final Leg frontLeft;
   public final Leg frontRight;

   public LegSolverQuadruped(float forward, float side) {
      this(0.0F, forward, side, side, 1.0F);
   }

   public LegSolverQuadruped(float forwardCenter, float forward, float sideBack, float sideFront, float range) {
      super(new Leg(forwardCenter - forward, sideBack, range, false), new Leg(forwardCenter - forward, -sideBack, range, false), new Leg(forwardCenter + forward, sideFront, range, true), new Leg(forwardCenter + forward, -sideFront, range, true));
      this.backLeft = this.legs[0];
      this.backRight = this.legs[1];
      this.frontLeft = this.legs[2];
      this.frontRight = this.legs[3];
   }
}
