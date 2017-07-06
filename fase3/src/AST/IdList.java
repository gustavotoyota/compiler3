package AST;

import Auxiliar.PW;

import java.util.ArrayList;

public class IdList {
    public final ArrayList<NameArray> nameArrays;

    public IdList(ArrayList<NameArray> nameArrays) {
        this.nameArrays = nameArrays;
    }
    
    public void genC(PW pw, String type) {
        for (int i = 0; i < nameArrays.size(); ++i) {
            nameArrays.get(i).genC(pw);
            if ("char".equals(type))
                pw.print("[20]");
            if (i < nameArrays.size()-1)
                pw.print(", ");
        }
    }
}
