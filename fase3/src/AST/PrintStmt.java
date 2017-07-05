package AST;

import Auxiliar.PW;
import java.util.ArrayList;

public class PrintStmt extends SimpleStmt {
    public final ArrayList<OrTest> values;
    
    public PrintStmt(ArrayList<OrTest> values) {
        this.values = values;
    }
    
    @Override
    public void genC(PW pw) {
        pw.indent();
        pw.print("printf(");
        for (int i = 0; i < values.size(); ++i)
            values.get(i).genC(pw);
        pw.println(");");
    }
}
