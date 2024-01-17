package net.tclproject.mysteriumlib.asm.fixes;

import am2.customdata.CustomChunkData;
import am2.network.AMDataWriter;
import am2.network.AMNetHandler;
import am2.network.AMPacketIDs;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S21PacketChunkData;
import net.minecraft.network.play.server.S26PacketMapChunkBulk;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.AnvilChunkLoader;
import net.minecraft.world.gen.ChunkProviderServer;
import net.tclproject.mysteriumlib.asm.annotations.EnumReturnSetting;
import net.tclproject.mysteriumlib.asm.annotations.MFix;
import net.tclproject.mysteriumlib.asm.annotations.ReturnedValue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

// forge events for this are broken beyond belief, and the documentation is less than 0 (it actively misleads you)
// the following edits are non-intrusive, meaning they will work alongside any other mod trying to do the same thing, as long as that mod doesn't overwrite chunk loading and saving completely
public class ChunkDataFixes {

    private static final Logger LOGGER = LogManager.getLogger("ChunkDataAdditions");

    // CHUNKLOADER CODE

    // saving (for any number of reasons, including unloading) a chunk
    @MFix
    public static void writeChunkToNBT(AnvilChunkLoader acl, Chunk chunk, World world, NBTTagCompound nbt) {
        CustomChunkData.saveCustomDataToNBT(chunk, nbt);
    }

    // loading a chunk
    @MFix(insertOnExit = true)
    public static void readChunkFromNBT(AnvilChunkLoader acl, World world, NBTTagCompound nbt, @ReturnedValue Chunk returnedChunk) {
        CustomChunkData.readCustomDataFromNBTForChunk(returnedChunk, nbt); // this should (I hope!) work without overwriting the returned value itself if I modify the returned Chunk instance
    }

    // thread safe version, guards against concurrent mod exception
    // if anyone has a suggestion how to fix this issue without this hack, I'd welcome it
    @MFix(returnSetting = EnumReturnSetting.ALWAYS)
    public static void unloadAllChunks(ChunkProviderServer cps)
    {
        for (int i = 0; i < cps.loadedChunks.size(); i++) {
            if (i >= cps.loadedChunks.size()) break;
            Chunk chunk = (Chunk)cps.loadedChunks.get(i);
            cps.unloadChunksIfNotNearSpawn(chunk.xPosition, chunk.zPosition);
        }
    }

    // CHUNKLOADER CODE: ChunkAPI detected variant (Experimental! May not work! ChunkAPI overwrites the above methods!)

//    @Optional.Method(modid="chunkapi")
    public static void readCustomData(Chunk chunk, NBTTagCompound nbt) {
        CustomChunkData.readCustomDataFromNBTForChunk(chunk, nbt);
    }

//    @Optional.Method(modid="chunkapi")
    public static void writeCustomData(Chunk chunk, NBTTagCompound nbt) {
        CustomChunkData.saveCustomDataToNBT(chunk, nbt);
    }

    // PACKET CODE

    @MFix(targetMethod = "<init>")
    public static void packetChunkDataConstructor(S21PacketChunkData packet, Chunk chunk, boolean force, int p_i45196_3_)
    {
        packetToChunkMap.put(packet, chunk);
    }

    @MFix(targetMethod = "<init>")
    public static void packetMapChunkBulkConstructor(S26PacketMapChunkBulk packet, List listOfChunks)
    {
        packetToChunkListMap.put(packet, listOfChunks);
    }

    private static HashMap<S21PacketChunkData, Chunk> packetToChunkMap = new HashMap<S21PacketChunkData, Chunk>();
    private static HashMap<S26PacketMapChunkBulk, List> packetToChunkListMap = new HashMap<S26PacketMapChunkBulk, List>();

    @MFix
    public static void sendPacket(NetHandlerPlayServer nhps, final Packet p_147359_1_)
    {
        if (p_147359_1_ instanceof S21PacketChunkData) {
            S21PacketChunkData packetChunkData = (S21PacketChunkData)p_147359_1_;
            AMDataWriter writer = new AMDataWriter();
            Chunk chunk = packetToChunkMap.get(packetChunkData);
            if (chunk != null) {
                writer.add(chunk.worldObj.provider.dimensionId);
                writer.add(chunk.xPosition); // x position
                writer.add(chunk.zPosition); // z position
                NBTTagCompound data = new NBTTagCompound();
                CustomChunkData.saveCustomDataToNBT(chunk, data);
                writer.add(data);
                AMNetHandler.INSTANCE.sendPacketToClientPlayer(nhps.playerEntity, AMPacketIDs.CHUNK_DATA_SYNC_TO_CLIENT, writer.generate());
                packetToChunkMap.remove(packetChunkData); // cleanup
            } else {
                throw new RuntimeException("Could not get chunk from map! The consequences could be disastrous; please report this to the mod author immediately.");
            }
        } else if (p_147359_1_ instanceof S26PacketMapChunkBulk) {
            S26PacketMapChunkBulk packetMapChunkBulk = (S26PacketMapChunkBulk)p_147359_1_;
            List listOfChunks = packetToChunkListMap.get(packetMapChunkBulk);
            for (int i = 0; i < listOfChunks.size(); i++) {
                AMDataWriter writer = new AMDataWriter();
                Chunk chunk = (Chunk)listOfChunks.get(i);
                writer.add(chunk.worldObj.provider.dimensionId);
                writer.add(chunk.xPosition); // x position
                writer.add(chunk.zPosition); // z position
                NBTTagCompound data = new NBTTagCompound();
                CustomChunkData.saveCustomDataToNBT(chunk, data);
                writer.add(data);
                AMNetHandler.INSTANCE.sendPacketToClientPlayer(nhps.playerEntity, AMPacketIDs.CHUNK_DATA_SYNC_TO_CLIENT, writer.generate());
            }
            packetToChunkListMap.remove(packetMapChunkBulk); // cleanup
        }
    }
}
