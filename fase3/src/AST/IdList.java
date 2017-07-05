package AST;

import Auxiliar.PW;

import java.util.ArrayList;

public class IdList {
    public final ArrayList<NameArray> nameArrays;

    public IdList(ArrayList<NameArray> nameArrays) {
        this.nameArrays = nameArrays;
    }
    
    public void genC(PW pw) {
        for (int i = 0; i < nameArrays.size(); ++i) {
            nameArrays.get(i).genC(pw);
            if (i < nameArrays.size()-1)
                pw.print(", ");
        }
    }
}
