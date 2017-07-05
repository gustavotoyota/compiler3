package AST;

import Auxiliar.PW;
import java.util.ArrayList;

public class OrList {
    public final ArrayList<OrTest> orTests;

    public OrList(ArrayList<OrTest> orTests) {
        this.orTests = orTests;
    } 
    
    public void genC(PW pw) {
        for (int i = 0; i < orTests.size(); ++i) {
            orTests.get(i).genC(pw);
            if (i < orTests.size()-1)
                pw.print(", ");
        }
    }
}
