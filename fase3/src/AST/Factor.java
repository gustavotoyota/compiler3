package AST;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Gustavo
 */
public class Factor {
    public final String signal;
    public final AtomExpr atomExpr;
    public final Factor exponent;
    
    public Factor(String signal, AtomExpr atomExpr, Factor exponent) {
        this.signal = signal;
        this.atomExpr = atomExpr;
        this.exponent = exponent;
    }
}
