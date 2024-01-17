package net.tclproject.mysteriumlib.render.image;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import org.lwjgl.opengl.GL11;

import javax.swing.*;

public class PopupUtils {

    private static int waitingfor2 = 0;
    private static int frame2 = 255;
    private static String[] texxts = new String[20];
    private static int countOfText = 0;
    private static boolean shouldRender;

    @SubscribeEvent(
            priority = EventPriority.NORMAL
    )
    public void renderHotbarOverlay(RenderGameOverlayEvent event) {
        if (event.isCancelable() || event.type != RenderGameOverlayEvent.ElementType.ALL)
        {
            return;
        }
        if (shouldRender) {
            if (!displayInfoI(event.resolution.getScaledWidth(),event.resolution.getScaledHeight())) {
                shouldRender = false;
            }
        }
    }

    public static void showJavaPopup(String title, String text, int width) {
        String html = "<html><body width='%1s'><h1>" + title + "</h1>"
                + "<p>" + text + "<br><br><p>";
        JOptionPane.showMessageDialog(null, String.format(html, width));
    }

    /**Displays information on the screen.
     * Similar to the new command in 1.9+, text above hotbar. Warning: Can't display more than 20 messages at a time.)*/
    public static void displayInfo(String title, String text) {
        if(countOfText == 20) return;
        countOfText++;
        texxts[countOfText - 1] = "§a" + title + ": §f" + text;
        frame2 = 255;
        waitingfor2 = 0;
        shouldRender = true;
        return;
    }

    // TODO: make the texts that appear last, disappear last (as it stands, if there are multiple texts, all of them share the same transparency values and will disappear at the same time)
    private static boolean displayInfoI(int width, int height) {
        if (texxts[0] != null) {
            for (int i = 0; i < countOfText; i++) {
                int xPos = (width - Minecraft.getMinecraft().fontRenderer.getStringWidth(texxts[i])) / 2;
                int yPos = (int)(height / 1.25) - ((Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT + 2) * (i));
                GL11.glPushMatrix();
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                GL11.glColor4f(1.0F, 1.0F, 1.0F, (1.0F/256) * (256-frame2));
                Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(I18n.format(texxts[i]), xPos, yPos, 0x000000);
                GL11.glDisable(GL11.GL_BLEND);
                GL11.glPopMatrix();
            }

            frame2--;
            if(frame2 <= 0) {
                frame2++;
                waitingfor2++;
            }

            int lengthtext = 0;
            for (int i = 0; i < texxts.length; i++) {
                lengthtext += texxts[i].length() / 2;
                if (i == texxts.length-1) lengthtext += texxts[i].length() / 2;
            }

            if(waitingfor2 >= 10*lengthtext) {
                frame2++;
                frame2++;
            }
            if(frame2 >= 256) {
                frame2 = 255;
                waitingfor2 = 0;
                if (texxts[0] != null) {
                    countOfText = 0;
                    for (int i = 0; i < 20; i++) texxts[i] = null;
                }
                return false;
            }
        }

        return true;
    }
}
