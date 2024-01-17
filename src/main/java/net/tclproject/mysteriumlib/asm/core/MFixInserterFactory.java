package net.tclproject.mysteriumlib.asm.core;

import org.objectweb.asm.MethodVisitor;

/**
 * Factory, specifying the type of the fix inserter. Factually, from the choice of the factory depends
 * in what part of the code the fix will be inserted.
 * By deault, there are three types of inserters: OnEnter, which inserts the fix to the start of the method,
 * OnExit, which inserts it on every exit, and ByAnchor, which allows to insert fixes at other method calls.
 */
public abstract class MFixInserterFactory {

	/**
     * The method AdviceAdapter#visitInsn() is a weird thing. For some reason, the calling of the next MethodVisitor
     * is done after the logic and not before, like in all the other cases. That's why for MethodExit the priority
     * of fixes is the reverse.
     */
    protected boolean priorityReversed = false;

    /**Creates a fix inserter object. A fix inserter will insert the fix using methods in MASMFix.*/
    abstract MFixInserter createFixInserter(MethodVisitor mv, int access, String name, String desc,
                                            MASMFix fix, FixInserterClassVisitor cv);

    /**Creates an inserter that will insert fixes at the start of a method.*/
    public static class OnEnter extends MFixInserterFactory {

        public static final OnEnter INSTANCE = new OnEnter();

        public OnEnter() {}

        @Override
        public MFixInserter createFixInserter(MethodVisitor mv, int access, String name, String desc,
                                              MASMFix fix, FixInserterClassVisitor cv) {
            return new MFixInserter.OnEnterInserter(mv, access, name, desc, fix, cv);
        }

    }

    public static class ByAnchor extends MFixInserterFactory {

        public static final ByAnchor INSTANCE = new ByAnchor();

        private ByAnchor() {}

        @Override
        public MFixInserter createFixInserter(MethodVisitor mv, int access, String name, String desc,
                                              MASMFix hook, FixInserterClassVisitor cv) {
            return new MFixInserter.ByAnchor(mv, access, name, desc, hook, cv);
        }
    }

    /**Creates an inserter that will insert fixes at exits from a method.*/
    public static class OnExit extends MFixInserterFactory {

        public static final OnExit INSTANCE = new OnExit();
        public boolean insertOnThrows;

        public OnExit() {
        	priorityReversed = true;
        	insertOnThrows = false;
        }
        
        public OnExit(boolean insertOnThrows) {
        	this.insertOnThrows = insertOnThrows;
        	priorityReversed = true;
        }

        @Override
        public MFixInserter createFixInserter(MethodVisitor mv, int access, String name, String desc,
                                              MASMFix fix, FixInserterClassVisitor cv) {
            return new MFixInserter.OnExitInserter(mv, access, name, desc, fix, cv, insertOnThrows);
        }
    }

    /**Creates an inserter that will insert fixes at a specific line number in a method.*/
    public static class OnLineNumber extends MFixInserterFactory {
    	
        private int lineNumber;

        public OnLineNumber(int lineNumber) {
            this.lineNumber = lineNumber;
        }

        @Override
        public MFixInserter createFixInserter(MethodVisitor mv, int access, String name, String desc,
                                              MASMFix fix, FixInserterClassVisitor cv) {
            return new MFixInserter.OnLineNumberInserter(mv, access, name, desc, fix, cv, lineNumber);
        }
    }
}
