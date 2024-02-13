package net.tclproject.mysteriumlib.asm.fixes;

import am2.preloader.BytecodeTransformers;
import net.tclproject.mysteriumlib.asm.common.CustomLoadingPlugin;
import net.tclproject.mysteriumlib.asm.common.FirstClassTransformer;

public class MysteriumPatchesFixLoaderMagicka extends CustomLoadingPlugin {
	
    // Turns on MysteriumASM Lib. You can do this in only one of your Fix Loaders.
    @Override
    public String[] getASMTransformerClass() {
        CustomLoadingPlugin.getMetaReader();
        return new String[]{
                FirstClassTransformer.class.getName(),
                BytecodeTransformers.class.getName(),
        };
    }

    @Override
    public void registerFixes() {
        //Registers the class where the methods with the @Fix annotation are
    	registerClassWithFixes("net.tclproject.mysteriumlib.asm.fixes.MysteriumPatchesFixesMagicka");
    }
	
}
