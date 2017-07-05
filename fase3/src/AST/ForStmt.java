package AST;

import AST.CompoundStmt;
import AST.Stmt;
import Auxiliar.PW;
import java.util.ArrayList;

public class ForStmt extends CompoundStmt {
    public final String iter;
    public final Num begin;
    public final Num end;
    public final ArrayList<Stmt> stmts;

    public ForStmt(String iter, Num begin, Num end, ArrayList<Stmt> stmts) {
        this.iter = iter;
        this.begin = begin;
        this.end = end;
        this.stmts = stmts;
    }
    
    @Override
    public void genC(PW pw) {        
        pw.indent();
        pw.print("for (" + iter + "=" + Integer.toString(begin.intValue) + "; " + iter);
        if (begin.intValue < end.intValue)
            pw.println("<" + Integer.toString(end.intValue) + "; " + iter + "++) {");
        else
            pw.println(">" + Integer.toString(end.intValue) + "; " + iter + "--) {");
        pw.increment();
        for (int i = 0; i < stmts.size(); ++i)
            stmts.get(i).genC(pw);
        pw.decrement();
        pw.println("}");
    }
}
