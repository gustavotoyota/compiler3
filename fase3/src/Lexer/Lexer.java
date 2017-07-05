package Lexer;

import java.util.HashMap;

import Auxiliar.CompilerError;
import java.util.Arrays;

public class Lexer {
    private static final HashMap<String, Symbol> keywordsTable;

    static {
        keywordsTable = new HashMap<>();
        keywordsTable.put("and", Symbol.AND );
        keywordsTable.put("boolean", Symbol.BOOLEAN);
        keywordsTable.put("break", Symbol.BREAK);
        keywordsTable.put("elif", Symbol.ELIF);
        keywordsTable.put("else", Symbol.ELSE);
        keywordsTable.put("end", Symbol.END);
        keywordsTable.put("False", Symbol.FALSE);
        keywordsTable.put("float", Symbol.FLOAT);
        keywordsTable.put("for", Symbol.FOR);
        keywordsTable.put("if", Symbol.IF);
        keywordsTable.put("in", Symbol.IN);
        keywordsTable.put("inrange", Symbol.INRANGE);
        keywordsTable.put("int", Symbol.INT);
        keywordsTable.put("not", Symbol.NOT);
        keywordsTable.put("or", Symbol.OR);
        keywordsTable.put("print", Symbol.PRINT);
        keywordsTable.put("program", Symbol.PROGRAM);
        keywordsTable.put("string", Symbol.STRING);
        keywordsTable.put("True", Symbol.TRUE);
        keywordsTable.put("while", Symbol.WHILE);
        keywordsTable.put("def", Symbol.DEF);
    }
    
    public Lexer(char[] input, CompilerError error) {
        this.input = input;
        
        input[input.length - 1] = '\0';
        
        tokenPos = 0;
        lineNumber = 1;
        
        this.error = error;
    }
    
    public void nextToken() {
        // Pular espaços em branco
        while(input[tokenPos] == ' ' || input[tokenPos] == '\t' || input[tokenPos] == '\r' || input[tokenPos] == '\n'){
            if (input[tokenPos] == '\n')
                lineNumber++;
            tokenPos++;
        }
        
        // Pular comentários
        if (input[tokenPos] == '#') {
            while (input[tokenPos] != '\0' && input[tokenPos] != '\n')
                tokenPos++;
            nextToken();
            return;
        }
        
        // Descobrir o token
        if (input[tokenPos] == '\0')
            token = Symbol.EOF;
        else if (Character.isLetter(input[tokenPos])) {      
            stringValue = "";
            while (Character.isLetter(input[tokenPos]) || Character.isDigit(input[tokenPos]))
                stringValue += Character.toString(input[tokenPos++]);
            Symbol value = keywordsTable.get(stringValue);
            if (value == null)
                token = Symbol.IDENT;
            else 
                token = value;                
        } else if (Character.isDigit(input[tokenPos])) {                
            String s = "";
            
            while (Character.isDigit(input[tokenPos]) || input[tokenPos] == '.')
                s += Character.toString(input[tokenPos++]);
            
            if (Character.isLetter(input[tokenPos]))
                error.signal("Formato de número inválido");
            
            isIntValue = !s.contains(".");
            try {
                if (isIntValue)
                    intValue = Integer.valueOf(s);
                floatValue = Float.valueOf(s);
            } catch (NumberFormatException e) {
                error.signal("Formato de número inválido");
            }
                    
            token = Symbol.NUMBER;
        } else {
            switch(input[tokenPos]) {
                case '+':
                    token = Symbol.PLUS;
                    break;
                case '-':
                    token = Symbol.MINUS;                        
                    break;
                case '*':
                    token = Symbol.MULT;
                    break;
                case '/':
                    token = Symbol.DIV;
                    break;
                case '^':
                    token = Symbol.POW;
                    break;
                case '<':
                    if (input[tokenPos + 1] == '='){
                        token = Symbol.LE;
                        tokenPos++;
                    } else if (input[tokenPos + 1] == '>') {
                        token = Symbol.NEQ;
                        tokenPos++;
                    } else token = Symbol.LT;                                                
                    break;
                case '>':
                    if (input[tokenPos + 1] == '='){
                        token = Symbol.GE;
                        tokenPos++;
                    } else token = Symbol.GT;
                    break;                    
                case '=':
                    if (input[tokenPos +1] == '=') {
                        token = Symbol.EQ;
                        tokenPos++;
                    } else token = Symbol.ASSIGN;
                    break;
                case '(':
                    token = Symbol.LEFTPAR;
                    break;
                case ')':
                    token = Symbol.RIGHTPAR;
                    break;
                case '{':
                    token = Symbol.LEFTBRACE;
                    break;
                case '}':
                    token = Symbol.RIGHTBRACE;
                    break;
                case '[':
                    token = Symbol.LEFTBRACKET;
                    break;
                case ']':
                    token = Symbol.RIGHTBRACKET;
                    break;
                case ';':
                    token = Symbol.SEMICOLON;
                    break;
                case ',':
                    token = Symbol.COMMA;
                    break;
                case ':':
                    token = Symbol.COLON;
                    break;                    
                case '\'':
                    stringValue = "";
                    token = Symbol.STRINGLIT;
                    ++tokenPos;
                    while (input[tokenPos] != '\'' && input[tokenPos] != '\0') {
                        stringValue += input[tokenPos];
                        ++tokenPos;
                    }
                    if (input[tokenPos] == '\0')
                        error.signal("Faltando aspas simples");
                    break;
                default:
                    error.signal("Uso de caractere inválido");
            }
            tokenPos++;
        }
    }
    
    public boolean check(Symbol symbol) {
        return token == symbol;
    }
    public boolean accept(Symbol symbol) {
        if (!check(symbol))
            return false;
        nextToken();
        return true;
    }
    public boolean confirm(Symbol symbol) {
        if (!check(symbol)) {
            error.signal("Símbolo" + symbol + " esperado");
            return false;
        }
        return true;
    }
    public boolean expect(Symbol symbol) {
        if (!accept(symbol)) {            
            error.signal("Símbolo " + symbol + " esperado");
            return false;
        }
        return true;
    }
    public String obtain(Symbol symbol) {
        String aux = stringValue;
        expect(symbol);
        return aux;
    }
    
    public boolean check(Group group) {
        return Arrays.asList(Group.groups.get(group)).contains(token);
    }
    public boolean accept(Group group) {
        if (!check(group))
            return false;
        nextToken();
        return true;
    }
    public boolean confirm(Group group) {
        if (!check(group)) {
            error.signal("Símbolo" + group + " esperado");
            return false;
        }
        return true;
    }
    public boolean expect(Group group) {
        if (!accept(group)) {            
            error.signal("Símbolo " + group + " esperado");
            return false;
        }
        return true;
    }
    public String obtain(Group group) {
        String aux = stringValue;
        expect(group);
        return aux;
    }
    
    public Symbol getToken() {
        return token;
    }
    public int getLineNumber() {
        return lineNumber;
    }
    
    public boolean checkIntValue() {
        return isIntValue;
    }
    public String getStringValue() {
        return stringValue;
    }
    public Integer getIntValue() {
        return intValue;
    }
    public Float getFloatValue() {
        return floatValue;
    }
    
    private final char[] input;
    private final CompilerError error;   
    
    private int  tokenPos;
    private int lineNumber;
    private String lineText;
    
    private Symbol token;
    private boolean isIntValue;
    private String stringValue;
    private int intValue;
    private float floatValue;
}
