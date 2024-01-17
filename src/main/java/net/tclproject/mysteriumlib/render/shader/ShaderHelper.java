package net.tclproject.mysteriumlib.render.shader;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import am2.proxy.tick.ClientTickHandler;
import net.minecraft.client.renderer.OpenGlHelper;

import org.apache.logging.log4j.Level;
import org.lwjgl.opengl.ARBFragmentShader;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.ARBVertexShader;
import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.FMLLog;

/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Botania Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 *
 * P.S. TCLProject: I could not find a *sane* implementation of both vert and frag shaders other than this.
 * No, the Thaumcraft implementation is *not* sane (plus closed source), and neither is Reika's. So there we go. I hope Vazkii doesn't mind.
 *
 * Sample Usage in TileEntities:
 * ..GL Calls like rotate, translate, setting state..
 * ShaderHelper.useShader(ShaderHelper.exampleShader, callback (optional));
 * model.renderAll(); (model is IModelCustom)
 * ShaderHelper.releaseShader();
 * ..resetting GL state..
 *
 * Sample Usage in Entities:
 * ShaderHelper.useShader(ShaderHelper.exampleShader, callback (optional));
 * super.doRender(par1Entity, par2, par4, par6, par8, par9);
 * ShaderHelper.releaseShader();
 *
 * Plus, see:
 * https://github.com/VazkiiMods/Botania/blob/1.7.10-final/src/main/java/vazkii/botania/client/render/entity/RenderDoppleganger.java
 * for a simple example of retrieving and changing shader values (uniforms) on the fly.
 */
public final class ShaderHelper {

    /**
     * A Callback for when a shader is called. Used to define shader uniforms.
     */
    public abstract class ShaderCallback {
        public abstract void call(int shader);
    }

    private static final int VERT = ARBVertexShader.GL_VERTEX_SHADER_ARB;
    private static final int FRAG = ARBFragmentShader.GL_FRAGMENT_SHADER_ARB;

    public static int exampleShader = 0;

    public static void initShaders() {
        if(!useShaders())
            return;

//        exampleShader = createProgram("/assets/arsmagica2/shader/example_shader.vert", "/assets/arsmagica2/shader/example_shader.frag");
    }

    public static void useShader(int shader, ShaderCallback callback) {
        if(!useShaders())
            return;

        ARBShaderObjects.glUseProgramObjectARB(shader);

        if(shader != 0) {
            int time = ARBShaderObjects.glGetUniformLocationARB(shader, "time");
            ARBShaderObjects.glUniform1iARB(time, ClientTickHandler.ticksInGame);

            if(callback != null)
                callback.call(shader);
        }
    }

    public static void useShader(int shader) {
        useShader(shader, null);
    }

    public static void releaseShader() {
        useShader(0);
    }

    public static boolean useShaders() {
        return OpenGlHelper.shadersSupported;
    }

    // Most of the code taken from the LWJGL wiki
    // http://lwjgl.org/wiki/index.php?title=GLSL_Shaders_with_LWJGL

    private static int createProgram(String vert, String frag) {
        int vertId = 0, fragId = 0, program = 0;
        if(vert != null)
            vertId = createShader(vert, VERT);
        if(frag != null)
            fragId = createShader(frag, FRAG);

        program = ARBShaderObjects.glCreateProgramObjectARB();
        if(program == 0)
            return 0;

        if(vert != null)
            ARBShaderObjects.glAttachObjectARB(program, vertId);
        if(frag != null)
            ARBShaderObjects.glAttachObjectARB(program, fragId);

        ARBShaderObjects.glLinkProgramARB(program);
        if(ARBShaderObjects.glGetObjectParameteriARB(program, ARBShaderObjects.GL_OBJECT_LINK_STATUS_ARB) == GL11.GL_FALSE) {
            FMLLog.log(Level.ERROR, getLogInfo(program));
            return 0;
        }

        ARBShaderObjects.glValidateProgramARB(program);
        if (ARBShaderObjects.glGetObjectParameteriARB(program, ARBShaderObjects.GL_OBJECT_VALIDATE_STATUS_ARB) == GL11.GL_FALSE) {
            FMLLog.log(Level.ERROR, getLogInfo(program));
            return 0;
        }

        return program;
    }

    private static int createShader(String filename, int shaderType){
        int shader = 0;
        try {
            shader = ARBShaderObjects.glCreateShaderObjectARB(shaderType);

            if(shader == 0)
                return 0;

            ARBShaderObjects.glShaderSourceARB(shader, readFileAsString(filename));
            ARBShaderObjects.glCompileShaderARB(shader);

            if (ARBShaderObjects.glGetObjectParameteriARB(shader, ARBShaderObjects.GL_OBJECT_COMPILE_STATUS_ARB) == GL11.GL_FALSE)
                throw new RuntimeException("Error creating shader: " + getLogInfo(shader));

            return shader;
        }
        catch(Exception e) {
            ARBShaderObjects.glDeleteObjectARB(shader);
            e.printStackTrace();
            return -1;
        }
    }

    private static String getLogInfo(int obj) {
        return ARBShaderObjects.glGetInfoLogARB(obj, ARBShaderObjects.glGetObjectParameteriARB(obj, ARBShaderObjects.GL_OBJECT_INFO_LOG_LENGTH_ARB));
    }

    private static String readFileAsString(String filename) throws Exception {
        StringBuilder source = new StringBuilder();
        InputStream in = ShaderHelper.class.getResourceAsStream(filename);
        Exception exception = null;
        BufferedReader reader;

        if(in == null)
            return "";

        try {
            reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));

            Exception innerExc= null;
            try {
                String line;
                while((line = reader.readLine()) != null)
                    source.append(line).append('\n');
            } catch(Exception exc) {
                exception = exc;
            } finally {
                try {
                    reader.close();
                } catch(Exception exc) {
                    if(innerExc == null)
                        innerExc = exc;
                    else exc.printStackTrace();
                }
            }

            if(innerExc != null)
                throw innerExc;
        } catch(Exception exc) {
            exception = exc;
        } finally {
            try {
                in.close();
            } catch(Exception exc) {
                if(exception == null)
                    exception = exc;
                else exc.printStackTrace();
            }

            if(exception != null)
                throw exception;
        }

        return source.toString();
    }

}
