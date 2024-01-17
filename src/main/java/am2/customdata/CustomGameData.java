package am2.customdata;

import am2.network.AMDataReader;
import am2.network.AMDataWriter;
import am2.network.AMNetHandler;
import am2.network.AMPacketIDs;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.SaveHandler;
import net.minecraftforge.common.DimensionManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

// per-game (per-game-client) data. Syncing individual variables still sucks.
// these variables *are not* synced (though they can be requested); they are, of course, saved. They exist per-game-client, or per-game-server.
public class CustomGameData {

    private static final Logger LOGGER = LogManager.getLogger("CustomGameData");
    private static HashMap<String, String> gameDataArray = new HashMap<String, String>();

    // do not call from outside this class unless you know what you're doing
    public static final HashMap<String, String> getGameData(){
        // only here for the possibility inserting custom logic into this method later
        return gameDataArray;
    }

    public static void addToGameVars(String name, String value) {
        gameDataArray.put(name, value);
        saveGameData();
    }

    public static void addToGameVarsNoSave(String name, String value) {
        gameDataArray.put(name, value);
    }

    public static void removeGameVar(String name) {
        gameDataArray.remove(name);
        saveGameData();
    }

    public static void clearGameVars() {
        gameDataArray.clear();
        saveGameData();
    }

    public static Map<String, String> getGameVarContains(String contains) {
        Map<String, String> toReturn = new HashMap<>();
        for (String key : gameDataArray.keySet()) {
            if (key.contains(contains)) toReturn.put(key, gameDataArray.get(key));
        }
        return toReturn;
    }

    public static String getGameVar(String name) {
        return gameDataArray.get(name);
    }

    public static boolean gameVarExists(String name) {
        return gameDataArray.get(name) != null;
    }

    public static void saveGameData() {
        try {
            File saveFile = new File(new File("."), "AM2GameData.txt");
            saveFile.createNewFile();
            ArrayList<String> lines = new ArrayList<String>();
            for (Map.Entry<String, String> gameEntry : gameDataArray.entrySet()) {
                    lines.add(gameEntry.getKey() + ":::" + gameEntry.getValue());
            }
            PrintWriter pw = new PrintWriter(saveFile);
            for (String str : lines) pw.println(str);
            pw.close();
        } catch (Exception e) {
            LOGGER.error("Failed saving game data! This could potentially be disastrous! Report this to the mod developer.");
            e.printStackTrace();
        }
    }

    public static void loadGameData() {
        try {
            File saveFile = new File(new File("."), "AM2GameData.txt");
            if (saveFile.exists()) {
                ArrayList<String> lines = new ArrayList<String>();
                try {
                    BufferedReader br = new BufferedReader(new FileReader(saveFile));
                    String line = br.readLine();
                    while (line != null) {
                        lines.add(line);
                        line = br.readLine();
                    }
                    br.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                clearGameVars();
                for (String entry : lines) {
                    addToGameVarsNoSave(entry.split(":::")[0], entry.split(":::")[1]);
                }
            }
        } catch (Exception e) {
            LOGGER.error("Failed loading game data! This could potentially be disastrous! Report this to the mod developer.");
            e.printStackTrace();
        }
    }

    /**Must call from client side only, *when on multiplayer server* (calling this in singleplayer is useless). There may be a delay before the data is retrievable. */
    @SideOnly(Side.CLIENT)
    public static void requestGameVarsFromServer() {
        AMDataWriter writer = new AMDataWriter();
        AMNetHandler.INSTANCE.sendPacketToServer(AMPacketIDs.REQUESTGAMEVARSFROMSERVER, writer.generate());
    }

    /**Must call from server side only.  There may be a delay before the data is retrievable. */
    @SideOnly(Side.SERVER)
    public static void requestGameVarsFromClient(EntityPlayerMP client) {
        if (client == null) {
            LOGGER.warn("Attempted to request variables for a null player!");
            return;
        }
        AMDataWriter writer = new AMDataWriter();
        AMNetHandler.INSTANCE.sendPacketToClientPlayer(client, AMPacketIDs.REQUESTGAMEVARSFROMCLIENT, writer.generate());
    }

    private static HashMap<String, String> requestedServerVars = null;
    private static HashMap<EntityPlayer, HashMap<String,String>> requestedClientVars = new HashMap<EntityPlayer, HashMap<String,String>>();

    /** Will return null if the updated vars have not arrived yet. This is to ensure no outdated variables will be retrieved. */
    public static HashMap<String, String> getRequestedServerVars() {
        if (requestedServerVars == null) return null;
        else {
            HashMap<String, String> cache = new HashMap<>();
            cache.putAll(requestedServerVars);
            requestedServerVars = null;
            return cache;
        }
    }

    /** Will return null if the updated vars have not arrived yet. This is to ensure no outdated variables will be retrieved. */
    public static HashMap<String, String> getRequestedClientVars(EntityPlayer client) {
        if (client == null) {
            LOGGER.warn("Attempted to get requested variables for a null player!");
            return null;
        }
        if (requestedClientVars.get(client) == null) return null;
        else {
            HashMap<String, String> cache = new HashMap<>();
            cache.putAll(requestedClientVars.get(client));
            requestedClientVars.remove(client);
            return cache;
        }
    }

    public static void handleServerResponded(byte[] remaining) {
        AMDataReader rdr = new AMDataReader(remaining, false);
        NBTTagCompound data = rdr.getNBTTagCompound();
        if (CustomGameData.requestedServerVars == null) CustomGameData.requestedServerVars = new HashMap<String, String>();
        else CustomGameData.requestedServerVars.clear();
        for (int j = 0; j < data.getInteger("sizeofdata"); j++) {
            CustomGameData.requestedServerVars.put(data.getString("entryname" + j), data.getString("entry" + j));
        }
    }

    public static void handleClientResponded(EntityPlayer clientThatResponded, byte[] remaining) {
        AMDataReader rdr = new AMDataReader(remaining, false);
        NBTTagCompound data = rdr.getNBTTagCompound();
        HashMap<String, String> dataRead = new HashMap<String, String>();
        for (int j = 0; j < data.getInteger("sizeofdata"); j++) {
            dataRead.put(data.getString("entryname" + j), data.getString("entry" + j));
        }
        CustomGameData.requestedClientVars.put(clientThatResponded, dataRead);
    }
}
