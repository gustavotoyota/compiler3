package AST;

import Lexer.Group;
import Auxiliar.PW;
import java.util.ArrayList;

public class Body {
    public final Declaration declaration;
    public final ArrayList<AST.Stmt> stmts;
    
    public Body(Declaration declaration, ArrayList<AST.Stmt> stmts) {
        this.declaration = declaration;
        this.stmts = stmts;
    }
    
    public void genC(PW pw) {
        declaration.genC(pw);
        for (int i = 0; i < stmts.size(); ++i)
            stmts.get(i).genC(pw);
    }
}
