package am2.proxy.tick;

import am2.AMCore;
import am2.AMEventHandler;
import am2.EntityItemWatcher;
import am2.MeteorSpawnHelper;
import am2.bosses.AM2Boss;
import am2.bosses.BossSpawnHelper;
import am2.items.ItemsCommonProxy;
import am2.network.AMDataWriter;
import am2.network.AMNetHandler;
import am2.network.AMPacketIDs;
import am2.spell.SpellHelper;
import am2.utility.DimensionUtilities;
import am2.worldgen.RetroactiveWorldgenerator;
import am2.worldgen.dynamic.DynamicBossWorldHelper;
import am2.worldgen.dynamic.DynamicBossWorldProvider;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.tclproject.mysteriumlib.asm.fixes.MysteriumPatchesFixesMagicka;

import java.util.*;
import java.util.Map.Entry;

import static am2.AMEventHandler.tempCurseMap;
import static am2.spell.SpellHelper.lingeringSpellList;

public class ServerTickHandler{

	private boolean firstTick = true;
	public static HashMap<EntityLiving, EntityLivingBase> targetsToSet = new HashMap<EntityLiving, EntityLivingBase>();
	private static World worldToDelete = null;

	public static String lastWorldName;

	private void gameTick_Start(){

		if (MinecraftServer.getServer().getFolderName() != lastWorldName){
			lastWorldName = MinecraftServer.getServer().getFolderName();
			firstTick = true;
		}

		if (firstTick){
			ItemsCommonProxy.crystalPhylactery.getSpawnableEntities(MinecraftServer.getServer().worldServers[0]);
			firstTick = false;
		}

		AMCore.proxy.itemFrameWatcher.checkWatchedFrames();
	}

	private void gameTick_End(){
		BossSpawnHelper.instance.tick();
		MeteorSpawnHelper.instance.tick();
		EntityItemWatcher.instance.tick();
	}

	@SubscribeEvent
	public void onServerTick(TickEvent.ServerTickEvent event){
		System.out.println("did something!!!");
		if (event.phase == TickEvent.Phase.START){
			gameTick_Start();
		}else if (event.phase == TickEvent.Phase.END){
			gameTick_End();
		}
	}

	@SubscribeEvent
	public void onWorldTick(TickEvent.WorldTickEvent event) {
		if (AMCore.config.retroactiveWorldgen())
			RetroactiveWorldgenerator.instance.continueRetrogen(event.world);

		System.out.println(event.world.provider.dimensionId + "!!");
		applyDeferredPotionEffects();
		if (event.phase == TickEvent.Phase.END) {
			applyDeferredDimensionTransfers();

			System.out.println(event.world.provider.dimensionId);
			// detect unorthodox bossfight finish, player falling off
			if (worldToDelete != null) DynamicBossWorldHelper.unregisterDimension(worldToDelete.provider.dimensionId, worldToDelete);
			else if (event.world.provider instanceof DynamicBossWorldProvider) {
				if (event.world.playerEntities.isEmpty()) worldToDelete = event.world;
				else {
					EntityPlayerMP entityPlayer = (EntityPlayerMP) event.world.playerEntities.get(0); // there's theoretically one
					if (AMEventHandler.tick % 100 == 0) { // computationally expensive, nothing bad will happen if we do it once every 5sec
						// no boss or too far
						List boss = event.world.getEntitiesWithinAABB(AM2Boss.class, entityPlayer.boundingBox.expand(40, 40, 40));
						if (boss.isEmpty()) {
							DynamicBossWorldHelper.returnPlayerToOriginalPosition(entityPlayer);
							worldToDelete = event.world;
//							DynamicBossWorldHelper.unregisterDimension here could cause issues (?)
						} else if (entityPlayer.fallDistance > 10 && entityPlayer.posY < 30) { // player is falling off
							int[] playerSpawn = BossSpawnHelper.playerBossfightCoordinates[BossSpawnHelper.getIntFromBoss((AM2Boss) boss.get(0))];
							entityPlayer.fallDistance = 0;
							entityPlayer.setPositionAndUpdate(playerSpawn[0] + 0.5D, playerSpawn[1] + 2D, playerSpawn[2] + 0.5D);
							event.world.playSoundEffect(playerSpawn[0] + 0.5D, playerSpawn[1], playerSpawn[2] + 0.5D, "mob.endermen.portal", 1.0F, 1.0F);
						}
					}
				}
			}
		}

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

		// handle temporary curses
		ArrayList<EntityCreature> toRemove = new ArrayList<EntityCreature>();
		HashMap<EntityCreature, Integer> toChange = new HashMap<EntityCreature, Integer>();
		for (Map.Entry<EntityCreature, Integer> entry : tempCurseMap.entrySet()) {
			EntityCreature key = entry.getKey();
			Integer value = entry.getValue();
			if (value <= 3) toRemove.add(key);
			else toChange.put(key, value-1);
		}
		for (Map.Entry<EntityCreature, Integer> entry : toChange.entrySet()) {
			tempCurseMap.put(entry.getKey(), entry.getValue()); // overwrite with new value
		}
		for (EntityCreature ec : toRemove) {
			tempCurseMap.remove(ec);
			ec.setDead();
		}

		MinecraftServer server = MinecraftServer.getServer();
		if((server != null) && (server.getConfigurationManager() != null)) {
			if (MysteriumPatchesFixesMagicka.countdownToChangeBack >= 3) {
				MysteriumPatchesFixesMagicka.countdownToChangeBack--;
			} else if (MysteriumPatchesFixesMagicka.countdownToChangeBack != -1) {
				MysteriumPatchesFixesMagicka.countdownToChangeBack = -1;
				MysteriumPatchesFixesMagicka.changeTickrate(20);
			}
		}
	}

	private void applyDeferredPotionEffects(){
		for (EntityLivingBase ent : AMCore.proxy.getDeferredPotionEffects().keySet()){
			ArrayList<PotionEffect> potions = AMCore.proxy.getDeferredPotionEffects().get(ent);
			for (PotionEffect effect : potions)
				ent.addPotionEffect(effect);
		}

		AMCore.proxy.clearDeferredPotionEffects();
	}

	private void applyDeferredDimensionTransfers(){
		for (EntityLivingBase ent : AMCore.proxy.getDeferredDimensionTransfers().keySet()){
			DimensionUtilities.doDimensionTransfer(ent, AMCore.proxy.getDeferredDimensionTransfers().get(ent));
		}

		AMCore.proxy.clearDeferredDimensionTransfers();
	}

	private void applyDeferredTargetSets(){
		Iterator<Entry<EntityLiving, EntityLivingBase>> it = targetsToSet.entrySet().iterator();
		while (it.hasNext()){
			Entry<EntityLiving, EntityLivingBase> entry = it.next();
			if (entry.getKey() != null && !entry.getKey().isDead)
				entry.getKey().setAttackTarget(entry.getValue());
			it.remove();
		}
	}

	public void addDeferredTarget(EntityLiving ent, EntityLivingBase target){
		targetsToSet.put(ent, target);
	}

	public void blackoutArmorPiece(EntityPlayerMP player, int slot, int cooldown){
		AMNetHandler.INSTANCE.sendPacketToClientPlayer(player, AMPacketIDs.FLASH_ARMOR_PIECE, new AMDataWriter().add(slot).add(cooldown).generate());
	}

}
