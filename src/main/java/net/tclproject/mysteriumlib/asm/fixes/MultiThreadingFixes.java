package net.tclproject.mysteriumlib.asm.fixes;

import net.minecraft.client.Minecraft;
import net.tclproject.mysteriumlib.asm.annotations.MFix;
import net.tclproject.mysteriumlib.multithreading.ThreadScheduler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MultiThreadingFixes {
    private static final Logger LOGGER = LogManager.getLogger("MultiThreading");

    @MFix
    public static void runGameLoop(Minecraft mc)
    {
        synchronized (ThreadScheduler.getMinecraft().scheduledTasks)
        {
            while (!ThreadScheduler.getMinecraft().scheduledTasks.isEmpty())
            {
                ThreadScheduler.runTask(ThreadScheduler.getMinecraft().scheduledTasks.poll(), LOGGER);
            }
        }
    }
}
