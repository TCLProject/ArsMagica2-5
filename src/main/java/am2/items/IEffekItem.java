package am2.items;

import am2.items.renderers.EnumHandRenderType;
import com.tfc.minecraft_effekseer_implementation.common.Effek;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public interface IEffekItem {
    // null to avoid displaying in 1st person, effek to display in 1st person
    public Effek getDisplayedEffek(EntityPlayer player, ItemStack stack);
    /**[x, y, z] scale operation to be applied.*/
    public float[] getScaleValues(EntityPlayer player, ItemStack stack);
    /**[x, y, z] translations to be applied.*/
    public float[] getTranslationValues(EntityPlayer player, ItemStack stack);
    /**[x, y, z] rotations to be applied.*/
    public float[] getRotationValues(EntityPlayer player, ItemStack stack);
    /**how long the effek runs before resetting. -1 for no reset. Special condition resets can be done manually.*/
    public int getEffekDuration(EntityPlayer player, ItemStack stack);

    /* THIRD PERSON START */
    // null to avoid displaying in 3rd person, effek to display in 3rd person
    public Effek getDisplayedEffekTP(EntityLivingBase player, ItemStack stack);
    // the ID of the 3rd person effek (0, 5, 192 etc)
    public int getDisplayedEffekTPId(EntityLivingBase player, ItemStack stack);
    public float[] getScaleValuesTP(EntityLivingBase player, ItemStack stack);
    public float[] getTranslationValuesTP(EntityLivingBase player, ItemStack stack);
    public float[] getRotationValuesTP(EntityLivingBase player, ItemStack stack);
    // can't use '-1' trick here. if it's needed to only play a 3rd person effek once, set reloadTime > actual animation length, it results in auto-destruction
    public int getEffekDurationTP(EntityLivingBase player, ItemStack stack);
    /* THIRD PERSON END */

    /**the type of hand rendering that will be used when the item/effek is active.
     * Note: To disable rendering of the hand/item as a whole, simply set a fully transparent texture for the item.*/
    public EnumHandRenderType getHandRenderType(EntityPlayer player, ItemStack stack);
    /**if true, will use custom hand render regardless of if there's an effek. If false, will only use the custom
     * hand render (getHandRenderType) when the effek is active.*/
    public boolean useHandRenderAlways(EntityPlayer player, ItemStack stack);
    /**does the effek move with movements of the hand (when walking/sprinting).*/
    public boolean bobEffek(EntityPlayer player, ItemStack stack);
    /**if 0, effek will not be offset. If 1, the effek will be offset by hardcoded values when swinging in order to
     * line up with the (default) hand. This enhances RENDER_DEFAULT_HAND. RENDER_ITEM depends on the item/individual
     * case, and RENDER_SPELL_HAND works great without this. If 2, effek will be hidden during swing.*/
    public int doSwingOffset(EntityPlayer player, ItemStack stack);
}
