package am2.worldgen.smartgen.struct.inventory;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import java.util.Random;

public interface GeneratingItem
{
    void generateInInventory(IInventory inventory, Random random, ItemStack stack, int fromSlot);
}
