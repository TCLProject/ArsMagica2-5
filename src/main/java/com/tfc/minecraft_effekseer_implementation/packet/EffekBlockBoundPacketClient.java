package com.tfc.minecraft_effekseer_implementation.packet;

import com.tfc.minecraft_effekseer_implementation.MEI;
import com.tfc.minecraft_effekseer_implementation.common.Effek;
import com.tfc.minecraft_effekseer_implementation.common.Effeks;
import com.tfc.minecraft_effekseer_implementation.common.api.EffekEmitter;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

import javax.vecmath.Vector3d;

//I will implement IPacket, and you cannot stop me.
public class EffekBlockBoundPacketClient implements IMessage {

	private NBTTagCompound data;

	public EffekBlockBoundPacketClient() {}

	public EffekBlockBoundPacketClient(boolean delete, String effekName, Vector3d position, String emmiterName, int dim, int bbminusx, int bbminusy, int bbminusz, int bbplusx, int bbplusy, int bbplusz) {
		data = new NBTTagCompound();
		data.setBoolean("delete", delete);
		data.setString("effekName", effekName);
		data.setDouble("xcoord", position.x);
		data.setDouble("ycoord", position.y);
		data.setDouble("zcoord", position.z);
		data.setInteger("dim", dim);
		data.setDouble("xcoordMIN", bbminusx);
		data.setDouble("ycoordMIN", bbminusy);
		data.setDouble("zcoordMIN", bbminusz);
		data.setDouble("xcoordMAX", bbplusx);
		data.setDouble("ycoordMAX", bbplusy);
		data.setDouble("zcoordMAX", bbplusz);
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

	 public static class Handler implements IMessageHandler<EffekBlockBoundPacketClient, IMessage> {

		 @Override
	     public IMessage onMessage(EffekBlockBoundPacketClient packet, MessageContext ctx) {
			 if (packet.data.getBoolean("delete")) {
			 	MEI.removeBlockBoundEffect(packet.data.getString("effekName"), packet.data.getString("emmiterName"), packet.data.getInteger("dim"));
			 } else {
				 MEI.addBlockBoundEffect(packet.data.getString("effekName"), packet.data.getString("emmiterName"),
						 packet.data.getDouble("xcoord"), packet.data.getDouble("ycoord"), packet.data.getDouble("zcoord"),
						 packet.data.getInteger("dim"),
						 packet.data.getDouble("xcoordMIN"), packet.data.getDouble("ycoordMIN"), packet.data.getDouble("zcoordMIN"),
						 packet.data.getDouble("xcoordMAX"), packet.data.getDouble("zcoordMAX"), packet.data.getDouble("zcoordMAX"));
			 }
			 return null;
		 }
	 }
}
