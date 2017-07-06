package AST;

import Auxiliar.PW;
import java.util.ArrayList;

public class OrTest {
    public final ArrayList<AndTest> andTests;

    public OrTest(ArrayList<AndTest> andTests) {
        this.andTests = andTests;
    }
    
    public void genC(PW pw) {
        for (int i = 0; i < andTests.size(); ++i) {
            andTests.get(i).genC(pw);
            if (i < andTests.size() - 1)
                pw.print(" || ");
        }
    }
}
