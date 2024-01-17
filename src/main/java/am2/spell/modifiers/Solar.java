package am2.spell.modifiers;

import am2.api.spell.component.interfaces.ISpellModifier;
import am2.api.spell.enums.SpellModifiers;
import am2.items.ItemsCommonProxy;
import am2.playerextensions.ExtendedProperties;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.EnumSet;

public class Solar implements ISpellModifier{

	@Override
	public int getID(){
		return 12;
	}

	@Override
	public EnumSet<SpellModifiers> getAspectsModified(){
		return EnumSet.of(SpellModifiers.RANGE, SpellModifiers.RADIUS, SpellModifiers.DAMAGE, SpellModifiers.DURATION, SpellModifiers.HEALING);
	}
	@SuppressWarnings("incomplete-switch")
	@Override
	public float getModifier(SpellModifiers type, EntityLivingBase caster, Entity target, World world, byte[] metadata){
		ExtendedProperties properties = ExtendedProperties.For(caster);
		float burnoutRatio = properties.getCurrentFatigue() / properties.getMaxFatigue();
		float spellBonus = getSpellTypeBonus(type);

		return (float) Math.max(1,
				Math.pow(Math.pow((burnoutRatio * spellBonus), (burnoutRatio+1)),(burnoutRatio+1))* 5);
	}

	@SuppressWarnings("incomplete-switch")
	@Override
	public Object[] getRecipeItems(){
		return new Object[]{
				new ItemStack(ItemsCommonProxy.essence, 1, ItemsCommonProxy.essence.META_NATURE),
				new ItemStack(ItemsCommonProxy.itemOre, 1, ItemsCommonProxy.itemOre.META_SUNSTONE),
				Items.clock
		};
	}

	@Override
	public float getManaCostMultiplier(ItemStack spellStack, int stage, int quantity, EntityLivingBase caster){
		World world = caster.worldObj;

		float multiplier = 2.5f;

		if (caster.dimension == -1)
			multiplier = 1.5f;
		else if (!world.provider.hasNoSky && world.isDaytime()){
			double time = world.getWorldTime() % 24000;
			//Returns a decreasing value between 2.4 and 2.0 as it approaches midday.
			multiplier = (float) Math.round((
					1.0f + Math.exp(0.058 * (Math.abs((time - 6000)/1000)))
			) * 100)/100;
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
			return 1.3f; //bonus at 90% = 881%
		case DAMAGE:
			return 1.4f; //bonus at 90% = 1152%
		case RADIUS:
			return 1.5f; //bonus at 90% = 1477%
		case RANGE:
			return 1.6f; //bonus at 90% = 1865%
		case DURATION:
			return 1.7f; //bonus at 90% = 2321%
		}
		return 1.2f; //bonus at 90% = 660%
	}

}
