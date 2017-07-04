package AST;


import AST.OrTest;
import AST.SimpleStmt;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Gustavo
 */
public class FuncStmt extends SimpleStmt {
    public final String name;
    public final OrTest param;

    public FuncStmt(String name, OrTest param) {
        this.name = name;
        this.param = param;
    }
    
}
