package AST;

import Lexer.Group;
import java.util.ArrayList;

public class Body {
    public final Declaration declaration;
    public final ArrayList<AST.Stmt> stmts;
    
    public Body(Declaration declaration, ArrayList<AST.Stmt> stmts) {
        this.declaration = declaration;
        this.stmts = stmts;
    }
}
