package com.tfc.minecraft_effekseer_implementation.packet;

import javax.vecmath.Vector3d;

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

//I will implement IPacket, and you cannot stop me.
public class EffekPacketClient implements IMessage {
	
	private NBTTagCompound data;
	
	public EffekPacketClient() {}
	
	public EffekPacketClient(String effekName, float progress, Vector3d position, String emmiterName) {
		data = new NBTTagCompound();
		data.setString("effekName", effekName);
		data.setFloat("progress", progress);
		data.setDouble("xcoord", position.x);
		data.setDouble("ycoord", position.y);
		data.setDouble("zcoord", position.z);
		data.setString("emmiterName", emmiterName);
	}
	
	@Override
 	public void fromBytes(ByteBuf buffer) {
	 	data = ByteBufUtils.readTag(buffer);
 	}

	 @Override
	 public void toBytes(ByteBuf buffer) {
		 ByteBufUtils.writeTag(buffer, data);
	 }

	 public static class Handler implements IMessageHandler<EffekPacketClient, IMessage> {

		 @Override
	     public IMessage onMessage(EffekPacketClient packet, MessageContext ctx) {
			 Effek effek = Effeks.get(new ResourceLocation(packet.data.getString("effekName")).toString());
			 if (effek != null) {
				EffekEmitter emitter = effek.getOrCreate(new ResourceLocation(packet.data.getString("emmiterName")).toString());
				emitter.setVisible(true);
				emitter.setPaused(false);
				emitter.setPlayProgress(packet.data.getFloat("progress"));
				emitter.setPosition(packet.data.getDouble("xcoord"), packet.data.getDouble("ycoord"), packet.data.getDouble("zcoord"));
			 }
			 return null;
		 }
	 }
}
