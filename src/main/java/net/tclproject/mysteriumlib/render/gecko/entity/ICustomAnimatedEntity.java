package net.tclproject.mysteriumlib.render.gecko.entity;

import net.tclproject.mysteriumlib.render.gecko.animation.controller.EntityAnimationController;

public interface ICustomAnimatedEntity extends IAnimatedEntity {
   EntityAnimationController getManualController();

   void setManualAnimation(String var1);

   void setAutonomousEnabled(boolean var1);

   void setManualEnabled(boolean var1);

   void setRevertTicks(int var1);
}
