package Lexer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public enum Group {
    TYPE("Tipo"),
    
    SIMPLE_STMT("Statement simples"),
    COMPOUND_STMT("Statement composto"),
    STMT("Statement"),
    OR_TEST("Teste OR"),
    COMP_OP("Operador de comparação"),
    TERM_OP("Operador de termo"),
    FACTOR_OP("Operador de fator"),
    SIGNAL("Sinal"),
    DETAILS("Detalhes de átomo"),
    OR_LIST("Lista de OR");

    public static final HashMap<Group, Symbol[]> groups = new HashMap<>(); 
    
    private static Symbol[] concat(Symbol[] a, Symbol[] b) {
        ArrayList<Symbol> result = new ArrayList<>();
        result.addAll(Arrays.asList(a));
        result.addAll(Arrays.asList(b));
        return result.toArray(new Symbol[a.length + b.length]);
    }
    
    static {
        groups.put(TYPE, new Symbol[] {Symbol.INT, Symbol.FLOAT, Symbol.STRING,
            Symbol.BOOLEAN, Symbol.VOID});
        groups.put(SIMPLE_STMT, new Symbol[] {Symbol.IDENT, Symbol.PRINT,
            Symbol.BREAK, Symbol.RETURN});
        groups.put(COMPOUND_STMT, new Symbol[] {Symbol.IF, Symbol.WHILE,
            Symbol.FOR});
        groups.put(STMT, concat(groups.get(SIMPLE_STMT),
                groups.get(COMPOUND_STMT)));
    }
    
    Group(String name) {
        this.name = name;
    }

    private final String name;
}