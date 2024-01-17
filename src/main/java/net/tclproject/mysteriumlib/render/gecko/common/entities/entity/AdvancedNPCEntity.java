package net.tclproject.mysteriumlib.render.gecko.common.entities.entity;

import net.ilexiconn.llibrary.server.animation.Animation;
import net.ilexiconn.llibrary.server.animation.AnimationHandler;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class AdvancedNPCEntity extends AdvancedEntity {
   public static final Animation DIE_ANIMATION = Animation.create(70);
   public static final Animation HURT_ANIMATION = Animation.create(30);
   public static final Animation HAMMER_SWING_ANIMATION = Animation.create(40);
   public static final Animation ATTACK_ANIMATION = Animation.create(24);
   public static final Animation IDLE_ANIMATION = Animation.create(35);
   public static final Animation RESET_TO_STANDING_ANIMATION = Animation.create(7);
   public static final Animation KNOCK_ANIMATION = Animation.create(70);
   public int timerSwing = 0;

   public AdvancedNPCEntity(World world) {
      super(world);
      this.setSize(1.0F, 2.0F);
      this.setCurrentItemOrArmor(0, new ItemStack(Items.diamond_pickaxe));
      this.setCurrentItemOrArmor(1, new ItemStack(Items.diamond_helmet));
      this.setCurrentItemOrArmor(2, new ItemStack(Items.diamond_chestplate));
      this.setCurrentItemOrArmor(3, new ItemStack(Items.diamond_leggings));
      this.setCurrentItemOrArmor(4, new ItemStack(Items.diamond_boots));
      this.active = true;
      this.setRotation(0.0F, 0.0F);
   }

   public void onUpdate() {
      super.onUpdate();
      this.setRotation(0.0F, 0.0F);
      if (this.timerSwing == 0) {
         AnimationHandler.INSTANCE.sendAnimationMessage(this, KNOCK_ANIMATION);
      }

      if (this.timerSwing < 75) {
         ++this.timerSwing;
      } else {
         this.timerSwing = 0;
      }

   }

   public Animation getDeathAnimation() {
      return DIE_ANIMATION;
   }

   public Animation getHurtAnimation() {
      return HURT_ANIMATION;
   }

   protected void entityInit() {
      super.entityInit();
      this.dataWatcher.addObject(28, 0);
   }

   public int getAnimState() {
      return this.dataWatcher.getWatchableObjectInt(28);
   }

   public void setAnimState(Integer state) {
      this.dataWatcher.updateObject(28, state);
   }

   public Animation[] getAnimations() {
      return new Animation[]{DIE_ANIMATION, HURT_ANIMATION, ATTACK_ANIMATION, IDLE_ANIMATION, HAMMER_SWING_ANIMATION, RESET_TO_STANDING_ANIMATION, KNOCK_ANIMATION};
   }
}
