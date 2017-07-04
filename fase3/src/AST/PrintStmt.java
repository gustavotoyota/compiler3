package AST;

import java.util.ArrayList;

public class PrintStmt extends SimpleStmt {
    public final ArrayList<OrTest> values;
    
    public PrintStmt(ArrayList<OrTest> values) {
        this.values = values;
    }
    
}
