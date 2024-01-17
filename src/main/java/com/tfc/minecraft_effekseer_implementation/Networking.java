package com.tfc.minecraft_effekseer_implementation;

import java.util.function.Predicate;

import javax.vecmath.Vector3d;

import com.tfc.minecraft_effekseer_implementation.packet.EffekBlockBoundPacketClient;
import com.tfc.minecraft_effekseer_implementation.packet.EffekEntityBoundPacketClient;
import com.tfc.minecraft_effekseer_implementation.packet.EffekPacketClient;
import com.tfc.minecraft_effekseer_implementation.packet.EndEmitterPacketClient;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class Networking {

	public static final SimpleNetworkWrapper dispatcher = NetworkRegistry.INSTANCE.newSimpleChannel("AM_mc_effekseer_impl");
	
	public static final void registerPackets() {
       	// Registration
		dispatcher.registerMessage(EffekPacketClient.Handler.class, EffekPacketClient.class, 0, Side.CLIENT);
		dispatcher.registerMessage(EndEmitterPacketClient.Handler.class, EndEmitterPacketClient.class, 1, Side.CLIENT);
		dispatcher.registerMessage(EffekBlockBoundPacketClient.Handler.class, EffekBlockBoundPacketClient.class, 2, Side.CLIENT);
		dispatcher.registerMessage(EffekEntityBoundPacketClient.Handler.class, EffekEntityBoundPacketClient.class, 3, Side.CLIENT);
    }
	
	public static void sendStartEffekPacket(
			Predicate<EntityPlayer> selector, World world,
			String effekName, String emitterName, float progress, Vector3d position
	) {
		EffekPacketClient packet = new EffekPacketClient(effekName, progress, position, emitterName);
		for (Object playerObj : world.playerEntities) {
			EntityPlayer player = (EntityPlayer)playerObj;
			if (selector.test(player) && player instanceof EntityPlayerMP) {
				dispatcher.sendTo(
						packet,
						(EntityPlayerMP)player
				);
			}
		}
	}
	
	public static void sendStartEffekPacket(EntityPlayerMP target, String effekName, String emitterName, float progress, Vector3d position) {
		EffekPacketClient packet = new EffekPacketClient(effekName, progress, position, emitterName);
		dispatcher.sendTo(packet, target);
	}
	
	public static void sendStartEffekPacketToDimension(String effekName, String emitterName, float progress, Vector3d position, int id) {
		EffekPacketClient packet = new EffekPacketClient(effekName, progress, position, emitterName);
		dispatcher.sendToDimension(packet, id);
	}
	
	public static void sendEndEffekPacket(EntityPlayerMP target, String effekName, String emitterName, boolean deleteEmitter) {
		EndEmitterPacketClient packet = new EndEmitterPacketClient(effekName, emitterName, deleteEmitter);
		dispatcher.sendTo(packet, target);
	}

	public static void sendEndEffekPacketToDimension(String effekName, String emitterName, boolean deleteEmitter, int id) {
		EndEmitterPacketClient packet = new EndEmitterPacketClient(effekName, emitterName, deleteEmitter);
		dispatcher.sendToDimension(packet, id);
	}
}
