package am2.customdata;

import com.google.common.collect.MapMaker;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

public class CustomChunkData {

    private static String identifier = "ArsMagicaChunkData";
    // should be safe for automatically cleaning up the data when chunks unload
    private static ConcurrentMap<Chunk, NBTTagCompound> chunkDataMap = new MapMaker().weakKeys().concurrencyLevel(3).makeMap();

    // write data associated with the chunk into the nbt
    public static void saveCustomDataToNBT(Chunk chunk, NBTTagCompound nbt) {
        nbt.setTag(identifier, chunkDataMap.getOrDefault(chunk, new NBTTagCompound()));
    }

    // reads the custom data stored in the nbt and saves it to be associated with the given chunk
    public static void readCustomDataFromNBTForChunk(Chunk returnedChunk, NBTTagCompound nbt) {
        if (nbt != null) {
            if (nbt.hasKey(identifier) && !(nbt.getCompoundTag(identifier).hasNoTags())) chunkDataMap.put(returnedChunk, nbt.getCompoundTag(identifier)); // cannot be null
        }
    }

    // String must be one character in length, as well as english/number/punctuation (no symbols) to be accurate
    public static byte getStringAsNumber(String string) {
        if (string == null) return 0;
        return string.substring(0, 1).getBytes(StandardCharsets.UTF_8)[0];
    }

    public static String getNumberAsString(byte[] number) {
        return new String(number, StandardCharsets.UTF_8);
    }

    public static String getNumberAsString(byte number) {
        return getNumberAsString(new byte[]{number});
    }

    public static void addToChunkVars(Chunk chunk, String name, String value) {
        NBTTagCompound newChunkNBT = chunkDataMap.getOrDefault(chunk, new NBTTagCompound()); // cannot be null
        newChunkNBT.setString(name, value);
        chunkDataMap.put(chunk, newChunkNBT);
    }

    public static void removeChunkVar(Chunk chunk, String name) {
        NBTTagCompound newChunkNBT = chunkDataMap.getOrDefault(chunk, new NBTTagCompound());
        newChunkNBT.removeTag(name); // should be safe to call even in the event that there is no such mapping
        chunkDataMap.put(chunk, newChunkNBT);
    }

    public static void clearChunkData(Chunk chunk) {
        chunkDataMap.put(chunk, new NBTTagCompound());
    }

    // returns empty string ("") if there is no such variable stored
    public static String getChunkVar(Chunk chunk, String name) {
        return chunkDataMap.getOrDefault(chunk, new NBTTagCompound()).getString(name);
    }

    public static boolean chunkVarExists(Chunk chunk, String name) {
        return chunkDataMap.getOrDefault(chunk, new NBTTagCompound()).hasKey(name);
    }
}
