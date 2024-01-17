package net.tclproject.mysteriumlib.asm.common;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import am2.LogHelper;
import net.tclproject.mysteriumlib.asm.fixes.MysteriumPatchesFixLoaderMagicka;
import org.apache.logging.log4j.Level;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.asm.transformers.DeobfuscationTransformer;
import cpw.mods.fml.relauncher.CoreModManager;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import net.tclproject.mysteriumlib.asm.core.MASMFix;
import net.tclproject.mysteriumlib.asm.core.MetaReader;
import net.tclproject.mysteriumlib.asm.core.MTargetClassTransformer;

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
	private static boolean obfuscated;
	/**A Metadata Reader instance for use inside this class.*/
    private static MetaReader mcMetaReader;

	public static boolean foundThaumcraft = false;
	public static boolean foundDragonAPI = false;
	public static boolean isDevEnvironment = false;
    
    public static File debugOutputLocation;

    static {
        mcMetaReader = new MinecraftMetaReader();
    }

    /**
     * Returns the transformer that we are using at the current moment in time to modify classes.
     * See why we have to use two separate ones in the documentation for FirstClassTransformer.
     * @return FirstClassTransformer if our built-in fixes haven't been applied, otherwise - MCustomClassTransformer.
     */
    public static MTargetClassTransformer getTransformer() {
        return FirstClassTransformer.instance.registeredBuiltinFixes ?
                MCustomClassTransformer.instance : FirstClassTransformer.instance;
    }

    /**
     * Registers a single manually made MASMFix.
     * It is not the most efficient way to make fixes, but if you want to go this way,
     * look at how the code already there builds an MASMFix out of a fix method or just
     * take a look at the documentation of the builder class within MASMFix.
     */
    public static void registerFix(MASMFix fix) {
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
            try {
                Field deobfuscatedField = CoreModManager.class.getDeclaredField("deobfuscatedEnvironment");
                deobfuscatedField.setAccessible(true);
                obfuscated = !deobfuscatedField.getBoolean(null);
            } catch (Exception e) {
            	FMLLog.log("Mysterium Patches", Level.ERROR, "Error occured when checking obfuscation.");
    			FMLLog.log("Mysterium Patches", Level.ERROR, "THIS IS MOST LIKELY HAPPENING BECAUSE OF MOD CONFLICTS. PLEASE CONTACT ME TO LET ME KNOW.");
    			FMLLog.log("Mysterium Patches", Level.ERROR, e.getMessage());
            }
            checkedObfuscation = true;
        }
        return obfuscated;
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
        if (((ArrayList)data.get("coremodList")).contains("DragonAPIASMHandler")) {
			foundDragonAPI = true;
		}

		// This is very crude check for mods presence using filename.
		// Some mods may refer to others in their name, so we'll to confirm those assumption with class presence check.
		File loc = (File)data.get("mcLocation");

		isDevEnvironment = !(Boolean)data.get("runtimeDeobfuscationEnabled");

		File mcFolder = new File(loc.getAbsolutePath() + File.separatorChar + "mods");
		File mcVersionFolder = new File(mcFolder.getAbsolutePath() + File.separatorChar + "1.7.10");
		ArrayList<File> subfiles = new ArrayList<>();
		if (mcFolder.listFiles() != null){
			subfiles = new ArrayList<>(Arrays.asList(mcFolder.listFiles()));
			if (mcVersionFolder.listFiles() != null){
				subfiles.addAll(Arrays.asList(mcVersionFolder.listFiles()));
			}
		}
		for (File file : subfiles){
			String name = file.getName();
			if (name != null) {
				name = name.toLowerCase();
				if (name.endsWith(".jar") || name.endsWith(".zip")){
					if (name.contains("thaumcraft")){
						LogHelper.info("Core: Located Thaumcraft in " + file.getName());
						foundThaumcraft = true;
					}else if (name.contains("optifine")){
						LogHelper.info("Core: Located OptiFine in " + file.getName() + ". We'll to confirm that...");
						MysteriumPatchesFixLoaderMagicka.foundOptiFine = true;
					}else if (name.contains("dragonapi")){
						LogHelper.info("Core: Located DragonAPI in " + file.getName());
						foundDragonAPI = true;
					}
					// Can look for if certain mods are loaded here
					
				}
			}
		}
        registerFixes();
    }
    
    public void registerFixes() {
    }
}
