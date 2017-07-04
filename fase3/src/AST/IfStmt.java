package AST;


import java.util.ArrayList;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Gustavo
 */
public class IfStmt extends CompoundStmt {
    public final OrTest cond;
    public final ArrayList<Stmt> ifStmts;
    public final ArrayList<Stmt> elseStmts;

    public IfStmt(OrTest cond, ArrayList<Stmt> ifStmts, ArrayList<Stmt> elseStmts) {
        this.cond = cond;
        this.ifStmts = ifStmts;
        this.elseStmts = elseStmts;
    }
    
}
