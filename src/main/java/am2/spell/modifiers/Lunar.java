package am2.spell.modifiers;

import am2.api.spell.component.interfaces.ISpellModifier;
import am2.api.spell.enums.SpellModifiers;
import am2.items.ItemsCommonProxy;
import am2.playerextensions.ExtendedProperties;
import am2.spell.SpellHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.EnumSet;

public class Lunar implements ISpellModifier{
	@Override
	public int getID(){
		return 10;
	}

	@Override
	public EnumSet<SpellModifiers> getAspectsModified(){
		return EnumSet.of(SpellModifiers.RANGE, SpellModifiers.RADIUS, SpellModifiers.DAMAGE, SpellModifiers.DURATION, SpellModifiers.HEALING);
	}

	@SuppressWarnings("incomplete-switch")
	@Override
	public float getModifier(SpellModifiers type, EntityLivingBase caster, Entity target, World world, byte[] metadata){
		ExtendedProperties properties = ExtendedProperties.For(caster);
		float manaRatio = properties.getCurrentMana() / properties.getMaxMana();
		float spellBonus = getSpellTypeBonus(type);

		return (float) Math.max(1,
				Math.pow(Math.pow((manaRatio * spellBonus), (manaRatio+1)),(manaRatio+1))
		);
	}

	@Override
	public Object[] getRecipeItems(){
		return new Object[]{
				new ItemStack(ItemsCommonProxy.essence, 1, ItemsCommonProxy.essence.META_NATURE),
				new ItemStack(ItemsCommonProxy.itemOre, 1, ItemsCommonProxy.itemOre.META_MOONSTONE),
				new ItemStack(ItemsCommonProxy.itemOre, 1, ItemsCommonProxy.itemOre.META_IMBUEDMOONFLOWER),
				Items.clock
		};
	}

	@Override
	public float getManaCostMultiplier(ItemStack spellStack, int stage, int quantity, EntityLivingBase caster){
		World world = caster.worldObj;

		float multiplier = 3.5f;

		if (caster.dimension == 1)
			multiplier = 2.0f;
		else if (!world.provider.hasNoSky && !world.isDaytime()){
			double time = world.getWorldTime() % 24000;
			//Returns a decreasing value between 3.4 and 2.5 as it approaches midnight.
			multiplier = (float)Math.round((
					1.5 + Math.exp(0.13 * (Math.abs((time - 18000) / 1000)))
			) * 100) / 100;
		}
		return quantity * multiplier;
	}
	@Override
	public byte[] getModifierMetadata(ItemStack[] matchedRecipe){
		return null;
	}
	public float getSpellTypeBonus(SpellModifiers type){
		switch (type){
		case HEALING:
			return 1.3f; //bonus at 100% = 286%
		case DAMAGE:
			return 1.4f; //bonus at 100% = 384%
		case RADIUS:
			return 1.5f; //bonus at 100% = 500%
		case RANGE:
			return 1.6f; //bonus at 100% = 655%
		case DURATION:
			return 1.7f; //bonus at 100% = 835%
		}
		return 1.2f; //bonus at 100% = 207%
	}

}
