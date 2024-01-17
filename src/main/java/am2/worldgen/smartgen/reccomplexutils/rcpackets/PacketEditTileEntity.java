/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package am2.worldgen.smartgen.reccomplexutils.rcpackets;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

/**
 * Created by lukas on 03.08.14.
 */
public class PacketEditTileEntity extends PacketFullTileEntityData
{
    public PacketEditTileEntity()
    {
    }

    public <TE extends TileEntity> PacketEditTileEntity(TE tileEntity)
    {
        super(tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord, getSyncedNBT(tileEntity));
    }

    private static NBTTagCompound getSyncedNBT(TileEntity structureGenerator)
    {
        NBTTagCompound compound = new NBTTagCompound();
        structureGenerator.writeToNBT(compound);
        return compound;
    }
}
