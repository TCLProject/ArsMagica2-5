package net.tclproject.mysteriumlib.render.dae.hea3ven.colladamodel.client.model.test;

import cpw.mods.fml.common.FMLLog;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.Random;

public class TileEntityPandorasChest extends TileEntityBaseChest {

    private int nextSelectedChest;
    public int selectedChest;
    private Random rand = new Random();

    public TileEntityPandorasChest() {
        super(27 * 5, new ResourceLocation("arsmagica2",
                "dae/pandoras_chest_open.dae"), new ResourceLocation(
                "arsmagica2", "dae/pandoras_chest_close.dae"));

        this.nextSelectedChest = 0;
        this.selectedChest = -1;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return this.chestContents[getInventoryOffset() + slot];
    }

    @Override
    protected void setStackInSlot(int slot, ItemStack stack) {
        this.chestContents[getInventoryOffset() + slot] = stack;
    }

    private int getInventoryOffset() {
        return (this.selectedChest != -1) ? this.selectedChest * 27 : this.rand
                .nextInt(5) * 27;
    }

    @Override
    public void openInventory() {
        if (!this.worldObj.isRemote) {
            if (this.numUsingPlayers == 0) {
                this.selectedChest = this.nextSelectedChest;
                this.nextSelectedChest = this.rand.nextInt(5);
            }

            this.worldObj.addBlockEvent(this.xCoord, this.yCoord, this.zCoord,
                    this.getBlockType(), 2, this.selectedChest);
            this.worldObj.addBlockEvent(this.xCoord, this.yCoord, this.zCoord,
                    this.getBlockType(), 3, this.nextSelectedChest);
        } else {
            if (this.selectedChest == -1) {
                this.selectedChest = this.nextSelectedChest;
            }
        }
        super.openInventory();
    }

    @Override
    public void closeInventory() {
        super.closeInventory();
        if (!this.worldObj.isRemote) {
            if (this.numUsingPlayers <= 0) {
                this.selectedChest = -1;
            }

            this.worldObj.addBlockEvent(this.xCoord, this.yCoord, this.zCoord,
                    this.getBlockType(), 2, this.selectedChest);
        }
    }

    @Override
    public boolean isItemValidForSlot(int i, ItemStack itemstack) {
        return true;
    }

    @Override
    public boolean receiveClientEvent(int par1, int par2) {
        if (par1 == 2) {
            if (this.selectedChest != par2)
//                FMLLog.warning(
//                        "SelectedChest received (%s) different from ours (%s).",
//                        par2, this.selectedChest);
            this.selectedChest = par2;
            return true;
        } else if (par1 == 3) {
            this.nextSelectedChest = par2;
            return true;
        } else {
            return super.receiveClientEvent(par1, par2);
        }
    }

    @Override
    protected String getOpenSound() {
        return "random.chestopen";
    }

    @Override
    protected String getCloseSound() {
        return "random.chestclosed";
    }

}