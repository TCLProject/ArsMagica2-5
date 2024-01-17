package com.tfc.minecraft_effekseer_implementation.packet;

import com.tfc.minecraft_effekseer_implementation.common.Effek;
import com.tfc.minecraft_effekseer_implementation.common.Effeks;
import com.tfc.minecraft_effekseer_implementation.common.api.EffekEmitter;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetHandler;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

public class EndEmitterPacketClient implements IMessage {
	
	private NBTTagCompound data;
	
	public EndEmitterPacketClient() {}
	
	public EndEmitterPacketClient(String effekName, String emitterName, boolean deleteEmitter) {
		data.setString("effekName", effekName);
		data.setString("emitterName", emitterName);
		data.setBoolean("deleteEmitter", deleteEmitter);
	}
	
	@Override
 	public void fromBytes(ByteBuf buffer) {
	 	data = ByteBufUtils.readTag(buffer);
 	}

	 @Override
	 public void toBytes(ByteBuf buffer) {
		 ByteBufUtils.writeTag(buffer, data);
	 }
	
	 public static class Handler implements IMessageHandler<EndEmitterPacketClient, IMessage> {

		 @Override
	     public IMessage onMessage(EndEmitterPacketClient packet, MessageContext ctx) {
			 Effek effek = Effeks.get(new ResourceLocation(packet.data.getString("effekName")).toString());
			 if (effek != null) effek.delete(effek.getOrCreate(new ResourceLocation(packet.data.getString("emitterName")).toString()));
			 return null;
		 }
	 }
}
