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
public class NotTest {
    public final boolean not;
    public final Comp comp;
    
    public NotTest(boolean not, Comp comp) {
        this.not = not;
        this.comp = comp;
    }
}
