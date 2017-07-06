package AST;

import Auxiliar.PW;
import AST.OrTest;
import AST.SimpleStmt;

public class ReturnStmt extends SimpleStmt {
    public final OrTest value;

    public ReturnStmt(OrTest value) {
        this.value = value;
    }
    
    public void genC(PW pw) {
        pw.indent();
        pw.print("return ");
        if (value != null)
            value.genC(pw);
        pw.println(";");        
    }
}
