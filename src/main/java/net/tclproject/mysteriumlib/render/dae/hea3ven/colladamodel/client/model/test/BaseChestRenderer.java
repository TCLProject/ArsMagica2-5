package net.tclproject.mysteriumlib.render.dae.hea3ven.colladamodel.client.model.test;

import am2.blocks.BlocksClientProxy;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;

public class BaseChestRenderer extends TileEntitySpecialRenderer implements
        ISimpleBlockRenderingHandler {

    private ModelBaseChest model;

    public BaseChestRenderer(String openAnimationResourceName,
                             String closeAnimationResourceName) {
        model = new ModelBaseChest(new ResourceLocation(
                openAnimationResourceName));
    }

    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelId,
                                     RenderBlocks renderer) {
        bindTexture(((BlockBaseChest) block).getTexture(metadata));
        model.renderItem();
    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z,
                                    Block block, int modelId, RenderBlocks renderer) {
        return false;
    }

    @Override
    public boolean shouldRender3DInInventory(int modelId) {
        return true;
    }

    @Override
    public int getRenderId() {
        return BlocksClientProxy.blockRenderID;
    }

    @Override
    public void renderTileEntityAt(TileEntity tileEntity, double x, double y,
                                   double z, float f) {
        TileEntityBaseChest te = (TileEntityBaseChest) tileEntity;
        bindTexture(((BlockBaseChest) tileEntity.blockType)
                .getTexture(tileEntity.getBlockMetadata()));

        model.render(te, x, y, z);
    }

}