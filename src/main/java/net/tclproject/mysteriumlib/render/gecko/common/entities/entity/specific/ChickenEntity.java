package net.tclproject.mysteriumlib.render.gecko.common.entities.entity.specific;

import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.ai.EntityAIFollowParent;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMate;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAITempt;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class ChickenEntity extends EntityChicken {
   public float destPos;
   public float oFlapSpeed;
   public float oFlap;
   public float wingRotDelta = 1.0F;

   public ChickenEntity(World p_i1681_1_) {
      super(p_i1681_1_);
      this.setSize(0.5F, 0.7F);
      this.tasks.addTask(0, new EntityAISwimming(this));
      this.tasks.addTask(1, new EntityAIPanic(this, 1.4D));
      this.tasks.addTask(2, new EntityAIMate(this, 1.0D));
      this.tasks.addTask(3, new EntityAITempt(this, 1.0D, Items.wheat_seeds, false));
      this.tasks.addTask(4, new EntityAIFollowParent(this, 1.1D));
      this.tasks.addTask(5, new EntityAIWander(this, 1.0D));
      this.tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
      this.tasks.addTask(7, new EntityAILookIdle(this));
   }

   public void onLivingUpdate() {
      super.onLivingUpdate();
      this.oFlap = this.field_70886_e;
      this.oFlapSpeed = this.destPos;
      this.destPos = (float)((double)this.destPos + (double)(!this.onGround && !this.isRiding() ? 4 : -1) * 0.3D);
      this.destPos = MathHelper.clamp_float(this.destPos, 0.0F, 1.0F);
      this.fallDistance = 0.0F;
      if (!this.onGround && !this.isRiding() && this.wingRotDelta < 1.0F) {
         this.wingRotDelta = 1.0F;
      }

      this.wingRotDelta = (float)((double)this.wingRotDelta * 0.9D);
      if (!this.onGround && !this.isRiding() && this.motionY < 0.0D) {
         this.motionY *= 0.6D;
      }

      this.field_70886_e += this.wingRotDelta * 2.0F;
   }

   public EntityChicken createChild(EntityAgeable p_90011_1_) {
      return new ChickenEntity(this.worldObj);
   }
}
