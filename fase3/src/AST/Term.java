package AST;

import Auxiliar.PW;
import java.util.ArrayList;

public class Term {
    public final ArrayList<Factor> factors;
    public final ArrayList<String> opers;
    
    public Term(ArrayList<Factor> factors, ArrayList<String> opers) {
        this.factors = factors;
        this.opers = opers;
    }
    
    public void genC(PW pw) {
        for (int i = 0; i < factors.size(); ++i) {
            factors.get(i).genC(pw);
            if (i < factors.size() - 1)
                pw.print(" " + opers.get(i) + " ");
        }
    }
}
