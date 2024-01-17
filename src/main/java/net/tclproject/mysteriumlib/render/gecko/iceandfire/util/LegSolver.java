package net.tclproject.mysteriumlib.render.gecko.iceandfire.util;

import net.minecraft.block.Block;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.tclproject.mysteriumlib.render.gecko.common.entities.entity.specific.iceandfire.EntityDragonBase;

public class LegSolver {
   public final Leg[] legs;

   public LegSolver(Leg... legs) {
      this.legs = legs;
   }

   public final void update(EntityDragonBase entity, float scale) {
      this.update(entity, entity.renderYawOffset, scale);
   }

   public final void update(EntityDragonBase entity, float yaw, float scale) {
      double sideTheta = (double)yaw / 57.29577951308232D;
      double sideX = Math.cos(sideTheta) * (double)scale;
      double sideZ = Math.sin(sideTheta) * (double)scale;
      double forwardTheta = sideTheta + 1.5707963267948966D;
      double forwardX = Math.cos(forwardTheta) * (double)scale;
      double forwardZ = Math.sin(forwardTheta) * (double)scale;
      Leg[] var16 = this.legs;
      int var17 = var16.length;

      for(int var18 = 0; var18 < var17; ++var18) {
         Leg leg = var16[var18];
         leg.update(entity, sideX, sideZ, forwardX, forwardZ, scale);
      }

   }

   public static final class Leg {
      public final float forward;
      public final float side;
      private final float range;
      private float height;
      private float prevHeight;
      private boolean isWing;

      public Leg(float forward, float side, float range, boolean isWing) {
         this.forward = forward;
         this.side = side;
         this.range = range;
         this.isWing = isWing;
      }

      public final float getHeight(float delta) {
         return this.prevHeight + (this.height - this.prevHeight) * delta;
      }

      public void update(EntityDragonBase entity, double sideX, double sideZ, double forwardX, double forwardZ, float scale) {
         this.prevHeight = this.height;
         double posY = entity.posY;
         float settledHeight = this.settle(entity, entity.posX + sideX * (double)this.side + forwardX * (double)this.forward, posY, entity.posZ + sideZ * (double)this.side + forwardZ * (double)this.forward, this.height);
         this.height = MathHelper.clamp_float(settledHeight, -this.range * scale, this.range * scale);
      }

      private float settle(EntityDragonBase entity, double x, double y, double z, float height) {
         float dist = this.getDistance(entity.worldObj, x, y, z);
         if ((double)(1.0F - dist) < 0.001D) {
            dist = this.getDistance(entity.worldObj, x, y - 1.0D, z) + (float)y % 1.0F;
         } else {
            dist = (float)((double)dist - (1.0D - y % 1.0D));
         }

         if (entity.onGround && height <= dist) {
            return height == dist ? height : Math.min(height + this.getFallSpeed(), dist);
         } else {
            return height > 0.0F ? Math.max(height - this.getRiseSpeed(), dist) : height;
         }
      }

      private float getDistance(World world, double x, double y, double z) {
         Block block = world.getBlock((int)x, (int)y, (int)z);
         AxisAlignedBB aabb = block.getCollisionBoundingBoxFromPool(world, (int)x, (int)y, (int)z);
         return aabb == null ? 1.0F : 1.0F - Math.min((float)aabb.maxY, 1.0F);
      }

      protected float getFallSpeed() {
         return 0.25F;
      }

      protected float getRiseSpeed() {
         return 0.25F;
      }
   }
}
