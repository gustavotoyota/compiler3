package AST;

public class ExprStmt extends SimpleStmt {
    public final String name;
    public final Atom index;
    public final OrTest value;
    public final OrList list;
    public final Boolean isValue;

    public ExprStmt(String name, Atom index, OrTest value, OrList list, Boolean isValue) {
        this.name = name;
        this.index = index;
        this.value = value;
        this.list = list;
        this.isValue = isValue;
    }
}
