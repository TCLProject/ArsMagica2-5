package am2.blocks.tileentities.presets;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public abstract class SimpleSyncedGui extends GuiContainer {

    private SimpleSyncedTE desyncedTile;
    protected final ResourceLocation[] guiTextures; // all the GUI textures needed for this GUI, the first one always being the GUI's overarching inventory texture

    /**guiTextureArray's first texture must be the GUI's overarching inventory texture*/
    protected SimpleSyncedGui(EntityPlayer p_i1091_1_, SimpleSyncedTE p_i1091_2_, ResourceLocation[] guiTextureArray)
    {
        super(new SimpleSyncedContainer(p_i1091_1_, p_i1091_2_));
        this.desyncedTile = p_i1091_2_;
        this.guiTextures = guiTextureArray;
    }

    /** use THIS (actualTile) instance of the tileentity, not this.desyncedTile, otherwise desyncs occur! */
    protected abstract void drawGuiContainerForeground(int p_146979_1_, int p_146979_2_, SimpleSyncedTE actualTile);
    /** use THIS (actualTile) instance of the tileentity, not this.desyncedTile, otherwise desyncs occur! */
    protected abstract void drawGuiContainerBackground(float p_146976_1_, int p_146976_2_, int p_146976_3_, SimpleSyncedTE actualTile);

    protected void drawGuiContainerForegroundLayer(int p_146979_1_, int p_146979_2_)
    {
        TileEntity currentTile = this.desyncedTile.getWorldObj().getTileEntity(this.desyncedTile.xCoord, this.desyncedTile.yCoord, this.desyncedTile.zCoord);
        if (currentTile instanceof SimpleSyncedTE) {
            String s = ((SimpleSyncedTE)currentTile).hasCustomInventoryName() ? ((SimpleSyncedTE)currentTile).getInventoryName() : I18n.format(((SimpleSyncedTE)currentTile).getInventoryName(), new Object[0]);
            this.fontRendererObj.drawString(s, this.xSize / 2 - this.fontRendererObj.getStringWidth(s) / 2, 6, 4210752);
            this.fontRendererObj.drawString(I18n.format("container.inventory", new Object[0]), 8, this.ySize - 96 + 2, 4210752);
            drawGuiContainerForeground(p_146979_1_, p_146979_2_, ((SimpleSyncedTE)currentTile));
        }
    }

    protected void drawGuiContainerBackgroundLayer(float p_146976_1_, int p_146976_2_, int p_146976_3_)
    {
        TileEntity currentTile = this.desyncedTile.getWorldObj().getTileEntity(this.desyncedTile.xCoord, this.desyncedTile.yCoord, this.desyncedTile.zCoord);
        if (guiTextures.length > 0) {
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.mc.getTextureManager().bindTexture(guiTextures[0]);
            int k = (this.width - this.xSize) / 2;
            int l = (this.height - this.ySize) / 2;
            this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);
        }
        // custom gui stuff like progress bars go here
        if (currentTile instanceof SimpleSyncedTE) drawGuiContainerBackground(p_146976_1_, p_146976_2_, p_146976_3_, ((SimpleSyncedTE)currentTile));
    }
}
