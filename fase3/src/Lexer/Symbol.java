package Lexer;

public enum Symbol {
    AND("and"),
    ASSIGN("="),
    BOOLEAN("boolean"),
    BREAK("break"),
    COLON(":"),
    COMMA(","),
    DEF("def"),
    DIV("/"),
    ELIF("elif"),
    ELSE("else"),
    END("end"),
    EOF("eof"),      
    EQ("=="),
    FALSE("False"),
    FLOAT("float"),
    FOR("for"),
    GE(">="),
    GT(">"),
    IDENT("Identifier"),
    IF("if"),
    IN("in"),
    INRANGE("inrange"),
    INT("int"),
    LE("<="),
    LEFTBRACE("{"),
    LEFTBRACKET("["),
    LEFTPAR("("),
    LT("<"),
    MINUS("-"),
    MULT("*"),
    NEQ("<>"),
    NOT("not"),
    NUMBER("Number"),
    OR("or"),
    PLUS("+"),
    POW("^"),
    PRINT("print"),
    PROGRAM("program"),
    RETURN("return"),
    RIGHTBRACE("}"),
    RIGHTBRACKET("]"),
    RIGHTPAR(")"),
    SEMICOLON(";"),
    STRINGLIT("String literal"),
    STRING("string"),
    TRUE("True"),
    VOID("void"),
    WHILE("while");

    Symbol(String name) {
        this.name = name;
    }

    private final String name;
}