package AST;

public class FuncDef {
    public final String name;
    public final ArgsList argsList;
    public final String type;
    public final Body body;
    
    public FuncDef(String name, ArgsList argsList, String type, Body body) {
        this.name =name;
        this.argsList = argsList;
        this.type = type;
        this.body = body;
    }
    public void genC() {
    }
}
