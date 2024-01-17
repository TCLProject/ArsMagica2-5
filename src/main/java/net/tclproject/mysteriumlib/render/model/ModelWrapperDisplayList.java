package net.tclproject.mysteriumlib.render.model;

import net.minecraftforge.client.model.IModelCustom;
import net.minecraftforge.client.model.obj.GroupObject;
import net.minecraftforge.client.model.obj.WavefrontObject;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Optimized wrapper for IModelCustom, enabling usage of all of its features in rendering obj models.
 * The performance gains are anywhere between tens- and hundreds-fold, without any large drawbacks
 * (other than perhaps a bit of extra RAM usage). All of which is achieved using Display Lists.
 * Sample Usage is as follows:
 * IModelCustom model;
 * model = AdvancedModelLoader.loadModel(resource);
 * model = new ModelWrapperDisplayList((WavefrontObject) model);
 * model.renderAll();
 */
public class ModelWrapperDisplayList implements IModelCustom
{
    // For each group, its own list
    private final Map<String, Integer> lists = new HashMap<>();
    // Buffer that will contain all the lists. For a quicker render of the entire model.
    private final IntBuffer bufAll;
    private final String type;

    public ModelWrapperDisplayList(WavefrontObject model)
    {
        type = model.getType();
        int list = GL11.glGenLists(model.groupObjects.size());
        for (GroupObject obj : model.groupObjects) {
            GL11.glNewList(list, GL11.GL_COMPILE);
            model.renderPart(obj.name);
            GL11.glEndList();
            lists.put(obj.name, list++);
        }
        bufAll = initBuffer();
    }

    private IntBuffer initBuffer()
    {
        IntBuffer buf = BufferUtils.createIntBuffer(lists.size());
        for (int i : lists.values()) {
            buf.put(i);
        }
        buf.flip();
        return buf;
    }

    @Override
    public String getType()
    {
        return type;
    }

    @Override
    public void renderAll()
    {
        GL11.glCallLists(bufAll);
    }

    @Override
    public void renderOnly(String... groupNames)
    {
        if (groupNames == null || groupNames.length == 0) {
            return;
        }

        for (String group : groupNames) {
            renderPart(group);
        }
    }

    @Override
    public void renderPart(String partName)
    {
        Integer list = lists.get(partName);
        if (list != null) {
            GL11.glCallList(list);
        }
    }

    @Override
    public void renderAllExcept(String... groupNames)
    {
        if (groupNames == null || groupNames.length == 0) {
            renderAll();
            return;
        }

        for (Map.Entry<String, Integer> it : lists.entrySet()) {
            if (Arrays.binarySearch(groupNames, it.getKey(), String::compareTo) < 0) {
                GL11.glCallList(it.getValue());
            }
        }
    }
}