package AST;

import Auxiliar.PW;

public class NotTest {
    public final boolean not;
    public final Comp comp;
    
    public NotTest(boolean not, Comp comp) {
        this.not = not;
        this.comp = comp;
    }
    
    public void genC(PW pw) {
        if (not)
            pw.print("!");
        comp.genC(pw);
    }
}
