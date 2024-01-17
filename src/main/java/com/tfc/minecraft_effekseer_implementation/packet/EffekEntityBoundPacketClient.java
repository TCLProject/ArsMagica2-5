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
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

import javax.vecmath.Vector3d;

//I will implement IPacket, and you cannot stop me.
public class EffekEntityBoundPacketClient implements IMessage {

	private NBTTagCompound data;

	public EffekEntityBoundPacketClient() {}

	public EffekEntityBoundPacketClient(boolean delete, boolean deleteAll, String effekName, Entity entity, String emmiterName, double xExpand, double yExpand, double zExpand, int rotatable) {
		data = new NBTTagCompound();
		data.setBoolean("delete", delete);
		data.setBoolean("deleteAll", deleteAll);
		data.setString("effekName", effekName);
		data.setString("emmiterName", emmiterName);
		data.setDouble("xExpand", xExpand);
		data.setDouble("yExpand", yExpand);
		data.setDouble("zExpand", zExpand);
		data.setString("EntityUUID", entity.getUniqueID().toString());
		data.setInteger("rotatable", rotatable);
	}
	
	@Override
 	public void fromBytes(ByteBuf buffer) {
	 	data = ByteBufUtils.readTag(buffer);
 	}

	 @Override
	 public void toBytes(ByteBuf buffer) {
		 ByteBufUtils.writeTag(buffer, data);
	 }

	 public static class Handler implements IMessageHandler<EffekEntityBoundPacketClient, IMessage> {

		 @Override
	     public IMessage onMessage(EffekEntityBoundPacketClient packet, MessageContext ctx) {
		 	if (packet.data.getBoolean("delete")) {
				if (packet.data.getBoolean("deleteAll")) MEI.removeAllEntityBoundEffects(packet.data.getString("EntityUUID"));
				else MEI.removeEntityBoundEffect(packet.data.getString("effekName"), packet.data.getString("emmiterName"),
						packet.data.getString("EntityUUID"),
						packet.data.getDouble("xExpand"), packet.data.getDouble("yExpand"), packet.data.getDouble("zExpand"),
						packet.data.getInteger("rotatable"));
			} else {
				MEI.addEntityBoundEffect(packet.data.getString("effekName"), packet.data.getString("emmiterName"),
						packet.data.getString("EntityUUID"),
						packet.data.getDouble("xExpand"), packet.data.getDouble("yExpand"), packet.data.getDouble("zExpand"),
						packet.data.getInteger("rotatable"));
			}
		 	return null;
		 }
	 }
}
