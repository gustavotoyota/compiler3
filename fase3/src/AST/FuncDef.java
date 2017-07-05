package AST;

import Auxiliar.PW;

public class FuncDef {
    public String name;
    public ArgsList argsList;
    public String type;
    public Body body;
    
    public FuncDef(String name, ArgsList argsList, String type, Body body) {
        this.name = name;
        this.argsList = argsList;
        this.type = type;
        this.body = body;
    }
    
    public void genC(PW pw) {
        String funcType;                
        switch (type) {
            case "boolean":
                funcType = "int";
                break;
            case "string":
                funcType = "char*";
                break;
            case "float":
                funcType = "double";
                break;
            default:
                funcType = type;
                break;
        }
        
        pw.indent();
        pw.print(funcType + " " + name + "(");
        if (argsList != null)
            argsList.genC(pw);        
        pw.println(") {");
        pw.increment();
        body.genC(pw);
        pw.decrement();
        pw.indent();
        pw.println("}");
    }
}
