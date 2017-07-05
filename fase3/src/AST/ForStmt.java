package AST;


import AST.CompoundStmt;
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
public class ForStmt extends CompoundStmt {
    public final String iter;
    public final Num begin;
    public final Num end;
    public final ArrayList<Stmt> stmts;

    public ForStmt(String iter, Num begin, Num end, ArrayList<Stmt> stmts) {
        this.iter = iter;
        this.begin = begin;
        this.end = end;
        this.stmts = stmts;
    }
    
}
