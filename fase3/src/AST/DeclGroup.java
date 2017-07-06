package AST;

import Auxiliar.PW;

public class DeclGroup {
    public final String type;
    public final IdList idList;
    
    public DeclGroup(String type, IdList idList) {
        this.type = type;
        this.idList = idList;
    }
    
    public void genC(PW pw) {
        String declType;                
        switch (type) {
            case "boolean":
                declType = "int";
                break;
            case "string":
                declType = "char";
                break;
            case "float":
                declType = "double";
                break;
            default:
                declType = type;
                break;
        }
        
        pw.indent();
        pw.print(declType + " ");
        idList.genC(pw, declType);
    }
}
