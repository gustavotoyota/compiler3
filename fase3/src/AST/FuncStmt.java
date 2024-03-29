package AST;

import Auxiliar.PW;
import AST.OrTest;
import AST.SimpleStmt;

public class FuncStmt extends SimpleStmt {
    public final String name;
    public final OrList param;

    public FuncStmt(String name, OrList param) {
        this.name = name;
        this.param = param;
    }
    
    @Override
    public void genC(PW pw) {
        pw.indent();
        pw.print(name + "(");
        if (param != null)
            param.genC(pw);
        pw.println(");");
    }
}
