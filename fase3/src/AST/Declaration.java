package AST;

import Auxiliar.PW;

import java.util.ArrayList;

public class Declaration {
    public final ArrayList<AST.DeclGroup> declGroups;
    
    public Declaration(ArrayList<AST.DeclGroup> declGroups) {
        this.declGroups = declGroups;
    }
    
    public void genC(PW pw) {
        for (int i = 0; i < declGroups.size(); ++i) {
            declGroups.get(i).genC(pw);
            pw.println(";");            
        }
    }
}
