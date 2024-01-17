package am2.items;

import am2.AMEventHandler;
import am2.armor.ItemEnderBoots;
import am2.playerextensions.ExtendedProperties;
import am2.utility.DummyEntityPlayer;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentFireAspect;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.Potion;
import net.minecraft.util.DamageSource;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import java.util.List;

public class ItemSoulspike extends ItemSword {

    private static final String KEY_NBT_MANA = "Stored_GREATERMANA";

    public ItemSoulspike(ToolMaterial material) {
        super(material);
        this.setMaxDamage(0);
    }

    public ItemSoulspike setUnlocalizedAndTextureName(String name){
        this.setUnlocalizedName(name);
        setTextureName(name);
        return this;
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack)
    {
        return false;
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4){
        list.add(String.format(StatCollector.translateToLocal("am2.tooltip.containedMana"), getManaInSpike(stack)));
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player){
        if (player.isSneaking() && player.isPotionActive(Potion.invisibility.id) && (getManaInSpike(stack) > 100)) { // ethereal form
            if (player.inventory.armorInventory[0] != null) {
                if (areEtherealApplicableBootsEquipped(player)) { // player wearing reality-bending boots
                    if (!ExtendedProperties.For(player).hasExtraVariable("karma")) { // good karma
                        deductManaFromSpike(stack, 100);
                        player.attackEntityFrom(DamageSource.outOfWorld, 5);
                        world.playSoundAtEntity(player, "arsmagica2:spell.cast.ender", 1F, 1F);
                        ExtendedProperties.For(player).addToExtraVariables("ethereal", String.valueOf(player.getActivePotionEffect(Potion.invisibility).getDuration()));
                        player.capabilities.disableDamage = true;
                        player.capabilities.allowEdit = false; // like spectator
                        addTagToBoots(player.inventory.armorInventory[0]); // I cross my fingers and hope this works on multiplayer, if it doesn't please report
                        player.curePotionEffects(new ItemStack(Items.milk_bucket));
                    }
                }
            }
        }

        return super.onItemRightClick(stack, world, player);
    }

    public static boolean areEtherealApplicableBootsEquipped(EntityPlayer player) {
        if (player.inventory.armorInventory[0] == null) return false;
        return player.inventory.armorInventory[0].getItem() instanceof ItemEnderBoots || player.inventory.armorInventory[0].getItem() == ItemsCommonProxy.archmageBoots;
    }

    public static void addTagToBoots(ItemStack boots) { // for 'telling' other players this player is ethereal. extremely roundabout, but it works (I hope!)
        if (boots.stackTagCompound == null)
        {
            boots.setTagCompound(new NBTTagCompound());
        }

        if (!boots.stackTagCompound.hasKey("display", 10))
        {
            boots.stackTagCompound.setTag("display", new NBTTagCompound());
        }

        NBTTagCompound nbttagcompound = boots.stackTagCompound.getCompoundTag("display");
        nbttagcompound.setBoolean("ethereal", true); // why are we storing this in display? Not sure.
    }

    public static void removeTagFromBoots(ItemStack boots) {
        if (boots == null) return;
        if (boots.stackTagCompound == null)
        {
            return;
        }

        if (!boots.stackTagCompound.hasKey("display", 10))
        {
            return;
        }

        NBTTagCompound nbttagcompound = boots.stackTagCompound.getCompoundTag("display");
        nbttagcompound.setBoolean("ethereal", false);
    }

    public static boolean bootsHaveEtherealTag(ItemStack boots) {
        if (boots == null) return false;
        if (boots.stackTagCompound == null)
        {
            return false;
        }

        if (!boots.stackTagCompound.hasKey("display", 10))
        {
            return false;
        }

        NBTTagCompound nbttagcompound = boots.stackTagCompound.getCompoundTag("display");
        if (!nbttagcompound.hasKey("ethereal"))
        {
            return false; // I know this is guarded against, but just in case
        }

        return nbttagcompound.getBoolean("ethereal");
    }

    @Override
    public String getItemStackDisplayName(ItemStack p_77653_1_)
    {
        return "§a" + ("" + StatCollector.translateToLocal(this.getUnlocalizedNameInefficiently(p_77653_1_) + ".name")).trim() + "§r";
    }

    public static void addManaToSpike(ItemStack spike, int amount){
        if (!spike.hasTagCompound())
            spike.stackTagCompound = new NBTTagCompound();
        int value = Math.min(spike.stackTagCompound.getInteger(KEY_NBT_MANA) + amount, 150000);
        spike.stackTagCompound.setInteger(KEY_NBT_MANA, value);
    }

    public static void deductManaFromSpike(ItemStack spike, int amount){
        addManaToSpike(spike, -amount);
    }

    public static int getManaInSpike(ItemStack spike){
        if (!spike.hasTagCompound())
            return 0;
        return spike.stackTagCompound.getInteger(KEY_NBT_MANA);
    }

    public static boolean isFull(ItemStack spike){
        if (!spike.hasTagCompound())
            return false;
        return spike.stackTagCompound.getInteger(KEY_NBT_MANA) >= 150000; // not entirely lore-accurate, but lowered for the sake of balance
    }
}
