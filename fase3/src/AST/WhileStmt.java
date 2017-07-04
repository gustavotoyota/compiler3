package AST;


import AST.CompoundStmt;
import AST.OrTest;
import AST.Stmt;
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
public class WhileStmt extends CompoundStmt {
    public final OrTest cond;
    public final ArrayList<Stmt> stmts;

    public WhileStmt(OrTest cond, ArrayList<Stmt> stmts) {
        this.cond = cond;
        this.stmts = stmts;
    }
    
}
