package AST;

import Auxiliar.PW;
import java.util.ArrayList;

public class Expr {
    public final ArrayList<Term> terms;
    public final ArrayList<String> opers;
    
    public Expr(ArrayList<Term> terms, ArrayList<String> opers) {
        this.terms = terms;
        this.opers = opers;
    }
    
    public void genC(PW pw) {
        for (int i = 0; i < terms.size(); ++i) {
            terms.get(i).genC(pw);
            if (i < terms.size() - 1)
                pw.print(" " + opers.get(i) + " ");
        }
    }
}
