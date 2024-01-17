package am2.blocks.tileentities.presets;

import am2.AMCore;
import am2.blocks.AMBlockContainer;
import am2.blocks.tileentities.TileEntityArcaneDeconstructor;
import am2.guis.ArsMagicaGuiIdList;
import cpw.mods.fml.common.network.internal.FMLNetworkHandler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public abstract class SimpleSyncedBlock extends AMBlockContainer {
    protected SimpleSyncedBlock(Material mat) {
        super(mat);
    }

    // Methods to be implemented in children block

    public abstract <V extends SimpleSyncedTE> V createNewTile(World world, int i);
    public abstract int getGuiID(); // generally from ArsMagicaGuiIdList

    // End block

    @Override
    public TileEntity createNewTileEntity(World world, int i) {
        return createNewTile(world, i);
    }

    public boolean onBlockActivated(World par1World, int par2, int par3, int par4, EntityPlayer par5EntityPlayer, int par6, float par7, float par8, float par9)
    {
        super.onBlockActivated(par1World, par2, par3, par4, par5EntityPlayer, par6, par7, par8, par9);
        if (!par1World.isRemote){
            FMLNetworkHandler.openGui(par5EntityPlayer, AMCore.instance, getGuiID(), par1World, par2, par3, par4);
			/*
			if (KeystoneUtilities.HandleKeystoneRecovery(par5EntityPlayer, ((IKeystoneLockable)par1World.getTileEntity(par2, par3, par4))))
				return true;
			if (KeystoneUtilities.instance.canPlayerAccess((IKeystoneLockable)par1World.getTileEntity(par2, par3, par4), par5EntityPlayer, KeystoneAccessType.USE)){
				super.onBlockActivated(par1World, par2, par3, par4, par5EntityPlayer, par6, par7, par8, par9);
				FMLNetworkHandler.openGui(par5EntityPlayer, AMCore.instance, getGuiID(), par1World, par2, par3, par4);
			}
			*/
        }
        return true;
    }

    public void onBlockAdded(World p_149726_1_, int p_149726_2_, int p_149726_3_, int p_149726_4_)
    {
        super.onBlockAdded(p_149726_1_, p_149726_2_, p_149726_3_, p_149726_4_);
        this.changeRotationBasedOnNeighbors(p_149726_1_, p_149726_2_, p_149726_3_, p_149726_4_);
    }

    @Override
    public void breakBlock(World world, int i, int j, int k, Block par5, int metadata){
        SimpleSyncedTE tile = (SimpleSyncedTE)world.getTileEntity(i, j, k);
        if (tile == null) return;
        for (int l = 0; l < tile.getSizeInventory() - 3; l++){
            ItemStack itemstack = tile.getStackInSlot(l);
            if (itemstack == null){
                continue;
            }
            float f = world.rand.nextFloat() * 0.8F + 0.1F;
            float f1 = world.rand.nextFloat() * 0.8F + 0.1F;
            float f2 = world.rand.nextFloat() * 0.8F + 0.1F;
            do{
                if (itemstack.stackSize <= 0){
                    break;
                }
                int i1 = world.rand.nextInt(21) + 10;
                if (i1 > itemstack.stackSize){
                    i1 = itemstack.stackSize;
                }
                itemstack.stackSize -= i1;
                ItemStack newItem = new ItemStack(itemstack.getItem(), i1, itemstack.getItemDamage());
                newItem.setTagCompound(itemstack.getTagCompound());
                EntityItem entityitem = new EntityItem(world, i + f, j + f1, k + f2, newItem);
                float f3 = 0.05F;
                entityitem.motionX = (float)world.rand.nextGaussian() * f3;
                entityitem.motionY = (float)world.rand.nextGaussian() * f3 + 0.2F;
                entityitem.motionZ = (float)world.rand.nextGaussian() * f3;
                world.spawnEntityInWorld(entityitem);
            }while (true);

        }
        super.breakBlock(world, i, j, k, par5, metadata);
    }

    public void onBlockPlacedBy(World p_149689_1_, int p_149689_2_, int p_149689_3_, int p_149689_4_, EntityLivingBase p_149689_5_, ItemStack p_149689_6_)
    {
        int l = MathHelper.floor_double((double)(p_149689_5_.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;

        if (l == 0)
        {
            p_149689_1_.setBlockMetadataWithNotify(p_149689_2_, p_149689_3_, p_149689_4_, 2, 2);
        }

        if (l == 1)
        {
            p_149689_1_.setBlockMetadataWithNotify(p_149689_2_, p_149689_3_, p_149689_4_, 5, 2);
        }

        if (l == 2)
        {
            p_149689_1_.setBlockMetadataWithNotify(p_149689_2_, p_149689_3_, p_149689_4_, 3, 2);
        }

        if (l == 3)
        {
            p_149689_1_.setBlockMetadataWithNotify(p_149689_2_, p_149689_3_, p_149689_4_, 4, 2);
        }

        if (p_149689_6_.hasDisplayName())
        {
            ((SimpleSyncedTE)p_149689_1_.getTileEntity(p_149689_2_, p_149689_3_, p_149689_4_)).setCustomInvName(p_149689_6_.getDisplayName());
        }
    }

    private void changeRotationBasedOnNeighbors(World p_149930_1_, int p_149930_2_, int p_149930_3_, int p_149930_4_)
    {
        if (!p_149930_1_.isRemote)
        {
            Block block = p_149930_1_.getBlock(p_149930_2_, p_149930_3_, p_149930_4_ - 1);
            Block block1 = p_149930_1_.getBlock(p_149930_2_, p_149930_3_, p_149930_4_ + 1);
            Block block2 = p_149930_1_.getBlock(p_149930_2_ - 1, p_149930_3_, p_149930_4_);
            Block block3 = p_149930_1_.getBlock(p_149930_2_ + 1, p_149930_3_, p_149930_4_);
            byte b0 = 3;

            if (block.func_149730_j() && !block1.func_149730_j())
            {
                b0 = 3;
            }

            if (block1.func_149730_j() && !block.func_149730_j())
            {
                b0 = 2;
            }

            if (block2.func_149730_j() && !block3.func_149730_j())
            {
                b0 = 5;
            }

            if (block3.func_149730_j() && !block2.func_149730_j())
            {
                b0 = 4;
            }

            p_149930_1_.setBlockMetadataWithNotify(p_149930_2_, p_149930_3_, p_149930_4_, b0, 2);
        }
    }
}
