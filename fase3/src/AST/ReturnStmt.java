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
public class ReturnStmt extends SimpleStmt {
    public final OrTest value;

    public ReturnStmt(OrTest value) {
        this.value = value;
    }
    
}
