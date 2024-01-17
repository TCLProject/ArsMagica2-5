package net.tclproject.mysteriumlib.asm.core;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.util.Arrays;
import java.util.List;

public class OpCodesUtil {
    public static final List<Integer> varStoreOpcodes =
            Arrays.asList(new Integer[]{ Opcodes.ISTORE, Opcodes.LSTORE, Opcodes.FSTORE, Opcodes.DSTORE, Opcodes.ASTORE });

    public static boolean isVarStore(int opcode) {
        return varStoreOpcodes.contains(opcode);
    }
    public static boolean isFieldStore(int opcode) {
        return opcode == Opcodes.PUTFIELD || opcode == Opcodes.PUTSTATIC;
    }

    public static int getVarStoreOpcode(Type type) {
        return type.getOpcode(Opcodes.ISTORE);
    }

    public static int getVarLoadOpcode(Type type) {
        return type.getOpcode(Opcodes.ILOAD);
    }

    public static int getReturnOpcode(Type type) {
        return type.getOpcode(Opcodes.IRETURN);
    }
}
