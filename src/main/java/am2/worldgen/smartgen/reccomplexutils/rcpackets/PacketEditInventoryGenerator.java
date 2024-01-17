/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package am2.worldgen.smartgen.reccomplexutils.rcpackets;

import am2.worldgen.smartgen.struct.inventory.GenericItemCollection;
import am2.worldgen.smartgen.struct.inventory.GenericItemCollectionRegistry;
import am2.worldgen.smartgen.struct.inventory.InventoryLoadException;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;

/**
 * Created by lukas on 03.08.14.
 */
public class PacketEditInventoryGenerator implements IMessage
{
    private String key;
    private GenericItemCollection.Component inventoryGenerator;

    public PacketEditInventoryGenerator()
    {
    }

    public PacketEditInventoryGenerator(String key, GenericItemCollection.Component inventoryGenerator)
    {
        this.key = key;
        this.inventoryGenerator = inventoryGenerator;
    }

    public String getKey()
    {
        return key;
    }

    public void setKey(String key)
    {
        this.key = key;
    }

    public GenericItemCollection.Component getInventoryGenerator()
    {
        return inventoryGenerator;
    }

    public void setInventoryGenerator(GenericItemCollection.Component inventoryGenerator)
    {
        this.inventoryGenerator = inventoryGenerator;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        key = ByteBufUtils.readUTF8String(buf);
        String json = ByteBufUtils.readUTF8String(buf);

        try
        {
            inventoryGenerator = GenericItemCollectionRegistry.INSTANCE.createComponentFromJSON(json);
        }
        catch (InventoryLoadException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        ByteBufUtils.writeUTF8String(buf, key);
        String json = GenericItemCollectionRegistry.INSTANCE.createJSONFromComponent(inventoryGenerator);
        ByteBufUtils.writeUTF8String(buf, json);
    }
}
