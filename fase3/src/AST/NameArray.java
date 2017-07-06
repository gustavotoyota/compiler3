package AST;

import Auxiliar.PW;

public class NameArray {
    public final String name;
    public final Num length;
    
    public NameArray(String name, Num length) {
        this.name = name;
        this.length = length;
    }
    
    public void genC(PW pw) {
        pw.print(name);
        if (length != null && length.intValue > 0)
            pw.print("[" + Integer.toString(length.intValue) + "]");
    }
}
