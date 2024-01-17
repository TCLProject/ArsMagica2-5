package am2.blocks.renderers;

import am2.blocks.tileentities.TileEntityBlackAurem;
import am2.blocks.tileentities.TileEntityCelestialPrism;
import am2.blocks.tileentities.TileEntityObelisk;
import am2.blocks.tileentities.TileEntitySpatialVortex;
import am2.texture.ResourceManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class SpatialVortexRenderer extends TileEntitySpecialRenderer{

	private ResourceLocation rLoc_spatial;

	public SpatialVortexRenderer(){
		rLoc_spatial = new ResourceLocation("arsmagica2", ResourceManager.getCustomBlockTexturePath("spatial_vortex.png"));
	}

	public void renderAModelAt(TileEntitySpatialVortex tile, double d, double d1, double d2, float f){

		GL11.glPushMatrix();
		GL11.glPushAttrib(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_TEXTURE_BIT);
		renderSpatialVortex(tile, d, d1, d2, f);
		GL11.glPopAttrib();
		GL11.glPopMatrix();
	}

	private void renderSpatialVortex(TileEntitySpatialVortex tile, double d, double d1, double d2, float f){
		GL11.glTranslatef((float)d + 0.5f, (float)d1 + 1f, (float)d2 + 0.5f);
		GL11.glDepthMask(false);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		RenderHelper.disableStandardItemLighting();

		Tessellator tessellator = Tessellator.instance;

		renderArsMagicaEffect(tessellator, tile.xCoord + tile.yCoord + tile.zCoord, 1);

		RenderHelper.enableStandardItemLighting();
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glDepthMask(true);
	}

	private void renderArsMagicaEffect(Tessellator tessellator, float offset, float scale){
		if (offset != 0){
			GL11.glRotatef(180F - RenderManager.instance.playerViewY, 0.0F, 1.0F, 0.0F);
			GL11.glRotatef(-RenderManager.instance.playerViewX, 1.0F, 0.0F, 0.0F);
		}else{
			GL11.glRotatef(35, 0, 1, 0);
			GL11.glTranslatef(0, -0.75f, 0);
		}

		Minecraft.getMinecraft().renderEngine.bindTexture(rLoc_spatial);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glTranslatef(0.0f, 0.25f, 0.0f);
		GL11.glPushMatrix();
		GL11.glRotatef(Minecraft.getMinecraft().thePlayer.ticksExisted, 0, 0, 1);
		GL11.glScalef(scale * 2, scale * 2, scale * 2);
		GL11.glTranslatef(0.0f, -0.25f, 0.0f);
		renderSprite(tessellator);
		GL11.glPopMatrix();

	}

	private void renderSprite(Tessellator tessellator){

		float TLX = 0;
		float BRX = 1;
		float TLY = 0;
		float BRY = 1;

		float f4 = 1.0F;
		float f5 = 0.5F;
		float f6 = 0.25F;

		try{
			tessellator.startDrawingQuads();
			tessellator.setBrightness(15728863);
			tessellator.addVertexWithUV(0.0F - f5, 0.0F - f6, 0.0D, TLX, BRY);
			tessellator.addVertexWithUV(f4 - f5, 0.0F - f6, 0.0D, BRX, BRY);
			tessellator.addVertexWithUV(f4 - f5, f4 - f6, 0.0D, BRX, TLY);
			tessellator.addVertexWithUV(0.0F - f5, f4 - f6, 0.0D, TLX, TLY);
			tessellator.draw();
		}catch (Throwable t){
		}
	}

	@Override
	public void renderTileEntityAt(TileEntity tileentity, double d, double d1, double d2, float f){
		renderAModelAt((TileEntitySpatialVortex)tileentity, d, d1, d2, f);
	}
}
