package am2.blocks.tileentities.presets;

import am2.containers.slots.AM2Container;
import am2.network.AMDataWriter;
import am2.network.AMNetHandler;
import am2.network.AMPacketIDs;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.AxisAlignedBB;

import java.util.List;

/**Though this class is not abstract, it is not to be used directly but extended as you would other preset classes.*/
public class SimpleSyncedContainer extends AM2Container {

    public SimpleSyncedTE syncedTile;
    private String lastString;

    protected SimpleSyncedContainer(EntityPlayer player, SimpleSyncedTE p_i1812_2_) {
        this.syncedTile = p_i1812_2_;
        this.addPlayerInventory(player, 8, 84);
        this.addPlayerActionBar(player, 8, 143);
    }

    public boolean canInteractWith(EntityPlayer p_75145_1_)
    {
        return this.syncedTile.isUseableByPlayer(p_75145_1_);
    }

    public void addCraftingToCrafters(ICrafting pl) {
        super.addCraftingToCrafters(pl);
        sendUpdatePackets();
    }

    private void sendUpdatePackets() {
        if (!syncedTile.getWorldObj().isRemote) {
            AMDataWriter writer = new AMDataWriter();
            writer.add(syncedTile.xCoord);
            writer.add(syncedTile.yCoord);
            writer.add(syncedTile.zCoord);
            writer.add(syncedTile.syncedString);
            AMNetHandler.INSTANCE.sendPacketToAllClientsNear(syncedTile.getWorldObj().provider.dimensionId, syncedTile.xCoord, syncedTile.yCoord, syncedTile.zCoord, 64, AMPacketIDs.SYNCEDSTRING_SYNC, writer.generate());
        }
    }

    /**
     * Looks for changes made in the container, sends them to every listener.
     */
    public void detectAndSendChanges()
    {
        super.detectAndSendChanges();

        if (!syncedTile.syncedString.equalsIgnoreCase(lastString))
        {
            sendUpdatePackets();
        }
        this.lastString = syncedTile.syncedString;
    }
}
