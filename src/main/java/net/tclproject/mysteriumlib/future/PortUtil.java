package net.tclproject.mysteriumlib.future;

import java.io.*;
import java.util.List;
import java.util.Locale;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.Queues;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListenableFutureTask;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class PortUtil {

    private static PortUtil instance = new PortUtil();

    public static PortUtil getMinecraft() {
        return instance;
    }

    public static <T> T getLastElement(List<T> list)
    {
        return list.get(list.size() - 1);
    }

    public static <T> T make(Supplier<T> p_199748_0_) {
        return p_199748_0_.get();
    }

    public static <T> T make(T p_200696_0_, Consumer<T> p_200696_1_) {
        p_200696_1_.accept(p_200696_0_);
        return p_200696_0_;
    }

    public static float lerp(float p_219799_0_, float p_219799_1_, float p_219799_2_) {
        return p_219799_1_ + p_219799_0_ * (p_219799_2_ - p_219799_1_);
    }

    public static double lerp(double p_219803_0_, double p_219803_2_, double p_219803_4_) {
        return p_219803_2_ + p_219803_0_ * (p_219803_4_ - p_219803_2_);
    }

    public static <T> T func_199748_a(Supplier<T> p_199748_0_) {
        return p_199748_0_.get();
    }

    @SideOnly(Side.CLIENT)
    public static float func_226165_i_(float p_226165_0_) {
        float f = 0.5F * p_226165_0_;
        int i = Float.floatToIntBits(p_226165_0_);
        i = 1597463007 - (i >> 1);
        p_226165_0_ = Float.intBitsToFloat(i);
        return p_226165_0_ * (1.5F - f * p_226165_0_ * p_226165_0_);
    }

    public static float func_76131_a(float p_76131_0_, float p_76131_1_, float p_76131_2_) {
        if (p_76131_0_ < p_76131_1_) {
            return p_76131_1_;
        } else {
            return p_76131_0_ > p_76131_2_ ? p_76131_2_ : p_76131_0_;
        }
    }

    public static void copy(File sourceLocation, File targetLocation) throws IOException {
        if (sourceLocation.isDirectory()) {
            copyDirectory(sourceLocation, targetLocation);
        } else {
            copyFile(sourceLocation, targetLocation);
        }
    }

    private static void copyDirectory(File source, File target) throws IOException {
        if (!target.exists()) {
            target.mkdir();
        }

        for (String f : source.list()) {
            copy(new File(source, f), new File(target, f));
        }
    }

    private static void copyFile(File source, File target) throws IOException {
        try (
                InputStream in = new FileInputStream(source);
                OutputStream out = new FileOutputStream(target)
        ) {
            byte[] buf = new byte[1024];
            int length;
            while ((length = in.read(buf)) > 0) {
                out.write(buf, 0, length);
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public static PortUtil.EnumOS getOSType()
    {
        String s = System.getProperty("os.name").toLowerCase(Locale.ROOT);

        if (s.contains("win"))
        {
            return PortUtil.EnumOS.WINDOWS;
        }
        else if (s.contains("mac"))
        {
            return PortUtil.EnumOS.OSX;
        }
        else if (s.contains("solaris"))
        {
            return PortUtil.EnumOS.SOLARIS;
        }
        else if (s.contains("sunos"))
        {
            return PortUtil.EnumOS.SOLARIS;
        }
        else if (s.contains("linux"))
        {
            return PortUtil.EnumOS.LINUX;
        }
        else
        {
            return s.contains("unix") ? PortUtil.EnumOS.LINUX : PortUtil.EnumOS.UNKNOWN;
        }
    }

    @SideOnly(Side.CLIENT)
    public static enum EnumOS
    {
        LINUX,
        SOLARIS,
        WINDOWS,
        OSX,
        UNKNOWN;
    }
}
