package net.tclproject.mysteriumlib.asm.fixes;

import net.tclproject.mysteriumlib.asm.common.CustomLoadingPlugin;

public class MysteriumPatchesFixLoaderMagicka extends CustomLoadingPlugin {

    // Turns on MysteriumASM Lib. You can do this in only one of your Fix Loaders.

    @Override
    public String[] getASMTransformerClass() {
        return transformers;
    }

    @Override
    public void registerFixes() {
        //Registers the class where the methods with the @Fix annotation are
    	registerClassWithFixes("net.tclproject.mysteriumlib.asm.fixes.MysteriumPatchesFixesMagicka");
    }
}
