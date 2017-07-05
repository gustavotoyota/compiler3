package AST;

import Auxiliar.PW;

public class ExprStmt extends SimpleStmt {
    public final String name;
    public final Atom index;
    public final OrTest value;
    public final OrList list;
    public final Boolean isList;

    public ExprStmt(String name, Atom index, OrTest value, OrList list, Boolean isList) {
        this.name = name;
        this.index = index;
        this.value = value;
        this.list = list;
        this.isList = isList;
    }
    
    @Override
    public void genC(PW pw) {
        pw.indent();
        pw.print(name);
        if (index != null) {
            pw.print("[");
            index.genC(pw);
            pw.print("]");
        }
        pw.print(" = ");
        if (isList) {
            pw.print("[");
            list.genC(pw);
            pw.print("]");
        } else
            value.genC(pw);        
        pw.println(";");
    }
}
