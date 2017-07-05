package AST;

import Auxiliar.PW;
import AST.CompoundStmt;
import AST.OrTest;
import AST.Stmt;
import java.util.ArrayList;

public class WhileStmt extends CompoundStmt {
    public final OrTest cond;
    public final ArrayList<Stmt> stmts;

    public WhileStmt(OrTest cond, ArrayList<Stmt> stmts) {
        this.cond = cond;
        this.stmts = stmts;
    }
    
    @Override
    public void genC(PW pw) {
        pw.indent();
        pw.print("while (");
        cond.genC(pw);
        pw.println(") {");
        pw.increment();
        for (int i = 0; i < stmts.size(); ++i)
            stmts.get(i).genC(pw);        
        pw.decrement();
        pw.indent();
        pw.println("}");
    }
}
