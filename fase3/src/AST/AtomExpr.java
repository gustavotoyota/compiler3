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
public class AtomExpr {
    public final Atom atom;
    public final Details details;
    
    public AtomExpr(Atom atom, Details details) {
        this.atom = atom;
        this.details = details;
    }
}
