package net.tclproject.mysteriumlib.render.dae.hea3ven.colladamodel.client.model.test;

import am2.blocks.BlocksClientProxy;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class BlockPandorasChest extends BlockBaseChest {

    public BlockPandorasChest() {
        super();
        setBlockName("pandorasChest");
        setBlockTextureName("arsmagica2:pandoras_chest");
        setCreativeTab(CreativeTabs.tabBlock);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int metadata) {
        TileEntityPandorasChest tileEntity = new TileEntityPandorasChest();
        return tileEntity;
    }

    @Override
    public int getRenderType() {
        return BlocksClientProxy.blockRenderID;
    }

    @Override
    public int getGuiId() {
        return 1;
    }

    @Override
    public ResourceLocation getTexture(int meta) {
        return new ResourceLocation("arsmagica2", "textures/blocks/pandoras_chest.png");
    }

}
