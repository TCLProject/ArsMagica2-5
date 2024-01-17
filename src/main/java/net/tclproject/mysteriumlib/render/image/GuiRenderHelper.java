package net.tclproject.mysteriumlib.render.image;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class GuiRenderHelper {

    private Minecraft mc;
    public static RenderItem itemRender = new RenderItem();

    public GuiRenderHelper(Minecraft mc)
    {
        // We need this to invoke the render engine.
        this.mc = mc;
    }

    public static void renderScaledItemIntoGUI(FontRenderer p_77015_1_, TextureManager p_77015_2_, ItemStack p_77015_3_, int p_77015_4_, int p_77015_5_, boolean renderEffect, int width, int height)
    {
        int k = p_77015_3_.getItemDamage();
        Object object = p_77015_3_.getIconIndex();
        int l;
        float f;
        float f3;
        float f4;
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_BLEND);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        ResourceLocation resourcelocation = p_77015_2_.getResourceLocation(p_77015_3_.getItemSpriteNumber());
        p_77015_2_.bindTexture(resourcelocation);

        if (object == null)
        {
            object = ((TextureMap)Minecraft.getMinecraft().getTextureManager().getTexture(resourcelocation)).getAtlasSprite("missingno");
        }

        l = p_77015_3_.getItem().getColorFromItemStack(p_77015_3_, 0);
        f3 = (float)(l >> 16 & 255) / 255.0F;
        f4 = (float)(l >> 8 & 255) / 255.0F;
        f = (float)(l & 255) / 255.0F;

        if (itemRender.renderWithColor)
        {
            GL11.glColor4f(f3, f4, f, 1.0F);
        }

        GL11.glDisable(GL11.GL_LIGHTING); //Forge: Make sure that render states are reset, a renderEffect can derp them up.
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glEnable(GL11.GL_BLEND);

        itemRender.renderIcon(p_77015_4_, p_77015_5_, (IIcon)object, width, height);

        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glDisable(GL11.GL_BLEND);

        if (renderEffect && p_77015_3_.hasEffect(0))
        {
            itemRender.renderEffect(p_77015_2_, p_77015_4_, p_77015_5_);
        }
        GL11.glEnable(GL11.GL_LIGHTING);

        GL11.glEnable(GL11.GL_CULL_FACE);
    }

    public static void renderScaledCutsceneIntoGUI(FontRenderer p_77015_1_, TextureManager p_77015_2_, ResourceLocation resourcelocation, int p_77015_4_, int p_77015_5_, boolean renderEffect, int width, int height)
    {
        int l;
        float f;
        float f3;
        float f4;
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_BLEND);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        p_77015_2_.bindTexture(resourcelocation);

        l = 16777215;
        f3 = (float)(l >> 16 & 255) / 255.0F;
        f4 = (float)(l >> 8 & 255) / 255.0F;
        f = (float)(l & 255) / 255.0F;

        GL11.glDisable(GL11.GL_LIGHTING); //Forge: Make sure that render states are reset, a renderEffect can derp them up.
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glEnable(GL11.GL_BLEND);

        Rendering.drawTexturedQuadFit(p_77015_4_, p_77015_5_, width, height);

        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_CULL_FACE);
    }
}
