package am2.worldgen.smartgen.struct;

import am2.AMCore;
import am2.worldgen.smartgen.StructurePresets;
import am2.worldgen.smartgen.reccomplexutils.ServerTranslations;
import am2.worldgen.smartgen.struct.info.StructureEntityInfo;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class OperationRegistry
{
    private static BiMap<String, Class<? extends StructurePresets.Operation>> operations = HashBiMap.create();

    public static void register(String id, Class<? extends StructurePresets.Operation> operation)
    {
        operations.put(id, operation);
    }

    @Nullable
    public static StructurePresets.Operation readOperation(@Nonnull NBTTagCompound compound)
    {
        String opID = compound.getString("opID");
        Class<? extends StructurePresets.Operation> clazz = operations.get(opID);

        if (clazz == null)
        {
            AMCore.logger.error(String.format("Unrecognized Operation ID '%s'", opID));
            return null;
        }

        try
        {
            StructurePresets.Operation operation = clazz.newInstance();
            operation.readFromNBT(compound);
            return operation;
        }
        catch (InstantiationException | IllegalAccessException e)
        {
            AMCore.logger.error(String.format("Could not read Operation with ID '%s'", opID), e);
        }

        return null;
    }

    public static NBTTagCompound writeOperation(@Nonnull StructurePresets.Operation operation)
    {
        NBTTagCompound compound = new NBTTagCompound();
        operation.writeToNBT(compound);
        compound.setString("opID", operations.inverse().get(operation.getClass()));
        return compound;
    }

    public static void queueOperation(StructurePresets.Operation operation, ICommandSender commandSender)
    {
        boolean instant = true;

        if (commandSender instanceof EntityPlayer)
        {
            EntityPlayer player = CommandBase.getCommandSenderAsPlayer(commandSender);
            StructureEntityInfo info = StructureEntityInfo.getStructureEntityInfo(player);
            if (info != null)
            {
                if (info.getPreviewType() != StructurePresets.Operation.PreviewType.NONE)
                {
                    info.queueOperation(operation, player);
                    instant = false;

                    IChatComponent confirmComponent = new ChatComponentText("/" + "ConfirmCommand");
                    confirmComponent.getChatStyle().setColor(EnumChatFormatting.GREEN);
                    confirmComponent.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + "ConfirmCommand"));
                    confirmComponent.getChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, ServerTranslations.get("Confirm")));

                    IChatComponent cancelComponent = new ChatComponentText("/" + "CancelCommand");
                    cancelComponent.getChatStyle().setColor(EnumChatFormatting.RED);
                    cancelComponent.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + "CancelCommand"));
                    cancelComponent.getChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, ServerTranslations.get("Cancel")));

                    IChatComponent component = ServerTranslations.format("QueuedOp", confirmComponent, cancelComponent);
                    commandSender.addChatMessage(component);
                }
            }
        }

        if (instant)
            operation.perform(commandSender.getEntityWorld());
    }
}