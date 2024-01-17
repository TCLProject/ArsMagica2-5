package am2.items;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemTrumpet extends ArsMagicaItem {

    public ItemTrumpet() {
        setMaxStackSize(1);
    }

    @Override
    public EnumAction getItemUseAction(ItemStack par1ItemStack) {
        return EnumAction.block;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        player.setItemInUse(stack, 70000);
        return stack;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack par1ItemStack) {
        return 70000;
    }

    @Override
    public void onUsingTick(ItemStack stack, EntityPlayer player, int count) {
        if (player != null && player.inventory != null) {
            if (count % 20 == 0 && player.inventory.armorInventory[3] != null && player.inventory.armorInventory[3].getItem() == Items.skull) player.worldObj.playSoundEffect(player.posX, player.posY, player.posZ, "arsmagica2:misc.event.trumpet", 1F, 1F);
            else if((count-80) % 115 == 0) player.worldObj.playSoundEffect(player.posX, player.posY, player.posZ, "arsmagica2:misc.event.trumpet2", 1F, 1F);
        }
    }
}
