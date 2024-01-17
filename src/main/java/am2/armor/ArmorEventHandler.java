package am2.armor;

import am2.AMCore;
import am2.LogHelper;
import am2.api.items.armor.ArmorTextureEvent;
import am2.api.items.armor.IArmorImbuement;
import am2.api.items.armor.ImbuementApplicationTypes;
import am2.bosses.EntityAirGuardian;
import am2.bosses.EntityArcaneGuardian;
import am2.bosses.EntityEnderGuardian;
import am2.bosses.EntityNatureGuardian;
import am2.items.ItemBoxOfIllusions;
import am2.items.ItemSoulspike;
import am2.items.ItemsCommonProxy;
import am2.particles.AMParticle;
import am2.particles.ParticleFadeOut;
import am2.playerextensions.ExtendedProperties;
import am2.proxy.gui.ModelLibrary;
import am2.texture.ResourceManager;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.ReflectionHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.*;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingJumpEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.tclproject.mysteriumlib.asm.fixes.MysteriumPatchesFixesMagicka;
import org.lwjgl.opengl.GL11;

import java.util.*;

public class ArmorEventHandler{

	@SubscribeEvent
	public void onEntityLiving(LivingUpdateEvent event){
		if (!(event.entityLiving instanceof EntityPlayer))
			return;

		doInfusions(ImbuementApplicationTypes.ON_TICK, event, (EntityPlayer)event.entityLiving);

		EntityPlayer player = (EntityPlayer)event.entityLiving;
		if (player.inventory.armorInventory[0] != null && player.inventory.armorInventory[0].getItem() == ItemsCommonProxy.archmageBoots){
			ModifiableAttributeInstance instance = (ModifiableAttributeInstance)player.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.movementSpeed);

			ArrayList<AttributeModifier> toRemove = new ArrayList<AttributeModifier>();

			Collection c = instance.func_111122_c();
			ArrayList arraylist = new ArrayList(c);
			Iterator iterator = arraylist.iterator();

			while (iterator.hasNext()){
				AttributeModifier attributemodifier = (AttributeModifier)iterator.next();
				if (attributemodifier.getOperation() == 2 && attributemodifier.getAmount() < 0.0f){
					toRemove.add(attributemodifier);
				}
			}

			for (AttributeModifier modifier : toRemove){
				instance.removeModifier(modifier);
			}

			float speedInAirLast = ReflectionHelper.getPrivateValue(EntityPlayer.class, player, "speedInAir", "field_71102_ce");
			if (speedInAirLast < 0.1F){
				ReflectionHelper.setPrivateValue(EntityPlayer.class, player, 0.1F, "speedInAir", "field_71102_ce");
				setSpeedInAir.put(player, speedInAirLast);
			}

			if (player.worldObj.isRemote) {
				if (Minecraft.getMinecraft().gameSettings.keyBindJump.getIsKeyPressed()) {
					player.motionY += 0.135;
				}
			}
		} else {
			if (setSpeedInAir.get(player) != null) {
				ReflectionHelper.setPrivateValue(EntityPlayer.class, player, setSpeedInAir.get(player), "speedInAir", "field_71102_ce");
			}
		}
	}

	private Map<EntityPlayer, Float> setSpeedInAir = new HashMap<EntityPlayer, Float>();

	@SubscribeEvent
	public void onEntityHurt(LivingHurtEvent event){
		if (!(event.entityLiving instanceof EntityPlayer))
			return;

		doInfusions(ImbuementApplicationTypes.ON_HIT, event, (EntityPlayer)event.entityLiving);

		if (event.entityLiving instanceof EntityPlayer)
			doXPInfusion((EntityPlayer)event.entityLiving, 0.01f, Math.max(0.05f, Math.min(event.ammount, 5)));
	}

	@SubscribeEvent
	public void onEntityJump(LivingJumpEvent event){
		if (!(event.entityLiving instanceof EntityPlayer))
			return;

		doInfusions(ImbuementApplicationTypes.ON_JUMP, event, (EntityPlayer)event.entityLiving);
	}

	@SubscribeEvent
	public void onMiningSpeed(BreakSpeed event){
		doInfusions(ImbuementApplicationTypes.ON_MINING_SPEED, event, (EntityPlayer)event.entityPlayer);
	}

	@SubscribeEvent
	public void onEntityDeath(LivingDeathEvent event){
		if (event.source.getSourceOfDamage() instanceof EntityPlayer)
			doXPInfusion((EntityPlayer)event.source.getSourceOfDamage(), 1, Math.min(20, event.entityLiving.getMaxHealth()));

		if (!(event.entityLiving instanceof EntityPlayer))
			return;

		doInfusions(ImbuementApplicationTypes.ON_DEATH, event, (EntityPlayer)event.entityLiving);
	}

	private void doInfusions(ImbuementApplicationTypes type, Event event, EntityPlayer player){
		ExtendedProperties props = ExtendedProperties.For(player);

		for (int i = 0; i < 4; ++i){
			IArmorImbuement[] infusions = ArmorHelper.getInfusionsOnArmor(player, i);
			int cd = props.armorProcCooldowns[i];
			for (IArmorImbuement inf : infusions){
				if (inf == null)
					continue;
				if (inf.getApplicationTypes().contains(type)){
					if (cd == 0 || inf.canApplyOnCooldown()){
						if (inf.applyEffect(player, player.worldObj, player.getCurrentArmor(i), type, event)){
							if (inf.getCooldown() > 0){
								if (props.armorProcCooldowns[i] < inf.getCooldown()){
									props.armorProcCooldowns[i] = inf.getCooldown();
									if (player instanceof EntityPlayerMP)
										AMCore.proxy.blackoutArmorPiece((EntityPlayerMP)player, i, inf.getCooldown());
								}
							}
						}
					}
				}
			}
		}
	}

	private void doXPInfusion(EntityPlayer player, float xpMin, float xpMax){
		float amt = (float)((player.worldObj.rand.nextFloat() * xpMin + (xpMax - xpMin)) * AMCore.config.getArmorXPInfusionFactor());
		ArmorHelper.addXPToArmor(amt, player);
	}

	@SubscribeEvent
	public void onArmorTexture(ArmorTextureEvent event){
		if (event.renderIndex == ArmorHelper.getArmorRenderIndex("mage")){
			if (event.slot == 2){
				event.texture = "arsmagica2:textures/models/mage_2.png";
			}else{
				event.texture = "arsmagica2:textures/models/mage_1.png";
			}
		}else if (event.renderIndex == ArmorHelper.getArmorRenderIndex("battlemage")){
			if (event.slot == 2){
				event.texture = "arsmagica2:textures/models/battlemage_2.png";
			}else{
				event.texture = "arsmagica2:textures/models/battlemage_1.png";
			}
		}else if (event.renderIndex == ArmorHelper.getArmorRenderIndex("archmage")){
			if (event.slot == 2){
				event.texture = "arsmagica2:textures/models/archmage_2.png";
			}else{
				event.texture = "arsmagica2:textures/models/archmage_1.png";
			}
		}else if (event.renderIndex == ArmorHelper.getArmorRenderIndex("bound")){
			if (event.slot == 2){
				event.texture = "arsmagica2:textures/models/bound_2.png";
			}else{
				event.texture = "arsmagica2:textures/models/bound_1.png";
			}
		}else if (event.renderIndex == ArmorHelper.getArmorRenderIndex("ender")){
			event.texture = "arsmagica2:textures/models/ender_1.png";
		}else if (event.renderIndex == ArmorHelper.getArmorRenderIndex("magitech")){
			event.texture = "arsmagica2:textures/models/magitech_1.png";
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onPlayerRenderPre(RenderPlayerEvent.Pre event) { // true invis
		if (MysteriumPatchesFixesMagicka.isPlayerEthereal(event.entityPlayer)) {event.setCanceled(true); return;}
		if (MysteriumPatchesFixesMagicka.playerModelMap.get(event.entityPlayer.getCommandSenderName()) != null && !MysteriumPatchesFixesMagicka.playerModelMap.get(event.entityPlayer.getCommandSenderName()).startsWith("maid")) {
			// custom mobs
			String toMob = MysteriumPatchesFixesMagicka.playerModelMap.get(event.entityPlayer.getCommandSenderName());
			EntityLivingBase elb = new EntityZombie(event.entityPlayer.worldObj);
			if (toMob.equalsIgnoreCase("spider")) {
				elb = new EntitySpider(event.entityPlayer.worldObj);
			} else if (toMob.equalsIgnoreCase("witch")) {
				elb = new EntityWitch(event.entityPlayer.worldObj);
			} else if (toMob.equalsIgnoreCase("snowman")) {
				elb = new EntitySnowman(event.entityPlayer.worldObj);
			} else if (toMob.equalsIgnoreCase("creeper")) {
				elb = new EntityCreeper(event.entityPlayer.worldObj);
			} else if (toMob.equalsIgnoreCase("chicken")) {
				elb = new EntityChicken(event.entityPlayer.worldObj);
			} else if (toMob.equalsIgnoreCase("cow")) {
				elb = new EntityCow(event.entityPlayer.worldObj);
			} else if (toMob.equalsIgnoreCase("ender")) {
				elb = new EntityArcaneGuardian(event.entityPlayer.worldObj);
			}
			ItemBoxOfIllusions.Copy(event.entityPlayer, elb);
			Render rle = RenderManager.instance.getEntityRenderObject(elb);
			if (rle instanceof RendererLivingEntity) {
				if (elb.ticksExisted == 0)
				{
					elb.lastTickPosX = elb.posX;
					elb.lastTickPosY = elb.posY;
					elb.lastTickPosZ = elb.posZ;
				}

				double d0 = elb.lastTickPosX + (elb.posX - elb.lastTickPosX) * (double)event.partialRenderTick;
				double d1 = elb.lastTickPosY + (elb.posY - elb.lastTickPosY) * (double)event.partialRenderTick;
				double d2 = elb.lastTickPosZ + (elb.posZ - elb.lastTickPosZ) * (double)event.partialRenderTick;
				float f1 = elb.prevRotationYaw + (elb.rotationYaw - elb.prevRotationYaw) * event.partialRenderTick;
				int i = elb.getBrightnessForRender(event.partialRenderTick);

				if (elb.isBurning())
				{
					i = 15728880;
				}

				int j = i % 65536;
				int k = i / 65536;
				OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)j / 1.0F, (float)k / 1.0F);
				GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
				((RendererLivingEntity) rle).doRender(elb, d0 - RenderManager.renderPosX, d1 - RenderManager.renderPosY - event.entityPlayer.yOffset, d2 - RenderManager.renderPosZ, f1, event.partialRenderTick);
				event.setCanceled(true);
			} else {
				LogHelper.warn("Renderer not of living entity type! Report this as an error.");
			}
			return;
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onPlayerRenderPreSpecial(RenderPlayerEvent.Specials.Pre event) { // true invis
		if (MysteriumPatchesFixesMagicka.isPlayerEthereal(event.entityPlayer)) {event.setCanceled(true); return;}
		if (MysteriumPatchesFixesMagicka.playerModelMap.get(event.entityPlayer.getCommandSenderName()) != null && !MysteriumPatchesFixesMagicka.playerModelMap.get(event.entityPlayer.getCommandSenderName()).startsWith("maid")) {event.setCanceled(true); return;}
	}

	// Sorry Roadhog for borrowing some of your code starting here :D

	private static boolean hadHeldItemTooltips;

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	@SideOnly(Side.CLIENT)
	public void onOverlayRenderPre(RenderGameOverlayEvent.Pre event) {
		if(MysteriumPatchesFixesMagicka.isPlayerEthereal(Minecraft.getMinecraft().thePlayer)) {
			if(event.type == RenderGameOverlayEvent.ElementType.HOTBAR ||
					event.type == RenderGameOverlayEvent.ElementType.CROSSHAIRS) {
				event.setCanceled(true);
			}
			if(event.type == RenderGameOverlayEvent.ElementType.ALL) {
				hadHeldItemTooltips = Minecraft.getMinecraft().gameSettings.heldItemTooltips;
				Minecraft.getMinecraft().gameSettings.heldItemTooltips = false;
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	@SideOnly(Side.CLIENT)
	public void onOverlayRenderPost(RenderGameOverlayEvent.Post event) {
		if(MysteriumPatchesFixesMagicka.isPlayerEthereal(Minecraft.getMinecraft().thePlayer)) {
			if(event.type == RenderGameOverlayEvent.ElementType.ALL) {
				Minecraft.getMinecraft().gameSettings.heldItemTooltips = hadHeldItemTooltips;
			}
		}
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onHandRender(RenderHandEvent event) {
		if(MysteriumPatchesFixesMagicka.isPlayerEthereal(Minecraft.getMinecraft().thePlayer))
			event.setCanceled(true);
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onFireRender(RenderBlockOverlayEvent event) {
		if(MysteriumPatchesFixesMagicka.isPlayerEthereal(Minecraft.getMinecraft().thePlayer))
			event.setCanceled(true);
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onRenderFogDensity(EntityViewRenderEvent.FogDensity event) {
		if(event.entity instanceof EntityPlayer) {
			if(MysteriumPatchesFixesMagicka.isPlayerEthereal((EntityPlayer)event.entity)) {
				if(event.block.getMaterial() == Material.water || event.block.getMaterial() == Material.lava) {
					event.setCanceled(true);
					event.density = 0;
				}
			}
		}
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onBlockHighlight(DrawBlockHighlightEvent event) {
		if(MysteriumPatchesFixesMagicka.isPlayerEthereal(event.player)) {
			Block block = Minecraft.getMinecraft().theWorld.getBlock(event.target.blockX, event.target.blockY, event.target.blockZ);
			int meta = Minecraft.getMinecraft().theWorld.getBlockMetadata(event.target.blockX, event.target.blockY, event.target.blockZ);
			if(!block.hasTileEntity(meta) || !(Minecraft.getMinecraft().theWorld.getTileEntity(event.target.blockX, event.target.blockY, event.target.blockZ) instanceof IInventory)) {
				event.setCanceled(true);
			}
		}
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onClientTick(TickEvent.ClientTickEvent event) {
		EntityPlayer player = FMLClientHandler.instance().getClientPlayerEntity();
		if(player != null && !(player instanceof FakePlayer) && Minecraft.getMinecraft().playerController != null && event.phase == TickEvent.Phase.START) {
			if(MysteriumPatchesFixesMagicka.isPlayerEthereal(Minecraft.getMinecraft().thePlayer)) {
				if(!player.capabilities.isFlying) {
					player.capabilities.isFlying = true;
					player.sendPlayerAbilities();
				}
			}
		}
	}

	@SubscribeEvent
	public void onInteract(PlayerInteractEvent event) {
		if(MysteriumPatchesFixesMagicka.isPlayerEthereal(event.entityPlayer)) {
			if(event.action != PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK)
				event.setCanceled(true);
			else {
				if(!event.world.blockExists(event.x, event.y, event.z)) {
					return;
				}
				Block block = event.world.getBlock(event.x, event.y, event.z);
				int meta = event.world.getBlockMetadata(event.x, event.y, event.z);
				if(!block.hasTileEntity(meta) || !(event.world.getTileEntity(event.x, event.y, event.z) instanceof IInventory)) {
					event.setCanceled(true);
				}
			}
		}
	}

	@SubscribeEvent
	public void onInteract(BlockEvent.PlaceEvent event) {
		if(MysteriumPatchesFixesMagicka.isPlayerEthereal(event.player)) {
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public void onItemPickup(EntityItemPickupEvent event) {
		if(MysteriumPatchesFixesMagicka.isPlayerEthereal(event.entityPlayer)) {
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public void onInteract(AttackEntityEvent event) {
		if(MysteriumPatchesFixesMagicka.isPlayerEthereal(event.entityPlayer)) {
			event.setCanceled(true);
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onPlayerTick(TickEvent.PlayerTickEvent event) {
		if(event.phase == TickEvent.Phase.START) {
			if(MysteriumPatchesFixesMagicka.isPlayerEthereal(event.player)) {
				event.player.noClip = true;
				event.player.onGround = false;
				event.player.setInvisible(true);
			}
		}
	}

	@SubscribeEvent
	public void breakSpeed(PlayerEvent.BreakSpeed event) {
		if(MysteriumPatchesFixesMagicka.isPlayerEthereal(event.entityPlayer))
			event.newSpeed = 0;
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void itemToss(ItemTossEvent event) {
		if(MysteriumPatchesFixesMagicka.isPlayerEthereal(event.player)) {
			event.setCanceled(true);
			ItemStack item = event.entityItem.getEntityItem();
			event.player.inventory.addItemStackToInventory(item);
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onPlayerRender(RenderPlayerEvent.Post event) {
		if (event.entityPlayer == Minecraft.getMinecraft().thePlayer) { // these additions only render clientside/SP
			if (event.entityPlayer.getCurrentArmor(3) != null) {
				if (event.entityPlayer.getCurrentArmor(3).getItem() == ItemsCommonProxy.archmageHood) {
					EntityPlayer p_76986_1_ = event.entityPlayer;
					int i1 = Minecraft.getMinecraft().gameSettings.limitFramerate;
					float p_76986_9_ = System.nanoTime() + (long) (1000000000 / i1);
					float f5 = 0.0625F;
					float f2 = 0; // interpolateRotation(p_76986_1_.prevRenderYawOffset, p_76986_1_.renderYawOffset, p_76986_9_);
					float f3 = -p_76986_1_.prevRotationYawHead; // , p_76986_1_.rotationYawHead, p_76986_9_);
					float f4 = 0;
					float f13 = 63;
					float f6 = p_76986_1_.prevLimbSwingAmount + (p_76986_1_.limbSwingAmount - p_76986_1_.prevLimbSwingAmount) * p_76986_9_;
					float f7 = p_76986_1_.limbSwing - p_76986_1_.limbSwingAmount * (1.0F - p_76986_9_);
					if (f6 > 1.0F) {
						f6 = 1.0F;
					}
					GL11.glPushMatrix();
					GL11.glTranslated(0, 0.4, 0);
					GL11.glColor4d(1, 1, 1, 1);
					Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation("arsmagica2:" + ResourceManager.getMobTexturePath("bosses/fire_guardian.png")));
					ModelLibrary.instance.fireEars.saveValues = true;
					ModelLibrary.instance.fireEars.render(event.entityPlayer, f7, f6, f4, f3, -f13, f5);
					GL11.glPopMatrix();
				}
			}
			if (event.entityPlayer.getCurrentArmor(2) != null) {
				if (event.entityPlayer.getCurrentArmor(2).getItem() == ItemsCommonProxy.archmageArmor) {
					EntityPlayer p_76986_1_ = event.entityPlayer;
					int i1 = Minecraft.getMinecraft().gameSettings.limitFramerate;
					float p_76986_9_ = System.nanoTime() + (long) (1000000000 / i1);
					float f5 = 0.0625F;
					float f2 = 0; // interpolateRotation(p_76986_1_.prevRenderYawOffset, p_76986_1_.renderYawOffset, p_76986_9_);
					float f3 = -p_76986_1_.rotationYawHead; // , p_76986_1_.rotationYawHead, p_76986_9_);
					float f4 = 0;
					float f13 = 0;
					float f6 = 0; // p_76986_1_.prevLimbSwingAmount + (p_76986_1_.limbSwingAmount - p_76986_1_.prevLimbSwingAmount) * p_76986_9_;
					float f7 = p_76986_1_.limbSwing - p_76986_1_.limbSwingAmount * (1.0F - p_76986_9_) + 50;
					if (f6 > 1.0F) {
						f6 = 1.0F;
					}
					GL11.glPushMatrix();
					GL11.glTranslated(0, -0.75, 0);
					GL11.glScaled(0.6, 0.6, 0.6);
					setRotation(rotation + 1);
					GL11.glRotated(rotation, 0, 1, 0);
					GL11.glColor4d(1, 1, 1, 1);
					Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation("arsmagica2:" + ResourceManager.getMobTexturePath("bosses/earth_guardian.png")));
					ModelLibrary.instance.earthArmor.saveValues = true;
					ModelLibrary.instance.earthArmor.renderArms(event.entityPlayer, f7, f6, f4, f3, f13, f5);
					GL11.glPopMatrix();
				}
			}
			if (event.entityPlayer.getCurrentArmor(1) != null) {
				if (event.entityPlayer.getCurrentArmor(1).getItem() == ItemsCommonProxy.archmageLeggings) {
					EntityPlayer p_76986_1_ = event.entityPlayer;
					int i1 = Minecraft.getMinecraft().gameSettings.limitFramerate;
					float p_76986_9_ = System.nanoTime() + (long) (1000000000 / i1);
					float f5 = 0.0625F;
					float f2 = 0; // interpolateRotation(p_76986_1_.prevRenderYawOffset, p_76986_1_.renderYawOffset, p_76986_9_);
					float f3 = -p_76986_1_.rotationYawHead; // , p_76986_1_.rotationYawHead, p_76986_9_);
					float f4 = 0;
					float f13 = 0;
					float f6 = 0; // p_76986_1_.prevLimbSwingAmount + (p_76986_1_.limbSwingAmount - p_76986_1_.prevLimbSwingAmount) * p_76986_9_;
					float f7 = p_76986_1_.limbSwing - p_76986_1_.limbSwingAmount * (1.0F - p_76986_9_) + 50;
					if (f6 > 1.0F) {
						f6 = 1.0F;
					}
					GL11.glPushMatrix();
					GL11.glTranslated(0, -1.3, 0);
					GL11.glScaled(1.05, 1.05, 1.05);
					setRotation2(rotation2 + 0.4f);
					GL11.glRotatef(rotation2, 0, -1, 0);
					Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation("arsmagica2:" + ResourceManager.getMobTexturePath("bosses/water_guardian.png")));
					GL11.glColor4d(1, 1, 1, 1);
					ModelLibrary.instance.earthArmor.saveValues = true;
					ModelLibrary.instance.waterOrbs.render(event.entityPlayer, f7, f6, f4, f3, f13, f5);
					GL11.glPopMatrix();
				}
			}
		}
	}

	private int rotation = 0;
	public void setRotation(int newRotation) {
		rotation = newRotation;
		if (rotation == 360) {
			rotation = 0;
		}
	}

	private float rotation2 = 0;
	public void setRotation2(float newRotation) {
		rotation2 = newRotation;
		if (rotation >= 360) {
			rotation2 = 0;
		}
	}

	private float interpolateRotation(float p_77034_1_, float p_77034_2_, float p_77034_3_)
	{
		float f3;

		for (f3 = p_77034_2_ - p_77034_1_; f3 < -180.0F; f3 += 360.0F)
		{
			;
		}

		while (f3 >= 180.0F)
		{
			f3 -= 360.0F;
		}

		return p_77034_1_ + p_77034_3_ * f3;
	}
}
