import AST.IdList;
import AST.Details;
import AST.AtomExpr;
import AST.Factor;
import AST.Term;
import AST.Expr;
import AST.Comp;
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
        
        return new FuncDef(name, argsList, type, body);
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
            
            IdList idList = parseIdList();
            
            declGroups.add(new DeclGroup(type, idList));
        } while (lexer.check(Group.TYPE));
        
        return new Declaration(declGroups);
    }
    private IdList parseIdList() {
        ArrayList<NameArray> nameArrays = new ArrayList<>();
        
        do {
            nameArrays.add(parseNameArray());
        } while (lexer.accept(Symbol.COMMA));
        
        return new IdList(nameArrays);
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
        
        lexer.expect(Symbol.SEMICOLON);
        
        return new ExprStmt(name, index, value, list, isValue);
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
        lexer.expect(Symbol.BREAK);
        lexer.expect(Symbol.SEMICOLON);
        
        return new BreakStmt();
    }
    private SimpleStmt parseReturnStmt() {
        lexer.expect(Symbol.RETURN);
        
        OrTest value = null;
        if (lexer.check(Group.OR_TEST))
            value = parseOrTest();
        
        lexer.expect(Symbol.SEMICOLON);
        
        return new ReturnStmt(value);
    }
    private SimpleStmt parseFuncStmt(String name) {
        lexer.expect(Symbol.LEFTPAR);
        
        OrTest param = null;
        if (lexer.check(Group.OR_TEST))
            param = parseOrTest();
        
        lexer.expect(Symbol.RIGHTPAR);
        lexer.expect(Symbol.SEMICOLON);
        
        return new FuncStmt(name, param);
    }
    private CompoundStmt parseIfStmt() {
        lexer.expect(Symbol.IF);
        
        OrTest cond = parseOrTest();
        
        lexer.expect(Symbol.LEFTBRACE);
        
        ArrayList<Stmt> ifStmts = new ArrayList<>();
        while (lexer.check(Group.STMT))
            ifStmts.add(parseStmt());
        
        lexer.expect(Symbol.RIGHTBRACE);
        
        ArrayList<Stmt> elseStmts = new ArrayList<>();
        if (lexer.accept(Symbol.ELSE)) {
            lexer.expect(Symbol.LEFTBRACE);
            
            while (lexer.check(Group.STMT))
                elseStmts.add(parseStmt());

            lexer.expect(Symbol.RIGHTBRACE);
        }
         
        return new IfStmt(cond, ifStmts, elseStmts);
    }
    private CompoundStmt parseWhileStmt() {
        lexer.expect(Symbol.WHILE);
        
        OrTest cond = parseOrTest();
        
        lexer.expect(Symbol.LEFTBRACE);
        
        ArrayList<Stmt> stmts = new ArrayList<>();
        while (lexer.check(Group.STMT))
            stmts.add(parseStmt());
        
        lexer.expect(Symbol.RIGHTBRACE);
        
        return new WhileStmt(cond, stmts);
    }
    private CompoundStmt parseForStmt() {
        lexer.expect(Symbol.FOR);
        
        String iter = lexer.obtainString(Symbol.IDENT);
        
        lexer.expect(Symbol.INRANGE);
        
        lexer.expect(Symbol.LEFTPAR);
        
        int begin = lexer.obtainInt();
        
        lexer.expect(Symbol.COMMA);
        
        int end = lexer.obtainInt();
        
        lexer.expect(Symbol.RIGHTPAR);
        
        lexer.expect(Symbol.LEFTBRACE);
        
        ArrayList<Stmt> stmts = new ArrayList<>();
        while (lexer.check(Group.STMT))
            stmts.add(parseStmt());
        
        lexer.expect(Symbol.RIGHTBRACE);
        
        return new ForStmt(iter, begin, end, stmts);
    }
    private Atom parseAtom() {
        return null;
    }
    private OrList parseOrList() {
        ArrayList<OrTest> orTests = new ArrayList<>();
        
        do {
            orTests.add(parseOrTest());
        } while (lexer.accept(Symbol.COMMA));
        
        return new OrList(orTests);
    }
    private OrTest parseOrTest() {
        ArrayList<AndTest> andTests = new ArrayList<>();
        
        do {
            andTests.add(parseAndTest());
        } while (lexer.accept(Symbol.OR));
        
        return new OrTest(andTests);
    }
    private AndTest parseAndTest() {
        ArrayList<NotTest> notTests = new ArrayList<>();
        
        do {
            notTests.add(parseNotTest());
        } while (lexer.accept(Symbol.AND));
        
        return new AndTest(notTests);
    }
    private NotTest parseNotTest() {
        boolean not = lexer.accept(Symbol.NOT);
        
        Comp comp = parseComparison();
        
        return new NotTest(not, comp);
    }
    private Comp parseComparison() {
        ArrayList<Expr> exprs = new ArrayList<>();
        ArrayList<String> opers = new ArrayList<>();
        
        boolean loop;
        do {
            exprs.add(parseExpr());
            
            loop = lexer.check(Group.COMP_OP);
            opers.add(lexer.obtainString(Group.COMP_OP));
        } while (loop);
        
        return new Comp(exprs, opers);
    }
    private Expr parseExpr() {
        ArrayList<Term> terms = new ArrayList<>();
        ArrayList<String> opers = new ArrayList<>();
        
        boolean loop;
        do {
            terms.add(parseTerm());
            
            loop = lexer.check(Group.TERM_OP);
            opers.add(lexer.obtainString(Group.TERM_OP));
        } while (loop);
        
        return new Expr(terms, opers);
    }
    private Term parseTerm() {
        ArrayList<Factor> factors = new ArrayList<>();
        ArrayList<String> opers = new ArrayList<>();
        
        boolean loop;
        do {
            factors.add(parseFactor());
            
            loop = lexer.check(Group.FACTOR_OP);
            opers.add(lexer.obtainString(Group.FACTOR_OP));
        } while (loop);
        
        return new Term(factors, opers);
    }
    private Factor parseFactor() {
        String signal = "";
        if (lexer.check(Group.SIGNAL))
            signal = lexer.obtainString(Group.SIGNAL);
        
        AtomExpr atomExpr = parseAtomExpr();
        
        Factor exponent = null;
        if (lexer.accept(Symbol.POW))
            exponent = parseFactor();
        
        return new Factor(signal, atomExpr, exponent);
    }
    private AtomExpr parseAtomExpr() {
        Atom atom = parseAtom();
        Details details = null;
        if (lexer.check(Group.DETAILS))
            details = parseDetails();
        
        return new AtomExpr(atom, details);
    }
    private Details parseDetails() {
        boolean isFunc = false;
        boolean isInt = false;
        OrList orList = null;
        int intValue = 0;
        String stringValue = "";
        if (lexer.accept(Symbol.LEFTBRACKET)) {
            isInt = lexer.checkIntValue();
            if (isInt)
                intValue = lexer.obtainInt();
            else
                stringValue = lexer.obtainString(Symbol.IDENT);
        } else if (lexer.accept(Symbol.LEFTPAR)) {
            isFunc = true;
            if (lexer.check(Group.OR_LIST))
                orList = parseOrList();
        } else
            error.signal("Detalhes de átomo esperados");
        
        return new Details(isFunc, orList, isInt, intValue, stringValue);
    }
}
