package AST;

import Auxiliar.PW;
import java.util.ArrayList;

public class AndTest {
    public final ArrayList<NotTest> notTests;

    public AndTest(ArrayList<NotTest> notTests) {
        this.notTests = notTests;
    }
    
    public void genC(PW pw) {
        for (int i = 0; i < notTests.size(); ++i) {
            notTests.get(i).genC(pw);
            if (i < notTests.size() - 1)
                pw.print(" && ");
        }
    }
}
