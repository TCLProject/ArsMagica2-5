package am2.spell.components;

import am2.AMCore;
import am2.RitualShapeHelper;
import am2.api.blocks.MultiblockStructureDefinition;
import am2.api.spell.component.interfaces.IRitualInteraction;
import am2.api.spell.component.interfaces.ISpellComponent;
import am2.api.spell.enums.Affinity;
import am2.api.spell.enums.SpellModifiers;
import am2.blocks.BlocksCommonProxy;
import am2.items.ItemsCommonProxy;
import am2.particles.AMParticle;
import am2.particles.ParticleApproachEntity;
import am2.playerextensions.ExtendedProperties;
import am2.spell.SpellUtils;
import cpw.mods.fml.common.Loader;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

import java.lang.reflect.Method;
import java.util.EnumSet;
import java.util.Random;

public class LifeTap implements ISpellComponent, IRitualInteraction{

	@Override
	public boolean applyEffectBlock(ItemStack stack, World world, int blockx, int blocky, int blockz, int blockFace, double impactX, double impactY, double impactZ, EntityLivingBase caster){

		if (world.getBlock(blockx, blocky, blockz) == Blocks.mob_spawner){
			ItemStack[] reagents = RitualShapeHelper.instance.checkForRitual(this, world, blockx, blocky, blockz);
			if (reagents != null){
				if (!world.isRemote){
					world.setBlockToAir(blockx, blocky, blockz);
					RitualShapeHelper.instance.consumeRitualReagents(this, world, blockx, blocky, blockz);
					RitualShapeHelper.instance.consumeRitualShape(this, world, blockx, blocky, blockz);
					EntityItem item = new EntityItem(world);
					item.setPosition(blockx + 0.5, blocky + 0.5, blockz + 0.5);
					item.setEntityItemStack(new ItemStack(BlocksCommonProxy.inertSpawner));
					world.spawnEntityInWorld(item);
				}else{

				}

				return true;
			}
		}

		return false;
	}

	@Override
	public boolean applyEffectEntity(ItemStack stack, World world, EntityLivingBase caster, Entity target){
		if (!(target instanceof EntityLivingBase)) return false;
		if (!world.isRemote){
			double damage = SpellUtils.instance.getModifiedDouble_Mul(2, stack, caster, target, world, 0, SpellModifiers.DAMAGE);
			tryDrainLP((EntityLivingBase)target, damage * 1000);
			ExtendedProperties casterProperties = ExtendedProperties.For((EntityLivingBase)target);
			if (caster != target) {
				ExtendedProperties p = ExtendedProperties.For((EntityLivingBase)caster);
				if (p.getCurrentMana() < (float)damage * 10) return false;
				p.deductMana((float)damage * 10);
			}
			float manaRefunded = (float)(((damage * 0.01)) * casterProperties.getMaxMana());

			if ((target).attackEntityFrom(DamageSource.outOfWorld, (int)Math.floor(damage))){
				if (!target.isDead && ((EntityLivingBase) target).getHealth() >= 1) {
					casterProperties.setCurrentMana(casterProperties.getCurrentMana() + manaRefunded);
					casterProperties.forceSync();
				}
			}else{
				return false;
			}
		}
		return true;
	}

	public static void tryDrainLP(EntityLivingBase elb, double LP) {
		if ((Loader.isModLoaded("AWWayofTime") || Loader.isModLoaded("AlchemicalWizardry")) && elb instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) elb;
			try {
				Class<?> clazz = Class.forName("WayofTime.alchemicalWizardry.api.soulNetwork.SoulNetworkHandler");
				Method getCurrentEssence = clazz.getMethod("getCurrentEssence", new Class<?>[]{String.class});
				Object essence = getCurrentEssence.invoke(null, new Object[]{player.getCommandSenderName()});
				int essenceAmount = (Integer) essence;
				int newEssenceAmount = (int)Math.max(essenceAmount - LP, 0);
				Method setCurrentEssence = clazz.getMethod("setCurrentEssence", new Class<?>[]{String.class, int.class});
				setCurrentEssence.invoke(null, new Object[]{player.getCommandSenderName(), newEssenceAmount});
			} catch (Exception e) {
				e.printStackTrace(); // How did we get here?
			}
		}
	}

	@Override
	public float manaCost(EntityLivingBase caster){
		return 0;
	}

	@Override
	public float burnout(EntityLivingBase caster){
		return 50;
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
				particle.addRandomOffset(2, 2, 2);
				particle.setMaxAge(15);
				particle.setParticleScale(0.1f);
				particle.AddParticleController(new ParticleApproachEntity(particle, target, 0.1, 0.1, 1, false));
				if (rand.nextBoolean())
					particle.setRGBColorF(0.4f, 0.1f, 0.5f);
				else
					particle.setRGBColorF(0.1f, 0.5f, 0.1f);
				if (colorModifier > -1){
					particle.setRGBColorF(((colorModifier >> 16) & 0xFF) / 255.0f, ((colorModifier >> 8) & 0xFF) / 255.0f, (colorModifier & 0xFF) / 255.0f);
				}
			}
		}
	}

	@Override
	public EnumSet<Affinity> getAffinity(){
		return EnumSet.of(Affinity.LIFE, Affinity.ENDER);
	}

	@Override
	public int getID(){
		return 32;
	}

	@Override
	public Object[] getRecipeItems(){
		return new Object[]{
				new ItemStack(ItemsCommonProxy.rune, 1, ItemsCommonProxy.rune.META_BLACK),
				BlocksCommonProxy.aum
		};
	}

	@Override
	public float getAffinityShift(Affinity affinity){
		return 0.01f;
	}

	@Override
	public MultiblockStructureDefinition getRitualShape(){
		return RitualShapeHelper.instance.corruption;
	}

	@Override
	public ItemStack[] getReagents(){
		return new ItemStack[]{
				new ItemStack(ItemsCommonProxy.mobFocus),
				new ItemStack(ItemsCommonProxy.essence, 1, ItemsCommonProxy.essence.META_ENDER)
		};
	}

	@Override
	public int getReagentSearchRadius(){
		return 3;
	}
}
