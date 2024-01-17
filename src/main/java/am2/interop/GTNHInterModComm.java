package am2.interop;

import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.nbt.NBTTagCompound;

public class GTNHInterModComm {

    public static void activate() {
        sendHandler("am2.interop.RecipeHandlerEssence", "arsmagica2:essenceRefiner");
        sendCatalyst("arsmagica2.essenceRefiner", "arsmagica2:essenceRefiner");
        sendHandler("am2.interop.RecipeHandlerEnervator", "arsmagica2:entropicEvervator");
        sendCatalyst("arsmagica2.entropicEvervator", "arsmagica2:entropicEvervator");
    }

    /*
     * These were copied from GTNewHorizons/GoodGenerator (Fork of GlodBlock/GoodGenerator)
     * Author: GlodBlock
     */

    private static void sendHandler(String aName, String aBlock) {
        sendHandler(aName, aBlock, 1);
    }

    private static void sendHandler(String aName, String aBlock, int maxRecipesPerPage) {
        NBTTagCompound aNBT = new NBTTagCompound();
        aNBT.setString("handler", aName);
        aNBT.setString("modName", "Ars Magica 2");
        aNBT.setString("modId", "arsmagica2");
        aNBT.setBoolean("modRequired", true);
        aNBT.setString("itemName", aBlock);
        aNBT.setInteger("handlerHeight", 65);
        aNBT.setInteger("handlerWidth", 166);
        aNBT.setInteger("maxRecipesPerPage", maxRecipesPerPage);
        aNBT.setInteger("yShift", 6);
        FMLInterModComms.sendMessage("NotEnoughItems", "registerHandlerInfo", aNBT);
    }

    private static void sendCatalyst(String aName, String aStack, int aPriority) {
        NBTTagCompound aNBT = new NBTTagCompound();
        aNBT.setString("handlerID", aName);
        aNBT.setString("itemName", aStack);
        aNBT.setInteger("priority", aPriority);
        FMLInterModComms.sendMessage("NotEnoughItems", "registerCatalystInfo", aNBT);
    }

    private static void sendCatalyst(String aName, String aStack) {
        sendCatalyst(aName, aStack, 0);
    }
}
