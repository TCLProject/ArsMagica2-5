package am2.worldgen.dynamic;

import am2.AMCore;
import am2.LogHelper;
import am2.bosses.AM2Boss;
import am2.bosses.BossSpawnHelper;
import am2.bosses.EntityWaterGuardian;
import am2.bosses.EntityWinterGuardian;
import am2.network.AMDataWriter;
import am2.network.AMNetHandler;
import am2.network.AMPacketIDs;
import am2.playerextensions.ExtendedProperties;
import am2.utility.DimensionUtilities;
import com.sun.deploy.util.JarUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Util;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraftforge.common.DimensionManager;
import org.apache.logging.log4j.Level;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static am2.AMCore.logger;

public class DynamicBossWorldHelper {
    public static void teleportPlayerToNewBossWorld(EntityPlayerMP player, AM2Boss boss) {
        ExtendedProperties extendedProperties = ExtendedProperties.For(player);
        extendedProperties.addToExtraVariables("origPos", player.posX + "," + player.posY + "," + player.posZ + "," + player.worldObj.provider.dimensionId);

        int[] newPlayerCoords = BossSpawnHelper.playerBossfightCoordinates[BossSpawnHelper.getIntFromBoss(boss)];
        int newDimensionID = DimensionManager.getNextFreeDimId();

        createNewDimension(newDimensionID);
        WorldServer bossWorld = MinecraftServer.getServer().worldServerForDimension(newDimensionID);
        int[] newBossCoords = BossSpawnHelper.bossBossfightCoordinates[BossSpawnHelper.getIntFromBoss(boss)][bossWorld.rand.nextInt(BossSpawnHelper.bossBossfightCoordinates[BossSpawnHelper.getIntFromBoss(boss)].length)];

//        if (boss.dimension != newDimensionID) {
//            boss.travelToDimension(newDimensionID);
//        }
        AM2Boss newBoss = null;
        try {
            newBoss = boss.getClass().getConstructor(World.class).newInstance(bossWorld);
        } catch (Exception e) {
             throw new RuntimeException("CRITICAL error: Cannot construct boss!", e);
        }
        newBoss.setPosition(newBossCoords[0] + 0.5D, newBossCoords[1] + 0.5D, newBossCoords[2] + 0.5D);
        bossWorld.spawnEntityInWorld(newBoss);
        ((DynamicBossWorldProvider)bossWorld.provider).setFogColorDynamically(newBoss);
        if (newBoss instanceof EntityWaterGuardian || newBoss instanceof EntityWinterGuardian) {
            bossWorld.getWorldInfo().setRainTime(30000);
            bossWorld.getWorldInfo().setRaining(true);
            bossWorld.setRainStrength(0.7F);
        } else {
            bossWorld.setRainStrength(0.0F);
            bossWorld.getWorldInfo().setRaining(false);
        }

        if (player.dimension != newDimensionID) {
//            DimensionUtilities.doDimensionTransfer(player, newDimensionID);
            player.travelToDimension(newDimensionID); // freezes????????????
//            player.mcServer.getConfigurationManager().transferPlayerToDimension(player, newDimensionID, new EmptyTeleporter(bossWorld));
//            MinecraftServer.getServer().getConfigurationManager().transferPlayerToDimension(player, newDimensionID, new EmptyTeleporter(bossWorld));
        }
        player.setPositionAndUpdate(newPlayerCoords[0] + 0.5D, newPlayerCoords[1] + 0.5D, newPlayerCoords[2] + 0.5D);
    }

    public static void returnPlayerToOriginalPosition(EntityPlayer player) {
        ExtendedProperties extendedProperties = ExtendedProperties.For(player);
        if (extendedProperties.hasExtraVariable("origPos")) {
            String[] originalPosition = extendedProperties.getExtraVariable("origPos").split(",");
            player.travelToDimension(Integer.parseInt(originalPosition[3])); // potentially use DimensionUtilities.doDimensionTransfer if this does not work perfectly
            player.setPositionAndUpdate(Double.parseDouble(originalPosition[0]), Double.parseDouble(originalPosition[1]), Double.parseDouble(originalPosition[2]));
            extendedProperties.removeFromExtraVariables("origPos");
        }
    }

    private static void createNewDimension(int id) {
        registerDimension(id);
        touchSpawnChunk(id);
    }

    private static void registerDimension(int id) {
        if (!DimensionManager.isDimensionRegistered(id)) {
            DimensionManager.registerProviderType(id, DynamicBossWorldProvider.class, false);
            DimensionManager.registerDimension(id, id);
        }
        AMDataWriter writer = new AMDataWriter();
        writer.add(id);
        AMNetHandler.INSTANCE.sendPacketToAllClients(AMPacketIDs.SYNCDIMENSIONSTOCLIENT, writer.generate());
    }

    public static void unregisterDimension(int id, World world) {
        deleteDirectory(new File(DimensionManager.getCurrentSaveRootDirectory(), world.provider.getSaveFolder()));
        if (DimensionManager.isDimensionRegistered(id)) {
            logger.log(Level.DEBUG, "Unregistering dimension from Dimension Manager, ID: " + id);
            try {
                DimensionManager.unregisterDimension(id);
            } catch (Exception e) {
                logger.log(Level.ERROR, "Could not unregister dimension, ID: " + id);
            }
            try {
                DimensionManager.unregisterProviderType(id);
            } catch (Exception e) {
                logger.log(Level.ERROR, "Could not unregister provider, ID: " + id);
            }
        } else {
            logger.log(Level.ERROR, "Already unregistered! ID: " + id);
        }
    }

    // courtesy of McJty
    private static void touchSpawnChunk(int id) {
        // Make sure world generation kicks in for at least one chunk so that our matter receiver
        // is generated and registered.
        WorldServer worldServerForDimension = MinecraftServer.getServer().worldServerForDimension(id);
        ChunkProviderServer providerServer = worldServerForDimension.theChunkProviderServer;
        try {
            copyFolderFromJar(new File(new File(DimensionManager.getCurrentSaveRootDirectory(), worldServerForDimension.provider.getSaveFolder()), "region"));
        } catch (Exception e) {
            logger.log(Level.FATAL, "FAILED TO COPY WORLD FILES FROM JAR! CRITICAL ERROR; IF YOU SEE THIS, PLEASE REPORT IT!");
            e.printStackTrace();
        }
        if (!providerServer.chunkExists(0, 0)) {
            try {
                providerServer.loadChunk(0, 0);
                providerServer.populate(providerServer, 0, 0);
                providerServer.unloadChunksIfNotNearSpawn(0, 0);
            } catch (Exception e) {
                logger.log(Level.DEBUG, "Something went wrong during creation of the dimension!");
                e.printStackTrace();
                // We catch this exception to make sure our dimension tab is at least ok.
            }
        }
    }

    public static final char slash = '/';

    static boolean deleteDirectory(File directoryToBeDeleted) {
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectory(file);
            }
        }
        return directoryToBeDeleted.delete();
    }

    public static void copyFolderFromJar(File destFolder) throws IOException, URISyntaxException {
//        copyFolderFromJar("assets/arsmagica2/worldfiles/region", destFolder, CopyOption.REPLACE_IF_EXIST, null);
        CodeSource src = AMCore.class.getProtectionDomain().getCodeSource();
        ArrayList<String> worldAssets = new ArrayList<String>();
        if (src != null){
            URL jar = src.getLocation();
            if (jar.getProtocol().equals("jar")){
                String path = jar.toString().replace("jar:", "").replace("file:", "").replace("!/am2/AMCore.class", "").replace('/', File.separatorChar);
                path = URLDecoder.decode(path, "UTF-8");
                LogHelper.debug(path);
                JarFile jarFile = new JarFile(path);
                Enumeration<JarEntry> entries = jarFile.entries();
                while (entries.hasMoreElements()){
                    JarEntry entry = entries.nextElement();
                    if (entry.getName().startsWith("assets/arsmagica2/worldfiles/region/")){
                        String name = entry.getName().replace("assets/arsmagica2/worldfiles/region/", "");
                        if (name.equals("")) continue;
                        worldAssets.add(entry.getName());
                    }
                }
                jarFile.close();
            }else if (jar.getProtocol().equals("file")){
                String path = (Util.getOSType() == Util.EnumOS.LINUX ? "/" : "") + jar.toURI().toString().replace("/am2/AMCore.class", "/assets/arsmagica2/worldfiles/region/").replace("file:/", "").replace("%20", " ").replace('/', File.separatorChar);
                File file = new File(path);
                if (file.exists() && file.isDirectory()){
                    for (File sub : file.listFiles()){
                        worldAssets.add("/assets/arsmagica2/worldfiles/region/" + sub.getName());
                    }
                }
            }
        }
        destFolder.mkdirs();
        for (String asset : worldAssets) {
            copy(AMCore.class.getResourceAsStream(asset),destFolder + "/" + asset.split(File.separator)[asset.split(File.separator).length-1]);
        }
    }

    public static boolean copy(InputStream source , String destination) {
        boolean succeess = true;

        logger.log(Level.DEBUG,"Copying ->" + source + "\n\tto ->" + destination);

        try {
            Files.copy(source, Paths.get(destination), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            logger.log(Level.WARN, "", ex);
            succeess = false;
        }

        return succeess;
    }

    public static void copyFolderFromJar(String folderName, File destFolder, CopyOption option, PathTrimmer trimmer) throws IOException {
        if (!destFolder.exists())
            destFolder.mkdirs();

        byte[] buffer = new byte[1024];

        File fullPath = null;
        String path = DynamicBossWorldHelper.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        if (trimmer != null)
            path = trimmer.trim(path);
        try {
            if (!path.startsWith("file"))
                path = "file://" + path;

            fullPath = new File(new URI(path));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        ZipInputStream zis = new ZipInputStream(new FileInputStream(fullPath));

        ZipEntry entry;
        while ((entry = zis.getNextEntry()) != null) {
            if (!entry.getName().startsWith(folderName + slash))
                continue;

            String fileName = entry.getName();

            if (fileName.charAt(fileName.length() - 1) == slash) {
                File file = new File(destFolder + File.separator + fileName);
                if (file.isFile()) {
                    file.delete();
                }
                file.mkdirs();
                continue;
            }

            File file = new File(destFolder + File.separator + fileName);
            if (option == CopyOption.COPY_IF_NOT_EXIST && file.exists())
                continue;

            if (!file.getParentFile().exists())
                file.getParentFile().mkdirs();

            if (!file.exists())
                file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);

            int len;
            while ((len = zis.read(buffer)) > 0) {
                fos.write(buffer, 0, len);
            }
            fos.close();
        }

        zis.closeEntry();
        zis.close();
    }

    public enum CopyOption {
        COPY_IF_NOT_EXIST, REPLACE_IF_EXIST;
    }

    @FunctionalInterface
    public interface PathTrimmer {
        String trim(String original);
    }
}
