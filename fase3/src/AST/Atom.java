package AST;

import Auxiliar.PW;
import Lexer.Symbol;

public class Atom {
    public final Symbol type;
    public final String name;
    public final Num number;
    public final String string;

    public Atom(Symbol type, String name, Num num, String string) {
        this.type = type;
        this.name = name;
        this.number = num;
        this.string = string;
    }
    
    public void genC(PW pw) {
        switch (type) {
            case IDENT:
                pw.print(name);
                break;
            case NUMBER:
                if (number.isInt)
                    pw.print(Integer.toString(number.intValue));
                else
                    pw.print(Float.toString(number.floatValue));
                break;
            case STRING:
                pw.print("\"" + string + "\"");
                break;
            case TRUE:
                pw.print("1");
                break;
            case FALSE:
                pw.print("0");
                break;
        }
    }
}
