package am2.blocks;

import am2.blocks.tileentities.TileEntityTickingAir;
import am2.worldgen.smartgen.reccomplexutils.IBlockState;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;

public class BlockTickingAir extends BlockContainer {
    protected BlockTickingAir() {
        super(Material.ground);
        this.setHardness(1f);
        this.setResistance(1f);
        this.setBlockTextureName("arsmagica2:empty");
        this.setStepSound(Block.soundTypeGrass);
    }

    @Override
    public boolean isOpaqueCube()
    {
        return false;
    }

    @Override
    public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) {
        return new TileEntityTickingAir();
    }

    @Override
    public void onBlockPlacedBy(World wrld, int x, int y, int z, EntityLivingBase pl, ItemStack p_149689_6_) {
        super.onBlockPlacedBy(wrld, x, y, z, pl, p_149689_6_);
        TileEntity te = wrld.getTileEntity(x, y, z);
        if (te != null && te instanceof TileEntityTickingAir && pl instanceof EntityPlayer && !(pl instanceof FakePlayer)) {
            ((TileEntityTickingAir)te).noDisappear = true;
        }
    }
}
