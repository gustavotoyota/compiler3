package AST;

import Auxiliar.PW;
import Auxiliar.SymbolTable;
import java.util.ArrayList;

public class PrintStmt extends SimpleStmt {
    public final ArrayList<OrTest> values;
    
    public PrintStmt(ArrayList<OrTest> values) {
        this.values = values;
    }
    
    @Override
    public void genC(PW pw) {
        Atom atom;
        
        pw.indent();
        pw.print("printf(\"");
        for (int i = 0; i < values.size(); ++i) {
            atom = values.get(i).andTests.get(0).notTests.get(0).comp.exprs.get(0).terms.get(0).factors.get(0).atomExpr.atom;
            switch(atom.type) {
            case IDENT:
                switch(SymbolTable.localTable.get(atom.name).type) {
                    case "int":
                        pw.print("%d");
                        break;
                    case "float":
                        pw.print("%lf");
                        break;
                    case "string":
                        pw.print("%s");
                        break;
                    case "boolean":
                        pw.print("%d");
                        break;
                    default:
                        break;
                }
                break;
            case NUMBER:
                if (atom.number.isInt)
                    pw.print("%d");
                else
                    pw.print("%lf");
                break;
            case STRINGLIT:  
                pw.print("%s");
                break;
            case TRUE:         
                pw.print("%d");
                break;
            case FALSE:                
                pw.print("%d");
                break; 
            }
        }
        pw.print("\", ");
        for (int i = 0; i < values.size(); ++i) {            
            values.get(i).genC(pw);
            if (i < values.size()-1)
                pw.print(", ");
        }
        pw.println(");");
    }
}
