package Auxiliar;

import Lexer.*;

public class CompilerError {
    public CompilerError(String fileName) {       
        this.lexer = null;           
        this.fileName = fileName;
    }
    
    public void setLexer(Lexer lexer) {
        this.lexer = lexer;
    }
    
    public void signal(String strMessage) {
        System.out.println(this.fileName + ", " + lexer.getLineNumber() + ", \"" + strMessage + "\"");                
        System.exit(0);
    }
    
    private Lexer lexer;
    private final String fileName;

}
