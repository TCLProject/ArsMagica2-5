package am2.customdata;

import am2.network.AMDataWriter;
import am2.network.AMNetHandler;
import am2.network.AMPacketIDs;
import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.SaveHandler;
import net.minecraftforge.common.DimensionManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

// per-world (per-all-dimensions) and per-dimension data. Syncing individual variables sucks.
public class CustomWorldData {

    private static final Logger LOGGER = LogManager.getLogger("CustomWorldData");
    private static Map<Integer, HashMap<String, String>> worldDataArray = new HashMap<Integer, HashMap<String, String>>(); // per dimension
    // local dimension data *should* overwrite global world data if need be, but it's best to simply avoid duplicates
    private static HashMap<String, String> universalWorldDataArray = new HashMap<String, String>(); // per world (all dimensions)

    private static final HashMap<String, String> WorldVarsFor(World world){
        if (world == null) {
            HashMap<String, String> reg = new HashMap<String, String>();
            reg.putAll(universalWorldDataArray);
            return reg;
        }

        if (worldDataArray.containsKey(world.provider.dimensionId)){
            HashMap<String, String> reg = new HashMap<String, String>();
            reg.putAll(universalWorldDataArray); // duplicates will not occur because this is a map.
            reg.putAll(worldDataArray.get(world.provider.dimensionId));
            return reg;
        }else{
            HashMap<String, String> reg = new HashMap<String, String>();
            reg.putAll(universalWorldDataArray);
            worldDataArray.put(world.provider.dimensionId, reg);
            return reg;
        }
    }

    public static boolean worldHasVar(World world, String varName) {
        return WorldVarsFor(world).containsKey(varName);
    }

    public static String getWorldVar(World world, String varName) {
        return WorldVarsFor(world).get(varName);
    }

    public static Map<String, String> getWorldVarsStartingWith(World world, String varNameStart) {
        Map<String, String> filteredMap = new HashMap<>();
        for (Map.Entry<String, String> var : WorldVarsFor(world).entrySet()) {
            if(var.getKey().startsWith(varNameStart)){
                filteredMap.put(var.getKey(), var.getValue());
            }
        }
        return filteredMap;
    }

    public static void removeWorldVar(World world, String varName) {
        if (world.isRemote) return; // Only server can set variables, client can request
        HashMap<String, String> data = WorldVarsFor(world);
        data.remove(varName);
        worldDataArray.put(world.provider.dimensionId, data);
        syncWorldVarsToClients(world, null);
    }

    public static void setWorldVar(World world, String varName, String varValue) {
        if (world.isRemote) return; // Only server can set variables, client can request
        HashMap<String, String> data = WorldVarsFor(world);
        data.put(varName, varValue);
        worldDataArray.put(world.provider.dimensionId, data);
        syncWorldVarsToClients(world, null);
    }

    public static void setWorldVarNoSync(World world, String varName, String varValue) { // doesn't sync anywhere. Used for easing network load and for client updating
        HashMap<String, String> data = WorldVarsFor(world);
        data.put(varName, varValue);
        worldDataArray.put(world.provider.dimensionId, data);
    }

    // any world can be passed into anyWorld, it does not matter. All this method needs it for is the isRemote check
    // set a universal variable for all worlds. Can't get it directly, but can retrieve it from any individual world
    public static void setUniversalWorldVar(World anyWorld, String varName, String varValue) {
        if (anyWorld.isRemote) return;
        universalWorldDataArray.put(varName, varValue);
        syncAllWorldVarsToClients(null);
    }

    public static void setUniversalWorldVarNoSync(String varName, String varValue) {
        universalWorldDataArray.put(varName, varValue);
    }

    public static void processRequest(World world, String varName, String varValue, EntityPlayer requester, boolean isUniversal) {
        boolean approved = false;

        if (varName.startsWith("CLIENT_")) approved = true; // more edge cases will be added in the future

        if (approved) {
            if (isUniversal) setUniversalWorldVar(world, varName, varValue);
            else setWorldVar(world, varName, varValue);
        }
    }

    public static void requestWorldVar(World world, String varName, String varValue) {
        requestWorldVar(world, varName, varValue, false);
    }

    public static void requestWorldVar(World world, String varName, String varValue, boolean isUniversal) { // the clientside method
        if (world == null) return;
        requestWorldVar(world.provider.dimensionId, varName, varValue, isUniversal);
    }

    public static void requestWorldVar(int worldID, String varName, String varValue, boolean isUniversal) {
        AMDataWriter writer = new AMDataWriter();
        writer.add(worldID);
        writer.add(varName);
        writer.add(varValue);
        writer.add(isUniversal);
        AMNetHandler.INSTANCE.sendPacketToServer(AMPacketIDs.REQUESTWORLDDATACHANGE, writer.generate());
    }

    private static void syncWorldVarsToClients(World world, EntityPlayer p) { // Yes, this method should be abstracted away. No, I'm not going to do it.
        AMDataWriter writer = new AMDataWriter();
        NBTTagCompound world_data = new NBTTagCompound();
        int c = 0;
        HashMap<String, String> data = WorldVarsFor(world);
        for (Object o : data.keySet()) {
            String iS = (String)o;
            String iValue = data.get(iS);
            world_data.setString("dataentry" + c, iValue);
            world_data.setString("dataentryname" + c, iS);
            c++;
        }
        world_data.setInteger("datasize", data.size());
        world_data.setInteger("dimensionid", world.provider.dimensionId);
        writer.add(world_data);

        // if null is passed, syncs to all clients
        if (p == null) AMNetHandler.INSTANCE.sendPacketToAllClients(AMPacketIDs.SYNCWORLDDATATOCLIENTS, writer.generate());
        else if (p instanceof EntityPlayerMP) AMNetHandler.INSTANCE.sendPacketToClientPlayer((EntityPlayerMP) p, AMPacketIDs.SYNCWORLDDATATOCLIENTS, writer.generate());
    }

    public static void saveAllWorldData() {
        try {
            if (DimensionManager.getWorld(0) != null) {
                if (DimensionManager.getWorld(0).isRemote) return;
                ISaveHandler handler = DimensionManager.getWorld(0).getSaveHandler();
                if (handler != null && handler instanceof SaveHandler) {
                    File saveFile = new File(((SaveHandler) handler).getWorldDirectory(), "AM2WorldData.txt");
                    saveFile.createNewFile();
                    ArrayList<String> lines = new ArrayList<String>();
                    for (Map.Entry<Integer, HashMap<String, String>> perDimension : worldDataArray.entrySet()) {
                        for (Map.Entry<String, String> perDimensionEntries : perDimension.getValue().entrySet()) {
                            lines.add(perDimension.getKey() + ":::" + perDimensionEntries.getKey() + ":::" + perDimensionEntries.getValue());
                        }
                    }
                    for (Map.Entry<String, String> universalEntries : universalWorldDataArray.entrySet()) {
                        lines.add("99399" + ":::" + universalEntries.getKey() + ":::" + universalEntries.getValue()); // The chance somebody extended dimension IDs and made a dimension with the ID 99399 is, extremely slim.
                    }
                    PrintWriter pw = new PrintWriter(saveFile);
                    for (String str : lines) pw.println(str);
                    pw.close();
                }
            }
        } catch (Exception e) {
            LOGGER.error("Failed saving world data! This could potentially be disastrous! Report this to the mod developer.");
            e.printStackTrace();
        }
    }

    public static void loadAllWorldData() {
        try {
            if (DimensionManager.getWorld(0) != null) {
                if (DimensionManager.getWorld(0).isRemote) return;
                ISaveHandler handler = DimensionManager.getWorld(0).getSaveHandler();
                if (handler != null && handler instanceof SaveHandler) {
                    File saveFile = new File(((SaveHandler) handler).getWorldDirectory(), "AM2WorldData.txt");
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

                        // this avoids syncing the same dimension multiple times clogging up the network
                        // considering this method is called *after* the server's loaded, getting worlds using DimensionManager from here shouldn't be an issue
                        for (String entry : lines) {
                            if (Integer.valueOf(entry.split(":::")[0]) == 99399) setUniversalWorldVarNoSync(entry.split(":::")[1], entry.split(":::")[2]);
                            else setWorldVarNoSync(DimensionManager.getWorld(Integer.valueOf(entry.split(":::")[0])), entry.split(":::")[1], entry.split(":::")[2]);
                        }
                        syncAllWorldVarsToClients(null);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("Failed loading world data! This could potentially be disastrous! Report this to the mod developer.");
            e.printStackTrace();
        }
    }

    // if null is passed, syncs to all clients
    public static void syncAllWorldVarsToClients(EntityPlayer p) {
        for (WorldServer ws : DimensionManager.getWorlds()) {
            syncWorldVarsToClients(ws, p);
        }
    }
}
