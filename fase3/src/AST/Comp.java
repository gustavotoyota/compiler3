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
public class Comp {
    public final ArrayList<Expr> exprs;
    public final ArrayList<String> opers;
    
    public Comp(ArrayList<Expr> exprs, ArrayList<String> opers) {
        this.exprs = exprs;
        this.opers = opers;
    }
}
