package am2.blocks.tileentities.presets;

import am2.network.AMDataWriter;
import am2.network.AMNetHandler;
import am2.network.AMPacketIDs;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.Constants;

public abstract class SimpleSyncedTE extends TileEntity implements IInventory {

    protected ItemStack[] inventory;
    // format: varname:varvalue,varname2:varvalue2,varname3:varvalue3,
    protected String syncedString;
    protected String lastSyncedString;
    private String customName;

    protected SimpleSyncedTE() {
        syncedString = "";
        lastSyncedString = "";
        inventory = new ItemStack[getSizeInventory()];
    }

    // Synced string methods

    public String getSyncedVar(String name) {
        if (hasSyncedVar(name)) return syncedString.substring(syncedString.indexOf(name) + name.length() + 1, syncedString.indexOf(",", syncedString.indexOf(name) + name.length() + 1));
        else return "";
    }

    public void updateEntity() {
        super.updateEntity();
        if (!worldObj.isRemote) {
            if (!syncedString.equalsIgnoreCase(lastSyncedString)) {
                sendUpdatePackets();
                this.lastSyncedString = syncedString;
            }
        }
    }

    private void sendUpdatePackets() {
        AMDataWriter writer = new AMDataWriter();
        writer.add(xCoord);
        writer.add(yCoord);
        writer.add(zCoord);
        writer.add(syncedString);
        AMNetHandler.INSTANCE.sendPacketToAllClientsNear(worldObj.provider.dimensionId, xCoord, yCoord, zCoord, 64, AMPacketIDs.SYNCEDSTRING_SYNC, writer.generate());
    }

    @SideOnly(Side.CLIENT)
    public void updateSyncedString(String synced)
    {
        this.syncedString = synced;
    }

    public void addSyncedVar(String name, String value) {
        syncedString += (name + ":" + value + ",");
    }

    public void removeSyncedVar(String name, String value) {
        syncedString = syncedString.replace(name + ":" + value + ",", "");
    }

    public boolean hasSyncedVar(String name) {
        return syncedString.indexOf(name) != -1;
    }

    public void clearSyncedVars() {
        syncedString = "";
    }

    // Need to be implemented in children block

    public abstract int getSizeInventory();
    public abstract String getCustomInventoryName();
    public abstract int getInventoryStackLimit();

    // End block

    public String getInventoryName()
    {
        return this.hasCustomInventoryName() ? this.customName : getCustomInventoryName();
    }

    @Override
    public void readFromNBT(NBTTagCompound nbttagcompound){
        super.readFromNBT(nbttagcompound);
        NBTTagList nbttaglist = nbttagcompound.getTagList("SyncedInv", Constants.NBT.TAG_COMPOUND);
        inventory = new ItemStack[getSizeInventory()];
        for (int i = 0; i < nbttaglist.tagCount(); i++){
            String tag = String.format("ArrayIndex", i);
            NBTTagCompound nbttagcompound1 = (NBTTagCompound)nbttaglist.getCompoundTagAt(i);
            byte byte0 = nbttagcompound1.getByte(tag);
            if (byte0 >= 0 && byte0 < inventory.length){
                inventory[byte0] = ItemStack.loadItemStackFromNBT(nbttagcompound1);
            }
        }

        this.syncedString = nbttagcompound.getString("SyncedString");

        if (nbttagcompound.hasKey("CustomName", 8))
        {
            this.customName = nbttagcompound.getString("CustomName");
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbttagcompound){
        super.writeToNBT(nbttagcompound);
        NBTTagList nbttaglist = new NBTTagList();
        for (int i = 0; i < inventory.length; i++){
            if (inventory[i] != null){
                String tag = String.format("ArrayIndex", i);
                NBTTagCompound nbttagcompound1 = new NBTTagCompound();
                nbttagcompound1.setByte(tag, (byte)i);
                inventory[i].writeToNBT(nbttagcompound1);
                nbttaglist.appendTag(nbttagcompound1);
            }
        }

        nbttagcompound.setTag("SyncedInv", nbttaglist);

        nbttagcompound.setString("SyncedString", syncedString);

        if (this.hasCustomInventoryName())
        {
            nbttagcompound.setString("CustomName", this.customName);
        }
    }

    public boolean hasCustomInventoryName() {
        return this.customName != null && this.customName.length() > 0;
    }

    @Override
    public void openInventory(){
    }

    @Override
    public void closeInventory(){
    }

    @Override
    public boolean isItemValidForSlot(int i, ItemStack itemstack){
        return false;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer entityplayer){
        if (worldObj.getTileEntity(xCoord, yCoord, zCoord) != this){
            return false;
        }

        return entityplayer.getDistanceSq(xCoord + 0.5D, yCoord + 0.5D, zCoord + 0.5D) <= 64D;
    }

    @Override
    public Packet getDescriptionPacket(){
        NBTTagCompound compound = new NBTTagCompound();
        this.writeToNBT(compound);
        S35PacketUpdateTileEntity packet = new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, worldObj.getBlockMetadata(xCoord, yCoord, zCoord), compound);
        return packet;
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt){
        this.readFromNBT(pkt.func_148857_g());
    }

    @Override
    public ItemStack getStackInSlot(int slot){
        if (slot >= inventory.length)
            return null;
        return inventory[slot];
    }

    @Override
    public ItemStack decrStackSize(int i, int j){
        if (inventory[i] != null){
            if (inventory[i].stackSize <= j){
                ItemStack itemstack = inventory[i];
                inventory[i] = null;
                return itemstack;
            }
            ItemStack itemstack1 = inventory[i].splitStack(j);
            if (inventory[i].stackSize == 0){
                inventory[i] = null;
            }
            return itemstack1;
        }else{
            return null;
        }
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int i){
        if (inventory[i] != null){
            ItemStack itemstack = inventory[i];
            inventory[i] = null;
            return itemstack;
        }else{
            return null;
        }
    }

    @Override
    public void setInventorySlotContents(int i, ItemStack itemstack){
        inventory[i] = itemstack;
        if (itemstack != null && itemstack.stackSize > getInventoryStackLimit()){
            itemstack.stackSize = getInventoryStackLimit();
        }
    }

    public void setCustomInvName(String displayName) {
        this.customName = displayName;
    }
}
