package am2.enchantments;

import am2.AMCore;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.item.ItemStack;

public class EnchantmentSoulbound extends Enchantment{

	public EnchantmentSoulbound(int par1, int par2){
		super(par1, par2, EnumEnchantmentType.all);
		setName("soulbound");
	}

	@Override
	public int getMinEnchantability(int par1){
		return 1;
	}

	@Override
	public int getMaxEnchantability(int par1){
		return 50;
	}

	@Override
	public int getMinLevel(){
		return 1;
	}

	@Override
	public int getMaxLevel(){
		return 1;
	}

	@Override
	public boolean canApplyAtEnchantingTable(ItemStack stack){
		return AMCore.config.isSoulboundEnchEnabledAtTable();
	}

	@Override
	public boolean canApplyTogether(Enchantment p_77326_1_)
	{
		String lowercaseName = p_77326_1_.getName().toLowerCase();
		if (lowercaseName.contains("soul")) // soul tether
		{
			return false;
		}
		else
		{
			return super.canApplyTogether(p_77326_1_);
		}
	}

	@Override
	public boolean isAllowedOnBooks()
	{
		return AMCore.config.isSoulboundEnchEnabledAtTable();
	}
}
