/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package am2.worldgen.smartgen.generic;

import am2.texture.ResourceManager;
import am2.worldgen.smartgen.struct.info.StructureEntityInfo;
import ivorius.ivtoolkit.blocks.BlockArea;
import ivorius.ivtoolkit.blocks.BlockCoord;
import ivorius.ivtoolkit.rendering.grid.AreaRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

/**
 * Created by lukas on 10.02.15.
 */
public class SelectionRenderer
{
    public static ResourceLocation[] TEXTURE;
    public static ResourceLocation[] LATTICE_TEXTURE;

    static
    {
        TEXTURE = new ResourceLocation[3];
        for (int i = 0; i < TEXTURE.length; i++)
            TEXTURE[i] = new ResourceLocation("arsmagica2", ResourceManager.GetFXTexturePath("smoke.png"));

        LATTICE_TEXTURE = new ResourceLocation[3];
        for (int i = 0; i < LATTICE_TEXTURE.length; i++)
            LATTICE_TEXTURE[i] = new ResourceLocation("arsmagica2", "textures/mobs/enderfish.png"); // don't laugh; placeholder textures
    }

    public static void renderSelection(EntityLivingBase entity, int ticks, float partialTicks)
    {
        Minecraft mc = Minecraft.getMinecraft();
        BlockCoord selPoint1 = null;
        BlockCoord selPoint2 = null;

        StructureEntityInfo structureEntityInfo = StructureEntityInfo.getStructureEntityInfo(entity);
        if (structureEntityInfo != null)
        {
            selPoint1 = structureEntityInfo.selectedPoint1;
            selPoint2 = structureEntityInfo.selectedPoint2;
        }

        GL11.glLineWidth(3.0f);

        if (selPoint1 != null)
        {
            GL11.glColor3f(0.6f, 0.8f, 0.95f);
            AreaRenderer.renderAreaLined(new BlockArea(selPoint1, selPoint1), 0.03f);
        }
        if (selPoint2 != null)
        {
            GL11.glColor3f(0.2f, 0.45f, 0.65f);
            AreaRenderer.renderAreaLined(new BlockArea(selPoint2, selPoint2), 0.04f);
        }

        if (selPoint1 != null && selPoint2 != null)
        {
            BlockArea selArea = new BlockArea(selPoint1, selPoint2);

            GL11.glColor3f(0.4f, 0.65f, 0.8f);
            AreaRenderer.renderAreaLined(selArea, 0.02f);

            GL11.glEnable(GL11.GL_BLEND);
            OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
            GL11.glAlphaFunc(GL11.GL_GREATER, 0.0001f);

            ResourceLocation curTex = TEXTURE[MathHelper.floor_float((ticks + partialTicks) * 0.75f) % TEXTURE.length];
            mc.renderEngine.bindTexture(curTex);

            GL11.glColor4f(0.2f, 0.5f, 0.6f, 0.5f);
            AreaRenderer.renderArea(selArea, false, true, 0.01f);

            GL11.glColor4f(0.4f, 0.65f, 0.8f, 0.75f);
            AreaRenderer.renderArea(selArea, false, false, 0.01f);

            GL11.glAlphaFunc(GL11.GL_GREATER, 0.002f);
            GL11.glDisable(GL11.GL_BLEND);
        }
    }
}
