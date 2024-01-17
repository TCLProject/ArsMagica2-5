package net.tclproject.mysteriumlib.render.image;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;

public class ImageRenderUtils {

    private static void glColor(int v, float a) {
        GL11.glColor4f((float)(v >> 16 & 255) / 255.0F, (float)(v >> 8 & 255) / 255.0F, (float)(v & 255) / 255.0F, a);
    }

    public int allocateBufferedImage(final BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();

        int[] pixels = image.getRGB(0, 0, width, height, null, 0, width);

        ByteBuffer buffer = BufferUtils.createByteBuffer(width * height * 4);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixel = pixels[y * width + x];

                buffer.put((byte) ((pixel >> 16) & 0xFF)); // red
                buffer.put((byte) ((pixel >> 8) & 0xFF)); // green
                buffer.put((byte) (pixel & 0xFF)); // blue
                buffer.put((byte) ((pixel >> 24) & 0xFF)); // alpha
            }
        }

        buffer.flip();

        int textureID = GL11.glGenTextures();
//		this.bind(textureID);

        GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        GL11.glTexImage2D(
                GL11.GL_TEXTURE_2D,
                0,
                GL11.GL_RGBA8,
                width,
                height,
                0,
                GL11.GL_RGBA,
                GL_UNSIGNED_BYTE,
                buffer);

        return textureID;
    }

    public static void loadBind(BufferedImage img) {
        int[] t = img.getRGB(0, 0, img.getWidth(), img.getHeight(), (int[])null, 0, img.getWidth());
        IntBuffer buf = BufferUtils.createIntBuffer(t.length);
        buf.put(t);
        buf.position(0);
        GL11.glTexSubImage2D(3553, 0, 0, 0, img.getWidth(), img.getHeight(), 32993, 33639, buf);
//		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, image.getWidth(), image.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, (ByteBuffer) buffer.flip());
    }

    private static void renderScaledImageWholeScreen(boolean scaleUp, boolean scaleDown, int backgroundColor, boolean imageSolid, int texture, BufferedImage img, int width, int height, int texWidth, int texHeight) {

        float alpha = 1.0F;

        float w = (float) Display.getWidth();
        float h = (float)Display.getHeight();
        float iw = (float)width;
        float ih = (float)height;
        GL11.glViewport(0, 0, (int)w, (int)h);
        GL11.glMatrixMode(5889);
        GL11.glLoadIdentity();
        GL11.glOrtho((double)(-w / 2.0F), (double)(w / 2.0F), (double)(h / 2.0F), (double)(-h / 2.0F), -1.0D, 1.0D);
        GL11.glMatrixMode(5888);
        GL11.glLoadIdentity();
        float maxU = iw / (float)texWidth;
        float maxV = ih / (float)texHeight;
        float imgRatio = iw / ih;
        float dispRatio = w / h;
        float scalingFactor = dispRatio > imgRatio ? h / ih : w / iw;
        scalingFactor += scalingFactor * 0.333;
        if (scaleUp && scalingFactor > 1.0F || scaleDown && scalingFactor < 1.0F) {
            iw *= scalingFactor;
            ih *= scalingFactor;
        }

        GL11.glDisable(3553);
        glColor(backgroundColor, alpha);
        GL11.glBegin(7);
        if (imageSolid && alpha < 1.0F) {
            GL11.glVertex2f(-w / 2.0F, -h / 2.0F);
            GL11.glVertex2f(-w / 2.0F, -ih / 2.0F);
            GL11.glVertex2f(w / 2.0F, -ih / 2.0F);
            GL11.glVertex2f(w / 2.0F, -h / 2.0F);
            GL11.glVertex2f(-w / 2.0F, ih / 2.0F);
            GL11.glVertex2f(-w / 2.0F, h / 2.0F);
            GL11.glVertex2f(w / 2.0F, h / 2.0F);
            GL11.glVertex2f(w / 2.0F, ih / 2.0F);
            GL11.glVertex2f(-w / 2.0F, -ih / 2.0F);
            GL11.glVertex2f(-w / 2.0F, ih / 2.0F);
            GL11.glVertex2f(-iw / 2.0F, ih / 2.0F);
            GL11.glVertex2f(-iw / 2.0F, -ih / 2.0F);
            GL11.glVertex2f(iw / 2.0F, -ih / 2.0F);
            GL11.glVertex2f(iw / 2.0F, ih / 2.0F);
            GL11.glVertex2f(w / 2.0F, ih / 2.0F);
            GL11.glVertex2f(w / 2.0F, -ih / 2.0F);
        } else {
            GL11.glVertex2f(-w / 2.0F, -h / 2.0F);
            GL11.glVertex2f(-w / 2.0F, h / 2.0F);
            GL11.glVertex2f(w / 2.0F, h / 2.0F);
            GL11.glVertex2f(w / 2.0F, -h / 2.0F);
        }

        GL11.glEnd();
        bindFrame(texture, img, texWidth, texHeight);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, alpha);
        GL11.glBegin(7);
        GL11.glTexCoord2f(0.0F, 0.0F);
        GL11.glVertex2f(-iw / 2.0F, -ih / 2.0F);
        GL11.glTexCoord2f(0.0F, maxV);
        GL11.glVertex2f(-iw / 2.0F, ih / 2.0F);
        GL11.glTexCoord2f(maxU, maxV);
        GL11.glVertex2f(iw / 2.0F, ih / 2.0F);
        GL11.glTexCoord2f(maxU, 0.0F);
        GL11.glVertex2f(iw / 2.0F, -ih / 2.0F);
        GL11.glEnd();
        GL11.glDisable(3553);
    }

    private static void bindFrame(int animTexture, BufferedImage img, int animTexWidth, int animTexHeight) {
        GL11.glEnable(3553);
        GL11.glBindTexture(3553, animTexture);

        if (img.getWidth() > animTexWidth || img.getHeight() > animTexHeight) {
            throw new RuntimeException("Splash frames not the same size!");
        }

        int[] t = img.getRGB(0, 0, img.getWidth(), img.getHeight(), (int[])null, 0, img.getWidth());
        IntBuffer buf = BufferUtils.createIntBuffer(t.length);
        buf.put(t);
        buf.position(0);
        GL11.glTexSubImage2D(3553, 0, 0, 0, img.getWidth(), img.getHeight(), 32993, 33639, buf);

    }
}
