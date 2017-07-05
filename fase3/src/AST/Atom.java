/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AST;

import Lexer.Symbol;

/**
 *
 * @author Gustavo
 */
public class Atom {
    public final Symbol type;
    public final String name;
    public final Num number;
    public final String string;

    public Atom(Symbol type, String name, Num num, String string) {
        this.type = type;
        this.name = name;
        this.number = number;
        this.string = string;
    }
}
