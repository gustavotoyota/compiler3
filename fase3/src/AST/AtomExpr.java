package AST;

import Auxiliar.PW;

public class AtomExpr {
    public final Atom atom;
    public final Details details;
    
    public AtomExpr(Atom atom, Details details) {
        this.atom = atom;
        this.details = details;
    }
    
    public void genC(PW pw) {
        atom.genC(pw);
        if (details != null)
            details.genC(pw);
    }
}
