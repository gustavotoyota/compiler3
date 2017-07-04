package AST;

import java.util.ArrayList;

public class Declaration {
    public final ArrayList<AST.DeclGroup> declGroups;
    
    public Declaration(ArrayList<AST.DeclGroup> declGroups) {
        this.declGroups = declGroups;
    }
}
