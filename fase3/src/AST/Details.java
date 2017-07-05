package AST;

import Auxiliar.PW;

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
    
    public void genC(PW pw) {
        if (isFunc) {
            pw.print("(");
            if (orList != null)
                orList.genC(pw);
            pw.print(")");
        } else {
            pw.print("[");
            if (isInt)
                pw.print(Integer.toString(numIndex.intValue));
            else
                pw.print(identIndex);
            pw.print("]");
        }
    }
}
