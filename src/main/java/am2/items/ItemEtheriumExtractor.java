package am2.items;

import am2.blocks.BlockWitchwoodLog;
import am2.blocks.BlockWitchwoodLogDrained;
import am2.blocks.BlocksCommonProxy;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;

public class ItemEtheriumExtractor extends ArsMagicaItem {

    public ItemEtheriumExtractor()
    {
        this.maxStackSize = 1;
        this.setMaxDamage(64);
    }

    @Override
    public boolean onItemUse(ItemStack p_77648_1_, EntityPlayer p_77648_2_, World p_77648_3_, int x, int y, int z, int side, float p_77648_8_, float p_77648_9_, float p_77648_10_)
    {
        if (p_77648_2_ instanceof FakePlayer) return false;
        if (p_77648_1_.getItemDamage() % 5 != 0) {
            p_77648_3_.playSoundEffect((double) x + 0.5D, (double) y + 0.5D, (double) z + 0.5D, "dig.wood", 1.0F, itemRand.nextFloat() * 0.4F + 0.8F);
            p_77648_1_.damageItem(1, p_77648_2_);
            return true;
        }

        if (!p_77648_2_.canPlayerEdit(x, y, z, side, p_77648_1_)) {
            return false;
        } else {
            if (p_77648_3_.getBlock(x, y, z) instanceof BlockWitchwoodLog && !(p_77648_3_.getBlock(x, y, z) instanceof BlockWitchwoodLogDrained)) {
                p_77648_1_.damageItem(1, p_77648_2_);
                p_77648_3_.playSoundEffect((double) x + 0.5D, (double) y + 0.5D, (double) z + 0.5D, "liquid.water", 1.0F, itemRand.nextFloat() * 0.4F + 0.8F);
                p_77648_3_.setBlock(x, y, z, BlocksCommonProxy.witchwoodLogDrained);
                fillPlayerEth(p_77648_2_);
            }
            return true;
        }
    }

    private void fillPlayerEth(EntityPlayer player) {
        for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
            if (player.inventory.getStackInSlot(i) != null) {
                if (player.inventory.getStackInSlot(i).getItem() == Items.bucket) {
                    if (player.inventory.getStackInSlot(i).stackSize-- == 1)
                    {
                        player.inventory.setInventorySlotContents(i, new ItemStack(ItemsCommonProxy.itemAMBucket));
                    }
                    else if (!player.inventory.addItemStackToInventory(new ItemStack(ItemsCommonProxy.itemAMBucket)))
                    {
                        player.worldObj.setBlock((int) player.posX, (int) player.posY - 1, (int) player.posZ, BlocksCommonProxy.liquidEssence);
                    }
                    return;
                }
            }
        }
        player.worldObj.setBlock((int) player.posX, (int) player.posY - 1, (int) player.posZ, BlocksCommonProxy.liquidEssence);
    }
}
