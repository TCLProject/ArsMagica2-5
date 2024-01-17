package am2;

import am2.api.ArsMagicaApi;
import am2.api.spell.enums.Affinity;
import am2.armor.infusions.ImbuementRegistry;
import am2.blocks.RecipesEssenceRefiner;
import am2.blocks.liquid.BlockLiquidEssence;
import am2.blocks.tileentities.flickers.*;
import am2.buffs.BuffList;
import am2.commands.*;
import am2.configuration.AMConfig;
import am2.configuration.SkillConfiguration;
import am2.customdata.CustomGameData;
import am2.customdata.CustomWorldData;
import am2.enchantments.AMEnchantmentHelper;
import am2.entities.GenericEntityTemplateRegistry;
import am2.entities.EntityManager;
import am2.entities.SpawnBlacklists;
import am2.interop.GTNHInterModComm;
import am2.interop.TC4Interop;
import am2.items.ItemsCommonProxy;
import am2.network.AMNetHandler;
import am2.network.SeventhSanctum;
import am2.network.TickrateMessage;
import am2.network.TickrateMessageHandler;
import am2.playerextensions.AffinityData;
import am2.playerextensions.ExtendedProperties;
import am2.playerextensions.RiftStorage;
import am2.playerextensions.SkillData;
import am2.power.PowerNodeCache;
import am2.proxy.CommonProxy;
import am2.spell.SkillManager;
import am2.spell.SkillTreeManager;
import am2.spell.SpellUtils;
import am2.utility.KeystoneUtilities;
import am2.worldgen.BiomeWitchwoodForest;
import am2.worldgen.SCLWorldProvider;
import am2.worldgen.smartgen.GenerationConstants;
import am2.worldgen.smartgen.GenericRegistry;
import am2.worldgen.smartgen.reccomplexutils.FMLRemapper;
import am2.worldgen.smartgen.reccomplexutils.FMLRemapperConvenience;
import am2.worldgen.smartgen.reccomplexutils.MCRegistryRemapping;
import am2.worldgen.smartgen.reccomplexutils.rcpackets.*;
import am2.worldgen.smartgen.registry.MCRegistrySpecial;
import am2.worldgen.smartgen.schematics.SchematicLoader;
import am2.worldgen.smartgen.struct.files.RCFileTypeRegistry;
import com.tfc.minecraft_effekseer_implementation.Command;
import com.tfc.minecraft_effekseer_implementation.MEI;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.*;
import cpw.mods.fml.common.event.FMLInterModComms.IMCMessage;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ivorius.ivtoolkit.network.PacketExtendedEntityPropertiesData;
import ivorius.ivtoolkit.network.PacketExtendedEntityPropertiesDataHandler;
import ivorius.ivtoolkit.network.PacketGuiAction;
import ivorius.ivtoolkit.network.PacketGuiActionHandler;
import ivorius.ivtoolkit.tools.MCRegistry;
import ivorius.ivtoolkit.tools.MCRegistryDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandManager;
import net.minecraft.command.ServerCommandManager;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.common.*;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.common.BiomeManager.BiomeEntry;
import net.minecraftforge.common.BiomeManager.BiomeType;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidContainerRegistry.FluidContainerData;
import net.minecraftforge.fluids.FluidRegistry;
import net.tclproject.mysteriumlib.render.dae.hea3ven.colladamodel.client.model.lib.ColladaModelLoader;
import net.tclproject.mysteriumlib.render.dae.hea3ven.colladamodel.client.model.lib.ModelManager;
import net.tclproject.mysteriumlib.render.dae.hea3ven.colladamodel.client.model.test.TestDaeMod;
import net.tclproject.mysteriumlib.render.gecko.GeckoLib;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

import static am2.preloader.BytecodeTransformers.checkIsRecurrentComplexFilePresent;
import static net.tclproject.mysteriumlib.asm.fixes.MysteriumPatchesFixLoaderMagicka.isRecurrentComplexPresent;

//@Mod(modid = "arsmagica2", modLanguage = "java", name = "Ars Magica 2", version = "1.6.5", dependencies = "required-after:AnimationAPI;required-after:llibrary;required-after:ivtoolkit;required-after:CoFHCore")
@Mod(modid = "arsmagica2", modLanguage = "java", name = "Ars Magica 2", version = "1.6.5", dependencies = "required-after:AnimationAPI;required-after:llibrary;required-after:ivtoolkit")
public class AMCore{

	@Instance(value = "arsmagica2")
	public static AMCore instance;

	@SidedProxy(clientSide = "am2.proxy.ClientProxy", serverSide = "am2.proxy.CommonProxy")
	public static CommonProxy proxy;

	public static AMConfig config;
	public static SkillConfiguration skillConfig;
	public static final int ANY_META = 32767;
	public static SimpleNetworkWrapper NETWORK;
	public static SimpleNetworkWrapper network;
	public static boolean rcpresent = false;

	private String compendiumBase;

	public static final boolean USE_JSON_FOR_NBT = true;
	public static final boolean USE_ZIP_FOR_STRUCTURE_FILES = true;
	public static RCFileTypeRegistry fileTypeRegistry;

	public static MCRegistrySpecial specialRegistry;
	public static MCRegistry mcRegistry;
	public static FMLRemapper remapper;
	public static FMLRemapperConvenience cremapper;

	public static final Logger logger = LogManager.getLogger("Ars Magica 2.5");

	public AMCore(){
	}

	@EventHandler
	public void preInit(FMLPreInitializationEvent event){
		rcpresent = (isRecurrentComplexPresent() || checkIsRecurrentComplexFilePresent());

		if (event.getSide() == Side.CLIENT) {
			AdvancedModelLoader.registerModelHandler(new ColladaModelLoader());
		}

		String configBase = event.getSuggestedConfigurationFile().getAbsolutePath();
		configBase = popPathFolder(configBase);
		compendiumBase = popPathFolder(configBase);

		configBase += File.separatorChar + "AM2" + File.separatorChar;

		config = new AMConfig(new File(configBase + File.separatorChar + "AM2.cfg"));

		GenerationConstants.loadConfig();

		remapper = new FMLRemapper("reccomplex"); // compatibility for RC format builds
		specialRegistry = new MCRegistrySpecial(mcRegistry = new MCRegistryRemapping(new MCRegistryDefault(), remapper), remapper);
		cremapper = new FMLRemapperConvenience(specialRegistry, remapper);

		fileTypeRegistry = new RCFileTypeRegistry();

		skillConfig = new SkillConfiguration(new File(configBase + "SkillConf.cfg"));

		NETWORK = NetworkRegistry.INSTANCE.newSimpleChannel("AM2TickrateChanger");
		NETWORK.registerMessage(TickrateMessageHandler.class, TickrateMessage.class, 0, Side.CLIENT);

		AMNetHandler.INSTANCE.init();
		GeckoLib.pre(event);
		TestDaeMod.pre(event);

		proxy.InitializeAndRegisterHandlers();
		proxy.preinit();

		MEI.instance = new MEI();
		MinecraftForge.EVENT_BUS.register(MEI.instance);
		FMLCommonHandler.instance().bus().register(MEI.instance);
		MEI.preInit(event);

		GenericRegistry.preInit(event, this);
	}

	private String popPathFolder(String path){
		int lastIndex = path.lastIndexOf(File.separatorChar);
		if (lastIndex == -1)
			lastIndex = path.length() - 1; //no path separator...strange, but ok.  Use full string.
		return path.substring(0, lastIndex);
	}

	@EventHandler
	public void init(FMLInitializationEvent event){

		FMLInterModComms.sendMessage("Waila", "register", "am2.interop.WailaSupport.callbackRegister");

		ForgeChunkManager.setForcedChunkLoadingCallback(this, AMChunkLoader.INSTANCE);
		proxy.init();
		MEI.init(event);

		initAPI();
		GenericEntityTemplateRegistry.init();
		TestDaeMod.modInit(event);

		DimensionManager.registerProviderType(config.getMMFDimensionID(), SCLWorldProvider.class, false);
		DimensionManager.registerDimension(config.getMMFDimensionID(), config.getMMFDimensionID());
		GeckoLib.init(event);

		if (AMCore.config.getEnableWitchwoodForest()){
			BiomeDictionary.registerBiomeType(BiomeWitchwoodForest.instance, Type.FOREST, Type.MAGICAL);
			BiomeManager.addBiome(BiomeType.WARM, new BiomeEntry(BiomeWitchwoodForest.instance, 6));
		}

		network = NetworkRegistry.INSTANCE.newSimpleChannel("AM2StructureGeneration");
		network.registerMessage(PacketExtendedEntityPropertiesDataHandler.class, PacketExtendedEntityPropertiesData.class, 0, Side.CLIENT);
		network.registerMessage(PacketGuiActionHandler.class, PacketGuiAction.class, 1, Side.SERVER);
		network.registerMessage(PacketEditInventoryGeneratorHandler.class, PacketEditInventoryGenerator.class, 2, Side.CLIENT);
		network.registerMessage(PacketEditInventoryGeneratorHandler.class, PacketEditInventoryGenerator.class, 3, Side.SERVER);
		network.registerMessage(PacketEditTileEntityHandler.class, PacketEditTileEntity.class, 4, Side.CLIENT);
		network.registerMessage(PacketEditTileEntityHandler.class, PacketEditTileEntity.class, 5, Side.SERVER);
		network.registerMessage(PacketEditStructureHandler.class, PacketEditStructure.class, 6, Side.CLIENT);
		network.registerMessage(PacketEditStructureHandler.class, PacketEditStructure.class, 7, Side.SERVER);
		network.registerMessage(PacketSyncItemHandler.class, PacketSyncItem.class, 8, Side.CLIENT);
		network.registerMessage(PacketSyncItemHandler.class, PacketSyncItem.class, 9, Side.SERVER);
		network.registerMessage(PacketItemEventHandler.class, PacketItemEvent.class, 10, Side.CLIENT);
		network.registerMessage(PacketItemEventHandler.class, PacketItemEvent.class, 11, Side.SERVER);

		GenericRegistry.load(event, this);
		GTNHInterModComm.activate();
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event){
		//Register Flicker Operators
		registerFlickerOperators();

		proxy.setCompendiumSaveBase(compendiumBase);
		proxy.postinit();

		if (config.retroactiveWorldgen()){
			LogHelper.info("Retroactive Worldgen is enabled");
		}

		FluidContainerRegistry.registerFluidContainer(
				new FluidContainerData(
						FluidRegistry.getFluidStack(BlockLiquidEssence.liquidEssenceFluid.getName(), FluidContainerRegistry.BUCKET_VOLUME),
						new ItemStack(ItemsCommonProxy.itemAMBucket),
						FluidContainerRegistry.EMPTY_BUCKET));

		SeventhSanctum.instance.init();
//		if (Loader.isModLoaded("BetterDungeons"))
//			BetterDungeons.init();
		if (Loader.isModLoaded("Thaumcraft"))
			TC4Interop.initialize();
//		if (Loader.isModLoaded("MineFactoryReloaded"))
//			MFRInterop.init();

		for (String modid : Loader.instance().getIndexedModList().keySet())
			if (modid.equalsIgnoreCase("arsmagica2")) fileTypeRegistry.loadFilesFromMod(modid); // only our structures
		fileTypeRegistry.reloadCustomFiles();
		SchematicLoader.initializeFolder();

		try {
			Class.forName("forestry.api.recipes.RecipeManagers", false, getClass().getClassLoader());
			Class.forName("magicbees.bees.BeeProductHelper", false, getClass().getClassLoader());
			Class.forName("magicbees.bees.BeeSpecies", false, getClass().getClassLoader());
			AMBeeCompat.init();
		} catch (ClassNotFoundException e) {
			LogHelper.info("A compatible MagicBees version was not found, compat not loading.");
		}

	}

	private void registerFlickerOperators(){
		FlickerOperatorRegistry.instance.registerFlickerOperator(
				new FlickerOperatorItemTransport(),
				Affinity.AIR
		);
		FlickerOperatorRegistry.instance.registerFlickerOperator(
				new FlickerOperatorButchery(),
				Affinity.FIRE, Affinity.LIFE
		);
		FlickerOperatorRegistry.instance.registerFlickerOperator(
				new FlickerOperatorContainment(),
				Affinity.AIR, Affinity.ENDER
		);
		FlickerOperatorRegistry.instance.registerFlickerOperator(
				new FlickerOperatorFelledOak(),
				Affinity.NATURE, Affinity.LIGHTNING
		);
		FlickerOperatorRegistry.instance.registerFlickerOperator(
				new FlickerOperatorFlatLands(),
				Affinity.EARTH, Affinity.ICE
		);
		FlickerOperatorRegistry.instance.registerFlickerOperator(
				new FlickerOperatorGentleRains(),
				Affinity.WATER
		);
		FlickerOperatorRegistry.instance.registerFlickerOperator(
				new FlickerOperatorInterdiction(),
				Affinity.AIR, Affinity.ARCANE
		);
		FlickerOperatorRegistry.instance.registerFlickerOperator(
				new FlickerOperatorLight(),
				Affinity.FIRE, Affinity.LIGHTNING
		);
		FlickerOperatorRegistry.instance.registerFlickerOperator(
				new FlickerOperatorMoonstoneAttractor(),
				Affinity.LIGHTNING, Affinity.ARCANE, Affinity.EARTH
		);
		FlickerOperatorRegistry.instance.registerFlickerOperator(
				new FlickerOperatorNaturesBounty(),
				Affinity.NATURE, Affinity.WATER, Affinity.LIFE
		);
		FlickerOperatorRegistry.instance.registerFlickerOperator(
				new FlickerOperatorPackedEarth(),
				Affinity.EARTH
		);
		FlickerOperatorRegistry.instance.registerFlickerOperator(
				new FlickerOperatorProgeny(),
				Affinity.LIFE
		);
		FlickerOperatorRegistry.instance.registerFlickerOperator(
				new FlickerOperatorFishing(),
				Affinity.WATER, Affinity.NATURE
		);
	}

	@EventHandler
	public void serverStarting(FMLServerStartingEvent event){
		ICommandManager commandManager = event.getServer().getCommandManager();
		ServerCommandManager serverCommandManager = ((ServerCommandManager)commandManager);
		serverCommandManager.registerCommand(new SetMagicLevelCommand());
		serverCommandManager.registerCommand(new UnlockAugmentedCastingCommand());
		serverCommandManager.registerCommand(new SetAffinityCommand());
		serverCommandManager.registerCommand(new ShiftAffinityCommand());
		serverCommandManager.registerCommand(new RecoverKeystoneCommand());
		serverCommandManager.registerCommand(new RegisterTeamHostilityCommand());
		serverCommandManager.registerCommand(new FillManaBarCommand());
		serverCommandManager.registerCommand(new ReloadSkillTree());
		serverCommandManager.registerCommand(new GiveSkillPoints());
		serverCommandManager.registerCommand(new TakeSkillPoints());
		serverCommandManager.registerCommand(new ClearKnownSpellParts());
		serverCommandManager.registerCommand(new Explosions());
		serverCommandManager.registerCommand(new DumpNBT());
		serverCommandManager.registerCommand(new Respec());
		serverCommandManager.registerCommand(new UnlockCompendiumEntry());
		event.registerServerCommand((ICommand)new Command());
	}

	@EventHandler
	public void serverStarted(FMLServerStartedEvent event){
		// custom data
		CustomWorldData.loadAllWorldData();
		CustomGameData.loadGameData(); // this should (I hope!) work, because this method would (I hope!) get called both on singleplayer and on servers
	}

	@EventHandler
	public void serverStopping(FMLServerStoppingEvent event){
		for (WorldServer ws : MinecraftServer.getServer().worldServers){
			PowerNodeCache.instance.saveWorldToFile(ws);
		}
		// custom data
		CustomWorldData.saveAllWorldData();
	}

	@EventHandler
	public void onIMCReceived(FMLInterModComms.IMCEvent event){
		for (IMCMessage msg : event.getMessages()){
			if (msg.key == "dsb"){
				LogHelper.info("Received dimension spawn blacklist IMC!  Processing.");
				String[] split = msg.getStringValue().split("|");
				if (split.length != 2){
					LogHelper.warn("Could not parse dsb IMC - malformed identifiers!  Syntax is 'ClassName|DimensionID', for example:  EntityDryad|22");
					continue;
				}
				try{
					SpawnBlacklists.addBlacklistedDimensionSpawn(split[0], Integer.parseInt(split[1]));
				}catch (NumberFormatException nex){
					LogHelper.warn("Could not parse dsb IMC - improper dimension ID (not a number)!  Syntax is 'ClassName|DimensionID', for example:  EntityDryad|22");
				}
			}else if (msg.key == "bsb"){
				LogHelper.info("Received biome spawn blacklist IMC!  Processing.");
				String[] split = msg.getStringValue().split("|");
				if (split.length != 2){
					LogHelper.warn("Could not parse bsb IMC - malformed identifiers!  Syntax is 'ClassName|BiomeID', for example:  EntityDryad|22");
					continue;
				}
				try{
					SpawnBlacklists.addBlacklistedBiomeSpawn(split[0], Integer.parseInt(split[1]));
				}catch (NumberFormatException nex){
					LogHelper.warn("Could not parse bsb IMC - improper biome ID (not a number)!  Syntax is 'ClassName|BiomeID', for example:  EntityDryad|22");
				}
			}else if (msg.key == "dwg"){
				LogHelper.info("Received dimension worldgen blacklist IMC!  Processing.");
				try{
					SpawnBlacklists.addBlacklistedDimensionForWorldgen(Integer.parseInt(msg.getStringValue()));
				}catch (NumberFormatException nex){
					LogHelper.warn("Could not parse dwg IMC - improper dimension ID (not a number)!  Syntax is 'dimensionID', for example:  2");
				}
			}else if (msg.key == "adb"){
				LogHelper.info("Received dispel blacklist IMC!  Processing.");
				try{
					BuffList.instance.addDispelExclusion(Integer.parseInt(msg.getStringValue()));
				}catch (NumberFormatException nex){
					LogHelper.warn("Could not parse adb IMC - improper potion ID (not a number)!  Syntax is 'potionID', for example:  10");
				}
			}
		}
	}

	public void initAPI(){
		LogHelper.info("Initializing API Hooks...");
		ArsMagicaApi.instance.setSpellPartManager(SkillManager.instance);
		ArsMagicaApi.instance.setEnchantmentHelper(new AMEnchantmentHelper());
		ArsMagicaApi.instance.setSkillTreeManager(SkillTreeManager.instance);
		ArsMagicaApi.instance.setKeystoneHelper(KeystoneUtilities.instance);
		ArsMagicaApi.instance.setEntityManager(EntityManager.instance);
		ArsMagicaApi.instance.setObeliskFuelHelper(ObeliskFuelHelper.instance);
		ArsMagicaApi.instance.setFlickerOperatorRegistry(FlickerOperatorRegistry.instance);
		ArsMagicaApi.instance.setInfusionRegistry(ImbuementRegistry.instance);
		ArsMagicaApi.instance.setEssenceRecipeHandler(RecipesEssenceRefiner.essenceRefinement());
		ArsMagicaApi.instance.setColourblindMode(config.colourblindMode());
		ArsMagicaApi.instance.setBuffHelper(BuffList.instance);
		ArsMagicaApi.instance.setSpellUtils(SpellUtils.instance);

		ArsMagicaApi.instance.setAffinityDataID(AffinityData.identifier);
		ArsMagicaApi.instance.setSkillDataID(SkillData.identifier);
		ArsMagicaApi.instance.setExtendedPropertiesID(ExtendedProperties.identifier);
		ArsMagicaApi.instance.setRiftStorageID(RiftStorage.identifier);
		LogHelper.info("Finished API Initialization");
	}

	public String getVersion(){
		Mod modclass = this.getClass().getAnnotation(Mod.class);
		return modclass.version();
	}
}
