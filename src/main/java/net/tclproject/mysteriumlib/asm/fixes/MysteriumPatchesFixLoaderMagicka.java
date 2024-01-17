package net.tclproject.mysteriumlib.asm.fixes;

import am2.LogHelper;
import am2.preloader.BytecodeTransformers;
import net.tclproject.mysteriumlib.asm.annotations.EnumReturnSetting;
import net.tclproject.mysteriumlib.asm.common.CustomLoadingPlugin;
import net.tclproject.mysteriumlib.asm.common.FieldClassTransformer;
import net.tclproject.mysteriumlib.asm.common.FirstClassTransformer;
import net.tclproject.mysteriumlib.asm.core.MASMFix;

import static am2.preloader.BytecodeTransformers.checkIsChunkApiFilePresent;

public class MysteriumPatchesFixLoaderMagicka extends CustomLoadingPlugin {

    public static boolean foundOptiFine = false;
    private static boolean confirmedOptiFine = false;

    // Turns on MysteriumASM Lib. You can do this in only one of your MFix Loaders.
    @Override
    public String[] getASMTransformerClass() {
        CustomLoadingPlugin.getMetaReader();
        return new String[]{
                FirstClassTransformer.class.getName(),
                BytecodeTransformers.class.getName(),
                FieldClassTransformer.class.getName(),
        };
    }

    @Override
    public void registerFixes() {
        //Registers the class where the methods with the @MFix annotation are
        registerClassWithFixes("net.tclproject.mysteriumlib.asm.fixes.ChunkDataFixes");
        registerClassWithFixes("net.tclproject.mysteriumlib.asm.fixes.MultiThreadingFixes");
    	registerClassWithFixes("net.tclproject.mysteriumlib.asm.fixes.MysteriumPatchesFixesMagicka");

    	if (isChunkAPIPresent() || checkIsChunkApiFilePresent()) {
            MysteriumPatchesFixLoaderMagicka.registerFix(MASMFix.newBuilder()
                    .setTargetClass("com.falsepattern.chunk.internal.mixin.mixins.common.vanilla.AnvilChunkLoaderMixin")
                    .setTargetMethod("readCustomData")
                    .addTargetMethodParameters("net.minecraft.world.chunk.Chunk", "net.minecraft.nbt.NBTTagCompound")
                    .setFixesClass("net.tclproject.mysteriumlib.asm.fixes.ChunkDataFixes")
                    .setFixMethod("readCustomData")
                    .addFixMethodParameter("net.minecraft.world.chunk.Chunk", 1)
                    .addFixMethodParameter("net.minecraft.nbt.NBTTagCompound", 2)
                    .setReturnSetting(EnumReturnSetting.NEVER)
                    .build());
            MysteriumPatchesFixLoaderMagicka.registerFix(MASMFix.newBuilder()
                    .setTargetClass("com.falsepattern.chunk.internal.mixin.mixins.common.vanilla.AnvilChunkLoaderMixin")
                    .setTargetMethod("writeCustomData")
                    .addTargetMethodParameters("net.minecraft.world.chunk.Chunk", "net.minecraft.nbt.NBTTagCompound")
                    .setFixesClass("net.tclproject.mysteriumlib.asm.fixes.ChunkDataFixes")
                    .setFixMethod("writeCustomData")
                    .addFixMethodParameter("net.minecraft.world.chunk.Chunk", 1)
                    .addFixMethodParameter("net.minecraft.nbt.NBTTagCompound", 2)
                    .setReturnSetting(EnumReturnSetting.NEVER)
                    .build());
        }
    }

    public static boolean isChunkAPIPresent(){
        try{
            Class.forName("com.falsepattern.chunk.internal.mixin.mixins.common.vanilla.AnvilChunkLoaderMixin");
        }
        catch (ClassNotFoundException exception1){
            return false;
        }
        return true;
    }

    public static boolean isRecurrentComplexPresent(){
        try{
            Class.forName("ivorius.reccomplex.RecurrentComplex");
        }
        catch (ClassNotFoundException exception1){
            return false;
        }
        return true;
    }

    public static boolean isOptiFinePresent(){
        if (!confirmedOptiFine && foundOptiFine){
            // Check presence of OptiFine core classes
            try{
                Class.forName("optifine.OptiFineForgeTweaker");
            }
            catch (ClassNotFoundException exception1){
                try{
                    Class.forName("optifine.OptiFineTweaker");
                }
                catch (ClassNotFoundException exception2){
                    foundOptiFine = false;
                }
            }
            if (foundOptiFine){
                LogHelper.info("Core: OptiFine presence has been confirmed.");
            } else {
                LogHelper.info("Core: OptiFine doesn't seem to be there actually.");
            }
            confirmedOptiFine = true;
        }
        return foundOptiFine;
    }
}
