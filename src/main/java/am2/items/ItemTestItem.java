package am2.items;

import am2.items.renderers.EnumHandRenderType;
import com.tfc.minecraft_effekseer_implementation.common.Effek;
import com.tfc.minecraft_effekseer_implementation.common.Effeks;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

// Simple test item to demonstrate new functionality
public class ItemTestItem extends Item implements IEffekItem {
    @Override
    public Effek getDisplayedEffek(EntityPlayer player, ItemStack stack) {
        return Effeks.get("arsmagica2:effeks_hand_firstperson0");
    }

    @Override
    public float[] getScaleValues(EntityPlayer player, ItemStack stack) {
        return new float[]{0.35F, 0.28F, 0.45F};
    }

    @Override
    public float[] getTranslationValues(EntityPlayer player, ItemStack stack) {
        return new float[]{0.2F, 0.4F, 0.0F};
    }

    @Override
    public float[] getRotationValues(EntityPlayer player, ItemStack stack) {
        return new float[]{0F, 0F, 0F};
    }

    @Override
    public int getEffekDuration(EntityPlayer player, ItemStack stack) {
        return 65;
    }

    @Override
    public Effek getDisplayedEffekTP(EntityLivingBase player, ItemStack stack) {
        return Effeks.get("arsmagica2:effeks_hand_thirdperson0");
    }

    @Override
    public int getDisplayedEffekTPId(EntityLivingBase player, ItemStack stack) {
        return 0;
    }

    @Override
    public float[] getScaleValuesTP(EntityLivingBase player, ItemStack stack) {
        return new float[]{0.35F, 0.55F, 0.35F};
    }

    @Override
    public float[] getTranslationValuesTP(EntityLivingBase player, ItemStack stack) {
        return new float[]{0F, 0F, 0F};
    }

    @Override
    public float[] getRotationValuesTP(EntityLivingBase player, ItemStack stack) {
        return new float[]{0F, 0F, 0F};
    }

    @Override
    public int getEffekDurationTP(EntityLivingBase player, ItemStack stack) {
        return 65;
    }

    @Override
    public EnumHandRenderType getHandRenderType(EntityPlayer player, ItemStack stack) {
        return EnumHandRenderType.RENDER_DEFAULT_HAND;
    }

    @Override
    public boolean useHandRenderAlways(EntityPlayer player, ItemStack stack) {
        return false;
    }

    @Override
    public boolean bobEffek(EntityPlayer player, ItemStack stack) {
        return true;
    }

    @Override
    public int doSwingOffset(EntityPlayer player, ItemStack stack) {
        return 1;
    }
}
