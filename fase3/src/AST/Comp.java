package AST;

import Auxiliar.PW;
import Auxiliar.SymbolTable;
import Lexer.Symbol;
import java.util.ArrayList;

public class Comp {
    public final ArrayList<Expr> exprs;
    public final ArrayList<String> opers;
    
    public Comp(ArrayList<Expr> exprs, ArrayList<String> opers) {
        this.exprs = exprs;
        this.opers = opers;
    }
    
    public void genC(PW pw) {
        Atom atom = exprs.get(0).terms.get(0).factors.get(0).atomExpr.atom;
        if (opers.isEmpty())
            exprs.get(0).genC(pw);
        else {
            if (atom.type == Symbol.STRINGLIT || 
                (SymbolTable.localTable.containsKey(atom.name) && "string".equals(SymbolTable.localTable.get(atom.name).type)) ||
                (SymbolTable.returnTypeTable.containsKey(atom.name) && "string".equals(SymbolTable.returnTypeTable.get(atom.name)))) {

                pw.print("strcmp(");
                exprs.get(0).genC(pw);
                pw.print(", ");
                exprs.get(1).genC(pw);
                pw.print(")");
                    
                switch (opers.get(0)) {
                    case "==":
                        pw.print(" == 0 ");
                        break;
                    case "!=":
                        pw.print(" != 0 ");
                        break;
                    case "<":
                        pw.print(" < 0 ");
                        break;
                    case "<=":
                        pw.print(" <= 0 ");
                        break;
                    case ">":
                        pw.print(" > 0 ");
                        break;
                    case ">=":
                        pw.print(" >= 0 ");
                        break;
                    default:
                        break;
                }
            } else {
                exprs.get(0).genC(pw);
                pw.print(" " + opers.get(0) + " ");
                exprs.get(1).genC(pw);
            }
        }        
    }
}
