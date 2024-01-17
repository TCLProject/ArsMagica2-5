package net.tclproject.mysteriumlib.render.gecko.iceandfire.util;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.Entity;

@SideOnly(Side.CLIENT)
public interface IIceAndFireTabulaModelAnimator {
   void init(IceAndFireTabulaModel var1);

   void setRotationAngles(IceAndFireTabulaModel var1, Entity var2, float var3, float var4, float var5, float var6, float var7, float var8);
}
