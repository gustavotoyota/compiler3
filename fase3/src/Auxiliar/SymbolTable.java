package Auxiliar;

import java.util.HashMap;

public class SymbolTable {
    public static HashMap<String, HashMap<String, Variable>> globalTable; 
    public static HashMap<String, String> returnTypeTable;
    public static HashMap<String, Variable> localTable; 
}
