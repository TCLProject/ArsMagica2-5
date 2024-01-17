package net.tclproject.mysteriumlib.render.gecko;


import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.tclproject.mysteriumlib.render.gecko.common.entities.KeyboardHandler;
import net.tclproject.mysteriumlib.render.gecko.listener.ModEventBus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * GeckoLib is an animation library made by bernie-g.
 * It has originally been ported to 1.7.10 by Icosider - full credit to him for that.
 * https://github.com/Icosider/Gecko
 * Changes, improvements and fixes were made on top by TCLProject.
 *
 * Various classes and assets from IceAndFire by AlexModGuy
 * https://github.com/AlexModGuy/Ice_and_Fire
 * are also included.
 *
 * Modifications were made. If one wishes to copy paste these classes
 * into their own mod or use as part of mysteriumlib, one would need
 * to rename 'arsmagica2' references to their modid, as well as getting
 * rid of almost everything in common/entities package (that's where the
 * specific entities are located) and EntityReg (where they are registered).
 * Though, some things in common/entities could be useful for everyone -
 * - take a look for yourself.
 *
 * LLibrary is only a requirement of some specific entities in common/entities,
 * and is not strictly required for the functioning of this Geckolib port.
 * */
public class GeckoLib
{
    public static final Logger LOGGER = LogManager.getLogger();

    public static Item spawn_egg;
    private static ModContainer container;

    public static ModContainer getModContainer() {
        if (container == null) {
            container = FMLCommonHandler.instance().findContainerFor("arsmagica2");
        }

        return container;
    }

    public static void pre(FMLPreInitializationEvent e) {
        final KeyboardHandler keyboardHandler = new KeyboardHandler();
        MinecraftForge.EVENT_BUS.register(keyboardHandler);
        FMLCommonHandler.instance().bus().register(keyboardHandler);

        ModEventBus.registerEntities();
        if (e.getSide() == Side.CLIENT) {
            ModEventBus.registerRenderers();
        }
    }

    public static void init(FMLInitializationEvent e) {
        EntityReg.INSTANCE.onInit();
        spawn_egg = new ItemSpawnEgg();
        GameRegistry.registerItem(spawn_egg, "spawnEgg");
    }
}