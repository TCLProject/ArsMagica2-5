package net.tclproject.mysteriumlib.asm.common;

import am2.LogHelper;
import cpw.mods.fml.common.asm.transformers.DeobfuscationTransformer;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import net.minecraft.launchwrapper.Launch;
import net.tclproject.mysteriumlib.asm.core.ASMFix;
import net.tclproject.mysteriumlib.asm.core.MetaReader;
import net.tclproject.mysteriumlib.asm.core.TargetClassTransformer;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

/**
 * Custom IFMLLoadingPlugin implementation.
 * @see IFMLLoadingPlugin
 * */
@IFMLLoadingPlugin.TransformerExclusions({"net.tclproject"})
public class CustomLoadingPlugin implements IFMLLoadingPlugin {
	
	/**A DeobfuscationTransformer instance for use inside this class.*/
	private static DeobfuscationTransformer deobfuscationTransformer;
	/**If we have checked if we're running inside an obfuscated environment.*/
	private static boolean checkedObfuscation;
	/**If we're running inside an obfuscated environment.*/
	private static boolean isObfuscated = false;
	/**A Metadata Reader instance for use inside this class.*/
    private static MetaReader mcMetaReader;

	public static boolean foundThaumcraft = false;

	public static boolean foundDragonAPI = false;

	public static boolean foundOptifine = false;
    
    public static File debugOutputLocation;

    static {
        mcMetaReader = new MinecraftMetaReader();
    }

    /**
     * Returns the transformer that we are using at the current moment in time to modify classes.
     * See why we have to use two separate ones in the documentation for FirstClassTransformer.
     * @return FirstClassTransformer if our built-in fixes haven't been applied, otherwise - CustomClassTransformer.
     */
    public static TargetClassTransformer getTransformer() {
        return FirstClassTransformer.instance.registeredBuiltinFixes ?
                CustomClassTransformer.instance : FirstClassTransformer.instance;
    }

    /**
     * Registers a single manually made ASMFix.
     * It is not the most efficient way to make fixes, but if you want to go this way,
     * look at how the code already there builds an ASMFix out of a fix method or just
     * take a look at the documentation of the builder class within ASMFix.
     */
    public static void registerFix(ASMFix fix) {
        getTransformer().registerFix(fix);
    }

    /** Registers all fix methods within a class. */
    public static void registerClassWithFixes(String className) {
        getTransformer().registerClassWithFixes(className);
    }

    /** Getter for mcMetaReader. */
    public static MetaReader getMetaReader() {
        return mcMetaReader;
    }
	
	static DeobfuscationTransformer getDeobfuscationTransformer() {
        if (isObfuscated() && deobfuscationTransformer == null) {
            deobfuscationTransformer = new DeobfuscationTransformer();
        }
        return deobfuscationTransformer;
    }
	
	/**
	 * If the obfuscation has not yet been checked, checks and returns it.
     * If it has, returns the value that the previous check returned.
     * @return If the mod is run in an obfuscated environment.
     * */
	public static boolean isObfuscated() {
		if (!checkedObfuscation) {
			// IDK why I CAN'T just use this, so I will just use this.
			// but FMLForgePlugin.RUNTIME_DEOBF is not init yet (maybe)
			isObfuscated = !(Boolean)Launch.blackboard.get("fml.deobfuscatedEnvironment");
			checkedObfuscation = true;
		}
		return isObfuscated;
	}
	
	// For further methods, forge has way better documentation than what I could ever write.

	// Only exists in 1.7.10. Comment out if not needed.
    public String getAccessTransformerClass() {
        return null;
    }
    
    
//  This only exists in 1.6.x. Uncomment if needed.
//  public String[] getLibraryRequestClass() {
//      return null;
//  }

    @Override
    public String[] getASMTransformerClass() {
        return null;
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {
		LogHelper.info("Core initializing...stand back!  I'm going to try MAGIC!");
    	debugOutputLocation = new File(data.get("mcLocation").toString(), "bg edited classes");
		// we can walk 'Launch.classLoader.getSources()' to search for all needed mods and then scan '*mod.info'
		// but DragonAPI just... scraps that idea (modid == ""). Optifine even doesn't have one.
		// So scan entire tree only for Thaumcraft? Don't think so.

		// bruteforce
		try {
			Class.forName("optifine.InstallerFrame"); // should be here, that's the entrypoint
			foundOptifine = true;
			LogHelper.info("OptiFine detected! If you do not have OptiFine, this is an error. Report it.");
		}
		catch(ClassNotFoundException ignored) {
			LogHelper.info("OptiFine not detected! If you do have OptiFine, this is an error. Report it.");
		}
		// scan for coremods - faster, easier, works.
		for(Object mod : (ArrayList<?>)data.get("coremodList")) {
			if(!foundThaumcraft &&  mod.toString().contains("DepLoader")) {
				foundThaumcraft = true;
				LogHelper.info("Thaumcraft detected! If you do not have Thaumcraft, this is an error. Report it.");
			}
			else if(!foundDragonAPI && mod.toString().contains("DragonAPIASMHandler")) {
				foundDragonAPI = true;
				LogHelper.info("DragonAPI detected! If you do not have DragonAPI, this is an error. Report it.");
			}
		}		
		registerFixes();
    }
    
    public void registerFixes() {
    }
}
