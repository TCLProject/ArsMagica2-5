package net.tclproject.mysteriumlib.asm.annotations;

public enum InsertionPoint {

    /**
     * Method start
     */
    HEAD,

    /**
     * Method end
     */
    RETURN,

    /**
     * When a call to another method is made during the execution of the one we're targeting
     */
    METHOD_CALL,

    /**
     * When a variable is assigned during the execution of the one we're targeting
     */
    VAR_ASSIGNMENT

}