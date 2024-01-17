package net.tclproject.mysteriumlib.asm.core;

import net.tclproject.mysteriumlib.asm.annotations.InsertionPoint;
import net.tclproject.mysteriumlib.asm.common.CustomLoadingPlugin;
import net.tclproject.mysteriumlib.asm.common.MCustomClassTransformer;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.AdviceAdapter;

import static net.tclproject.mysteriumlib.asm.annotations.InsertionPoint.*;

/**Custom MethodVisitor that calls the insert method in MASMFix to insert fixes.*/
public abstract class MFixInserter extends AdviceAdapter {
	
	/**The fix that this visitor needs to insert.*/
	protected final MASMFix fix;
	/**The class visitor that visited this method and created this MethodVisitor.*/
    protected final FixInserterClassVisitor classVisitor;
    /**The target method name.*/
    public final String methodName;
    /**The target method return type.*/
    public final Type methodType;
    /**If the target method is static.*/
    public final boolean isStatic;

    protected MFixInserter(MethodVisitor mv, int access, String name, String descriptor, MASMFix fix, FixInserterClassVisitor classVisitor) {
        super(Opcodes.ASM5, mv, access, name, descriptor);
        this.fix = fix;
        this.classVisitor = classVisitor;
        isStatic = (access & Opcodes.ACC_STATIC) != 0;
        this.methodName = name;
        this.methodType = Type.getMethodType(descriptor);
    }

    /**
     * Inserts the fix into the bytecode.
     */
    protected final void insertFix() {
    	if (!classVisitor.visitingFix) {
	        classVisitor.visitingFix = true;
	        fix.insertFix(this);
	        classVisitor.visitingFix = false;
    	}
    }

    /**
     * Inserts the fix into an arbitrary method.
     */

    public static class ByAnchor extends MFixInserter {

        private Integer ordinal;

        public ByAnchor(MethodVisitor mv, int access, String name, String desc,
                        MASMFix fix, FixInserterClassVisitor cv) {
            super(mv, access, name, desc, fix, cv);
            ordinal = fix.getAnchorOrdinal();
        }

        @Override
        public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
            String targetName =
                    CustomLoadingPlugin.isObfuscated()
                            ? MCustomClassTransformer.instance.getMethodNames().getOrDefault(MCustomClassTransformer.getMethodIndex(name), name)
                            : name;
            if (fix.getAnchorPoint() == METHOD_CALL && fix.getAnchorTarget().equals(targetName))
                switch (fix.getShift()) {
                    case BEFORE:
                        insertFixByOrdinal();
                        super.visitMethodInsn(opcode, owner, name, desc, itf);
                        break;
                    case AFTER:
                        super.visitMethodInsn(opcode, owner, name, desc, itf);
                        insertFixByOrdinal();
                        break;
                    case INSTEAD:
                        if (insertFixByOrdinal())
                            for (int i = 0; i < Type.getArgumentTypes(desc).length + 1; i++)
                                visitInsn(Opcodes.POP);
                        else
                            super.visitMethodInsn(opcode, owner, name, desc, itf);
                        break;
                }
            else
                super.visitMethodInsn(opcode, owner, name, desc, itf);
        }

        public void visitVarInsn(int opcode, int var) {
            super.visitVarInsn(opcode, var);

            if (fix.getAnchorPoint() == VAR_ASSIGNMENT && OpCodesUtil.isVarStore(opcode) && var == fix.getAnchorTargetVar()) {
                insertFixByOrdinal();
            }
        }

        protected void onMethodEnter() {
            if(fix.getAnchorPoint()==HEAD)
                insertFix();
        }
        protected void onMethodExit(int opcode) {
            if(fix.getAnchorPoint()== InsertionPoint.RETURN && opcode != Opcodes.ATHROW)
                insertFixByOrdinal();
        }

        private boolean insertFixByOrdinal() {
            if (ordinal == 0) {
                insertFix();
                ordinal = -2;
                return true;
            } else if (ordinal == -1) {
                insertFix();
                return true;
            } else if (ordinal > 0)
                ordinal -= 1;
            return false;
        }

    }

    /**
     * Inserts the fix when visiting the start of the method.
     */
    public static class OnEnterInserter extends MFixInserter {

        public OnEnterInserter(MethodVisitor mv, int access, String name, String desc, MASMFix fix, FixInserterClassVisitor cv) {
            super(mv, access, name, desc, fix, cv);
        }

        /**
         * Inserts the fix into the bytecode.
         */
        @Override
        protected void onMethodEnter() {
        	insertFix();
        }

    }

    /**
     * Inserts the fix when visiting every exit from the method, except for exiting through throwing an error (configurable).
     */
    public static class OnExitInserter extends MFixInserter {
    	
    	public boolean insertOnThrows;
    	
    	public OnExitInserter(MethodVisitor mv, int access, String name, String desc, MASMFix fix, FixInserterClassVisitor cv) {
            super(mv, access, name, desc, fix, cv);
            this.insertOnThrows = false;
        }

        public OnExitInserter(MethodVisitor mv, int access, String name, String desc, MASMFix fix, FixInserterClassVisitor cv, boolean insertOnThrows) {
            super(mv, access, name, desc, fix, cv);
            this.insertOnThrows = insertOnThrows;
        }

        /**
         * Inserts the fix into the bytecode.
         */
        @Override
        protected void onMethodExit(int opcode) {
            if (opcode != Opcodes.ATHROW || this.insertOnThrows) {
                insertFix();
            }
        }
    }

    /**
     * Inserts the fix when visiting the specific line number.
     */
    public static class OnLineNumberInserter extends MFixInserter {

        private int lineNumber;

        public OnLineNumberInserter(MethodVisitor mv, int access, String name, String desc, MASMFix fix, FixInserterClassVisitor cv, int lineNumber) {
            super(mv, access, name, desc, fix, cv);
            this.lineNumber = lineNumber;
        }
        
        /**
         * Inserts the fix into the bytecode.
         */
        @Override
        public void visitLineNumber(int lineVisiting, Label start) {
            super.visitLineNumber(lineVisiting, start);
            if (lineVisiting == lineNumber) {
            	insertFix();
            }
        }
    }
}
