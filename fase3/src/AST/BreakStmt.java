package AST;

import Auxiliar.PW;
import AST.SimpleStmt;

public class BreakStmt extends SimpleStmt {

    public BreakStmt() {
    }
    
    @Override
    public void genC(PW pw) {
        pw.indent();
        pw.println("break;");
    }
}
