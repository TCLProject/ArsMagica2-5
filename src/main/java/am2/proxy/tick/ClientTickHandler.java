package am2.proxy.tick;

import am2.AMCore;
import am2.EntityItemWatcher;
import am2.LogHelper;
import am2.MeteorSpawnHelper;
import am2.api.math.AMVector3;
import am2.api.power.IPowerNode;
import am2.api.power.PowerTypes;
import am2.api.spell.ItemSpellBase;
import am2.api.spell.component.interfaces.ISpellComponent;
import am2.armor.ArmorHelper;
import am2.armor.infusions.GenericImbuement;
import am2.bosses.BossSpawnHelper;
import am2.commands.ConfigureAMUICommand;
import am2.guis.AMGuiHelper;
import am2.guis.AMIngameGUI;
import am2.guis.GuiHudCustomization;
import am2.items.ItemSpellBook;
import am2.items.ItemsCommonProxy;
import am2.lore.ArcaneCompendium;
import am2.lore.CompendiumEntryTypes;
import am2.network.AMDataWriter;
import am2.network.AMNetHandler;
import am2.network.AMPacketIDs;
import am2.network.AMPacketProcessorClient;
import am2.particles.AMLineArc;
import am2.particles.AMParticle;
import am2.particles.ParticleFadeOut;
import am2.playerextensions.ExtendedProperties;
import am2.power.PowerNodeEntry;
import am2.proxy.ClientProxy;
import am2.spell.SpellHelper;
import am2.spell.SpellUtils;
import am2.spell.components.Telekinesis;
import am2.utility.DimensionUtilities;
import am2.worldgen.RetroactiveWorldgenerator;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.sound.PlaySoundEvent17;
import net.tclproject.mysteriumlib.asm.fixes.MysteriumPatchesFixesMagicka;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import static am2.spell.SpellHelper.lingeringSpellList;

@SideOnly(Side.CLIENT)
public class ClientTickHandler{

	private final AMIngameGUI inGameGui = new AMIngameGUI();
	public static HashMap<EntityLiving, EntityLivingBase> targetsToSet = new HashMap<EntityLiving, EntityLivingBase>();
	private int mouseWheelValue = 0;
	private int currentSlot = -1;
	private boolean usingItem;

	public static String worldName;

	private boolean firstTick = true;
	private boolean compendiumLoad;

	private ArrayList<AMLineArc> arcs = new ArrayList<AMLineArc>();
	private int arcSpawnCounter = 0;
	private final int arcSpawnFrequency = 95;

	private int powerWatchSyncTick = 0;
	private AMVector3 powerWatch = AMVector3.zero();
	private boolean hasSynced = false;
	private PowerNodeEntry powerData = null;

	private String lastWorldName;

	public static int ticksInGame = 0;

	private void gameTick_Start(){

		if (!CompendiumEntryTypes.instance.hasInitialized())
			CompendiumEntryTypes.instance.initTextures();

		if (Minecraft.getMinecraft().isIntegratedServerRunning()){
			if (worldName == null || !worldName.equals(Minecraft.getMinecraft().getIntegratedServer().getWorldName().replace(" ", "_"))){
				worldName = Minecraft.getMinecraft().getIntegratedServer().getWorldName().replace(" ", "_");
				firstTick = true;
			}
		}else{
			if (worldName != null && (lastWorldName == null || lastWorldName != worldName.replace(" ", "_"))){
				lastWorldName = worldName.replace(" ", "_");
				firstTick = true;
			}
		}

		if (firstTick){
			ItemsCommonProxy.crystalPhylactery.getSpawnableEntities(Minecraft.getMinecraft().theWorld);
			compendiumLoad = true;
			firstTick = false;
		}

		if (compendiumLoad){
			ArcaneCompendium.instance.loadUnlockData();
			compendiumLoad = false;
		}
		AMCore.proxy.itemFrameWatcher.checkWatchedFrames();
	}

	private void applyDeferredPotionEffects(){
		for (EntityLivingBase ent : AMCore.proxy.getDeferredPotionEffects().keySet()){
			ArrayList<PotionEffect> potions = AMCore.proxy.getDeferredPotionEffects().get(ent);
			for (PotionEffect effect : potions)
				ent.addPotionEffect(effect);
		}

		AMCore.proxy.clearDeferredPotionEffects();
	}

	@SubscribeEvent
	public void onGuiOpen(GuiOpenEvent event) { // you're a ghost, how are you looking inside yourself
		if (MysteriumPatchesFixesMagicka.isPlayerEthereal(Minecraft.getMinecraft().thePlayer) && event.gui instanceof GuiInventory) event.setCanceled(true);
	}

	@SubscribeEvent
	public void onPlaySound(PlaySoundEvent17 event) {
		if (Minecraft.getMinecraft() != null) {
			if (Minecraft.getMinecraft().thePlayer != null && (AMPacketProcessorClient.deaf > 0)) event.result = null; // prevent sound playing
		}
	}

	private void applyDeferredDimensionTransfers(){
		for (EntityLivingBase ent : AMCore.proxy.getDeferredDimensionTransfers().keySet()){
			DimensionUtilities.doDimensionTransfer(ent, AMCore.proxy.getDeferredDimensionTransfers().get(ent));
		}

		AMCore.proxy.clearDeferredDimensionTransfers();
	}

	private void gameTick_End(){
		ticksInGame++;

		AMGuiHelper.instance.tick();
		EntityItemWatcher.instance.tick();
		checkMouseDWheel();

		if (Minecraft.getMinecraft().isIntegratedServerRunning()){
			MeteorSpawnHelper.instance.tick();
			applyDeferredPotionEffects();
		}

		if (!powerWatch.equals(AMVector3.zero())){
			if (powerWatchSyncTick++ == 0){
				AMNetHandler.INSTANCE.sendPowerRequestToServer(powerWatch);
			}
			powerWatchSyncTick %= 20;
		}
	}

	private void spawnPowerPathVisuals(){
		if (Minecraft.getMinecraft().thePlayer.getCurrentArmor(3) != null &&
				(Minecraft.getMinecraft().thePlayer.getCurrentArmor(3).getItem() == ItemsCommonProxy.magitechGoggles ||
						ArmorHelper.isInfusionPreset(Minecraft.getMinecraft().thePlayer.getCurrentArmor(3), GenericImbuement.magitechGoggleIntegration))
				){

			if (arcSpawnCounter++ >= arcSpawnFrequency){
				arcSpawnCounter = 0;

				AMVector3 playerPos = new AMVector3(Minecraft.getMinecraft().thePlayer);

				HashMap<PowerTypes, ArrayList<LinkedList<AMVector3>>> paths = AMCore.proxy.getPowerPathVisuals();
				if (paths != null){
					for (PowerTypes type : paths.keySet()){

						String texture =
								type == PowerTypes.LIGHT ? "textures/blocks/oreblockbluetopaz.png" :
										type == PowerTypes.NEUTRAL ? "textures/blocks/oreblockvinteum.png" :
												type == PowerTypes.DARK ? "textures/blocks/oreblocksunstone.png" :
														"textures/blocks/oreblocksunstone.png";

						ArrayList<LinkedList<AMVector3>> pathList = paths.get(type);
						for (LinkedList<AMVector3> individualPath : pathList){
							for (int i = 0; i < individualPath.size() - 1; ++i){
								AMVector3 start = individualPath.get(i + 1);
								AMVector3 end = individualPath.get(i);

								if (start.distanceSqTo(playerPos) > 2500 || end.distanceSqTo(playerPos) > 2500){
									continue;
								}


								TileEntity teStart = Minecraft.getMinecraft().theWorld.getTileEntity((int)start.x, (int)start.y, (int)start.z);
								TileEntity teEnd = Minecraft.getMinecraft().theWorld.getTileEntity((int)end.x, (int)end.y, (int)end.z);

								if (teEnd == null || !(teEnd instanceof IPowerNode))
									break;

								double startX = start.x + ((teStart != null && teStart instanceof IPowerNode) ? ((IPowerNode)teStart).particleOffset(0) : 0.5f);
								double startY = start.y + ((teStart != null && teStart instanceof IPowerNode) ? ((IPowerNode)teStart).particleOffset(1) : 0.5f);
								double startZ = start.z + ((teStart != null && teStart instanceof IPowerNode) ? ((IPowerNode)teStart).particleOffset(2) : 0.5f);

								double endX = end.x + ((IPowerNode)teEnd).particleOffset(0);
								double endY = end.y + ((IPowerNode)teEnd).particleOffset(1);
								double endZ = end.z + ((IPowerNode)teEnd).particleOffset(2);

								AMLineArc arc = (AMLineArc)AMCore.proxy.particleManager.spawn(
										Minecraft.getMinecraft().theWorld,
										texture,
										startX,
										startY,
										startZ,
										endX,
										endY,
										endZ);

								if (arc != null){
									arcs.add(arc);
								}
							}
						}
					}
				}
			}
		}else{
			Iterator<AMLineArc> it = arcs.iterator();
			while (it.hasNext()){
				AMLineArc arc = it.next();
				if (arc == null || arc.isDead)
					it.remove();
				else
					arc.setDead();
			}
			arcSpawnCounter = arcSpawnFrequency;
		}
	}

	private void checkMouseDWheel(){
		if (this.mouseWheelValue != 0 && this.currentSlot > -1){
			Minecraft.getMinecraft().thePlayer.inventory.currentItem = this.currentSlot;

			ItemStack stack = Minecraft.getMinecraft().thePlayer.getCurrentEquippedItem();

			if (checkForTKMove(stack)){
				ExtendedProperties props = ExtendedProperties.For(Minecraft.getMinecraft().thePlayer);
				if (this.mouseWheelValue > 0 && props.TK_Distance < 10){
					props.TK_Distance += 0.5f;
				}else if (this.mouseWheelValue < 0 && props.TK_Distance > 0.3){
					props.TK_Distance -= 0.5f;
				}
				LogHelper.debug("TK Distance: %.2f", props.TK_Distance);
				props.syncTKDistance();
			}else if (stack.getItem() instanceof ItemSpellBook && Minecraft.getMinecraft().thePlayer.isSneaking()){
				ItemSpellBook isb = (ItemSpellBook)stack.getItem();
				if (this.mouseWheelValue != 0){
					byte subID = 0;
					if (this.mouseWheelValue < 0){
						isb.SetNextSlot(Minecraft.getMinecraft().thePlayer.getCurrentEquippedItem());
						subID = ItemSpellBook.ID_NEXT_SPELL;
					}else{
						isb.SetPrevSlot(Minecraft.getMinecraft().thePlayer.getCurrentEquippedItem());
						subID = ItemSpellBook.ID_PREV_SPELL;
					}
					//send packet to server
					AMNetHandler.INSTANCE.sendPacketToServer(
							AMPacketIDs.SPELLBOOK_CHANGE_ACTIVE_SLOT,
							new AMDataWriter()
									.add(subID)
									.add(Minecraft.getMinecraft().thePlayer.getEntityId())
									.add(Minecraft.getMinecraft().thePlayer.inventory.currentItem)
									.generate()
					);
				}
			}
			this.currentSlot = -1;
			this.mouseWheelValue = 0;
		}
	}

	private boolean checkForTKMove(ItemStack stack){
		if (stack.getItem() instanceof ItemSpellBook){
			ItemStack activeStack = ((ItemSpellBook)stack.getItem()).GetActiveItemStack(stack);
			if (activeStack != null)
				stack = activeStack;
		}
		if (stack.getItem() instanceof ItemSpellBase && stack.hasTagCompound() && this.usingItem){
			for (ISpellComponent component : SpellUtils.instance.getComponentsForStage(stack, 0)){
				if (component instanceof Telekinesis){
					return true;
				}
			}
		}
		return false;
	}

	private void renderTick_Start(){
		if (!Minecraft.getMinecraft().inGameHasFocus)
			AMGuiHelper.instance.guiTick();
	}

	private void renderTick_End(){
	}

	public void renderOverlays(){
		GuiScreen guiScreen = Minecraft.getMinecraft().currentScreen;

		if (Minecraft.getMinecraft().thePlayer != null && Minecraft.getMinecraft().theWorld != null && (Minecraft.getMinecraft().inGameHasFocus || guiScreen instanceof GuiHudCustomization)){
			this.inGameGui.renderGameOverlay();
			ConfigureAMUICommand.showIfQueued();
		}
	}

	private void localServerTick_End(){
		BossSpawnHelper.instance.tick();
	}

	@SubscribeEvent
	public void onClientTick(TickEvent.ClientTickEvent event){
		if (AMCore.proxy instanceof ClientProxy) ((ClientProxy)AMCore.proxy).cullTask.requestCull = true;
		if (event.phase == TickEvent.Phase.START){
			GuiScreen guiscreen = Minecraft.getMinecraft().currentScreen;
			if (guiscreen != null){
			}else{
				gameTick_Start();
			}
		}else if (event.phase == TickEvent.Phase.END){
			GuiScreen guiscreen = Minecraft.getMinecraft().currentScreen;
			if (guiscreen != null && guiscreen.doesGuiPauseGame()){
			}else{
				gameTick_End();
			}

			if (Minecraft.getMinecraft().theWorld != null)
				spawnPowerPathVisuals();
		}

		if (Minecraft.getMinecraft().thePlayer != null){
			if (AMPacketProcessorClient.cloaking > 0) AMPacketProcessorClient.cloaking--;
			if (AMPacketProcessorClient.cloaking < 0) AMPacketProcessorClient.cloaking = 0;
			if (AMPacketProcessorClient.deaf > 0) AMPacketProcessorClient.deaf--;
			if (AMPacketProcessorClient.deaf < 0) AMPacketProcessorClient.deaf = 0;
			if (Minecraft.getMinecraft().thePlayer.inventory.armorInventory[3] != null && Minecraft.getMinecraft().thePlayer.inventory.armorInventory[3].getItem() == ItemsCommonProxy.archmageHood){
				if (!Minecraft.getMinecraft().thePlayer.isPotionActive(Potion.nightVision)){
					Minecraft.getMinecraft().thePlayer.addPotionEffect(new PotionEffect(Potion.nightVision.id, Integer.MAX_VALUE));
					Minecraft.getMinecraft().thePlayer.getActivePotionEffect(Potion.nightVision).setPotionDurationMax(true);
					appliedEffect = true;
				}else if (Minecraft.getMinecraft().thePlayer.isPotionActive(Potion.nightVision) && Minecraft.getMinecraft().thePlayer.getActivePotionEffect(Potion.nightVision).getDuration() < (Integer.MAX_VALUE / 10)){
					Minecraft.getMinecraft().thePlayer.addPotionEffect(new PotionEffect(Potion.nightVision.id, Integer.MAX_VALUE));
					Minecraft.getMinecraft().thePlayer.getActivePotionEffect(Potion.nightVision).setPotionDurationMax(true);
					appliedEffect = true;
				}
			}else if (appliedEffect){
				Minecraft.getMinecraft().thePlayer.removePotionEffectClient(Potion.nightVision.id);
				appliedEffect = false;
			}
			if (Minecraft.getMinecraft().thePlayer.getCurrentArmor(0) != null){
				if (Minecraft.getMinecraft().thePlayer.getCurrentArmor(0).getItem() == ItemsCommonProxy.archmageBoots){
					if (Minecraft.getMinecraft().thePlayer.worldObj.isAirBlock((int)Minecraft.getMinecraft().thePlayer.posX, (int)Minecraft.getMinecraft().thePlayer.posY - 2, (int)Minecraft.getMinecraft().thePlayer.posZ) && !Minecraft.getMinecraft().thePlayer.noClip){
						for (int i = 0; i < AMCore.config.getGFXLevel(); ++i){
							AMParticle cloud = (AMParticle)AMCore.proxy.particleManager.spawn(Minecraft.getMinecraft().thePlayer.worldObj, "sparkle2", Minecraft.getMinecraft().thePlayer.posX, Minecraft.getMinecraft().thePlayer.posY - 1.7, Minecraft.getMinecraft().thePlayer.posZ);
							if (cloud != null){
								cloud.addRandomOffset(0.5, 0.65, 0.75);
								cloud.AddParticleController(new ParticleFadeOut(cloud, 1, false).setFadeSpeed(0.01f));
							}
						}
					}
				}
			}

			if (Minecraft.getMinecraft().theWorld != null) {
				//update lingering spells
				if (lingeringSpellList.size() > 0){
					SpellHelper.LingeringSpell[] toRemove = new SpellHelper.LingeringSpell[lingeringSpellList.size()];
					for (int i = 0; i < lingeringSpellList.size(); i++){
						boolean toRemoveThis = lingeringSpellList.get(i).doUpdate();
						if (toRemoveThis) toRemove[i] = lingeringSpellList.get(i);
						else toRemove[i] = null;
					}

					for (int j = 0; j < toRemove.length; j++){
						if (toRemove[j] != null){
							lingeringSpellList.remove(toRemove[j]);
						}
					}
				}
			}
		}
	}

	private boolean appliedEffect = false;

	@SubscribeEvent
	public void onRenderTick(TickEvent.RenderTickEvent event){
		if (event.phase == TickEvent.Phase.START){
			renderTick_Start();
		}else if (event.phase == TickEvent.Phase.END){
			renderTick_End();
		}
	}

	@SubscribeEvent
	public void onWorldTick(TickEvent.WorldTickEvent event){
		if (Minecraft.getMinecraft().isIntegratedServerRunning()){
			if (AMCore.config.retroactiveWorldgen())
				RetroactiveWorldgenerator.instance.continueRetrogen(event.world);
		}
		if (event.phase == TickEvent.Phase.END){
			applyDeferredDimensionTransfers();
		}
		if (AMCore.proxy instanceof ClientProxy) ((ClientProxy)AMCore.proxy).cullTask.requestCull = true;
	}

	@SubscribeEvent
	public void onServerTick(TickEvent.ServerTickEvent event){
		if (event.phase == TickEvent.Phase.END){
			localServerTick_End();
		}
	}

	public void setDWheel(int dWheel, int slot, boolean usingItem){
		this.mouseWheelValue = dWheel;
		this.currentSlot = slot;
		this.usingItem = usingItem;
	}

	public AMVector3 getTrackLocation(){
		return this.powerWatch;
	}

	public PowerNodeEntry getTrackData(){
		return this.powerData;
	}

	public void setTrackLocation(AMVector3 location){
		if (location.equals(AMVector3.zero())){
			this.hasSynced = false;
			this.powerWatch = location;
			return;
		}
		if (!this.powerWatch.equals(location)){
			this.powerWatch = location;
			this.powerWatchSyncTick = 0;
			this.hasSynced = false;
		}
	}

	public void setTrackData(NBTTagCompound compound){
		this.powerData = new PowerNodeEntry();
		this.powerData.readFromNBT(compound);
		this.hasSynced = true;
	}

	public boolean getHasSynced(){
		return this.hasSynced;
	}

	public void addDeferredTarget(EntityLiving ent, EntityLivingBase target){
		targetsToSet.put(ent, target);
	}
}
