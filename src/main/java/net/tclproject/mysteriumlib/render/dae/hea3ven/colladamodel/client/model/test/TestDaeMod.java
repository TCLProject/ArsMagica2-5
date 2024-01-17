package net.tclproject.mysteriumlib.render.dae.hea3ven.colladamodel.client.model.test;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;

public class TestDaeMod {
    public static BlockPandorasChest PandorasChest;

    public static void pre(FMLPreInitializationEvent e) {
        PandorasChest = new BlockPandorasChest();
        GameRegistry.registerBlock(PandorasChest, "Pandora's Chest");
        GameRegistry.registerTileEntity(TileEntityPandorasChest.class,
                "tileentity.pandoraschest");
    }

    public static void modInit(FMLInitializationEvent event) {
    }
}
