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
public class Num {
    public final boolean isInt;
    public final int intValue;
    public final float floatValue;

    public Num(boolean isInt, int intValue, float floatValue) {
        this.isInt = isInt;
        this.intValue = intValue;
        this.floatValue = floatValue;
    }
}
