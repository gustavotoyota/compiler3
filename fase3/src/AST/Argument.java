package AST;

public class Argument {
    public final String type;
    public final NameArray nameArray;
    
    public Argument(String type, NameArray nameArray) {
        this.type = type;
        this.nameArray = nameArray;
    }
}
