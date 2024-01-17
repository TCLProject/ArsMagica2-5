package net.tclproject.mysteriumlib.asm.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface At {
    /**The type of the insertion point.*/
    public InsertionPoint point();
    /**The shift relative to the insertion point.*/
    public Shift shift() default Shift.AFTER;
    /**For specifying, for example, the method name.*/
    public String target() default "";
    /**The operation number (count goes in order). -1 if every operation.*/
    public int ordinal() default -1;
    /**The number (count goes in order) of the intercepted local variable.*/
    public int targetVar() default -1;
}