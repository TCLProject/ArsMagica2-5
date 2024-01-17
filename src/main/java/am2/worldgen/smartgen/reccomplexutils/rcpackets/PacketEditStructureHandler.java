/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package am2.worldgen.smartgen.reccomplexutils.rcpackets;

import am2.AMCore;
import am2.worldgen.smartgen.generic.GenericStructureInfo;
import am2.worldgen.smartgen.generic.StructureSaveHandler;
import am2.worldgen.smartgen.reccomplexutils.ServerTranslations;
import am2.worldgen.smartgen.struct.files.RCFileTypeRegistry;
import am2.worldgen.smartgen.struct.info.StructureEntityInfo;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;

import java.util.Collections;

/**
 * Created by lukas on 03.08.14.
 */
public class PacketEditStructureHandler implements IMessageHandler<PacketEditStructure, IMessage>
{
    public static void openEditStructure(GenericStructureInfo structureInfo, String structureID, boolean saveAsActive, EntityPlayerMP player)
    {
        StructureEntityInfo structureEntityInfo = StructureEntityInfo.getStructureEntityInfo(player);

        if (structureEntityInfo != null)
            structureEntityInfo.setCachedExportStructureBlockDataNBT(structureInfo.worldDataCompound);

        AMCore.network.sendTo(new PacketEditStructure(structureInfo, structureID, saveAsActive,
                StructureSaveHandler.INSTANCE.hasGenericStructure(structureID, true),
                StructureSaveHandler.INSTANCE.hasGenericStructure(structureID, false)), player);
    }

    public static void finishEditStructure(GenericStructureInfo structureInfo, String structureID, boolean saveAsActive, boolean deleteOther)
    {
        AMCore.network.sendToServer(new PacketEditStructure(structureInfo, structureID, saveAsActive, deleteOther));
    }

    @Override
    public IMessage onMessage(PacketEditStructure message, MessageContext ctx)
    {
        if (ctx.side == Side.CLIENT)
        {
            onMessageClient(message, ctx);
        }
        else
        {
            NetHandlerPlayServer netHandlerPlayServer = ctx.getServerHandler();
            EntityPlayerMP player = netHandlerPlayServer.playerEntity;
            StructureEntityInfo structureEntityInfo = StructureEntityInfo.getStructureEntityInfo(player);

            GenericStructureInfo genericStructureInfo = message.getStructureInfo();

            if (structureEntityInfo != null)
                genericStructureInfo.worldDataCompound = structureEntityInfo.getCachedExportStructureBlockDataNBT();

            String path = RCFileTypeRegistry.getStructuresDirectoryName(message.isSaveAsActive()) + "/";
            String structureID = message.getStructureID();

            if (!StructureSaveHandler.INSTANCE.saveGenericStructure(genericStructureInfo, structureID, message.isSaveAsActive()))
            {
                player.addChatMessage(ServerTranslations.format("structure.save.failure", path + structureID));
            }
            else
            {
                player.addChatMessage(ServerTranslations.format("structure.save.success", path + structureID));

                if (message.isDeleteOther() && StructureSaveHandler.INSTANCE.hasGenericStructure(structureID, !message.isSaveAsActive()))
                {
                    String otherPath = RCFileTypeRegistry.getStructuresDirectoryName(!message.isSaveAsActive()) + "/";

                    if (StructureSaveHandler.INSTANCE.deleteGenericStructure(structureID, !message.isSaveAsActive()))
                        player.addChatMessage(ServerTranslations.format("structure.delete.success", otherPath + structureID));
                    else
                        player.addChatMessage(ServerTranslations.format("structure.delete.failure", otherPath + structureID));
                }

                AMCore.fileTypeRegistry.reloadCustomFiles(Collections.singletonList(StructureSaveHandler.FILE_SUFFIX));
            }
        }

        return null;
    }

    @SideOnly(Side.CLIENT)
    private void onMessageClient(PacketEditStructure message, MessageContext ctx)
    {
//        Minecraft.getMinecraft().displayGuiScreen(new GuiEditGenericStructure(message.getStructureID(), message.getStructureInfo(), message.isSaveAsActive(), message.isStructureInActive(), message.isStructureInInactive()));
    }
}
