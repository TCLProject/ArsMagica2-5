/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package am2.worldgen.smartgen.struct.inventory;

import am2.worldgen.smartgen.registry.MCRegistrySpecial;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.apache.commons.lang3.tuple.Triple;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by lukas on 05.01.15.
 */
public class InventoryGenerationHandler
{
    public static void generateAllTags(IInventory inventory, MCRegistrySpecial.ItemHidingRegistry registry, Random random)
    {
        // TODO: Make custom chest gen logic
        for (int i = 0; i < inventory.getSizeInventory(); i++)
        {
            ItemStack stack = inventory.getStackInSlot(i);

            if (stack != null)
            {
                inventory.setInventorySlotContents(i, null); // clear out the tags
            }
        }
//        List<Triple<ItemStack, GeneratingItem, Integer>> foundGenerators = new ArrayList<>();
//        boolean didChange = true;
//        int cycles = 0;
//
//        do
//        {
//            if (didChange)
//            {
//                for (int i = 0; i < inventory.getSizeInventory(); i++)
//                {
//                    ItemStack stack = inventory.getStackInSlot(i);
//
//                    if (stack != null)
//                    {
//                        Item item = registry.containedItem(stack);
//                        if (item instanceof GeneratingItem)
//                        {
//                            foundGenerators.add(Triple.of(stack, (GeneratingItem) item, i));
//                            inventory.setInventorySlotContents(i, null);
//                        }
//                    }
//                }
//
//                didChange = false;
//            }
//
//            if (foundGenerators.size() > 0)
//            {
//                Triple<ItemStack, GeneratingItem, Integer> pair = foundGenerators.get(0);
//                pair.getMiddle().generateInInventory(inventory, random, pair.getLeft(), pair.getRight());
//
//                foundGenerators.remove(0);
//                didChange = true;
//            }
//
//            cycles++;
//        }
//        while ((foundGenerators.size() > 0 || didChange) && cycles < 1000);
    }
}
