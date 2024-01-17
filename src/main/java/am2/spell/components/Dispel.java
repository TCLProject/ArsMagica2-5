package am2.spell.components;

import am2.AMCore;
import am2.api.ArsMagicaApi;
import am2.api.spell.component.interfaces.ISpellComponent;
import am2.api.spell.enums.Affinity;
import am2.api.spell.enums.SpellModifiers;
import am2.buffs.BuffEffect;
import am2.buffs.BuffList;
import am2.items.ItemsCommonProxy;
import am2.particles.AMParticle;
import am2.particles.ParticleOrbitEntity;
import am2.playerextensions.ExtendedProperties;
import am2.spell.SpellUtils;
import am2.utility.EntityUtilities;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.IBossDisplayData;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

import java.util.*;

public class Dispel implements ISpellComponent{

	@Override
	public boolean applyEffectBlock(ItemStack stack, World world, int blockx, int blocky, int blockz, int blockFace, double impactX, double impactY, double impactZ, EntityLivingBase caster){
		ArrayList<EntityItem> items = (ArrayList<EntityItem>)world.getEntitiesWithinAABB(EntityItem.class, AxisAlignedBB.getBoundingBox(blockx - 2, blocky - 2, blockz - 2, blockx + 2, blocky + 2, blockz + 2));
		if (items.size() > 0) {
			for (EntityItem item : items) {
				removeEnchants(item);
			}
			return true;
		}
		return false;
	}

	@Override
	public boolean applyEffectEntity(ItemStack stack, World world, EntityLivingBase caster, Entity target){

		if (!(target instanceof EntityLivingBase) || target instanceof IBossDisplayData) return false;

		if (EntityUtilities.isSummon((EntityLivingBase)target)){
			if (EntityUtilities.getOwner((EntityLivingBase)target) == caster.getEntityId()){
				target.attackEntityFrom(DamageSource.magic, 50000);
				return true;
			}
		}

		List<Integer> effectsToRemove = new ArrayList<Integer>();

		Iterator iter = ((EntityLivingBase)target).getActivePotionEffects().iterator();

		int magnitudeLeft = 6 + (SpellUtils.instance.countModifiers(SpellModifiers.BUFF_POWER, stack) * 2);

		while (iter.hasNext()){
			Integer potionID = ((PotionEffect)iter.next()).getPotionID();
			if (BuffList.instance.isDispelBlacklisted(potionID)){
				continue;
			}
			PotionEffect pe = ((EntityLivingBase)target).getActivePotionEffect(Potion.potionTypes[potionID]);

			int magnitudeCost = pe.getAmplifier();

			if (magnitudeLeft >= magnitudeCost){
				magnitudeLeft -= magnitudeCost;
				effectsToRemove.add(potionID);

				if (pe instanceof BuffEffect && !world.isRemote){
					((BuffEffect)pe).stopEffect((EntityLivingBase)target);
				}
			}
		}

		if (effectsToRemove.size() == 0 && ExtendedProperties.For((EntityLivingBase)target).getNumSummons() == 0){
			return false;
		}

		if (!world.isRemote){
			removePotionEffects((EntityLivingBase)target, effectsToRemove);
		}

		return true;
	}

	private void removeEnchants(EntityItem target) {
		if (target.getEntityItem().stackTagCompound != null) {
			if (target.getEntityItem().stackTagCompound.getTagList("ench", 10).tagCount() > 0) {
				target.getEntityItem().stackTagCompound.removeTag("ench");
			}
		}
	}

	private void removePotionEffects(EntityLivingBase target, List<Integer> effectsToRemove){
		for (Integer i : effectsToRemove){
			if (i == BuffList.flight.id || i == BuffList.levitation.id){
				if (target instanceof EntityPlayer && target.isPotionActive(BuffList.flight.id)){
					((EntityPlayer)target).capabilities.isFlying = false;
					((EntityPlayer)target).capabilities.allowFlying = false;
				}
			}
			target.removePotionEffect(i);
		}
	}

	@Override
	public float manaCost(EntityLivingBase caster){
		return 200;
	}

	@Override
	public float burnout(EntityLivingBase caster){
		return ArsMagicaApi.getBurnoutFromMana(manaCost(caster));
	}

	@Override
	public ItemStack[] reagents(EntityLivingBase caster){
		return null;
	}

	@Override
	public void spawnParticles(World world, double x, double y, double z, EntityLivingBase caster, Entity target, Random rand, int colorModifier){
		for (int i = 0; i < 25; ++i){
			AMParticle particle = (AMParticle)AMCore.proxy.particleManager.spawn(world, "sparkle2", x, y, z);
			if (particle != null){
				particle.addRandomOffset(1, 2, 1);
				particle.AddParticleController(new ParticleOrbitEntity(particle, target, 0.1f + rand.nextFloat() * 0.1f, 1, false));
				if (rand.nextBoolean())
					particle.setRGBColorF(0.7f, 0.1f, 0.7f);
				particle.setMaxAge(20);
				particle.setParticleScale(0.1f);
				if (colorModifier > -1){
					particle.setRGBColorF(((colorModifier >> 16) & 0xFF) / 255.0f, ((colorModifier >> 8) & 0xFF) / 255.0f, (colorModifier & 0xFF) / 255.0f);
				}
			}
		}
	}

	@Override
	public EnumSet<Affinity> getAffinity(){
		return EnumSet.of(Affinity.NONE);
	}

	@Override
	public int getID(){
		return 10;
	}

	@Override
	public Object[] getRecipeItems(){
		return new Object[]{
				new ItemStack(ItemsCommonProxy.rune, 1, ItemsCommonProxy.rune.META_PURPLE),
				new ItemStack(ItemsCommonProxy.itemOre, 1, ItemsCommonProxy.itemOre.META_ARCANEASH),
				new ItemStack(ItemsCommonProxy.itemOre, 1, ItemsCommonProxy.itemOre.META_BLUETOPAZ),
				Items.milk_bucket
		};
	}

	@Override
	public float getAffinityShift(Affinity affinity){
		return 0;
	}
}
