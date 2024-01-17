package net.tclproject.mysteriumlib.render.dae.hea3ven.colladamodel.client.model.test;
/**
 * 
 * Copyright (c) 2014 Hea3veN
 * 
 *  This file is part of Pandora's Chest.
 *
 *  Pandora's Chest is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Pandora's Chest is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with Pandora's Chest.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.tclproject.mysteriumlib.render.dae.hea3ven.colladamodel.client.model.lib.AnimationState;

public abstract class TileEntityBaseChest extends TileEntity implements
		IInventory {
	protected ItemStack[] chestContents;

	private String customName;
	private int rotation;

	public int numUsingPlayers;

	private AnimationState animation;

	protected ResourceLocation openAnimation;
	protected ResourceLocation closeAnimation;

	public TileEntityBaseChest(int slotsNum, ResourceLocation openAnimation,
			ResourceLocation closeAnimation) {
		chestContents = new ItemStack[slotsNum];
		numUsingPlayers = 0;
		customName = null;
		rotation = 2;
		animation = null;
		this.openAnimation = openAnimation;
		this.closeAnimation = closeAnimation;
	}

	@Override
	public void setWorldObj(World p_145834_1_) {
		super.setWorldObj(p_145834_1_);
		if (worldObj.isRemote) {
			animation = new AnimationState();
			animation.setAnimation(openAnimation);
			animation.setLockFrame(true);
			animation.setRepeat(false);
		}
	}

	@Override
	public int getSizeInventory() {
		// 5 * 3 * 9 = 135
		return 27;
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return this.chestContents[slot];
	}

	protected void setStackInSlot(int slot, ItemStack stack) {
		this.chestContents[slot] = stack;
	}

	@Override
	public ItemStack decrStackSize(int slot, int amount) {

		ItemStack itemStack = getStackInSlot(slot);
		if (itemStack != null) {

			if (itemStack.stackSize <= amount) {
				setStackInSlot(slot, null);
				this.markDirty();
				return itemStack;
			} else {
				ItemStack returnItemStack = itemStack.splitStack(amount);

				if (itemStack.stackSize == 0)
					setStackInSlot(slot, null);

				this.markDirty();
				return returnItemStack;
			}
		} else {
			return null;
		}
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {
		ItemStack itemStack = getStackInSlot(slot);
		if (itemStack != null) {
			setStackInSlot(slot, null);
			return itemStack;
		} else {
			return null;
		}
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack itemStack) {
		setStackInSlot(slot, itemStack);

		if (itemStack != null
				&& itemStack.stackSize > this.getInventoryStackLimit()) {
			itemStack.stackSize = this.getInventoryStackLimit();
		}

		this.markDirty();
	}

	@Override
	public String getInventoryName() {
		return this.hasCustomInventoryName() ? this.customName : blockType
				.getLocalizedName();
	}

	@Override
	public boolean hasCustomInventoryName() {
		return this.customName != null && this.customName.length() > 0;
	}

	public void setChestGuiName(String par1Str) {
		this.customName = par1Str;
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer entityplayer) {
		return this.worldObj.getTileEntity(this.xCoord, this.yCoord,
				this.zCoord) != this ? false : entityplayer.getDistanceSq(
				(double) this.xCoord + 0.5D, (double) this.yCoord + 0.5D,
				(double) this.zCoord + 0.5D) <= 64.0D;
	}

	protected abstract String getOpenSound();

	protected abstract String getCloseSound();

	@Override
	public void openInventory() {
		if (!this.worldObj.isRemote) {
			if (this.numUsingPlayers < 0)
				this.numUsingPlayers = 0;

			if (numUsingPlayers == 0)
				worldObj.playSoundEffect((double) xCoord + 0.5D,
						(double) yCoord + 0.5D, (double) zCoord + 0.5D,
						getOpenSound(), 0.5F,
						worldObj.rand.nextFloat() * 0.1F + 0.9F);

			++this.numUsingPlayers;
			this.worldObj.addBlockEvent(this.xCoord, this.yCoord, this.zCoord,
					this.getBlockType(), 1, this.numUsingPlayers);
			this.worldObj.notifyBlocksOfNeighborChange(this.xCoord,
					this.yCoord, this.zCoord, this.getBlockType());
			this.worldObj.notifyBlocksOfNeighborChange(this.xCoord,
					this.yCoord - 1, this.zCoord, this.getBlockType());
		}
	}

	@Override
	public void closeInventory() {
		if (!this.worldObj.isRemote) {
			--this.numUsingPlayers;

			if (numUsingPlayers == 0)
				worldObj.playSoundEffect((double) xCoord + 0.5D,
						(double) yCoord + 0.5D, (double) zCoord + 0.5D,
						getCloseSound(), 0.5F,
						worldObj.rand.nextFloat() * 0.1F + 0.9F);

			this.worldObj.notifyBlocksOfNeighborChange(this.xCoord,
					this.yCoord, this.zCoord, this.getBlockType());
			this.worldObj.addBlockEvent(this.xCoord, this.yCoord, this.zCoord,
					this.getBlockType(), 1, this.numUsingPlayers);
			this.worldObj.notifyBlocksOfNeighborChange(this.xCoord,
					this.yCoord - 1, this.zCoord, this.getBlockType());
		}
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		return true;
	}

	private boolean lastClicked = false;

	@Override
	public boolean receiveClientEvent(int par1, int par2) {
		if (par1 == 1) {
			if (worldObj.isRemote) {
				if (!lastClicked && par2 > 0) {
					animation.setAnimation(openAnimation);
					animation.setLockFrame(false);
					this.lastClicked = true;
				}
				else if (lastClicked) {
					animation.setAnimation(closeAnimation);
					animation.setLockFrame(false);
					this.lastClicked = false;
				}
			}
			numUsingPlayers = par2;
			return true;
		} else {
			return super.receiveClientEvent(par1, par2);
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound par1NBTTagCompound) {
		super.readFromNBT(par1NBTTagCompound);
		NBTTagList nbttaglist = par1NBTTagCompound.getTagList("Items", 10);

		if (par1NBTTagCompound.hasKey("CustomName")) {
			this.customName = par1NBTTagCompound.getString("CustomName");
		}

		this.rotation = par1NBTTagCompound.getInteger("Rotation");

		for (int i = 0; i < nbttaglist.tagCount(); ++i) {
			NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
			int j = nbttagcompound1.getByte("Slot") & 255;

			if (j >= 0 && j < this.chestContents.length) {
				this.chestContents[j] = ItemStack
						.loadItemStackFromNBT(nbttagcompound1);
			}
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound par1NBTTagCompound) {
		super.writeToNBT(par1NBTTagCompound);
		NBTTagList nbttaglist = new NBTTagList();

		for (int i = 0; i < this.chestContents.length; ++i) {
			if (this.chestContents[i] != null) {
				NBTTagCompound nbttagcompound1 = new NBTTagCompound();
				nbttagcompound1.setByte("Slot", (byte) i);
				this.chestContents[i].writeToNBT(nbttagcompound1);
				nbttaglist.appendTag(nbttagcompound1);
			}
		}

		par1NBTTagCompound.setTag("Items", nbttaglist);

		if (this.hasCustomInventoryName()) {
			par1NBTTagCompound.setString("CustomName", this.customName);
		}

		par1NBTTagCompound.setInteger("Rotation", this.rotation);
	}

	@Override
	public void updateEntity() {
		if (this.worldObj.isRemote) {
			if (!animation.addFrame()) {
				if (animation.getResource().equals(openAnimation)) {
					animation.setAnimation(closeAnimation);
					animation.setLockFrame(true);
				} else {
					animation.setAnimation(openAnimation);
					animation.setLockFrame(true);
				}
			}
		}
	}

	public AnimationState getAnimationState() {
		return animation;
	}

	public void setRotation(int rotation) {
		this.rotation = rotation;
	}

	public int getRotation() {
		return rotation;
	}

	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound nbttagcompound = new NBTTagCompound();
		this.writeToNBT(nbttagcompound);
		return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord,
				this.zCoord, 3, nbttagcompound);
	}

	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
		this.readFromNBT(pkt.func_148857_g());
		super.onDataPacket(net, pkt);
	}
}
