package net.tclproject.mysteriumlib.asm.annotations;

public enum Shift {

    /**
     * Before the specified insertion point
     */
    BEFORE,

    /**
     * After the specified insertion point
     */
    AFTER,

    /**
     * Instead of the specified insertion point
     */
    INSTEAD;

    public static Shift valueOfNullable(String shift) {
        return shift == null ? Shift.AFTER : valueOf(shift);
    }
}