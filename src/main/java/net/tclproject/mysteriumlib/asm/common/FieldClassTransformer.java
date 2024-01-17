package net.tclproject.mysteriumlib.asm.common;

import net.minecraft.launchwrapper.IClassTransformer;
import net.tclproject.mysteriumlib.asm.annotations.InsertField;
import net.tclproject.mysteriumlib.asm.core.MMiscUtils;
import org.apache.commons.io.IOUtils;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

// a lot of the following code is taken from ASJLib (thanks AlexSocol!) and rewritten to java, because reinventing bicycles is not ideal
public class FieldClassTransformer implements IClassTransformer {

    public static HashMap<String, ArrayList<MMiscUtils.MFieldData>> fieldsMap = new HashMap<String, ArrayList<MMiscUtils.MFieldData>>();

    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (basicClass == null || basicClass.length == 0) return basicClass;
        if (!fieldsMap.containsKey(transformedName)) return basicClass;
        ClassReader cr = new ClassReader(basicClass);
        ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        for (MMiscUtils.MFieldData fd : fieldsMap.get(transformedName)) {
            cw.visitField(fd.access, fd.name, fd.desc, null, null).visitEnd();
        }
        cr.accept(cw, 0);
        return cw.toByteArray();
    }

    public static void registerFieldHookContainer(String className) {
        try {
            byte[] bytes = IOUtils.toByteArray(FieldClassTransformer.class.getResourceAsStream("/" + className.replace(".", "/") + ".class"));
            transform(bytes);
        } catch (IOException e) {
            System.err.println("[MysteriumLib] Can not parse fix class " + className + "!");
            e.printStackTrace();
        }
    }

    private static void transform(byte[] basicClass) {
        ClassReader cr = new ClassReader(basicClass);
        ClassNode cn = new ClassNode();
        cr.accept(cn, 0);

        for (FieldNode fn : cn.fields) {
            boolean flag = false;
            String targetClassName = "";

            if (fn.visibleAnnotations != null && !fn.visibleAnnotations.isEmpty()) {
                for (AnnotationNode an : fn.visibleAnnotations) {
                    if (an.desc.equals(Type.getDescriptor(InsertField.class))) {
                        flag = true;

                        if (an.values != null && !an.values.isEmpty()) {
                            int i = 0;
                            while (i < an.values.size()) {
                                if (an.values.get(i).toString().equals("targetClassName")) {
                                    targetClassName = an.values.get(i + 1).toString();
                                    break;
                                }
                                i += 2;
                            }
                        }

                        if (flag && !targetClassName.isEmpty()) break;
                    }
                }
            }

            if (!flag || targetClassName.isEmpty()) continue;

            if (!fieldsMap.containsKey(targetClassName)) fieldsMap.put(targetClassName, new ArrayList<>());
            if (fieldsMap.get(targetClassName) != null) fieldsMap.get(targetClassName).add(new MMiscUtils.MFieldData(fn.access, fn.name, fn.desc));
        }
    }
}
