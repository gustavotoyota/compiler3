package Auxiliar;

import AST.Num;

public class Variable {
    public final String type;
    public final Num length;
    
    public Variable(String type, Num length) {
        this.type = type;
        this.length = length;
    }
}
