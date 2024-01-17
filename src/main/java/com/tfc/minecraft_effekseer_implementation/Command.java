package com.tfc.minecraft_effekseer_implementation;

import javax.vecmath.Vector3d;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;

public class Command extends CommandBase {
	
    @Override
    public String getCommandName()
    {
        return "effek";
    }
    
    public int getRequiredPermissionLevel()
    {
        return 2;
    }

    @Override
    public String getCommandUsage(final ICommandSender sender)
    {
        return "Performs effek operations";
    }

    @Override
    public void processCommand(final ICommandSender sender, final String[] args)
    {
        if (!(sender instanceof EntityPlayerMP))
        {
            return;
        }
        
        // /effek [effect] [emitter] [delete]
        
        if (args[2].equalsIgnoreCase("true")) { // delete
			Networking.sendEndEffekPacketToDimension(
					args[0],
					args[1],
					true,
					sender.getEntityWorld().provider.dimensionId
			);
		} else {
			Networking.sendStartEffekPacketToDimension(
					args[0],
					args[1],
					0, 
					new Vector3d(sender.getPlayerCoordinates().posX, sender.getPlayerCoordinates().posY, sender.getPlayerCoordinates().posZ),
					sender.getEntityWorld().provider.dimensionId
			);
		}
    }
}