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
public class Details {
    public final boolean isFunc;
    public final OrList orList;
    public final boolean isInt;
    public final Num numIndex;
    public final String identIndex;

    public Details(boolean isFunc, OrList orList, boolean isInt, Num numIndex, String identIndex) {
        this.isFunc = isFunc;
        this.orList = orList;
        this.isInt = isInt;
        this.numIndex = numIndex;
        this.identIndex = identIndex;
    }
}
