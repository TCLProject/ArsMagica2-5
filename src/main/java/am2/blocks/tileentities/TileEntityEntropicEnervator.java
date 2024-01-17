package am2.blocks.tileentities;

import am2.EnervatorRecipeHelper;
import am2.api.power.PowerTypes;
import am2.network.AMDataReader;
import am2.network.AMDataWriter;
import am2.network.AMNetHandler;
import am2.network.AMPacketIDs;
import am2.proxy.ClientProxy;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.util.Constants;

import java.util.List;

public class TileEntityEntropicEnervator extends TileEntityAMPower implements IInventory{
	private ItemStack[] inventory;

	public int rendTimeRemaining = 0;
	public int maxRendTime = 140;

	private static final byte PK_BURNTIME_CHANGE = 1;

	public TileEntityEntropicEnervator(){
		super(1000);
		inventory = new ItemStack[this.getSizeInventory()];
	}

	public boolean isActive(){
		return rendTimeRemaining > 0;
	}

	private void setMaxRendTime(int rendTime){
		if (rendTime == 0)
			rendTime = 140;
		maxRendTime = rendTime;
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

	public int getRendProgressScaled(int par1){
		return this.rendTimeRemaining * par1 / maxRendTime;
	}

	private void setActiveTexture(){
		if (isActive()){
			if (!worldObj.isRemote && (worldObj.getBlockMetadata(xCoord, yCoord, zCoord) & 0x8) != 0x8){
				worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, worldObj.getBlockMetadata(xCoord, yCoord, zCoord) | 0x8, 2);
			}
		}else{
			if (!worldObj.isRemote && (worldObj.getBlockMetadata(xCoord, yCoord, zCoord) & 0x8) == 0x8){
				worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, worldObj.getBlockMetadata(xCoord, yCoord, zCoord) & ~0x8, 2);
			}
		}
	}

	private void sendEnervatorUpdateToClient(){
		if (!worldObj.isRemote){
			NBTTagCompound nbt = new NBTTagCompound();
			this.writeToNBT(nbt);
			AMNetHandler.INSTANCE.sendPacketToAllClientsNear(this.worldObj.provider.dimensionId, this.xCoord, this.yCoord, this.zCoord, 100, AMPacketIDs.ENERVATOR_BLOCK_UPDATE, new AMDataWriter().add(this.xCoord).add(this.yCoord).add(this.zCoord).add(nbt).generate());
		}
	}

	@Override
	public void updateEntity(){

		if (isActive()){
			rendTimeRemaining--;

			if (rendTimeRemaining == 0) {
				if (!worldObj.isRemote){
					this.setInventorySlotContents(0, EnervatorRecipeHelper.instance.getRecipe(this.inventory[0]));
				}
				setActiveTexture();
			}
			sendEnervatorUpdateToClient();
		} else {
			if (EnervatorRecipeHelper.instance.getRecipe(this.inventory[0]) != null) {
				rendTimeRemaining = 140;
				setActiveTexture();
				if (!worldObj.isRemote) sendEnervatorUpdateToClient();
			} else{
				rendTimeRemaining = 0;
				setActiveTexture();
			}
		}

		super.updateEntity();
	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound){
		super.writeToNBT(nbttagcompound);
		nbttagcompound.setInteger("rendTimeRemaining", rendTimeRemaining);
		nbttagcompound.setInteger("maxRendTime", maxRendTime);

		if (inventory != null){
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

			nbttagcompound.setTag("BurnInventory", nbttaglist);
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound){
		super.readFromNBT(nbttagcompound);
		rendTimeRemaining = nbttagcompound.getInteger("rendTimeRemaining");
		setMaxRendTime(nbttagcompound.getInteger("maxRendTime"));

		if (nbttagcompound.hasKey("BurnInventory")){
			NBTTagList nbttaglist = nbttagcompound.getTagList("BurnInventory", Constants.NBT.TAG_COMPOUND);
			inventory = new ItemStack[getSizeInventory()];
			for (int i = 0; i < nbttaglist.tagCount(); i++){
				String tag = String.format("ArrayIndex", i);
				NBTTagCompound nbttagcompound1 = (NBTTagCompound)nbttaglist.getCompoundTagAt(i);
				byte byte0 = nbttagcompound1.getByte(tag);
				if (byte0 >= 0 && byte0 < inventory.length){
					inventory[byte0] = ItemStack.loadItemStackFromNBT(nbttagcompound1);
				}
			}
		}
	}

	@Override
	public int getChargeRate(){
		return 500;
	}

	@Override
	public PowerTypes[] getValidPowerTypes(){
		return new PowerTypes[]{PowerTypes.DARK};
	}

	@Override
	public boolean canRelayPower(PowerTypes type){
		return false;
	}

	@Override
	public int getSizeInventory(){
		return 1;
	}

	@Override
	public ItemStack getStackInSlot(int i){
		if (i < 0 || i >= this.getSizeInventory())
			return null;
		return inventory[i];
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

	@Override
	public String getInventoryName(){
		return "entropic_enervator";
	}

	@Override
	public boolean hasCustomInventoryName(){
		return false;
	}

	@Override
	public int getInventoryStackLimit(){
		return 1;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer entityplayer){
		if (worldObj.getTileEntity(xCoord, yCoord, zCoord) != this){
			return false;
		}
		return entityplayer.getDistanceSq(xCoord + 0.5D, yCoord + 0.5D, zCoord + 0.5D) <= 64D;
	}

	@Override
	public void openInventory(){
	}

	@Override
	public void closeInventory(){
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack){
		return EnervatorRecipeHelper.instance.getRecipe(itemstack) != null;
	}
}
