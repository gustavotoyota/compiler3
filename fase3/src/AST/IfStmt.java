package AST;

import Auxiliar.PW;
import java.util.ArrayList;

public class IfStmt extends CompoundStmt {
    public final OrTest cond;
    public final ArrayList<Stmt> ifStmts;
    public final ArrayList<Stmt> elseStmts;

    public IfStmt(OrTest cond, ArrayList<Stmt> ifStmts, ArrayList<Stmt> elseStmts) {
        this.cond = cond;
        this.ifStmts = ifStmts;
        this.elseStmts = elseStmts;
    }

    @Override
    public void genC(PW pw) {
        pw.indent();
        pw.print("if (");
        cond.genC(pw);
        pw.println(") {");
        pw.increment();        
        for (int i = 0; i < ifStmts.size(); ++i)
            ifStmts.get(i).genC(pw);        
        pw.decrement();
        pw.indent();
        pw.println("}");     
        if (!elseStmts.isEmpty()) {
            pw.indent();
            pw.println("else {");
            pw.increment();
            for (int i = 0; i < elseStmts.size(); ++i)
                elseStmts.get(i).genC(pw);
            pw.decrement();
            pw.println("}");
        }
    }
    
}
