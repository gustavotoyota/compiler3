package AST;
public class DeclGroup {
    public final String type;
    public final IdList idList;
    
    public DeclGroup(String type, IdList idList) {
        this.type = type;
        this.idList = idList;
    }
}
