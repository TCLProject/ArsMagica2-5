package net.tclproject.mysteriumlib.render.model;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;
import org.lwjgl.opengl.GL11;

/**
 * An incredibly simple Tile Entity Renderer implementation using Display Lists, to be extended/used as an example.
 * @see ModelWrapperDisplayList for the benefits this provides.
 * */
@SideOnly(Side.CLIENT)
public class TileEntityModelRenderer extends TileEntitySpecialRenderer
{
    private int list;

    public TileEntityModelRenderer(ResourceLocation res)
    {
        // The model is a local object. It's important not to leave vertex information behind in memory.
        IModelCustom model = AdvancedModelLoader.loadModel(res);
        list = GL11.glGenLists(1);
        GL11.glNewList(list, GL11.GL_COMPILE);
        // Render. Yes, right here.
        model.renderAll();
        GL11.glEndList();
    }

    @Override
    public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float pt)
    {
        GL11.glPushMatrix();
        GL11.glTranslated(x, y, z);
        GL11.glCallList(list);
        GL11.glPopMatrix();
    }
}