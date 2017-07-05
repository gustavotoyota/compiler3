package AST;

public class Num {
    public final boolean isInt;
    public final int intValue;
    public final float floatValue;

    public Num(boolean isInt, int intValue, float floatValue) {
        this.isInt = isInt;
        this.intValue = intValue;
        this.floatValue = floatValue;
    }
}
