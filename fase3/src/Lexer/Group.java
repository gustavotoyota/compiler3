package Lexer;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public enum Group {
    TYPE("Tipo"),
    
    SIMPLE_STMT("Statement simples"),
    COMPOUND_STMT("Statement composto"),
    STMT("Statement");

    public static final HashMap<Group, Symbol[]> groups = new HashMap<>(); 
    
    public static Symbol[] concat(Symbol[] a, Symbol[] b) {
        List<Symbol> result = Arrays.asList(a);
        result.addAll(Arrays.asList(b));
        return (Symbol [])result.toArray();
    }
    
    static {
        groups.put(TYPE, new Symbol[] {Symbol.INT, Symbol.FLOAT,
            Symbol.STRING, Symbol.BOOLEAN, Symbol.VOID});
        groups.put(STMT, concat(groups.get(SIMPLE_STMT), groups.get(COMPOUND_STMT)));
    }
    
    Group(String name) {
        this.name = name;
    }

    private final String name;
}