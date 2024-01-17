package net.tclproject.mysteriumlib.render.dae.hea3ven.colladamodel.client.model.test;


import am2.AMClientEventHandler;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class ModelBaseChest {
    private ResourceLocation openResourceName;

    public ModelBaseChest(ResourceLocation resource) {
        openResourceName = resource;
    }

    public void render(TileEntityBaseChest chest, double x, double y, double z) {
        GL11.glPushMatrix();

        GL11.glTranslatef((float) x + 0.5f, (float) y + 0.5f, (float) z + 0.5f);
        GL11.glRotatef(-90.0f + chest.getRotation() * -90.0f, 0.0f, 1.0f, 0.0f);

        chest.getAnimationState().render();

        GL11.glPopMatrix();
    }

    public void renderItem() {
        GL11.glPushMatrix();
        GL11.glDisable(GL11.GL_LIGHTING);
        // GL11.glTranslatef(0.0f, -0.5f, 0.0f);
        GL11.glRotatef(180.0f, 0.0f, 1.0f, 0.0f);

        AMClientEventHandler.getModelManager().getModel(openResourceName)
                .renderAll();

        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glPopMatrix();
    }
}