package AST;

import java.util.ArrayList;

import Auxiliar.PW;

public class Program {
    public final String name;
    public final ArrayList<FuncDef> funcDefs;
    
    public Program(String name, ArrayList<FuncDef> funcDefs) {
        this.name = name;
        this.funcDefs = funcDefs;
    }
    
    public void genC(PW pw){
        pw.println("#include <stdio.h>");
        pw.println("#include <string.h>");
        pw.println("#include <math.h>");
        pw.println("");
        
        for (FuncDef funcDef: funcDefs)
            funcDef.genC();
    }
}
