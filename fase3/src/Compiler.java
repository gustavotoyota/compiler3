import AST.FuncDef;

import java.util.HashMap;
import java.util.ArrayList;

import Auxiliar.*;
import Lexer.*;
import AST.*;

public class Compiler {
    public Program compile(char []input, String fileName) {
        SymbolTable.symbolTable = new HashMap<>();
        lexer = new Lexer(input, error);
        error = new CompilerError(lexer, fileName);
        
        lexer.nextToken();
        
        return parseProgram();
    }
    
    private Lexer lexer;
    private CompilerError error;
    
    private Program parseProgram() {
        lexer.expect(Symbol.PROGRAM);
        
        String name = lexer.obtainString(Symbol.IDENT);
        
        lexer.expect(Symbol.COLON);
        
        ArrayList<FuncDef> funcDefs = new ArrayList<>();
        do {
            funcDefs.add(parseFuncDef());
        } while (lexer.check(Symbol.DEF));
        
        lexer.expect(Symbol.END);
        
        return new Program(name, funcDefs);
    }
    
    private FuncDef parseFuncDef() {
        lexer.expect(Symbol.DEF);
        
        String name = lexer.obtainString(Symbol.IDENT);
        
        lexer.expect(Symbol.LEFTPAR);
        
        ArgsList argsList = null;
        if (lexer.check(Group.TYPE))
            argsList = parseArgsList();
        
        lexer.expect(Symbol.RIGHTPAR);
        lexer.expect(Symbol.COLON);
        
        String type = lexer.obtainString(Group.TYPE);
        
        lexer.expect(Symbol.LEFTBRACE);
        
        Body body = parseBody();
        
        lexer.expect(Symbol.RIGHTBRACE);
        
        return new FuncDef();
    }
    
    private ArgsList parseArgsList() {
        ArrayList<Argument> arguments = new ArrayList<>();
        
        do {
            String type = lexer.obtainString(Group.TYPE);
            NameArray nameArray = parseNameArray();
            
            arguments.add(new Argument(type, nameArray));
        } while (lexer.accept(Symbol.COMMA));
        
        return new ArgsList(arguments);
    }
    
    private NameArray parseNameArray() {
        String name = lexer.obtainString(Symbol.IDENT);
        
        Integer length = 0;
        if (lexer.accept(Symbol.LEFTBRACKET)) {
            length = lexer.obtainInt();
            
            lexer.expect(Symbol.RIGHTBRACKET);
        }
        
        return new NameArray(name, length);
    }
    
    private Body parseBody() {
        Declaration declaration = null;
        if (lexer.check(Group.TYPE))
            declaration = parseDeclaration();
        
        ArrayList<AST.Stmt> stmts = new ArrayList<>();
        while (lexer.check(Group.STMT))
            stmts.add(parseStmt());
        
        return new Body(declaration, stmts);
    }

    private Declaration parseDeclaration() {
        ArrayList<AST.DeclGroup> declGroups = new ArrayList<>();
        
        do {
            String type = lexer.obtainString(Group.TYPE);
            
            NameArray nameArray = parseNameArray();
            
            declGroups.add(new DeclGroup(type, nameArray));
        } while (lexer.check(Group.TYPE));
        
        return new Declaration(declGroups);
    }

    private Stmt parseStmt() {
        if (lexer.check(Group.SIMPLE_STMT))
            return parseSimpleStmt();
        else if (lexer.check(Group.COMPOUND_STMT))
            return parseCompoundStmt();
        error.signal("Statement esperado");
        return null;
    }

    private SimpleStmt parseSimpleStmt() {
        if (lexer.check(Symbol.IDENT)) {
            String name = lexer.obtainString(Symbol.IDENT);
            if (lexer.check(Symbol.LEFTPAR))
                return parseFuncStmt(name);
            else
                return parseExprStmt(name);
        } else if (lexer.check(Symbol.PRINT))
            return parsePrintStmt();
        else if (lexer.check(Symbol.BREAK))
            return parseBreakStmt();
        else if (lexer.check(Symbol.RETURN))
            return parseReturnStmt();
        error.signal("Statement simples esperado");
        return null;
    }

    private CompoundStmt parseCompoundStmt() {
        if (lexer.check(Symbol.IF))
            return parseIfStmt();
        else if (lexer.check(Symbol.WHILE))
            return parseWhileStmt();
        else if (lexer.check(Symbol.FOR))
            return parseForStmt();
        error.signal("Statement compost esperado");
        return null;
    }

    private SimpleStmt parseExprStmt(String name) {
        Atom index = null;
        if (lexer.accept(Symbol.LEFTBRACKET)) {
            index = parseAtom();
            lexer.expect(Symbol.RIGHTBRACKET);
        }
        
        lexer.expect(Symbol.EQ);
        
        Boolean isValue;
        AST.OrTest value = null;
        AST.OrList list = null;
        if (isValue = lexer.accept(Symbol.LEFTBRACKET)) {
            list = parseOrList();
            lexer.expect(Symbol.RIGHTBRACKET);
        } else
            value = parseOrTest();
        
        return new ExprStmt(name, value, list, isValue);
    }

    private SimpleStmt parsePrintStmt() {
        lexer.expect(Symbol.PRINT);
        
        ArrayList<OrTest> values = new ArrayList<>();
        
        do {
            values.add(parseOrTest());
        } while (lexer.accept(Symbol.COMMA));
        
        return new PrintStmt(values);
    }

    private SimpleStmt parseBreakStmt() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private SimpleStmt parseReturnStmt() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private SimpleStmt parseFuncStmt(String name) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private CompoundStmt parseIfStmt() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private CompoundStmt parseWhileStmt() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private CompoundStmt parseForStmt() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private Atom parseAtom() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private OrList parseOrList() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private OrTest parseOrTest() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
