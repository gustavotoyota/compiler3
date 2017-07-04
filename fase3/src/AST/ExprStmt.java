package AST;

public class ExprStmt extends SimpleStmt {
    public final String name;
    public final OrTest value;
    public final OrList list;
    public final Boolean isValue;
    
    public ExprStmt(String name, OrTest value, OrList list, Boolean isValue) {
        this.name = name;
        this.value = value;
        this.list = list;
        this.isValue = isValue;
    }
}
