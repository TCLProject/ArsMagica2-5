package am2.guis;

import am2.blocks.tileentities.TileEntityEntropicEnervator;
import am2.containers.ContainerEntropicEnervator;
import am2.texture.ResourceManager;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class GuiEntropicEnervator extends GuiContainer{

	private static final ResourceLocation background = new ResourceLocation("arsmagica2", ResourceManager.GetGuiTexturePath("enervatorGUI.png"));
	private final TileEntityEntropicEnervator enervator;

	public GuiEntropicEnervator(TileEntityEntropicEnervator enervator, EntityPlayer player){
		super(new ContainerEntropicEnervator(enervator, player));
		this.enervator = enervator;
		xSize = 176;
		ySize = 165;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {
		mc.renderEngine.bindTexture(background);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		int l = (width - xSize) / 2;
		int i1 = (height - ySize) / 2;
		drawTexturedModalRect(l, i1, 0, 0, xSize, ySize);
		TileEntity te = this.enervator.getWorldObj().getTileEntity(this.enervator.xCoord, this.enervator.yCoord, this.enervator.zCoord);
		if (te instanceof TileEntityEntropicEnervator) {
			int overlayHeight = ((TileEntityEntropicEnervator)te).getRendProgressScaled(14);
			if (overlayHeight > 0)
				this.drawTexturedModalRect(l + 79, i1 + 30 + 14 - overlayHeight, 176, 14 - overlayHeight, 14, overlayHeight + 2);
		}
	}

}
