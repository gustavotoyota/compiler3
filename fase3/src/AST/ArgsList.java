package AST;

import Auxiliar.PW;
import java.util.ArrayList;

public class ArgsList {
    public final ArrayList<Argument> arguments;
    
    public ArgsList(ArrayList<Argument> arguments) {
        this.arguments = arguments;
    }
    
    public void genC(PW pw) {
        for (int i = 0; i < arguments.size(); ++i) {               
            arguments.get(i).genC(pw);
            if (i < arguments.size() - 1)
                pw.print(", ");
        }
    }
}
