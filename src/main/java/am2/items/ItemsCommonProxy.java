package am2.items;

import am2.AMCore;
import am2.AMCreativeTab;
import am2.LogHelper;
import am2.api.flickers.IFlickerFunctionality;
import am2.api.spell.enums.Affinity;
import am2.armor.*;
import am2.blocks.BlocksCommonProxy;
import am2.blocks.tileentities.flickers.FlickerOperatorRegistry;
import am2.enchantments.AMEnchantmentHelper;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionHelper;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import net.tclproject.mysteriumlib.asm.fixes.MysteriumPatchesFixesMagicka;

import java.util.ArrayList;
import java.util.List;

public class ItemsCommonProxy{

	//Core Crafting Components
	public static ItemOre itemOre;

	//various technological intermediates
	public static ItemTechJunk techJunk;

	//Essences
	public static ItemEssence essence;

	public static ItemEssenceBag essenceBag;

	//spell casting items
	public static ArsMagicaItem spellParchment;
	public static ItemSpellBook spellBook;

	//runes
	public static ItemRune rune;

	public static AMSpawnEgg spawnEgg;

	public static ItemFocusLesser lesserFocus;
	public static ItemFocusStandard standardFocus;
	public static ItemFocusGreater greaterFocus;
	public static ItemFocusMana manaFocus;
	public static ItemFocusCharge chargeFocus;
	public static ItemPlayerFocus playerFocus;
	public static ItemMobFocus mobFocus;
	public static ItemItemFocus itemFocus;
	public static ItemCreatureFocus creatureFocus;
	public static ItemFlickerFocus flickerFocus;

	public static Item wizardChalk;

	public static Item boundCatalystHelmet;
	public static Item boundCatalystArmor;
	public static Item boundCatalystLeggings;
	public static Item boundCatalystBoots;

	public static ItemManaCake manaCake;

	public static ItemKeystone keystone;

	//affinity books
	public static ItemAffinityBook bookAffinity;

	public static ItemArcaneCompendium arcaneCompendium;

	public static ItemManaPotion lesserManaPotion;
	public static ItemManaPotion standardManaPotion;
	public static ItemManaPotion greaterManaPotion;
	public static ItemManaPotion epicManaPotion;
	public static ItemManaPotion legendaryManaPotion;
	public static ItemManaMartini manaMartini;
	public static ItemAstrocalis astrocalis;
	public static ItemCasparazoid casparazoid;
	public static ItemLiquidEssenceBottle liquidEssenceBottle;

	public static ItemBoxOfIllusions boxOfIllusions;

	public static ItemManaPotionBundle manaPotionBundle;
	public static ItemFlickerJar flickerJar;
	public static ItemJournal playerjournal;

	//spell staves
	public static ItemSpellStaff spellStaffMagitech;
	public static ItemSpellStaff spellStaffNovice;
	public static ItemSpellStaff spellStaffJourneyman;
	public static ItemSpellStaff spellStaffMaster;

	public static ItemCrystalWrench crystalWrench;
	public static ItemArcaneFishingRod itemArcaneFishingRod;
	public static ItemInfernalFishingRod itemInfernalFishingRod;

	public static ItemSpellPart spell_component;

	public static ItemAMBucket itemAMBucket;

	public static ItemMagicBroom magicBroom;

	public static ArsMagicaItem workbenchUpgrade;

	public static ItemBindingCatalyst bindingCatalyst;
	public static ItemKeystoneDoor itemKeystoneDoor;
	public static ItemCandle wardingCandle;
	public static ItemRuneBag runeBag;
	public static InscriptionTableUpgrade inscriptionUpgrade;

	//hee hee
	public static ItemNatureGuardianSickle scythe;
	public static ItemArcaneGuardianSpellbook arcaneSpellbook;
	public static ItemWinterGuardianArm winterGuardianArm;
	public static ItemAirSled airGuardianLower;
	public static ItemEarthGuardianArmor earthGuardianArmor;
	public static ItemWaterGuardianOrbs waterGuardianOrbs;
	public static ItemFireGuardianEars fireEars;
	public static ItemEnderBoots enderBoots;
	public static ItemLightningCharm lightningCharm;
	public static ItemLifeWard lifeWard;
	public static Item evilBook;
	public static Item woodenLeg;
	public static Item testItem;
	public static ItemHellCowHorn cowHorn;

	public static Item recordBlues;
	public static Item recordGreens;
	public static Item recordReds;
	public static Item recordEnder;

	//--------------------------------------------------------------
	// Spells
	//--------------------------------------------------------------
	//public static ArsMagicaItem spellRecipeScroll;
	//--------------------------------------------------------------
	// End Spells
	//--------------------------------------------------------------

	//armor
	public static Item mageHood;
	public static Item mageArmor;
	public static Item mageLeggings;
	public static Item mageBoots;

	public static Item battlemageHood;
	public static Item battlemageArmor;
	public static Item battlemageLeggings;
	public static Item battlemageBoots;

	public static Item archmageHood;
	public static Item archmageArmor;
	public static Item archmageLeggings;
	public static Item archmageBoots;

	//bound items
	public static Item BoundHoe;
	public static Item BoundShovel;
	public static Item BoundAxe;
	public static Item BoundPickaxe;
	public static Item BoundSword;

	public static Item BoundHelmet;
	public static Item BoundArmor;
	public static Item BoundLeggings;
	public static Item BoundBoots;

	public static ItemTrumpet trumpet;

	public static ItemManaStone manaStone;
	public static ItemSoulspike soulspike;
	public static ItemEtheriumExtractor etheriumExtractor;

	public static Item BoundBow;

	public static ItemCrystalPhylactery crystalPhylactery;
	public static ItemMagitechGoggles magitechGoggles;
	public static ArsMagicaItem deficitCrystal;

	public static AMCreativeTab itemTab;

	public static ItemSeeds stormSawtoothSeeds;
	public static ItemSeeds rainRockroseSeeds;
	public static ItemSeeds imbuedMoonflowerSeeds;

	/**
	 * Indicates what armors have additional protective effects which need to be handled by the AMEventHandler class.
	 */
	public static Item[] protectiveArmors;
	public static SpellBase spell;

	public static ItemStack natureScytheEnchanted;
	public static ItemStack arcaneSpellBookEnchanted;
	public static ItemStack winterArmEnchanted;
	public static ItemStack fireEarsEnchanted;
	public static ItemStack earthArmorEnchanted;
	public static ItemStack airSledEnchanted;
	public static ItemStack enderBootsEnchanted;
	public static ItemStack waterOrbsEnchanted;
	public static ItemStack lightningCharmEnchanted;
	public static ItemStack lifeWardEnchanted;
	public static AMCreativeTab summonsTab;

	private final ArrayList<Item> arsMagicaItemsList;

	public static ToolMaterial soulMat = EnumHelper.addToolMaterial("Soul", 0, 1000, 4.0F, 4F, 100);

	public ItemsCommonProxy(){
		itemTab = new AMCreativeTab("Ars Magica Items");
		summonsTab = new AMCreativeTab("Ars Magica Summons [WIP]");
		arsMagicaItemsList = new ArrayList<Item>();
	}

	public List<Item> getArsMagicaItems(){
		return arsMagicaItemsList;
	}

	public void InstantiateItems(){

		LogHelper.trace("Instantiating Items");

		// --  WIP  -- //
		// TODO: REMOVE
		testItem = new ItemTestItem().setUnlocalizedName("arsmagica2:test_item").setTextureName("arsmagica2:empty").setCreativeTab(summonsTab);
		// --  WIP  -- //

		itemOre = (ItemOre)new ItemOre().setUnlocalizedAndTextureName("arsmagica2:itemOre").setCreativeTab(itemTab);
		techJunk = (ItemTechJunk) new ItemTechJunk().setUnlocalizedAndTextureName("arsmagica2:techJunk").setCreativeTab(itemTab);
		essence = (ItemEssence)new ItemEssence().setUnlocalizedAndTextureName("arsmagica2:essence").setCreativeTab(itemTab);
		spellBook = (ItemSpellBook)new ItemSpellBook().setUnlocalizedAndTextureName("arsmagica2:spell_book").setCreativeTab(itemTab);
		spellParchment = (ArsMagicaItem)new ArsMagicaItem().setUnlocalizedAndTextureName("arsmagica2:spell_parchment").setCreativeTab(itemTab);
		spell = (SpellBase)new SpellBase().setUnlocalizedName("arsmagica2:spell_base");
		rune = (ItemRune)new ItemRune(0).setUnlocalizedAndTextureName("runes").setCreativeTab(itemTab);
		mageHood = (new ItemMageHood(ArmorMaterial.GOLD, ArsMagicaArmorMaterial.MAGE, ArmorHelper.getArmorRenderIndex("mage"), 0)).setUnlocalizedAndTextureName("arsmagica2:helmet_mage").setCreativeTab(itemTab);
		mageArmor = (new AMArmor(ArmorMaterial.GOLD, ArsMagicaArmorMaterial.MAGE, ArmorHelper.getArmorRenderIndex("mage"), 1)).setUnlocalizedAndTextureName("arsmagica2:chest_mage").setCreativeTab(itemTab);
		mageLeggings = (new AMArmor(ArmorMaterial.GOLD, ArsMagicaArmorMaterial.MAGE, ArmorHelper.getArmorRenderIndex("mage"), 2)).setUnlocalizedAndTextureName("arsmagica2:legs_mage").setCreativeTab(itemTab);
		mageBoots = (new AMArmor(ArmorMaterial.GOLD, ArsMagicaArmorMaterial.MAGE, ArmorHelper.getArmorRenderIndex("mage"), 3)).setUnlocalizedAndTextureName("arsmagica2:boots_mage").setCreativeTab(itemTab);
		battlemageHood = (new ItemMageHood(ArmorMaterial.IRON, ArsMagicaArmorMaterial.BATTLEMAGE, ArmorHelper.getArmorRenderIndex("battlemage"), 0)).setUnlocalizedAndTextureName("arsmagica2:helmet_battlemage").setCreativeTab(itemTab);
		battlemageArmor = (new AMArmor(ArmorMaterial.IRON, ArsMagicaArmorMaterial.BATTLEMAGE, ArmorHelper.getArmorRenderIndex("battlemage"), 1)).setUnlocalizedAndTextureName("arsmagica2:chest_battlemage").setCreativeTab(itemTab);
		battlemageLeggings = (new AMArmor(ArmorMaterial.IRON, ArsMagicaArmorMaterial.BATTLEMAGE, ArmorHelper.getArmorRenderIndex("battlemage"), 2)).setUnlocalizedAndTextureName("arsmagica2:legs_battlemage").setCreativeTab(itemTab);
		battlemageBoots = (new AMArmor(ArmorMaterial.IRON, ArsMagicaArmorMaterial.BATTLEMAGE, ArmorHelper.getArmorRenderIndex("battlemage"), 3)).setUnlocalizedAndTextureName("arsmagica2:boots_battlemage").setCreativeTab(itemTab);
		BoundHelmet = (new BoundArmor(ArmorMaterial.IRON, ArsMagicaArmorMaterial.BOUND, ArmorHelper.getArmorRenderIndex("bound"), 0)).setUnlocalizedAndTextureName("arsmagica2:bound_helmet").setCreativeTab(itemTab);
		BoundArmor = (new BoundArmor(ArmorMaterial.IRON, ArsMagicaArmorMaterial.BOUND, ArmorHelper.getArmorRenderIndex("bound"), 1)).setUnlocalizedAndTextureName("arsmagica2:bound_chest").setCreativeTab(itemTab);
		BoundLeggings = (new BoundArmor(ArmorMaterial.IRON, ArsMagicaArmorMaterial.BOUND, ArmorHelper.getArmorRenderIndex("bound"), 2)).setUnlocalizedAndTextureName("arsmagica2:bound_legs").setCreativeTab(itemTab);
		BoundBoots = (new BoundArmor(ArmorMaterial.IRON, ArsMagicaArmorMaterial.BOUND, ArmorHelper.getArmorRenderIndex("bound"), 3)).setUnlocalizedAndTextureName("arsmagica2:bound_boots").setCreativeTab(itemTab);
		archmageHood = (new AMArmor(ArmorMaterial.DIAMOND, ArsMagicaArmorMaterial.ARCHMAGE, ArmorHelper.getArmorRenderIndex("archmage"), 0)).setUnlocalizedAndTextureName("arsmagica2:helmet_archmage").setCreativeTab(itemTab).setMaxDamage(0);
		archmageArmor = new AMArmor(ArmorMaterial.DIAMOND, ArsMagicaArmorMaterial.ARCHMAGE, ArmorHelper.getArmorRenderIndex("archmage"), 1) {
			public void onArmorTick(World world, EntityPlayer player, ItemStack itemStack){
				if (player.ticksExisted % 20 == 0 && player instanceof EntityLivingBase){
					float abs = ((EntityLivingBase)player).getAbsorptionAmount();
					if (abs < 38){ // 2 rows of hearts
						abs++;
						if (!world.isRemote) ((EntityLivingBase)player).setAbsorptionAmount(abs);
					}
				}
			}
		}.setUnlocalizedAndTextureName("arsmagica2:chest_archmage").setCreativeTab(itemTab).setMaxDamage(0);
		stormSawtoothSeeds = (ItemSeeds)(new ItemSeeds(BlocksCommonProxy.stormSawtooth, Blocks.soul_sand) {
			@Override
			public EnumPlantType getPlantType(IBlockAccess world, int x, int y, int z)
			{
				return EnumPlantType.Nether;
			}
		}.setUnlocalizedName("arsmagica2:sawtoothseeds").setTextureName("arsmagica2:sawtoothseeds").setPotionEffect(PotionHelper.blazePowderEffect).setCreativeTab(itemTab));
		imbuedMoonflowerSeeds = (ItemSeeds)((new ItemSeeds(BlocksCommonProxy.imbuedMoonflower, Blocks.farmland)).setUnlocalizedName("arsmagica2:moonflowerseeds").setTextureName("arsmagica2:moonflowerseeds").setCreativeTab(itemTab));
		rainRockroseSeeds = (ItemSeeds)((new ItemSeeds(BlocksCommonProxy.rainRockrose, Blocks.farmland)).setUnlocalizedName("arsmagica2:rockroseseeds").setTextureName("arsmagica2:rockroseseeds").setCreativeTab(itemTab));
		archmageLeggings = (new AMArmor( ArmorMaterial.DIAMOND, ArsMagicaArmorMaterial.ARCHMAGE, ArmorHelper.getArmorRenderIndex("archmage"), 2)).setUnlocalizedAndTextureName("arsmagica2:legs_archmage").setCreativeTab(itemTab).setMaxDamage(0);
		archmageBoots = (new AMArmor(ArmorMaterial.DIAMOND, ArsMagicaArmorMaterial.ARCHMAGE, ArmorHelper.getArmorRenderIndex("archmage"), 3)).setUnlocalizedAndTextureName("arsmagica2:boots_archmage").setCreativeTab(itemTab).setMaxDamage(0);
		wizardChalk = new ItemChalk().setUnlocalizedAndTextureName("arsmagica2:wizardChalk").setCreativeTab(itemTab);
		lesserFocus = (ItemFocusLesser)new ItemFocusLesser().setUnlocalizedAndTextureName("arsmagica2:lesserfocus").setCreativeTab(itemTab);
		standardFocus = (ItemFocusStandard)new ItemFocusStandard().setUnlocalizedAndTextureName("arsmagica2:standardfocus").setCreativeTab(itemTab);
		greaterFocus = (ItemFocusGreater)new ItemFocusGreater().setUnlocalizedAndTextureName("arsmagica2:greaterfocus").setCreativeTab(itemTab);
		manaFocus = (ItemFocusMana)new ItemFocusMana().setUnlocalizedAndTextureName("arsmagica2:manafocus").setCreativeTab(itemTab);
		chargeFocus = (ItemFocusCharge)new ItemFocusCharge().setUnlocalizedAndTextureName("arsmagica2:chargefocus").setCreativeTab(itemTab);
		bookAffinity = (ItemAffinityBook)new ItemAffinityBook().setUnlocalizedAndTextureName("arsmagica2:affinity_tome").setCreativeTab(itemTab);
		keystone = (ItemKeystone)new ItemKeystone().setUnlocalizedAndTextureName("arsmagica2:keystone").setCreativeTab(itemTab);
		playerFocus = (ItemPlayerFocus)new ItemPlayerFocus().setUnlocalizedAndTextureName("arsmagica2:player_focus").setCreativeTab(itemTab);
		mobFocus = (ItemMobFocus)new ItemMobFocus().setUnlocalizedAndTextureName("arsmagica2:monster_focus").setCreativeTab(itemTab);
		itemFocus = (ItemItemFocus)new ItemItemFocus().setUnlocalizedAndTextureName("arsmagica2:item_focus").setCreativeTab(itemTab);
		creatureFocus = (ItemCreatureFocus)new ItemCreatureFocus().setUnlocalizedAndTextureName("arsmagica2:creature_focus").setCreativeTab(itemTab);
		recordBlues = (ItemBossRecord)new ItemBossRecord("fightingblues").setUnlocalizedName("arsmagica2:record1").setCreativeTab(itemTab);
		recordGreens = (ItemBossRecord)new ItemBossRecord("greensymphony").setUnlocalizedName("arsmagica2:record2").setCreativeTab(itemTab);
		recordReds = (ItemBossRecord)new ItemBossRecord("redrevival").setUnlocalizedName("arsmagica2:record3").setCreativeTab(itemTab);
		recordEnder = (ItemBossRecord)new ItemBossRecord("canyouheartheend").setUnlocalizedName("arsmagica2:record4").setCreativeTab(itemTab);
		BoundHoe = new ItemBoundHoe(ToolMaterial.IRON).setUnlocalizedAndTextureName("arsmagica2:bound_hoe");
		BoundAxe = new ItemBoundAxe(ToolMaterial.IRON).setUnlocalizedAndTextureName("arsmagica2:bound_axe");
		BoundPickaxe = new ItemBoundPickaxe(ToolMaterial.IRON).setUnlocalizedAndTextureName("arsmagica2:bound_pickaxe");
		BoundShovel = new ItemBoundShovel(ToolMaterial.IRON).setUnlocalizedAndTextureName("arsmagica2:bound_shovel");
		BoundSword = new ItemBoundSword(ToolMaterial.IRON).setUnlocalizedAndTextureName("arsmagica2:bound_sword");
		BoundBow = new ItemBoundBow().setUnlocalizedAndTextureName("arsmagica2:bound_bow");
		manaCake = (ItemManaCake)new ItemManaCake().setUnlocalizedAndTextureName("arsmagica2:mana_cake").setAlwaysEdible().setCreativeTab(itemTab);
		lesserManaPotion = (ItemManaPotion)new ItemManaPotion().setUnlocalizedAndTextureName("arsmagica2:mana_potion_lesser").setCreativeTab(itemTab);
		standardManaPotion = (ItemManaPotion)new ItemManaPotion().setUnlocalizedAndTextureName("arsmagica2:mana_potion_standard").setCreativeTab(itemTab);
		greaterManaPotion = (ItemManaPotion)new ItemManaPotion().setUnlocalizedAndTextureName("arsmagica2:mana_potion_greater").setCreativeTab(itemTab);
		epicManaPotion = (ItemManaPotion)new ItemManaPotion().setUnlocalizedAndTextureName("arsmagica2:mana_potion_epic").setCreativeTab(itemTab);
		legendaryManaPotion = (ItemManaPotion)new ItemManaPotion().setUnlocalizedAndTextureName("arsmagica2:mana_potion_legendary").setCreativeTab(itemTab);
		deficitCrystal = (ArsMagicaItem)new ArsMagicaItem().setUnlocalizedAndTextureName("arsmagica2:deficit_crystal").setCreativeTab(itemTab);
		essenceBag = (ItemEssenceBag)new ItemEssenceBag().setUnlocalizedAndTextureName("arsmagica2:essence_bag").setCreativeTab(itemTab);
		manaPotionBundle = (ItemManaPotionBundle)new ItemManaPotionBundle().setUnlocalizedAndTextureName("arsmagica2:mana_potion_bundle").setCreativeTab(itemTab);
		crystalWrench = (ItemCrystalWrench)new ItemCrystalWrench().setUnlocalizedAndTextureName("arsmagica2:crystal_wrench").setCreativeTab(itemTab);
		itemArcaneFishingRod = (ItemArcaneFishingRod) new ItemArcaneFishingRod().setUnlocalizedAndTextureName("arsmagica2:arcane_rod").setCreativeTab(itemTab);
		itemInfernalFishingRod = (ItemInfernalFishingRod) new ItemInfernalFishingRod().setUnlocalizedAndTextureName("arsmagica2:infernal_rod").setCreativeTab(itemTab);
		spawnEgg = (AMSpawnEgg)new AMSpawnEgg().setUnlocalizedAndTextureName("am_spawnegg").setCreativeTab(summonsTab);
		arcaneCompendium = (ItemArcaneCompendium)new ItemArcaneCompendium().setUnlocalizedAndTextureName("arsmagica2:ArcaneCompendium").setCreativeTab(itemTab);
		spell_component = new ItemSpellPart();
		crystalPhylactery = (ItemCrystalPhylactery)new ItemCrystalPhylactery().setUnlocalizedAndTextureName("arsmagica2:crystal_phylactery").setCreativeTab(itemTab);
		itemAMBucket = (ItemAMBucket)new ItemAMBucket().setUnlocalizedAndTextureName("arsmagica2:liquidEssenceBucket").setCreativeTab(itemTab);
		scythe = (ItemNatureGuardianSickle)new ItemNatureGuardianSickle().setUnlocalizedName("arsmagica2:nature_scythe").setCreativeTab(itemTab);
		spellStaffNovice = (ItemSpellStaff)(new ItemSpellStaff(300, 0)).setStaffHeadIndex(0).setUnlocalizedAndTextureName("arsmagica2:spell_staff_novice").setCreativeTab(itemTab);
		spellStaffJourneyman = (ItemSpellStaff)(new ItemSpellStaff(900, 1)).setStaffHeadIndex(1).setUnlocalizedAndTextureName("arsmagica2:spell_staff_journeyman").setCreativeTab(itemTab);
		spellStaffMaster = (ItemSpellStaff)(new ItemSpellStaff(5000, 2)).setStaffHeadIndex(2).setUnlocalizedAndTextureName("arsmagica2:spell_staff_master").setCreativeTab(itemTab);
		spellStaffMagitech = (ItemSpellStaff)new ItemSpellStaff(0, -1).setStaffHeadIndex(3).setUnlocalizedAndTextureName("arsmagica2:spell_staff_magitech").setCreativeTab(itemTab);
		liquidEssenceBottle = (ItemLiquidEssenceBottle)new ItemLiquidEssenceBottle().setUnlocalizedAndTextureName("arsmagica2:mana_boost_potion").setCreativeTab(itemTab);
		evilBook = new Item().setUnlocalizedName("arsmagica2:evilBook").setTextureName("arsmagica2:evilBook").setCreativeTab(itemTab);
		trumpet = (ItemTrumpet)new ItemTrumpet().setUnlocalizedAndTextureName("arsmagica2:trumpet").setCreativeTab(itemTab);
		woodenLeg = new Item().setUnlocalizedName("arsmagica2:woodenLeg").setTextureName("arsmagica2:woodenLeg").setCreativeTab(itemTab);
		boundCatalystHelmet = new Item().setUnlocalizedName("arsmagica2:bound_helmet_catalyst").setTextureName("arsmagica2:bound_helmet_catalyst").setCreativeTab(itemTab);
		boundCatalystArmor =new Item().setUnlocalizedName("arsmagica2:bound_chestplate_catalyst").setTextureName("arsmagica2:bound_chestplate_catalyst").setCreativeTab(itemTab);
		boundCatalystLeggings =new Item().setUnlocalizedName("arsmagica2:bound_leggings_catalyst").setTextureName("arsmagica2:bound_leggings_catalyst").setCreativeTab(itemTab);
		boundCatalystBoots =new Item().setUnlocalizedName("arsmagica2:bound_boots_catalyst").setTextureName("arsmagica2:bound_boots_catalyst").setCreativeTab(itemTab);
		cowHorn = (ItemHellCowHorn)new ItemHellCowHorn().setUnlocalizedName("arsmagica2:cowhorn").setCreativeTab(itemTab);
		magicBroom = (ItemMagicBroom)new ItemMagicBroom().setUnlocalizedAndTextureName("arsmagica2:magic_broom").setCreativeTab(itemTab);
		boxOfIllusions = (ItemBoxOfIllusions) new ItemBoxOfIllusions().setUnlocalizedAndTextureName("arsmagica2:boxOfIllusions").setCreativeTab(itemTab);
		//witchwoodSlab = new ItemSlab(AMCore.config.getConfigurableItemID("witchwood__doubleslab_placer", 20119), BlocksCommonProxy.witchwoodSingleSlab, BlocksCommonProxy.witchwoodDoubleSlab, true);
		arcaneSpellbook = (ItemArcaneGuardianSpellbook)new ItemArcaneGuardianSpellbook().setUnlocalizedName("arsmagica2:arcane_spellbook").setCreativeTab(itemTab);
		winterGuardianArm = (ItemWinterGuardianArm)new ItemWinterGuardianArm().setUnlocalizedName("arsmagica2:winter_arm").setCreativeTab(itemTab);
		airGuardianLower = (ItemAirSled)new ItemAirSled().setUnlocalizedName("arsmagica2:air_sled").setCreativeTab(itemTab);
		earthGuardianArmor = (ItemEarthGuardianArmor)new ItemEarthGuardianArmor(ArmorMaterial.GOLD, ArsMagicaArmorMaterial.UNIQUE, 0, 1).setUnlocalizedName("arsmagica2:earth_armor").setCreativeTab(itemTab);
		waterGuardianOrbs = (ItemWaterGuardianOrbs)new ItemWaterGuardianOrbs(ArmorMaterial.GOLD, ArsMagicaArmorMaterial.UNIQUE, 0, 2).setUnlocalizedName("arsmagica2:water_orbs").setCreativeTab(itemTab);
		fireEars = (ItemFireGuardianEars)new ItemFireGuardianEars(ArmorMaterial.GOLD, ArsMagicaArmorMaterial.UNIQUE, 0, 0).setUnlocalizedName("arsmagica2:fire_ears").setCreativeTab(itemTab);
		workbenchUpgrade = (ArsMagicaItem)new ArsMagicaItem().setUnlocalizedAndTextureName("arsmagica2:workbenchUpgrade").setCreativeTab(itemTab);
		bindingCatalyst = (ItemBindingCatalyst)new ItemBindingCatalyst().setUnlocalizedName("arsmagica2:bindingCatalyst").setCreativeTab(itemTab);
		itemKeystoneDoor = (ItemKeystoneDoor)new ItemKeystoneDoor().setUnlocalizedName("arsmagica2:keystoneDoor").setCreativeTab(BlocksCommonProxy.blockTab);
		enderBoots = (ItemEnderBoots)new ItemEnderBoots(ArmorMaterial.GOLD, ArsMagicaArmorMaterial.UNIQUE, ArmorHelper.getArmorRenderIndex("ender"), 3).setUnlocalizedName("arsmagica2:enderBoots").setCreativeTab(itemTab);
		wardingCandle = (ItemCandle)new ItemCandle().setUnlocalizedAndTextureName("arsmagica2:warding_candle").setCreativeTab(itemTab);
		magitechGoggles = (ItemMagitechGoggles)new ItemMagitechGoggles(ArmorHelper.getArmorRenderIndex("magitech")).setUnlocalizedAndTextureName("arsmagica2:magitechGoggles").setCreativeTab(itemTab);
		flickerJar = (ItemFlickerJar)new ItemFlickerJar().setUnlocalizedAndTextureName("arsmagica2:flicker_jar").setCreativeTab(itemTab);
		runeBag = (ItemRuneBag)new ItemRuneBag().setUnlocalizedName("arsmagica2:runebag").setCreativeTab(itemTab);
		lightningCharm = (ItemLightningCharm)new ItemLightningCharm().setUnlocalizedAndTextureName("arsmagica2:lightningcharm").setCreativeTab(itemTab);
		lifeWard = (ItemLifeWard)new ItemLifeWard().setUnlocalizedAndTextureName("arsmagica2:lifeward").setCreativeTab(itemTab);
		flickerFocus = (ItemFlickerFocus)new ItemFlickerFocus().setUnlocalizedAndTextureName("arsmagica2:flickerFocus").setCreativeTab(itemTab);
		playerjournal = (ItemJournal)new ItemJournal().setUnlocalizedName("arsmagica2:playerjournal").setCreativeTab(itemTab);
		manaMartini = (ItemManaMartini)new ItemManaMartini().setUnlocalizedAndTextureName("arsmagica2:mana_martini").setCreativeTab(itemTab);
		astrocalis = (ItemAstrocalis) new ItemAstrocalis().setUnlocalizedAndTextureName("arsmagica2:astrocalis").setCreativeTab(itemTab);
		casparazoid = (ItemCasparazoid) new ItemCasparazoid().setUnlocalizedAndTextureName("arsmagica2:casparazoid").setCreativeTab(itemTab);
		inscriptionUpgrade = (InscriptionTableUpgrade)new InscriptionTableUpgrade().setUnlocalizedName("arsmagica2:inscription_upgrade").setCreativeTab(itemTab);
		manaStone = (ItemManaStone)new ItemManaStone().setUnlocalizedAndTextureName("arsmagica2:mana_stone").setCreativeTab(itemTab);
		soulspike = (ItemSoulspike) new ItemSoulspike(soulMat).setUnlocalizedAndTextureName("arsmagica2:soulspike").setCreativeTab(itemTab);
		etheriumExtractor = (ItemEtheriumExtractor) new ItemEtheriumExtractor().setUnlocalizedAndTextureName("arsmagica2:etheriumExtractor").setCreativeTab(itemTab);

		addItemStackToChestGen(new ItemStack(rune, 1, rune.META_INF_ORB_BLUE), 1, 1, 3, ChestGenHooks.DUNGEON_CHEST, ChestGenHooks.MINESHAFT_CORRIDOR, ChestGenHooks.PYRAMID_DESERT_CHEST, ChestGenHooks.PYRAMID_JUNGLE_CHEST, ChestGenHooks.STRONGHOLD_CORRIDOR, ChestGenHooks.STRONGHOLD_CROSSING);
		addItemStackToChestGen(new ItemStack(rune, 1, rune.META_INF_ORB_BLUE), 1, 1, 5, ChestGenHooks.STRONGHOLD_LIBRARY);

		addItemStackToChestGen(new ItemStack(rune, 1, rune.META_INF_ORB_GREEN), 1, 1, 1, ChestGenHooks.DUNGEON_CHEST, ChestGenHooks.MINESHAFT_CORRIDOR, ChestGenHooks.PYRAMID_DESERT_CHEST, ChestGenHooks.PYRAMID_JUNGLE_CHEST, ChestGenHooks.STRONGHOLD_CORRIDOR, ChestGenHooks.STRONGHOLD_CROSSING);
		addItemStackToChestGen(new ItemStack(rune, 1, rune.META_INF_ORB_GREEN), 1, 1, 1, ChestGenHooks.STRONGHOLD_LIBRARY);

		addItemStackToChestGen(new ItemStack(rune, 1, rune.META_INF_ORB_RED), 1, 1, 6, ChestGenHooks.STRONGHOLD_LIBRARY);

		protectiveArmors = new Item[]{
				mageHood, mageArmor, mageLeggings, mageBoots,
				battlemageHood, battlemageArmor, battlemageLeggings, battlemageBoots,
				archmageHood, archmageArmor, archmageLeggings, archmageBoots,
				BoundHelmet, BoundArmor, BoundLeggings, BoundBoots
		};

		RegisterItems();

		itemTab.setIconItemIndex(itemOre);
		summonsTab.setIconItemIndex(mobFocus);

		Affinity.NONE.setRepresentItem(essence, essence.META_BASE_CORE);
		Affinity.AIR.setRepresentItem(essence, essence.META_AIR);
		Affinity.ARCANE.setRepresentItem(essence, essence.META_ARCANE);
		Affinity.EARTH.setRepresentItem(essence, essence.META_EARTH);
		Affinity.ENDER.setRepresentItem(essence, essence.META_ENDER);
		Affinity.FIRE.setRepresentItem(essence, essence.META_FIRE);
		Affinity.ICE.setRepresentItem(essence, essence.META_ICE);
		Affinity.LIFE.setRepresentItem(essence, essence.META_LIFE);
		Affinity.LIGHTNING.setRepresentItem(essence, essence.META_LIGHTNING);
		Affinity.NATURE.setRepresentItem(essence, essence.META_NATURE);
		Affinity.WATER.setRepresentItem(essence, essence.META_WATER);

		natureScytheEnchanted = AMEnchantmentHelper.soulbindStack(new ItemStack(scythe));
		arcaneSpellBookEnchanted = AMEnchantmentHelper.soulbindStack(new ItemStack(arcaneSpellbook));
		winterArmEnchanted = AMEnchantmentHelper.soulbindStack(new ItemStack(winterGuardianArm));
		fireEarsEnchanted = AMEnchantmentHelper.soulbindStack(new ItemStack(fireEars));
		earthArmorEnchanted = AMEnchantmentHelper.soulbindStack(new ItemStack(earthGuardianArmor));
		airSledEnchanted = AMEnchantmentHelper.soulbindStack(new ItemStack(airGuardianLower));
		waterOrbsEnchanted = AMEnchantmentHelper.soulbindStack(new ItemStack(waterGuardianOrbs));
		enderBootsEnchanted = AMEnchantmentHelper.soulbindStack(new ItemStack(enderBoots));
		lightningCharmEnchanted = AMEnchantmentHelper.soulbindStack(new ItemStack(lightningCharm));
		lifeWardEnchanted = AMEnchantmentHelper.soulbindStack(new ItemStack(lifeWard));
	}

	private void addItemStackToChestGen(ItemStack stack, int min, int max, int weight, String... categories){
		for (String s : categories){
			ChestGenHooks.addItem(s, new WeightedRandomChestContent(stack, min, max, weight));
		}
	}

	private void RegisterItems(){
		registerItem(spell, "spellBase");
		registerItem(testItem, "testItem");
		registerItem(itemOre, "itemOre");
		registerItem(techJunk, "techJunk");
		//Essences
		registerItem(essence, "essence");
		registerItem(essenceBag, "essenceBag");

		//spell casting items
		registerItem(spellParchment, "spellParchment");
		registerItem(spellBook, "spellBook");

		//runes
		registerItem(rune, "rune");

		registerItem(lesserFocus, "lesserFocus");
		registerItem(standardFocus, "standardFocus");
		registerItem(greaterFocus, "greaterFocus");
		registerItem(manaFocus, "manaFocus");
		registerItem(chargeFocus, "chargeFocus");
		registerItem(playerFocus, "playerFocus");
		registerItem(mobFocus, "mobFocus");
		registerItem(itemFocus, "itemFocus");
		registerItem(creatureFocus, "creatureFocus");
		registerItem(flickerFocus, "flickerFocus");

		registerItem(wizardChalk, "blueChalk");

		registerItem(manaCake, "manaCake");

		registerItem(keystone, "keystone");

		registerItem(manaStone, "manaStone");
		registerItem(soulspike, "soulspike");
		registerItem(etheriumExtractor, "etheriumExtractor");

		registerItem(imbuedMoonflowerSeeds, "imbuedMoonflowerSeeds");
		registerItem(rainRockroseSeeds, "rainRockroseSeeds");
		registerItem(stormSawtoothSeeds, "stormSawtoothSeeds");

		//affinity books
		registerItem(bookAffinity, "bookAffinity");

		registerItem(lesserManaPotion, "lesserManaPotion");
		registerItem(standardManaPotion, "standardManaPotion");
		registerItem(greaterManaPotion, "greaterManaPotion");
		registerItem(epicManaPotion, "epicManaPotion");
		registerItem(legendaryManaPotion, "legendaryManaPotion");

		registerItem(manaPotionBundle, "manaPotionBundle");

		//armor
		registerItem(mageHood, "mageHood");
		registerItem(mageArmor, "mageArmor");
		registerItem(mageLeggings, "mageLeggings");
		registerItem(mageBoots, "mageBoots");

		registerItem(battlemageHood, "battlemageHood");
		registerItem(battlemageArmor, "battlemageArmor");
		registerItem(battlemageLeggings, "battlemageLeggings");
		registerItem(battlemageBoots, "battlemageBoots");

		registerItem(BoundHelmet, "boundHood");
		registerItem(BoundArmor, "boundArmor");
		registerItem(BoundLeggings, "boundLeggings");
		registerItem(BoundBoots, "boundBoots");

		registerItem(archmageHood, "ArchmageHood");
		registerItem(archmageArmor, "ArchmageArmor");
		registerItem(archmageLeggings, "ArchmageLeggings");
		registerItem(archmageBoots, "ArchmageBoots");

		registerItem(recordBlues, "recordBlues");
		registerItem(recordGreens, "recordGreens");
		registerItem(recordReds, "recordReds");
		registerItem(recordEnder, "recordEnder");

		//bound items
		registerItem(BoundHoe, "BoundHoeNor");
		registerItem(BoundShovel, "BoundShovelNor");
		registerItem(BoundAxe, "BoundAxeNor");
		registerItem(BoundPickaxe, "BoundPickaxeNor");
		registerItem(BoundSword, "BoundSwordNor");
		registerItem(BoundBow, "BoundBowNor");
		registerItem(spawnEgg, "AMSpawnEgg");

		registerItem(deficitCrystal, "deficitCrystal");

		registerItem(crystalWrench, "crystal_wrench");
		registerItem(itemArcaneFishingRod, "arcane_rod");
		registerItem(itemInfernalFishingRod, "infernal_rod");

		registerItem(arcaneCompendium, "ArcaneCompendium");
		registerItem(spell_component, "SpellComponent");

		registerItem(crystalPhylactery, "Crystal Phylactery");

		registerItem(itemAMBucket, "liquidEssenceBucket");

		registerItem(scythe, "natureScythe");

		registerItem(spellStaffNovice, "spellStaffNovice");
		registerItem(spellStaffJourneyman, "spellStaffJourneyman");
		registerItem(spellStaffMaster, "spellStaffMaster");
		registerItem(spellStaffMagitech, "magitechStaff");

		registerItem(liquidEssenceBottle, "liquidEssenceBottle");
		registerItem(bindingCatalyst, "bindingCatalyst");

		registerItem(evilBook, "evilBook");
		registerItem(trumpet, "trumpet");
		registerItem(woodenLeg, "woodenLeg");
		registerItem(cowHorn, "cowHorn");

		registerItem(boundCatalystArmor, "boundCatalystArmor");
		registerItem(boundCatalystBoots, "boundCatalystBoots");
		registerItem(boundCatalystHelmet, "boundCatalystHelmet");
		registerItem(boundCatalystLeggings, "boundCatalystLeggings");

		registerItem(magicBroom, "magicBroom");
		registerItem(boxOfIllusions, "boxOfIllusions");

		registerItem(arcaneSpellbook, "arcane_spellbook");
		registerItem(winterGuardianArm, "winter_arm");
		registerItem(airGuardianLower, "air_sled");
		registerItem(earthGuardianArmor, "earth_armor");
		registerItem(waterGuardianOrbs, "water_orbs");
		registerItem(fireEars, "fire_ears");

		registerItem(workbenchUpgrade, "workbenchUpgrade");
		registerItem(itemKeystoneDoor, "keystoneDoorPlacer");
		registerItem(enderBoots, "enderBoots");
		registerItem(wardingCandle, "wardingCandle");
		registerItem(magitechGoggles, "magitechGoggles");
		registerItem(flickerJar, "flickerJar");
		registerItem(runeBag, "runeBag");
		registerItem(lightningCharm, "lightningCharm");
		registerItem(lifeWard, "lifeWard");
		registerItem(playerjournal, "playerjournal");
		registerItem(manaMartini, "manaMartini");
		registerItem(astrocalis, "astrocalis");
		registerItem(casparazoid, "casparazoid");
		registerItem(inscriptionUpgrade, "inscriptionUpgrade");

		// tech modIDs registration
		MysteriumPatchesFixesMagicka.addTechModToList("Mekanism");
		MysteriumPatchesFixesMagicka.addTechModToList("MekanismTools");
		MysteriumPatchesFixesMagicka.addTechModToList("MekanismGenerators");
		MysteriumPatchesFixesMagicka.addTechModToList("ThermalExpansion");
		MysteriumPatchesFixesMagicka.addTechModToList("ThermalDynamics");
		MysteriumPatchesFixesMagicka.addTechModToList("ThermalFoundation");
		MysteriumPatchesFixesMagicka.addTechModToList("IC2");
		MysteriumPatchesFixesMagicka.addTechModToList("BuildCraft|Core");
		MysteriumPatchesFixesMagicka.addTechModToList("BuildCraft|Silicon");
		MysteriumPatchesFixesMagicka.addTechModToList("BuildCraft|Robotics");
		MysteriumPatchesFixesMagicka.addTechModToList("BuildCraft|Energy");
		MysteriumPatchesFixesMagicka.addTechModToList("BuildCraft|Transport");
		MysteriumPatchesFixesMagicka.addTechModToList("BuildCraft|Factory");
		MysteriumPatchesFixesMagicka.addTechModToList("BuildCraft|Builders");
		MysteriumPatchesFixesMagicka.addTechModToList("BuildCraft|Compat");
		MysteriumPatchesFixesMagicka.addTechModToList("appliedenergistics2");
		MysteriumPatchesFixesMagicka.addTechModToList("Railcraft");
		MysteriumPatchesFixesMagicka.addTechModToList("qCraft");
		MysteriumPatchesFixesMagicka.addTechModToList("powersuits");
		MysteriumPatchesFixesMagicka.addTechModToList("minechem");
		MysteriumPatchesFixesMagicka.addTechModToList("Forestry");
		MysteriumPatchesFixesMagicka.addTechModToList("GraviSuite");
		MysteriumPatchesFixesMagicka.addTechModToList("gregtech_addon");
		MysteriumPatchesFixesMagicka.addTechModToList("RedstoneArsenal");
		MysteriumPatchesFixesMagicka.addTechModToList("rftools");
		MysteriumPatchesFixesMagicka.addTechModToList("WarpDrive");
		MysteriumPatchesFixesMagicka.addTechModToList("NuclearCraft");
		MysteriumPatchesFixesMagicka.addTechModToList("NVLWarpdriveExtensions");
		MysteriumPatchesFixesMagicka.addTechModToList("MineFactoryReloaded");
		MysteriumPatchesFixesMagicka.addTechModToList("MineFactoryReloaded|CompatAppliedEnergistics");
		MysteriumPatchesFixesMagicka.addTechModToList("MineFactoryReloaded|CompatAtum");
		MysteriumPatchesFixesMagicka.addTechModToList("MineFactoryReloaded|CompatBackTools");
		MysteriumPatchesFixesMagicka.addTechModToList("MineFactoryReloaded|CompatBuildCraft");
		MysteriumPatchesFixesMagicka.addTechModToList("MineFactoryReloaded|CompatChococraft");
		MysteriumPatchesFixesMagicka.addTechModToList("MineFactoryReloaded|CompatExtraBiomes");
		MysteriumPatchesFixesMagicka.addTechModToList("MineFactoryReloaded|CompatForestry");
		MysteriumPatchesFixesMagicka.addTechModToList("MineFactoryReloaded|CompatForgeMicroblock");
		MysteriumPatchesFixesMagicka.addTechModToList("MineFactoryReloaded|CompatIC2");
		MysteriumPatchesFixesMagicka.addTechModToList("MineFactoryReloaded|CompatProjRed");
		MysteriumPatchesFixesMagicka.addTechModToList("MineFactoryReloaded|CompatRailcraft");
		MysteriumPatchesFixesMagicka.addTechModToList("MineFactoryReloaded|CompatSufficientBiomes");
		MysteriumPatchesFixesMagicka.addTechModToList("MineFactoryReloaded|CompatThaumcraft");
		MysteriumPatchesFixesMagicka.addTechModToList("MineFactoryReloaded|CompatThermalExpansion");
		MysteriumPatchesFixesMagicka.addTechModToList("MineFactoryReloaded|CompatTConstruct");
		MysteriumPatchesFixesMagicka.addTechModToList("MineFactoryReloaded|CompatTwilightForest");
		MysteriumPatchesFixesMagicka.addTechModToList("MineFactoryReloaded|CompatVanilla");
		MysteriumPatchesFixesMagicka.addTechModToList("OpenComputers");
		MysteriumPatchesFixesMagicka.addTechModToList("openmodularturrets");
		MysteriumPatchesFixesMagicka.addTechModToList("opensecurity");
		MysteriumPatchesFixesMagicka.addTechModToList("ProjRed|Core");
		MysteriumPatchesFixesMagicka.addTechModToList("ProjRed|Compatibility");
		MysteriumPatchesFixesMagicka.addTechModToList("ProjRed|Integration");
		MysteriumPatchesFixesMagicka.addTechModToList("ProjRed|Transmission");
		MysteriumPatchesFixesMagicka.addTechModToList("ProjRed|Illumination");
		MysteriumPatchesFixesMagicka.addTechModToList("ProjRed|Expansion");
		MysteriumPatchesFixesMagicka.addTechModToList("ProjRed|Transportation");
		MysteriumPatchesFixesMagicka.addTechModToList("StevesCarts");
		MysteriumPatchesFixesMagicka.addTechModToList("compactkineticgenerators");
		MysteriumPatchesFixesMagicka.addTechModToList("CompactMachines");
		MysteriumPatchesFixesMagicka.addTechModToList("CompactSolars");
		MysteriumPatchesFixesMagicka.addTechModToList("gendustry");
		MysteriumPatchesFixesMagicka.addTechModToList("gtnhlib");
		MysteriumPatchesFixesMagicka.addTechModToList("GregTech");
		MysteriumPatchesFixesMagicka.addTechModToList("gregtech");
		MysteriumPatchesFixesMagicka.addTechModToList("qwertech");
		MysteriumPatchesFixesMagicka.addTechModToList("IC2-Classic-Spmod");
		MysteriumPatchesFixesMagicka.addTechModToList("IC2NuclearControl");
		MysteriumPatchesFixesMagicka.addTechModToList("ihl");
		MysteriumPatchesFixesMagicka.addTechModToList("ImmersiveEngineering");
		MysteriumPatchesFixesMagicka.addTechModToList("rfdrills");
		MysteriumPatchesFixesMagicka.addTechModToList("mo");
		MysteriumPatchesFixesMagicka.addTechModToList("matteroverdrive");
		MysteriumPatchesFixesMagicka.addTechModToList("ExtraUtilities");
		MysteriumPatchesFixesMagicka.addTechModToList("EnderIO");
		MysteriumPatchesFixesMagicka.addTechModToList("Magneticraft");
		MysteriumPatchesFixesMagicka.addTechModToList("BigReactors");
		MysteriumPatchesFixesMagicka.addTechModToList("hbm");
		MysteriumPatchesFixesMagicka.addTechModToList("Eln");
		MysteriumPatchesFixesMagicka.addTechModToList("RotaryCraft");
		MysteriumPatchesFixesMagicka.addTechModToList("ReactorCraft");
		MysteriumPatchesFixesMagicka.addTechModToList("ElectriCraft");
		MysteriumPatchesFixesMagicka.addTechModToList("voltzengine");
		MysteriumPatchesFixesMagicka.addTechModToList("mffs");
		MysteriumPatchesFixesMagicka.addTechModToList("icbmclassic");
		MysteriumPatchesFixesMagicka.addTechModToList("atomicscience");
		MysteriumPatchesFixesMagicka.addTechModToList("ComputerCraft");
		MysteriumPatchesFixesMagicka.addTechModToList("ActuallyAdditions");
		MysteriumPatchesFixesMagicka.addTechModToList("PneumaticCraft");
		MysteriumPatchesFixesMagicka.addTechModToList("Steamcraft");
		MysteriumPatchesFixesMagicka.addTechModToList("steamcraft2");
		MysteriumPatchesFixesMagicka.addTechModToList("DraconicEvolution");
		MysteriumPatchesFixesMagicka.addTechModToList("Redpower");
		MysteriumPatchesFixesMagicka.addTechModToList("bluepower");
		MysteriumPatchesFixesMagicka.addTechModToList("progressiveautomation");
		MysteriumPatchesFixesMagicka.addTechModToList("factorization");
		MysteriumPatchesFixesMagicka.addTechModToList("OpenBlocks");
		MysteriumPatchesFixesMagicka.addTechModToList("WarpDriveCore");
		MysteriumPatchesFixesMagicka.addTechModToList("GalacticraftCore");
		MysteriumPatchesFixesMagicka.addTechModToList("GalacticraftMars");
		MysteriumPatchesFixesMagicka.addTechModToList("GalaxySpace");
		MysteriumPatchesFixesMagicka.addTechModToList("advancedRocketry");
		MysteriumPatchesFixesMagicka.addTechModToList("ExtraPlanets");
		MysteriumPatchesFixesMagicka.addTechModToList("ProjectE");
		MysteriumPatchesFixesMagicka.addTechModToList("gregapi");
		MysteriumPatchesFixesMagicka.addTechModToList("gregapi_post");
		MysteriumPatchesFixesMagicka.addTechModToList("lightningcraft");
		MysteriumPatchesFixesMagicka.addTechModToList("QuarryPlus");
		MysteriumPatchesFixesMagicka.addTechModToList("nuclearscience");
		MysteriumPatchesFixesMagicka.addTechModToList("nuclearphysics");
		MysteriumPatchesFixesMagicka.addTechModToList("forcefields");
		MysteriumPatchesFixesMagicka.addTechModToList("physica");
		MysteriumPatchesFixesMagicka.addTechModToList("PortalGun");
		MysteriumPatchesFixesMagicka.addTechModToList("portalgun");
		MysteriumPatchesFixesMagicka.addTechModToList("refinedstorage");
		MysteriumPatchesFixesMagicka.addTechModToList("tc");

		//registerItem(boundBow, "boundBow");

		OreDictionary.registerOre("dustVinteum", new ItemStack(itemOre, 1, itemOre.META_VINTEUMDUST));
		OreDictionary.registerOre("arcaneAsh", new ItemStack(itemOre, 1, itemOre.META_ARCANEASH));
		OreDictionary.registerOre("gemBlueTopaz", new ItemStack(itemOre, 1, itemOre.META_BLUETOPAZ));
		OreDictionary.registerOre("gemChimerite", new ItemStack(itemOre, 1, itemOre.META_CHIMERITE));
		OreDictionary.registerOre("gemMoonstone", new ItemStack(itemOre, 1, itemOre.META_MOONSTONE));
		OreDictionary.registerOre("gemSunstone", new ItemStack(itemOre, 1, itemOre.META_SUNSTONE));
	}

	private void registerItem(Item item, String name){
		arsMagicaItemsList.add(item);
		GameRegistry.registerItem(item, name);
	}

	public void InitRecipes(){

		GameRegistry.addRecipe(new ShapedOreRecipe(
				new ItemStack(bookAffinity, 1, bookAffinity.META_GENERAL),
				new Object[]
						{
								"VGV", "RBR", "VGV",
								Character.valueOf('V'), new ItemStack(ItemsCommonProxy.itemOre, 1, ItemsCommonProxy.itemOre.META_PURIFIEDVINTEUM),
								Character.valueOf('B'), Items.book,
								Character.valueOf('R'), deficitCrystal,
								Character.valueOf('G'), new ItemStack(ItemsCommonProxy.essence, 1, ItemsCommonProxy.essence.META_PURE),
						}));
		GameRegistry.addRecipe(new ShapedOreRecipe(
				new ItemStack(bookAffinity, 1, bookAffinity.META_ARCANE),
				new Object[]
						{
								"VGV", "RBR", "VGV",
								Character.valueOf('V'), new ItemStack(ItemsCommonProxy.itemOre, 1, ItemsCommonProxy.itemOre.META_PURIFIEDVINTEUM),
								Character.valueOf('B'), Items.book,
								Character.valueOf('R'), new ItemStack(ItemsCommonProxy.rune, 1, ItemsCommonProxy.rune.META_INF_ORB_RED),
								Character.valueOf('G'), new ItemStack(ItemsCommonProxy.essence, 1, ItemsCommonProxy.essence.META_ARCANE),
						}));
		GameRegistry.addRecipe(new ShapedOreRecipe(
				new ItemStack(bookAffinity, 1, bookAffinity.META_WATER),
				new Object[]
						{
								"VGV", "RBR", "VGV",
								Character.valueOf('V'), new ItemStack(ItemsCommonProxy.itemOre, 1, ItemsCommonProxy.itemOre.META_PURIFIEDVINTEUM),
								Character.valueOf('B'), Items.book,
								Character.valueOf('R'), new ItemStack(ItemsCommonProxy.rune, 1, ItemsCommonProxy.rune.META_INF_ORB_RED),
								Character.valueOf('G'), new ItemStack(ItemsCommonProxy.essence, 1, ItemsCommonProxy.essence.META_WATER),
						}));
		GameRegistry.addRecipe(new ShapedOreRecipe(
				new ItemStack(bookAffinity, 1, bookAffinity.META_FIRE),
				new Object[]
						{
								"VGV", "RBR", "VGV",
								Character.valueOf('V'), new ItemStack(ItemsCommonProxy.itemOre, 1, ItemsCommonProxy.itemOre.META_PURIFIEDVINTEUM),
								Character.valueOf('B'), Items.book,
								Character.valueOf('R'), new ItemStack(ItemsCommonProxy.rune, 1, ItemsCommonProxy.rune.META_INF_ORB_RED),
								Character.valueOf('G'), new ItemStack(ItemsCommonProxy.essence, 1, ItemsCommonProxy.essence.META_FIRE),
						}));
		GameRegistry.addRecipe(new ShapedOreRecipe(
				new ItemStack(bookAffinity, 1, bookAffinity.META_EARTH),
				new Object[]
						{
								"VGV", "RBR", "VGV",
								Character.valueOf('V'), new ItemStack(ItemsCommonProxy.itemOre, 1, ItemsCommonProxy.itemOre.META_PURIFIEDVINTEUM),
								Character.valueOf('B'), Items.book,
								Character.valueOf('R'), new ItemStack(ItemsCommonProxy.rune, 1, ItemsCommonProxy.rune.META_INF_ORB_RED),
								Character.valueOf('G'), new ItemStack(ItemsCommonProxy.essence, 1, ItemsCommonProxy.essence.META_EARTH),
						}));
		GameRegistry.addRecipe(new ShapedOreRecipe(
				new ItemStack(bookAffinity, 1, bookAffinity.META_AIR),
				new Object[]
						{
								"VGV", "RBR", "VGV",
								Character.valueOf('V'), new ItemStack(ItemsCommonProxy.itemOre, 1, ItemsCommonProxy.itemOre.META_PURIFIEDVINTEUM),
								Character.valueOf('B'), Items.book,
								Character.valueOf('R'), new ItemStack(ItemsCommonProxy.rune, 1, ItemsCommonProxy.rune.META_INF_ORB_RED),
								Character.valueOf('G'), new ItemStack(ItemsCommonProxy.essence, 1, ItemsCommonProxy.essence.META_AIR),
						}));
		GameRegistry.addRecipe(new ShapedOreRecipe(
				new ItemStack(bookAffinity, 1, bookAffinity.META_LIGHTNING),
				new Object[]
						{
								"VGV", "RBR", "VGV",
								Character.valueOf('V'), new ItemStack(ItemsCommonProxy.itemOre, 1, ItemsCommonProxy.itemOre.META_PURIFIEDVINTEUM),
								Character.valueOf('B'), Items.book,
								Character.valueOf('R'), new ItemStack(ItemsCommonProxy.rune, 1, ItemsCommonProxy.rune.META_INF_ORB_RED),
								Character.valueOf('G'), new ItemStack(ItemsCommonProxy.essence, 1, ItemsCommonProxy.essence.META_LIGHTNING),
						}));
		GameRegistry.addRecipe(new ShapedOreRecipe(
				new ItemStack(bookAffinity, 1, bookAffinity.META_ICE),
				new Object[]
						{
								"VGV", "RBR", "VGV",
								Character.valueOf('V'), new ItemStack(ItemsCommonProxy.itemOre, 1, ItemsCommonProxy.itemOre.META_PURIFIEDVINTEUM),
								Character.valueOf('B'), Items.book,
								Character.valueOf('R'), new ItemStack(ItemsCommonProxy.rune, 1, ItemsCommonProxy.rune.META_INF_ORB_RED),
								Character.valueOf('G'), new ItemStack(ItemsCommonProxy.essence, 1, ItemsCommonProxy.essence.META_ICE),
						}));
		GameRegistry.addRecipe(new ShapedOreRecipe(
				new ItemStack(bookAffinity, 1, bookAffinity.META_NATURE),
				new Object[]
						{
								"VGV", "RBR", "VGV",
								Character.valueOf('V'), new ItemStack(ItemsCommonProxy.itemOre, 1, ItemsCommonProxy.itemOre.META_PURIFIEDVINTEUM),
								Character.valueOf('B'), Items.book,
								Character.valueOf('R'), new ItemStack(ItemsCommonProxy.rune, 1, ItemsCommonProxy.rune.META_INF_ORB_RED),
								Character.valueOf('G'), new ItemStack(ItemsCommonProxy.essence, 1, ItemsCommonProxy.essence.META_NATURE),
						}));
		GameRegistry.addRecipe(new ShapedOreRecipe(
				new ItemStack(bookAffinity, 1, bookAffinity.META_LIFE),
				new Object[]
						{
								"VGV", "RBR", "VGV",
								Character.valueOf('V'), new ItemStack(ItemsCommonProxy.itemOre, 1, ItemsCommonProxy.itemOre.META_PURIFIEDVINTEUM),
								Character.valueOf('B'), Items.book,
								Character.valueOf('R'), new ItemStack(ItemsCommonProxy.rune, 1, ItemsCommonProxy.rune.META_INF_ORB_RED),
								Character.valueOf('G'), new ItemStack(ItemsCommonProxy.essence, 1, ItemsCommonProxy.essence.META_LIFE),
						}));
		GameRegistry.addRecipe(new ShapedOreRecipe(
				new ItemStack(bookAffinity, 1, bookAffinity.META_ENDER),
				new Object[]
						{
								"VGV", "RBR", "VGV",
								Character.valueOf('V'), new ItemStack(ItemsCommonProxy.itemOre, 1, ItemsCommonProxy.itemOre.META_PURIFIEDVINTEUM),
								Character.valueOf('B'), Items.book,
								Character.valueOf('R'), new ItemStack(ItemsCommonProxy.rune, 1, ItemsCommonProxy.rune.META_INF_ORB_RED),
								Character.valueOf('G'), new ItemStack(ItemsCommonProxy.essence, 1, ItemsCommonProxy.essence.META_ENDER),
						}));
		//crafting recipes
		GameRegistry.addRecipe(new ShapedOreRecipe(
				new ItemStack(itemOre, 1, itemOre.META_ARCANECOMPOUND),
				new Object[]
						{
								"BRN", "G G", "NRB",
								Character.valueOf('B'), "stone",
								Character.valueOf('R'), "dustRedstone",
								Character.valueOf('N'), Blocks.netherrack,
								Character.valueOf('G'), "dustGlowstone"
						}));

		//soulspike
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(soulspike),
				new Object[]{
						"FOF",
						"FOF",
						"EBE",
						Character.valueOf('O'), Blocks.obsidian,
						Character.valueOf('B'), Items.blaze_rod,
						Character.valueOf('F'), new ItemStack(itemOre, 1, itemOre.META_SOULFRAGMENT),
						Character.valueOf('E'), liquidEssenceBottle,
				}));

		//spell crafting
		GameRegistry.addRecipe(new ShapedOreRecipe(
				new ItemStack(spellParchment, 1),
				new Object[]{
						"S",
						"P",
						"S",
						Character.valueOf('S'), "stickWood",
						Character.valueOf('P'), Items.paper
				}));

		//spell book
		GameRegistry.addRecipe(
				new ItemStack(spellBook, 1),
				new Object[]{
						"SLL",
						"SPP",
						"SLL",
						Character.valueOf('S'), Items.string,
						Character.valueOf('L'), Items.leather,
						Character.valueOf('P'), Items.paper
				});

		//crystal wrench
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(crystalWrench),
				new Object[]{
						"I I",
						"AVD",
						" I ",
						Character.valueOf('I'), "ingotIron",
						Character.valueOf('A'), BlocksCommonProxy.cerublossom,
						Character.valueOf('D'), BlocksCommonProxy.desertNova,
						Character.valueOf('V'), "dustVinteum",
				}));

		//arcane rod
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemArcaneFishingRod),
				new Object[]{
						" V ",
						"ERE",
						" V ",
						Character.valueOf('R'), Items.fishing_rod,
						Character.valueOf('E'), new ItemStack(ItemsCommonProxy.essence, 1, ItemsCommonProxy.essence.META_ENDER),
						Character.valueOf('V'), "dustVinteum",
				}));

		//infernal rod
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemInfernalFishingRod),
				new Object[]{
						" V ",
						"ERE",
						" V ",
						Character.valueOf('R'), itemArcaneFishingRod,
						Character.valueOf('E'), new ItemStack(ItemsCommonProxy.essence, 1, ItemsCommonProxy.essence.META_FIRE),
						Character.valueOf('V'), new ItemStack(ItemsCommonProxy.itemOre, 1, ItemsCommonProxy.itemOre.META_ARCANEASH),
				}));

		//temporal cluster
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemOre, 1, itemOre.META_TEMPORALCLUSTER),
				new Object[]{
						" D ",
						"FCF",
						" D ",
						Character.valueOf('D'), new ItemStack(ItemsCommonProxy.itemOre, 1, ItemsCommonProxy.itemOre.META_CHIMERITE),
						Character.valueOf('C'), new ItemStack(ItemsCommonProxy.itemOre, 1, ItemsCommonProxy.itemOre.META_SPATIALSTAR),
						Character.valueOf('F'), new ItemStack(ItemsCommonProxy.itemOre, 1, ItemsCommonProxy.itemOre.META_FRACTALFRAGMENT),
				}));

		//fragments combining & breaking down
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ItemsCommonProxy.itemOre, 1, ItemsCommonProxy.itemOre.META_SUNSTONE),
				new Object[]{
						"FF",
						"FF",
						Character.valueOf('F'), new ItemStack(ItemsCommonProxy.itemOre, 1, ItemsCommonProxy.itemOre.META_SUNSTONEFRAGMENT),
				}));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ItemsCommonProxy.itemOre, 1, ItemsCommonProxy.itemOre.META_MOONSTONE),
				new Object[]{
						"FF",
						"FF",
						Character.valueOf('F'), new ItemStack(ItemsCommonProxy.itemOre, 1, ItemsCommonProxy.itemOre.META_MOONSTONEFRAGMENT),
				}));
		GameRegistry.addShapelessRecipe(
				new ItemStack(ItemsCommonProxy.itemOre, 4, ItemsCommonProxy.itemOre.META_MOONSTONEFRAGMENT), new Object[]{
						new ItemStack(ItemsCommonProxy.itemOre, 1, ItemsCommonProxy.itemOre.META_MOONSTONE)
				});
		GameRegistry.addShapelessRecipe(
				new ItemStack(ItemsCommonProxy.itemOre, 4, ItemsCommonProxy.itemOre.META_SUNSTONEFRAGMENT), new Object[]{
						new ItemStack(ItemsCommonProxy.itemOre, 1, ItemsCommonProxy.itemOre.META_SUNSTONE)
				});

		//mana stone
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(manaStone),
				new Object[]{
						"PGP",
						"GDG",
						"PGP",
						Character.valueOf('G'), "ingotGold",
						Character.valueOf('D'), "gemDiamond",
						Character.valueOf('P'), "dustVinteum",
				}));

		//mana stone
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(trumpet),
				new Object[]{
						" GG",
						"GVA",
						" GG",
						Character.valueOf('G'), "ingotGold",
						Character.valueOf('A'), new ItemStack(ItemsCommonProxy.essence, 1, ItemsCommonProxy.essence.META_AIR),
						Character.valueOf('V'), "dustVinteum",
				}));

		//extractor
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(etheriumExtractor),
				new Object[]{
						" T ",
						"G G",
						" I ",
						Character.valueOf('T'), "gemBlueTopaz",
						Character.valueOf('G'), "blockGlassColorless",
						Character.valueOf('I'), "ingotIron",
				}));

		//spell book colors
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(spellBook, 1, 0), new Object[]{"dyeBrown", new ItemStack(spellBook, 1, AMCore.ANY_META)}));  //brown
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(spellBook, 1, 1), new Object[]{"dyeCyan", new ItemStack(spellBook, 1, AMCore.ANY_META)}));  //cyan
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(spellBook, 1, 2), new Object[]{"dyeGray", new ItemStack(spellBook, 1, AMCore.ANY_META)}));  //gray
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(spellBook, 1, 3), new Object[]{"dyeLightBlue", new ItemStack(spellBook, 1, AMCore.ANY_META)}));  //light blue
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(spellBook, 1, 4), new Object[]{"dyeWhite", new ItemStack(spellBook, 1, AMCore.ANY_META)}));  //white
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(spellBook, 1, 5), new Object[]{"dyeBlack", new ItemStack(spellBook, 1, AMCore.ANY_META)}));  //black
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(spellBook, 1, 6), new Object[]{"dyeOrange", new ItemStack(spellBook, 1, AMCore.ANY_META)}));  //orange
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(spellBook, 1, 7), new Object[]{"dyePurple", new ItemStack(spellBook, 1, AMCore.ANY_META)}));  //purple
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(spellBook, 1, 8), new Object[]{"dyeBlue", new ItemStack(spellBook, 1, AMCore.ANY_META)}));  //blue
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(spellBook, 1, 9), new Object[]{"dyeGreen", new ItemStack(spellBook, 1, AMCore.ANY_META)}));  //green
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(spellBook, 1, 10), new Object[]{"dyeYellow", new ItemStack(spellBook, 1, AMCore.ANY_META)}));  //yellow
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(spellBook, 1, 11), new Object[]{"dyeRed", new ItemStack(spellBook, 1, AMCore.ANY_META)}));  //red
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(spellBook, 1, 12), new Object[]{"dyeLime", new ItemStack(spellBook, 1, AMCore.ANY_META)}));  //lime
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(spellBook, 1, 13), new Object[]{"dyePink", new ItemStack(spellBook, 1, AMCore.ANY_META)}));  //pink
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(spellBook, 1, 14), new Object[]{"dyeMagenta", new ItemStack(spellBook, 1, AMCore.ANY_META)}));  //magenta
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(spellBook, 1, 15), new Object[]{"dyeLightGray", new ItemStack(spellBook, 1, AMCore.ANY_META)}));  //light gray

		GameRegistry.addRecipe(new ItemStack(runeBag), new Object[]{
				"LLL", "W W", "LLL",
				Character.valueOf('L'), Items.leather,
				Character.valueOf('W'), new ItemStack(Blocks.wool, 1, AMCore.ANY_META),
		});

		GameRegistry.addRecipe(new ShapedOreRecipe(magicBroom, new Object[]{
				" S ",
				"ASA",
				" H ",
				Character.valueOf('S'), "stickWood",
				Character.valueOf('A'), "arcaneAsh",
				Character.valueOf('H'), Blocks.hay_block
		}));

		GameRegistry.addRecipe(new ShapedOreRecipe(boxOfIllusions, new Object[]{
				"WBW",
				"TCT",
				"WSW",
				Character.valueOf('S'), new ItemStack(ItemsCommonProxy.itemOre, 1, ItemsCommonProxy.itemOre.META_SOULFRAGMENT),
				Character.valueOf('B'), new ItemStack(ItemsCommonProxy.itemOre, 1, ItemsCommonProxy.itemOre.META_COGNITIVEDUST),
				Character.valueOf('T'), new ItemStack(ItemsCommonProxy.itemOre, 1, ItemsCommonProxy.itemOre.META_BLUETOPAZ),
				Character.valueOf('C'), Blocks.chest,
				Character.valueOf('W'), new ItemStack(Blocks.wool, 1, AMCore.ANY_META),
		}));

		GameRegistry.addRecipe(new ShapedOreRecipe(woodenLeg, new Object[]{
				"P",
				"L",
				"S",
				Character.valueOf('P'), "plankWood",
				Character.valueOf('L'), "slabWood",
				Character.valueOf('S'), "stickWood"
		}));

		GameRegistry.addRecipe(new ShapedOreRecipe(boundCatalystHelmet, new Object[]{
				"ASA",
				"EIE",
				"ASA",
				Character.valueOf('S'), Items.slime_ball,
				Character.valueOf('I'), Items.iron_helmet,
				Character.valueOf('A'), "arcaneAsh",
				Character.valueOf('E'), Items.ender_eye
		}));

		GameRegistry.addRecipe(new ShapedOreRecipe(boundCatalystArmor, new Object[]{
				"ASA",
				"BIB",
				"ASA",
				Character.valueOf('S'), Items.slime_ball,
				Character.valueOf('I'), Items.iron_chestplate,
				Character.valueOf('A'), "arcaneAsh",
				Character.valueOf('B'), Items.blaze_powder
		}));

		GameRegistry.addRecipe(new ShapedOreRecipe(boundCatalystLeggings, new Object[]{
				"ASA",
				"EIE",
				"ASA",
				Character.valueOf('S'), Items.slime_ball,
				Character.valueOf('I'), Items.iron_leggings,
				Character.valueOf('A'), "arcaneAsh",
				Character.valueOf('E'), Items.experience_bottle
		}));

		GameRegistry.addRecipe(new ShapedOreRecipe(boundCatalystBoots, new Object[]{
				"ASA",
				"NIN",
				"ASA",
				Character.valueOf('S'), Items.slime_ball,
				Character.valueOf('I'), Items.iron_boots,
				Character.valueOf('A'), "arcaneAsh",
				Character.valueOf('N'), Items.gold_nugget
		}));

		GameRegistry.addShapelessRecipe(
				new ItemStack(evilBook), new Object[]{
						new ItemStack(woodenLeg),
						new ItemStack(arcaneCompendium)
				});

		GameRegistry.addShapelessRecipe(new ItemStack(playerjournal), new Object[]{
				new ItemStack(ItemsCommonProxy.essence, 1, ItemsCommonProxy.essence.META_ARCANE),
				new ItemStack(Items.writable_book)
		});

		GameRegistry.addShapelessRecipe(
				new ItemStack(manaPotionBundle, 1, (0 << 8) + 3), new Object[]{
						new ItemStack(lesserManaPotion, 1, AMCore.ANY_META),
						new ItemStack(lesserManaPotion, 1, AMCore.ANY_META),
						new ItemStack(lesserManaPotion, 1, AMCore.ANY_META),
						new ItemStack(Items.string)
				});

		GameRegistry.addShapelessRecipe(
				new ItemStack(manaPotionBundle, 1, (1 << 8) + 3), new Object[]{
						new ItemStack(standardManaPotion, 1, AMCore.ANY_META),
						new ItemStack(standardManaPotion, 1, AMCore.ANY_META),
						new ItemStack(standardManaPotion, 1, AMCore.ANY_META),
						new ItemStack(Items.string)
				});

		GameRegistry.addShapelessRecipe(
				new ItemStack(manaPotionBundle, 1, (2 << 8) + 3), new Object[]{
						new ItemStack(greaterManaPotion, 1, AMCore.ANY_META),
						new ItemStack(greaterManaPotion, 1, AMCore.ANY_META),
						new ItemStack(greaterManaPotion, 1, AMCore.ANY_META),
						new ItemStack(Items.string)
				});

		GameRegistry.addShapelessRecipe(
				new ItemStack(manaPotionBundle, 1, (3 << 8) + 3), new Object[]{
						new ItemStack(epicManaPotion, 1, AMCore.ANY_META),
						new ItemStack(epicManaPotion, 1, AMCore.ANY_META),
						new ItemStack(epicManaPotion, 1, AMCore.ANY_META),
						new ItemStack(Items.string)
				});

		GameRegistry.addShapelessRecipe(
				new ItemStack(manaPotionBundle, 1, (4 << 8) + 3), new Object[]{
						new ItemStack(legendaryManaPotion, 1, AMCore.ANY_META),
						new ItemStack(legendaryManaPotion, 1, AMCore.ANY_META),
						new ItemStack(legendaryManaPotion, 1, AMCore.ANY_META),
						new ItemStack(Items.string)
				});

		//blank rune
		GameRegistry.addRecipe(new ShapedOreRecipe(
				new ItemStack(rune, 2, rune.META_BLANK),
				new Object[]{
						" S ", "SSS", "SS ",
						Character.valueOf('S'), "cobblestone"
				}));
		//blue rune
		GameRegistry.addRecipe(new ShapelessOreRecipe(
				new ItemStack(rune, 1, rune.META_BLUE),
				new Object[]{
						"dyeBlue",
						new ItemStack(rune, 1, rune.META_BLANK)
				}));
		//red rune
		GameRegistry.addRecipe(new ShapelessOreRecipe(
				new ItemStack(rune, 1, rune.META_RED),
				new Object[]{
						"dyeRed",
						new ItemStack(rune, 1, rune.META_BLANK)
				}));
		//yellow rune
		GameRegistry.addRecipe(new ShapelessOreRecipe(
				new ItemStack(rune, 1, rune.META_YELLOW),
				new Object[]{
						"dyeYellow",
						new ItemStack(rune, 1, rune.META_BLANK)
				}));
		//orange rune
		GameRegistry.addRecipe(new ShapelessOreRecipe(
				new ItemStack(rune, 1, rune.META_ORANGE),
				new Object[]{
						"dyeOrange",
						new ItemStack(rune, 1, rune.META_BLANK)
				}));
		//green rune
		GameRegistry.addRecipe(new ShapelessOreRecipe(
				new ItemStack(rune, 1, rune.META_GREEN),
				new Object[]{
						"dyeGreen",
						new ItemStack(rune, 1, rune.META_BLANK)
				}));
		//purple rune
		GameRegistry.addRecipe(new ShapelessOreRecipe(
				new ItemStack(rune, 1, rune.META_PURPLE),
				new Object[]{
						"dyePurple",
						new ItemStack(rune, 1, rune.META_BLANK)
				}));
		//white rune
		GameRegistry.addRecipe(new ShapelessOreRecipe(
				new ItemStack(rune, 1, rune.META_WHITE),
				new Object[]{
						"dyeWhite",
						new ItemStack(rune, 1, rune.META_BLANK)
				}));
		//black rune
		GameRegistry.addRecipe(new ShapelessOreRecipe(
				new ItemStack(rune, 1, rune.META_BLACK),
				new Object[]{
						"dyeBlack",
						new ItemStack(rune, 1, rune.META_BLANK)
				}));
		//brown rune
		GameRegistry.addRecipe(new ShapelessOreRecipe(
				new ItemStack(rune, 1, rune.META_BROWN),
				new Object[]{
						"dyeBrown",
						new ItemStack(rune, 1, rune.META_BLANK)
				}));
		//cyan rune
		GameRegistry.addRecipe(new ShapelessOreRecipe(
				new ItemStack(rune, 1, rune.META_CYAN),
				new Object[]{
						"dyeCyan",
						new ItemStack(rune, 1, rune.META_BLANK)
				}));
		//gray rune
		GameRegistry.addRecipe(new ShapelessOreRecipe(
				new ItemStack(rune, 1, rune.META_GRAY),
				new Object[]{
						"dyeGray",
						new ItemStack(rune, 1, rune.META_BLANK)
				}));
		//light blue rune
		GameRegistry.addRecipe(new ShapelessOreRecipe(
				new ItemStack(rune, 1, rune.META_LIGHTBLUE),
				new Object[]{
						"dyeLightBlue",
						new ItemStack(rune, 1, rune.META_BLANK)
				}));
		//light gray rune
		GameRegistry.addRecipe(new ShapelessOreRecipe(
				new ItemStack(rune, 1, rune.META_LIGHTGRAY),
				new Object[]{
						"dyeLightGray",
						new ItemStack(rune, 1, rune.META_BLANK)
				}));
		//magenta rune
		GameRegistry.addRecipe(new ShapelessOreRecipe(
				new ItemStack(rune, 1, rune.META_MAGENTA),
				new Object[]{
						"dyeMagenta",
						new ItemStack(rune, 1, rune.META_BLANK)
				}));
		//pink rune
		GameRegistry.addRecipe(new ShapelessOreRecipe(
				new ItemStack(rune, 1, rune.META_PINK),
				new Object[]{
						"dyePink",
						new ItemStack(rune, 1, rune.META_BLANK)
				}));
		//pink rune
		GameRegistry.addRecipe(new ShapelessOreRecipe(
				new ItemStack(rune, 1, rune.META_LIME),
				new Object[]{
						"dyeLime",
						new ItemStack(rune, 1, rune.META_BLANK)
				}));

		//wizard chalk
		GameRegistry.addRecipe(new ShapelessOreRecipe(
				new ItemStack(wizardChalk, 1),
				new Object[]{
						"dyeWhite",
						new ItemStack(Items.clay_ball),
						"dustVinteum",
						new ItemStack(Items.flint),
						new ItemStack(Items.paper)
				}));

		//empty flicker jar
		GameRegistry.addRecipe(new ShapedOreRecipe(
				new ItemStack(flickerJar, 1),
				new Object[]{
						"NWN",
						"G G",
						" G ",
						Character.valueOf('W'), BlocksCommonProxy.magicWall,
						Character.valueOf('N'), "nuggetGold",
						Character.valueOf('G'), "paneGlassColorless"
				}));

		//warding candle
		GameRegistry.addRecipe(new ItemStack(wardingCandle), new Object[]{
				"S",
				"F",
				"P",
				Character.valueOf('S'), Items.string,
				Character.valueOf('F'), new ItemStack(ItemsCommonProxy.itemOre, 1, ItemsCommonProxy.itemOre.META_ANIMALFAT),
				Character.valueOf('P'), BlocksCommonProxy.witchwoodSingleSlab
		});

		//magitech goggles
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(magitechGoggles), new Object[]{
				"LLL",
				"CGC",
				"TLT",
				Character.valueOf('C'), "gemChimerite",
				Character.valueOf('T'), "gemBlueTopaz",
				Character.valueOf('L'), Items.leather,
				Character.valueOf('G'), "nuggetGold",
		}));

		// staves
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(spellStaffNovice), new Object[]{
				"  F",
				" S ",
				"S  ", 'F', lesserFocus, 'S', Items.blaze_rod}));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(spellStaffJourneyman), new Object[]{
				"  F",
				" S ",
				"S  ", 'F', standardFocus, 'S', Items.blaze_rod}));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(spellStaffMaster), new Object[]{
				"  F",
				" S ",
				"S  ", 'F', greaterFocus, 'S', Items.blaze_rod}));

		//magitech staff
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(spellStaffMagitech), new Object[]{
				" GT",
				"G G",
				"GG ",
				Character.valueOf('T'), "gemBlueTopaz",
				Character.valueOf('G'), "nuggetGold",
		}));

		//armor recipes
		//MAGE
		GameRegistry.addRecipe(new ItemStack(mageHood, 1),
				new Object[]{
						"WLW", "WRW", " B ",
						Character.valueOf('W'), new ItemStack(Blocks.wool, 1, 12),
						Character.valueOf('L'), Items.leather,
						Character.valueOf('R'), new ItemStack(rune, 1, rune.META_PURPLE),
						Character.valueOf('B'), new ItemStack(Items.potionitem, 1, 0),
				});
		GameRegistry.addRecipe(new ItemStack(mageArmor, 1),
				new Object[]{
						"RCR", "WLW", "WWW",
						Character.valueOf('W'), new ItemStack(Blocks.wool, 1, 12),
						Character.valueOf('L'), Items.leather,
						Character.valueOf('R'), new ItemStack(rune, 1, rune.META_WHITE),
						Character.valueOf('C'), Items.coal
				});
		GameRegistry.addRecipe(new ItemStack(mageLeggings, 1),
				new Object[]{
						"WRW", "WGW", "L L",
						Character.valueOf('W'), new ItemStack(Blocks.wool, 1, 12),
						Character.valueOf('L'), Items.leather,
						Character.valueOf('R'), new ItemStack(rune, 1, rune.META_YELLOW),
						Character.valueOf('G'), Items.gunpowder
				});
		GameRegistry.addRecipe(new ItemStack(mageBoots, 1),
				new Object[]{
						"R R", "L L", "WFW",
						Character.valueOf('W'), new ItemStack(Blocks.wool, 1, 12),
						Character.valueOf('L'), Items.leather,
						Character.valueOf('R'), new ItemStack(rune, 1, rune.META_BLACK),
						Character.valueOf('F'), Items.feather
				});
		//BATTLEMAGE
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(battlemageHood, 1),
				new Object[]{
						"WLW", "WRW", " E ",
						Character.valueOf('W'), new ItemStack(Blocks.obsidian),
						Character.valueOf('L'), BlocksCommonProxy.goldInlay,
						Character.valueOf('R'), new ItemStack(rune, 1, 1),
						Character.valueOf('E'), new ItemStack(essence, 1, 4)
				}));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(battlemageArmor, 1),
				new Object[]{
						"RER", "WLW", "WWW",
						Character.valueOf('W'), new ItemStack(Blocks.obsidian),
						Character.valueOf('E'), new ItemStack(essence, 1, 1),
						Character.valueOf('R'), new ItemStack(rune, 1, 1),
						Character.valueOf('L'), BlocksCommonProxy.goldInlay
				}));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(battlemageLeggings, 1),
				new Object[]{
						"WRW", "LEL", "W W",
						Character.valueOf('W'), new ItemStack(Blocks.obsidian),
						Character.valueOf('L'), BlocksCommonProxy.goldInlay,
						Character.valueOf('R'), new ItemStack(rune, 1, 1),
						Character.valueOf('E'), new ItemStack(essence, 1, 3)
				}));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(battlemageBoots, 1),
				new Object[]{
						"R R", "WEW", "WLW",
						Character.valueOf('W'), new ItemStack(Blocks.obsidian),
						Character.valueOf('L'), BlocksCommonProxy.goldInlay,
						Character.valueOf('R'), new ItemStack(rune, 1, 1),
						Character.valueOf('E'), new ItemStack(essence, 1, 2)
				}));

		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BoundBoots, 1),
				new Object[]{
						"B B", "BEB", " L ",
						Character.valueOf('L'), new ItemStack(essence, 1, essence.META_LIFE),
						Character.valueOf('B'), new ItemStack(rune, 1, rune.META_RED),
						Character.valueOf('E'), boundCatalystBoots
				}));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BoundArmor, 1),
				new Object[]{
						"BLB", "BEB", "RBR",
						Character.valueOf('L'), new ItemStack(essence, 1, essence.META_LIFE),
						Character.valueOf('B'), new ItemStack(rune, 1, rune.META_RED),
						Character.valueOf('E'), boundCatalystArmor,
						Character.valueOf('R'), new ItemStack(BlocksCommonProxy.redstoneInlay)
				}));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BoundLeggings, 1),
				new Object[]{
						"RBR", "BEB", "BLB",
						Character.valueOf('L'), new ItemStack(essence, 1, essence.META_LIFE),
						Character.valueOf('B'), new ItemStack(rune, 1, rune.META_RED),
						Character.valueOf('E'), boundCatalystLeggings,
						Character.valueOf('R'), new ItemStack(BlocksCommonProxy.redstoneInlay)
				}));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BoundHelmet, 1),
				new Object[]{
						"RBR", "BEB", " L ",
						Character.valueOf('L'), new ItemStack(essence, 1, essence.META_LIFE),
						Character.valueOf('B'), new ItemStack(rune, 1, rune.META_RED),
						Character.valueOf('E'), boundCatalystHelmet,
						Character.valueOf('R'), new ItemStack(BlocksCommonProxy.redstoneInlay)
				}));


		//ARCHMAGE
		GameRegistry.addRecipe(new ItemStack(archmageHood, 1),
				new Object[]{
			"EAE", "WBW", "RPR",
			Character.valueOf('B'), new ItemStack(battlemageHood),
			Character.valueOf('A'), fireEarsEnchanted,
			Character.valueOf('R'), new ItemStack(rune, 1, rune.META_ORANGE),
			Character.valueOf('E'), new ItemStack(essence, 1, essence.META_FIRE),
			Character.valueOf('P'), new ItemStack(essence, 1, essence.META_PURE),
			Character.valueOf('W'), new ItemStack(itemOre, 1, itemOre.META_SUNSTONE),
		});
		GameRegistry.addRecipe(new ItemStack(archmageArmor, 1),
				new Object[]{
			"EAE", "WBW", "RPR",
			Character.valueOf('B'), new ItemStack(battlemageArmor),
			Character.valueOf('A'), earthArmorEnchanted,
			Character.valueOf('R'), new ItemStack(rune, 1, rune.META_BROWN),
			Character.valueOf('E'), new ItemStack(essence, 1, essence.META_EARTH),
			Character.valueOf('P'), lifeWardEnchanted,
			Character.valueOf('W'), new ItemStack(Items.nether_star),
		});
		GameRegistry.addRecipe(new ItemStack(archmageLeggings, 1),
				new Object[]{
			"EAE", "WBW", "RPR",
			Character.valueOf('B'), new ItemStack(battlemageLeggings),
			Character.valueOf('A'), waterOrbsEnchanted,
			Character.valueOf('R'), new ItemStack(rune, 1, rune.META_CYAN),
			Character.valueOf('E'), new ItemStack(essence, 1, essence.META_WATER),
			Character.valueOf('P'), new ItemStack(essence, 1, essence.META_PURE),
			Character.valueOf('W'), new ItemStack(Blocks.diamond_block),
		});
		GameRegistry.addRecipe(new ItemStack(archmageBoots, 1),
				new Object[]{
				"EAE", "WBW", "RPR",
				Character.valueOf('B'), new ItemStack(battlemageBoots),
				Character.valueOf('A'), enderBootsEnchanted,
				Character.valueOf('R'), new ItemStack(rune, 1, rune.META_WHITE),
				Character.valueOf('E'), new ItemStack(essence, 1, essence.META_AIR),
				Character.valueOf('P'), airSledEnchanted,
				Character.valueOf('W'), new ItemStack(itemOre, 1, itemOre.META_MOONSTONE),
		});


		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(essenceBag), new Object[]{
				"LLL", "WNW", "LLL",
				Character.valueOf('L'), Items.leather,
				Character.valueOf('W'), new ItemStack(Blocks.wool, 1, AMCore.ANY_META),
				Character.valueOf('N'), "nuggetGold"
		}));

		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(crystalPhylactery), new Object[]{
				" B ", "GPG", " W ",
				Character.valueOf('B'), "gemMoonstone",
				Character.valueOf('W'), BlocksCommonProxy.magicWall,
				Character.valueOf('G'), "blockGlassColorless",
				Character.valueOf('P'), new ItemStack(ItemsCommonProxy.itemOre, 1, ItemsCommonProxy.itemOre.META_PURIFIEDVINTEUM)
		}));

		//lesser mana potion
		//GameRegistry.addRecipe(new ItemStack(Item.potion, 1, ))

		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(keystone, 1), new Object[]{
				"GIG",
				"IVI",
				"GIG",
				Character.valueOf('G'), "ingotGold",
				Character.valueOf('I'), "ingotIron",
				Character.valueOf('V'), "dustVinteum"
		}));

		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(itemOre, 1, itemOre.META_PURIFIEDVINTEUM), new Object[]{
				new ItemStack(BlocksCommonProxy.cerublossom),
				new ItemStack(BlocksCommonProxy.desertNova),
				"dustVinteum",
				"arcaneAsh"
		}));

		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(manaCake, 3, 0), new Object[]{
				new ItemStack(BlocksCommonProxy.cerublossom),
				new ItemStack(BlocksCommonProxy.desertNova),
				new ItemStack(Items.sugar),
				"cropWheat"
		}));

		GameRegistry.addShapelessRecipe(new ItemStack(lesserManaPotion), new Object[]{
				new ItemStack(Items.wheat_seeds),
				new ItemStack(Items.sugar),
				new ItemStack(Items.potionitem, 1, AMCore.ANY_META)
		});

		GameRegistry.addShapelessRecipe(new ItemStack(standardManaPotion), new Object[]{
				new ItemStack(Items.gunpowder),
				new ItemStack(lesserManaPotion, 1, AMCore.ANY_META)
		});

		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(greaterManaPotion), new Object[]{
				"dustVinteum",
				new ItemStack(standardManaPotion, 1, AMCore.ANY_META)
		}));

		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(epicManaPotion), new Object[]{
				"arcaneAsh",
				new ItemStack(greaterManaPotion, 1, AMCore.ANY_META)
		}));

		GameRegistry.addShapelessRecipe(new ItemStack(legendaryManaPotion), new Object[]{
				new ItemStack(itemOre, 1, itemOre.META_PURIFIEDVINTEUM),
				new ItemStack(epicManaPotion, 1, AMCore.ANY_META)
		});

		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(liquidEssenceBottle), new Object[]{
				"gemChimerite",
				new ItemStack(BlocksCommonProxy.tarmaRoot),
				new ItemStack(ItemsCommonProxy.itemAMBucket)
		}));

		GameRegistry.addRecipe(new ItemStack(lesserFocus), ((ItemFocus)lesserFocus).getRecipeItems());
		GameRegistry.addRecipe(new ItemStack(standardFocus), ((ItemFocus)standardFocus).getRecipeItems());
		GameRegistry.addRecipe(new ItemStack(greaterFocus), ((ItemFocus)greaterFocus).getRecipeItems());
		GameRegistry.addRecipe(new ItemStack(manaFocus), ((ItemFocus)manaFocus).getRecipeItems());
		GameRegistry.addRecipe(new ItemStack(chargeFocus), ((ItemFocus)chargeFocus).getRecipeItems());

		GameRegistry.addRecipe(new ItemStack(playerFocus), ((ItemFocus)playerFocus).getRecipeItems());
		GameRegistry.addRecipe(new ItemStack(mobFocus), ((ItemFocus)mobFocus).getRecipeItems());
		GameRegistry.addRecipe(new ItemStack(itemFocus), ((ItemFocus)itemFocus).getRecipeItems());
		GameRegistry.addRecipe(new ItemStack(creatureFocus), ((ItemFocus)creatureFocus).getRecipeItems());

		//binding catalysts
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(bindingCatalyst, 1, bindingCatalyst.META_AXE), new Object[]{
				"SVS",
				"SAS",
				"SVS",
				Character.valueOf('V'), "dustVinteum",
				Character.valueOf('S'), "slimeball",
				Character.valueOf('A'), Items.golden_axe
		}));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(bindingCatalyst, 1, bindingCatalyst.META_PICK), new Object[]{
				"SVS",
				"SAS",
				"SVS",
				Character.valueOf('V'), "dustVinteum",
				Character.valueOf('S'), "slimeball",
				Character.valueOf('A'), Items.golden_pickaxe
		}));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(bindingCatalyst, 1, bindingCatalyst.META_SHOVEL), new Object[]{
				"SVS",
				"SAS",
				"SVS",
				Character.valueOf('V'), "dustVinteum",
				Character.valueOf('S'), "slimeball",
				Character.valueOf('A'), Items.golden_shovel
		}));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(bindingCatalyst, 1, bindingCatalyst.META_SWORD), new Object[]{
				"SVS",
				"SAS",
				"SVS",
				Character.valueOf('V'), new ItemStack(itemOre, 1, itemOre.META_PURIFIEDVINTEUM),
				Character.valueOf('S'), "slimeball",
				Character.valueOf('A'), Items.golden_sword
		}));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(bindingCatalyst, 1, bindingCatalyst.META_HOE), new Object[]{
				"SVS",
				"SAS",
				"SVS",
				Character.valueOf('V'), "dustVinteum",
				Character.valueOf('S'), "slimeball",
				Character.valueOf('A'), Items.golden_hoe
		}));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(bindingCatalyst, 1, bindingCatalyst.META_BOW), new Object[]{
				"SVS",
				"SAS",
				"SVS",
				Character.valueOf('V'), "dustVinteum",
				Character.valueOf('S'), "slimeball",
				Character.valueOf('A'), Items.bow
		}));

		GameRegistry.addRecipe(new ItemStack(itemKeystoneDoor, 1, itemKeystoneDoor.KEYSTONE_DOOR), new Object[]{
				"PWP",
				"RRR",
				"PWP",
				Character.valueOf('P'), BlocksCommonProxy.witchwoodPlanks,
				Character.valueOf('R'), new ItemStack(rune, 1, rune.META_BLANK),
				Character.valueOf('W'), BlocksCommonProxy.magicWall
		});

		GameRegistry.addRecipe(new ItemStack(itemKeystoneDoor, 1, itemKeystoneDoor.SPELL_SEALED_DOOR), new Object[]{
				" G ",
				"SKS",
				" L ",
				Character.valueOf('G'), ItemsCommonProxy.greaterFocus,
				Character.valueOf('S'), ItemsCommonProxy.standardFocus,
				Character.valueOf('L'), ItemsCommonProxy.lesserFocus,
				Character.valueOf('K'), new ItemStack(ItemsCommonProxy.itemKeystoneDoor, 1, itemKeystoneDoor.KEYSTONE_DOOR)
		});

		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(manaMartini), new Object[]{
				new ItemStack(Blocks.ice),
				"cropPotato",
				new ItemStack(Items.sugar),
				"stickWood",
				new ItemStack(standardManaPotion)
		}));

		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(astrocalis), new Object[]{
				new ItemStack(Blocks.vine),
				new ItemStack(Blocks.red_flower, 1, 2),
				new ItemStack(Blocks.red_flower, 1, 3),
				new ItemStack(Blocks.red_flower, 1, 5),
				new ItemStack(Blocks.red_flower, 1, 6),
				new ItemStack(itemOre, 1, itemOre.META_HELLFISH),
				new ItemStack(itemOre, 1, itemOre.META_NIGHTMAREESSENCE),
				new ItemStack(BlocksCommonProxy.wakebloom),
				new ItemStack(casparazoid)
		}));

		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(casparazoid), new Object[]{
				new ItemStack(Blocks.red_flower, 1, 3),
				new ItemStack(Blocks.tallgrass, 1, 1),
				new ItemStack(Blocks.tallgrass, 1, 2),
				new ItemStack(itemOre, 1, itemOre.META_RAINROCKROSE),
				new ItemStack(itemOre, 1, itemOre.META_STORMSAWTOOTH),
				new ItemStack(itemOre, 1, itemOre.META_COSMICDUST),
				new ItemStack(standardManaPotion)
		}));

		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(inscriptionUpgrade, 1, 0),
				new ItemStack(Items.book),
				new ItemStack(Items.string),
				new ItemStack(Items.feather),
				"dyeBlack"
		));

		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(inscriptionUpgrade, 1, 1),
				new ItemStack(Blocks.carpet, 1, AMCore.ANY_META),
				new ItemStack(Items.book),
				new ItemStack(wizardChalk)
		));

		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(inscriptionUpgrade, 1, 2),
				new ItemStack(wardingCandle),
				new ItemStack(Items.flint_and_steel),
				new ItemStack(itemOre, 1, itemOre.META_ANIMALFAT),
				new ItemStack(Items.glass_bottle),
				new ItemStack(Items.book)
		));

		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(workbenchUpgrade), new ItemStack(BlocksCommonProxy.magiciansWorkbench), "chestWood", "craftingTableWood", "craftingTableWood", "ingotGold"));

		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(deficitCrystal), "gemDiamond", "arcaneAsh", new ItemStack(Items.ender_eye)));
	}

	public void postInit(){
		//flicker focus recipes
		for (int i : FlickerOperatorRegistry.instance.getMasks()){
			IFlickerFunctionality func = FlickerOperatorRegistry.instance.getOperatorForMask(i);
			if (func != null){
				Object[] recipeItems = func.getRecipe();
				if (recipeItems != null){
					GameRegistry.addRecipe(new ItemStack(flickerFocus, 1, i), recipeItems);
				}else{
					LogHelper.info("Flicker operator %s was registered with no recipe.  It is un-craftable.  This may have been intentional.", func.getClass().getSimpleName());
				}
			}
		}
	}
}
