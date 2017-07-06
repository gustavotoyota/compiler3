package AST;

import Auxiliar.PW;
import Lexer.Symbol;

public class Factor {
    public final String signal;
    public final AtomExpr atomExpr;
    public final Factor exponent;
    
    public Factor(String signal, AtomExpr atomExpr, Factor exponent) {
        this.signal = signal;
        this.atomExpr = atomExpr;
        this.exponent = exponent;
    }
    
    public void genC(PW pw) {                        
        if (exponent != null) {
            if (atomExpr.atom.type == Symbol.NUMBER && atomExpr.atom.number.isInt)
                pw.print("(int)");
            pw.print("pow(");
        }
        if ("-".equals(signal))
            pw.print(signal);
        atomExpr.genC(pw);
        if (exponent != null) {
            pw.print(", ");
            exponent.genC(pw);
            pw.print(")");
        }
    }
}
