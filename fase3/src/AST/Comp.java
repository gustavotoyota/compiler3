package AST;

import Auxiliar.PW;
import java.util.ArrayList;

public class Comp {
    public final ArrayList<Expr> exprs;
    public final ArrayList<String> opers;
    
    public Comp(ArrayList<Expr> exprs, ArrayList<String> opers) {
        this.exprs = exprs;
        this.opers = opers;
    }
    
    public void genC(PW pw) {
        for (int i = 0; i < exprs.size(); ++i) {
            exprs.get(i).genC(pw);
            if (i < exprs.size() - 1)
                pw.print(" " + opers.get(i) + " ");
        }
    }
}
