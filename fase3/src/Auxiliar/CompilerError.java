package Auxiliar;

import Lexer.*;

public class CompilerError {
    public CompilerError(Lexer lexer, String fileName) {       
        this.lexer = lexer;           
        this.fileName = fileName;
    }
    
    public void signal(String strMessage) {
        System.out.println(this.fileName + ", " + lexer.getLineNumber() + ", \"" + strMessage + "\"");                
        System.exit(0);
    }
    
    private final Lexer lexer;
    private final String fileName;

}
