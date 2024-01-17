package net.tclproject.mysteriumlib.render.dae.hea3ven.colladamodel.client.model.lib;

public abstract class Transform {
   public abstract void apply();

   public abstract void applyAnimation(double var1);

   public abstract void setAnimation(String var1, Animation var2);

   public abstract double getAnimationLength();
}
