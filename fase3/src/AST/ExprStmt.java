package AST;

import Auxiliar.PW;
import Auxiliar.SymbolTable;

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
        boolean isString = "string".equals(SymbolTable.localTable.get(name).type);
                 
        if (isList) {
            for (int i = 0; i < list.orTests.size(); ++i) {
                pw.indent();
                pw.print(name + "[" + Integer.toString(i) + "] = ");
                list.orTests.get(i).genC(pw);
                pw.println(";");
            }
        } else {
            pw.indent();
            if (isString)
                pw.print("strcpy(");
            pw.print(name);
            if (index != null) {
                pw.print("[");
                index.genC(pw);
                pw.print("]");
            }
            if (isString)
                pw.print(", ");
            else
                pw.print(" = ");

            value.genC(pw); 

            if (isString)
                pw.print(")");
            pw.println(";");
        }
    }
}
