package AST;
public class DeclGroup {
    public final String type;
    public final NameArray nameArray;
    
    public DeclGroup(String type, NameArray nameArray) {
        this.type = type;
        this.nameArray = nameArray;
    }
}
