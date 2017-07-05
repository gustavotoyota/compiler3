package AST;

import Auxiliar.PW;
import Auxiliar.SymbolTable;
import Lexer.Symbol;
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
                switch(SymbolTable.symbolTable.get(atom.name).type) {
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
            atom = values.get(i).andTests.get(0).notTests.get(0).comp.exprs.get(0).terms.get(0).factors.get(0).atomExpr.atom;
            switch(atom.type) {
            case IDENT:
                pw.print(atom.name);
                break;
            case NUMBER:
                if (atom.number.isInt)
                    pw.print(Integer.toString(atom.number.intValue));
                else
                    pw.print(Double.toString(atom.number.floatValue));
                break;
            case STRINGLIT:  
                pw.print("\"" + atom.string + "\"");
                break;
            case TRUE:         
                pw.print("1");
                break;
            case FALSE:                
                pw.print("2");
                break; 
            }
            if (i < values.size()-1)
                pw.print(", ");
        }
        pw.println(");");
    }
}
