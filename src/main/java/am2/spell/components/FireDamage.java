package am2.spell.components;

import am2.AMCore;
import am2.LogHelper;
import am2.RitualShapeHelper;
import am2.api.blocks.MultiblockStructureDefinition;
import am2.api.power.IPowerNode;
import am2.api.spell.component.interfaces.IRitualInteraction;
import am2.api.spell.component.interfaces.ISpellComponent;
import am2.api.spell.enums.Affinity;
import am2.api.spell.enums.SpellModifiers;
import am2.blocks.BlocksCommonProxy;
import am2.damage.DamageSourceUnsummon;
import am2.damage.DamageSources;
import am2.entities.EntityDarkling;
import am2.entities.EntityFireElemental;
import am2.items.ItemsCommonProxy;
import am2.particles.AMParticle;
import am2.power.PowerNodeRegistry;
import am2.spell.SpellHelper;
import am2.spell.SpellUtils;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.relauncher.ReflectionHelper;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import org.apache.logging.log4j.Level;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.EnumSet;
import java.util.Random;

public class FireDamage implements ISpellComponent, IRitualInteraction{

	public static Field recentlyHit;

	static {
		try {
			recentlyHit = ReflectionHelper.findField(EntityLivingBase.class, "recentlyHit", "field_70718_bc");
		} catch(Exception e){ }
	}

	@Override
	public boolean applyEffectBlock(ItemStack stack, World world, int blockx, int blocky, int blockz, int blockFace, double impactX, double impactY, double impactZ, EntityLivingBase caster){
		Block block = world.getBlock(blockx, blocky, blockz);

		if (block == BlocksCommonProxy.obelisk){
			ItemStack[] reagents = RitualShapeHelper.instance.checkForRitual(this, world, blockx, blocky, blockz);
			if (reagents != null){
				if (!world.isRemote){
					RitualShapeHelper.instance.consumeRitualReagents(this, world, blockx, blocky, blockz);
					RitualShapeHelper.instance.consumeRitualShape(this, world, blockx, blocky, blockz);
					world.setBlock(blockx, blocky, blockz, BlocksCommonProxy.blackAurem);
					PowerNodeRegistry.For(world).registerPowerNode((IPowerNode)world.getTileEntity(blockx, blocky, blockz));
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
		EntityLivingBase elb = (EntityLivingBase) target;
		float baseDamage = 6;
		double damage = SpellUtils.instance.getModifiedDouble_Add(baseDamage, stack, caster, target, world, 0, SpellModifiers.DAMAGE);
		if (isNetherMob(target))
			return true;
		boolean vampire = isElbVampire(elb);
		if (vampire) {
			causeUnblockableDamage(elb, damage);
			return true;
		}
		return SpellHelper.instance.attackTargetSpecial(stack, target, DamageSources.causeEntityFireDamage(caster), SpellUtils.instance.modifyDamage(caster, (float)damage));
	}

	// not entirely unblockable, but close enough
	public static void causeUnblockableDamage(EntityLivingBase elb, double damage) {
		if(elb.worldObj.isRemote) return;
		if (elb.getHealth() <= damage) {
			try {
				recentlyHit.setInt(elb, 60);
			}
			catch(Exception e){}
			elb.func_110142_aN().func_94547_a(new DamageSourceUnsummon(), elb.getHealth(), elb.getHealth());
			elb.setHealth(0);
			elb.onDeath(new EntityDamageSource("hubris", elb).setFireDamage());
		} else { // the setHealth function but without an opportunity to coremod into it
			elb.getDataWatcher().updateObject(6, Float.valueOf(MathHelper.clamp_float(((float)(elb.getHealth() - damage)), 0.0F, elb.getMaxHealth())));
		}
	}

	public static boolean isElbVampire(Entity entity) {
		try {
			Class<?> clazz = Class.forName("com.emoniph.witchery.util.CreatureUtil");
			Method method = clazz.getMethod("isVampire", new Class<?>[]{Entity.class});
			Object value = method.invoke(null, new Object[] {entity});
			return (Boolean)value;
		} catch (NoClassDefFoundError e) { // in theory not needed, but just in case
		} catch (Exception e) { // witchery not found
		}
		return false;
	}

	public static boolean isElbWerewolf(Entity entity) {
		try {
			Class<?> clazz = Class.forName("com.emoniph.witchery.util.CreatureUtil");
			Method method = clazz.getMethod("isWerewolf", new Class<?>[]{Entity.class});
			Object value = method.invoke(null, new Object[] {entity});
			return (Boolean)value;
		} catch (NoClassDefFoundError e) { // in theory not needed, but just in case
		} catch (Exception e) { // witchery not found
		}
		return false;
	}

	private boolean isNetherMob(Entity target){
		return target instanceof EntityPigZombie || target instanceof EntityDarkling || target instanceof EntityFireElemental || target instanceof EntityGhast;
	}

	@Override
	public float manaCost(EntityLivingBase caster){
		return 120;
	}

	@Override
	public float burnout(EntityLivingBase caster){
		return 20;
	}

	@Override
	public ItemStack[] reagents(EntityLivingBase caster){
		return null;
	}

	@Override
	public void spawnParticles(World world, double x, double y, double z, EntityLivingBase caster, Entity target, Random rand, int colorModifier){
		for (int i = 0; i < 5; ++i){
			AMParticle particle = (AMParticle)AMCore.proxy.particleManager.spawn(world, "explosion_2", x, y, z);
			if (particle != null){
				particle.addRandomOffset(1, 0.5, 1);
				particle.addVelocity(rand.nextDouble() * 0.2 - 0.1, rand.nextDouble() * 0.2, rand.nextDouble() * 0.2 - 0.1);
				particle.setAffectedByGravity();
				particle.setDontRequireControllers();
				particle.setMaxAge(5);
				particle.setParticleScale(0.1f);
				if (colorModifier > -1){
					particle.setRGBColorF(((colorModifier >> 16) & 0xFF) / 255.0f, ((colorModifier >> 8) & 0xFF) / 255.0f, (colorModifier & 0xFF) / 255.0f);
				}
			}
		}
	}

	@Override
	public EnumSet<Affinity> getAffinity(){
		return EnumSet.of(Affinity.FIRE);
	}

	@Override
	public int getID(){
		return 15;
	}

	@Override
	public Object[] getRecipeItems(){
		return new Object[]{
				new ItemStack(ItemsCommonProxy.rune, 1, ItemsCommonProxy.rune.META_RED),
				Items.flint_and_steel,
				new ItemStack(ItemsCommonProxy.itemOre, 1, ItemsCommonProxy.itemOre.META_VINTEUMDUST),
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
				new ItemStack(ItemsCommonProxy.itemOre, 1, ItemsCommonProxy.itemOre.META_SUNSTONE)
		};
	}

	@Override
	public int getReagentSearchRadius(){
		return 3;
	}
}
