package AST;

import Auxiliar.PW;

public class Argument {
    public final String type;
    public final NameArray nameArray;
    
    public Argument(String type, NameArray nameArray) {
        this.type = type;
        this.nameArray = nameArray;
    }
    
    public void genC(PW pw) {
        String argType;
        switch (type) {
            case "boolean":
                argType = "int";
                break;
            case "string":
                argType = "char";
                break;
            case "float":
                argType = "double";
                break;
            default:
                argType = type;
                break;
        }

        pw.print(argType + " ");
        nameArray.genC(pw);
        if ("char".equals(argType))
            pw.print("[20]");
    }
}
