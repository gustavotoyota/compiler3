import AST.Num;
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
    private int loopCount = 0;
    
    public Program compile(char []input, String fileName) {
        SymbolTable.globalTable = new HashMap<>();   
        SymbolTable.returnTypeTable = new HashMap<>();
        SymbolTable.paramsCountTable = new HashMap<>();
        SymbolTable.localTable = new HashMap<>();
        error = new CompilerError(fileName);
        lexer = new Lexer(input, error);
        error.setLexer(lexer);
        
        lexer.nextToken();
        
        return parseProgram();
    }
    
    private Lexer lexer;
    private CompilerError error;
    
    private Program parseProgram() {
        lexer.expect(Symbol.PROGRAM);
        
        String name = lexer.obtain(Symbol.IDENT);
        
        lexer.expect(Symbol.COLON);
        
        ArrayList<FuncDef> funcDefs = new ArrayList<>();
        do {
            funcDefs.add(parseFuncDef());
        } while (lexer.check(Symbol.DEF));
        
        /* ERRO SEMANTICO 28
        !!!!!!!!!!!!!!!!!
        !!!!!!!!!!!!!!!!!
        */
        if (!SymbolTable.globalTable.containsKey("main"))
            error.signal("Função main ausente");
        
        lexer.expect(Symbol.END);
        
        return new Program(name, funcDefs);
    }
    
    private FuncDef parseFuncDef() {
        lexer.expect(Symbol.DEF);
        
        String name = lexer.obtain(Symbol.IDENT);
        
        /* ERRO SEMANTICO 22
        !!!!!!!!!!!!!!!!!!
        !!!!!!!!!!!!!!!!!!
        */
        if (SymbolTable.globalTable.containsKey(name))
            error.signal("Função " + name + " já declarada");               
        
        lexer.expect(Symbol.LEFTPAR);
        
        ArgsList argsList = null;
        if (lexer.check(Group.TYPE))
            argsList = parseArgsList();
        
        lexer.expect(Symbol.RIGHTPAR);
        lexer.expect(Symbol.COLON);
        
        String type = lexer.obtain(Group.TYPE);
        
        SymbolTable.returnTypeTable.put(name, type);
        
        /* ERRO SEMANTICO 31
        !!!!!!!!!!!!!!!!!!!
        !!!!!!!!!!!!!!!!!!
        */
        if ("main".equals(name) && !"void".equals(type))
            error.signal("Função main deve ser do tipo void");
        
        lexer.expect(Symbol.LEFTBRACE);
        
        Body body = parseBody();                
        
        lexer.expect(Symbol.RIGHTBRACE);
                
        SymbolTable.globalTable.put(name, (HashMap)SymbolTable.localTable.clone()); 
        SymbolTable.paramsCountTable.put(name, argsList == null ? 0 : argsList.arguments.size());        
        SymbolTable.localTable.clear();
        
        return new FuncDef(name, argsList, type, body);
    }
    private ArgsList parseArgsList() {
        ArrayList<Argument> arguments = new ArrayList<>();
        
        do {
            String type = lexer.obtain(Group.TYPE);
            NameArray nameArray = parseNameArray();       
            SymbolTable.localTable.put(nameArray.name, new Variable(type, nameArray.length));
            arguments.add(new Argument(type, nameArray));
        } while (lexer.accept(Symbol.COMMA));
        
        return new ArgsList(arguments);
    }
    private NameArray parseNameArray() {
        String name = lexer.obtain(Symbol.IDENT);
        
        Num length = null;
        if (lexer.accept(Symbol.LEFTBRACKET)) {
            length = parseNum(true);
            
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
            String type = lexer.obtain(Group.TYPE);
            
            IdList idList = parseIdList();
            if (idList != null && idList.nameArrays != null) {
                for (int i = 0; i < idList.nameArrays.size(); ++i) {
                    /* ERRO SEMANTICO 27
                    !!!!!!!!!!!!!!!!!!!!!!
                    !!!!!!!!!!!!!!!!!!!!!!
                    */
                    if (SymbolTable.returnTypeTable.containsKey(idList.nameArrays.get(i).name) &&
                            !SymbolTable.globalTable.containsKey(idList.nameArrays.get(i).name))
                        error.signal("Variável " + idList.nameArrays.get(i).name + " com mesmo nome de função");
                    if (SymbolTable.localTable.put(idList.nameArrays.get(i).name, new Variable(type, idList.nameArrays.get(i).length)) != null)                            
                        error.signal("Variável " + idList.nameArrays.get(i).name + " já declarada");
                }
            }
            declGroups.add(new DeclGroup(type, idList));
            
            lexer.expect(Symbol.SEMICOLON);
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
            String name = lexer.obtain(Symbol.IDENT);
            /* ERROSEMANTICO1
            !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
            !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
            */            
            if (lexer.check(Symbol.LEFTPAR)) {                            
                if (!SymbolTable.globalTable.containsKey(name))
                    error.signal("Chamada de função não existente");
                return parseFuncStmt(name);
            }
            else {
                if (!SymbolTable.localTable.containsKey(name))
                    error.signal("Uso de variável não declarada");
                return parseExprStmt(name);
            }
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
        
        lexer.expect(Symbol.ASSIGN);         
                       
        Boolean isList;
        AST.OrTest value = null;
        AST.OrList list = null;
        if (isList = lexer.accept(Symbol.LEFTBRACKET)) {
            list = parseOrList();
            lexer.expect(Symbol.RIGHTBRACKET);
             /* ERRO SEMANTICO 13
            !!!!!!!!!!!!!!!!!!!!!!!!
            !!!!!!!!!!!!!!!!!!!!!!!!
            */
            if (list.orTests.size() != SymbolTable.localTable.get(name).length.intValue)
                error.signal("Array com excesso de elementos");
        } else
            value = parseOrTest();
        
        lexer.expect(Symbol.SEMICOLON);
        
        return new ExprStmt(name, index, value, list, isList);
    }
    private SimpleStmt parsePrintStmt() {
        lexer.expect(Symbol.PRINT);
        
        ArrayList<OrTest> values = new ArrayList<>();
        
        do {
            values.add(parseOrTest());
        } while (lexer.accept(Symbol.COMMA));
        
        lexer.expect(Symbol.SEMICOLON);
        
        return new PrintStmt(values);
    }
    private SimpleStmt parseBreakStmt() {
        lexer.expect(Symbol.BREAK);
        
        if (loopCount <= 0)
            error.signal("Uso de break fora de loop");
        
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
        
        OrList param = null;
        if (lexer.check(Group.OR_LIST))
            param = parseOrList();
        
        /* ERRO SEMANTICO 30
        !!!!!!!!!!!!!!!!
        !!!!!!!!!!!!!!!!
        */
        if ((param == null ? 0 : param.orTests.size()) != SymbolTable.paramsCountTable.get(name))
            error.signal("Chamada de função com número incorreto de parâmetros");
        
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
        
        loopCount++;
        
        OrTest cond = parseOrTest();
        
        lexer.expect(Symbol.LEFTBRACE);
        
        ArrayList<Stmt> stmts = new ArrayList<>();
        while (lexer.check(Group.STMT))
            stmts.add(parseStmt());
        
        lexer.expect(Symbol.RIGHTBRACE);
        
        loopCount--;
        
        return new WhileStmt(cond, stmts);
    }
    private CompoundStmt parseForStmt() {
        lexer.expect(Symbol.FOR);
        
        loopCount++;
        
        String iter = lexer.obtain(Symbol.IDENT);
        
        lexer.expect(Symbol.INRANGE);
        
        lexer.expect(Symbol.LEFTPAR);
        
        Num begin = parseNum(true);
        
        lexer.expect(Symbol.COMMA);
        
        Num end = parseNum(true);
        
        lexer.expect(Symbol.RIGHTPAR);
        
        lexer.expect(Symbol.LEFTBRACE);
        
        ArrayList<Stmt> stmts = new ArrayList<>();
        while (lexer.check(Group.STMT))
            stmts.add(parseStmt());
        
        lexer.expect(Symbol.RIGHTBRACE);
        
        loopCount--;
        
        return new ForStmt(iter, begin, end, stmts);
    }
    private Atom parseAtom() {
        Symbol type = lexer.getToken();        
        String name = "";        
        Num number = null;
        String string = "";
        
        switch (type) {
            case IDENT:
                name = lexer.obtain(Symbol.IDENT);           
                break;
            case NUMBER:
                number = parseNum(false);
                break;
            case STRINGLIT:
                string = lexer.obtain(Symbol.STRINGLIT);
                break;
            case TRUE:
                lexer.nextToken();
                break;
            case FALSE: 
                lexer.nextToken();
                break;
            default:
                /* ERRO SINTATICO 12
                !!!!!!!!!!!!!
                !!!!!!!!!!!!!
                */
                error.signal("Valor faltando");
                break;
        }
        
        return new Atom(type, name, number, string);
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
            if (loop) {
                String compOp = "";
                switch(lexer.getToken()) {
                    case LT:
                        compOp = "<";
                        break;
                    case LE:
                        compOp = "<=";
                        break;
                    case EQ:
                        compOp = "==";
                        break;
                    case GT:
                        compOp = ">";
                        break;
                    case GE:
                        compOp = ">=";
                        break;
                    case NEQ:
                        compOp = "!=";
                        break;
                    default:
                        break;
                }
                opers.add(compOp);
                lexer.nextToken();
            }
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
            if (loop) {
                opers.add(lexer.getToken() == Symbol.PLUS ? "+" : "-");
                lexer.nextToken();
            }
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
            if (loop) {
                opers.add(lexer.getToken() == Symbol.MULT ? "*" : "/");
                lexer.nextToken();
            }
        } while (loop);
        
        return new Term(factors, opers);
    }
    private Factor parseFactor() {
        String signal = "";
        if (lexer.check(Group.SIGNAL))
            signal = lexer.obtain(Group.SIGNAL);
        
        AtomExpr atomExpr = parseAtomExpr();
        
        Factor exponent = null;
        if (lexer.accept(Symbol.POW))
            exponent = parseFactor();
        
        return new Factor(signal, atomExpr, exponent);
    }
    private AtomExpr parseAtomExpr() {
        Atom atom = parseAtom();
        Details details = null;
        if (lexer.check(Group.DETAILS)) {         
            details = parseDetails();
            /* ERRO SEMANTICO 14
            !!!!!!!!!!!!!!!!!!!
            !!!!!!!!!!!!!!!!!!!
            */
            if (details.isInt && (details.numIndex.intValue < 0 ||
                    details.numIndex.intValue >= SymbolTable.localTable.get(atom.name).length.intValue))
                error.signal("Acesso de posição inválida no array");
            else if (details.isFunc && (details.orList.orTests.size() != SymbolTable.paramsCountTable.get(atom.name)))
                error.signal("Chamada de função com parâmetros incorretos");
        }
        
        return new AtomExpr(atom, details);
    }
    private Details parseDetails() {
        boolean isFunc = false;
        boolean isInt = false;
        OrList orList = null;
        Num numIndex = null;
        String identIndex = "";
        if (lexer.accept(Symbol.LEFTBRACKET)) {
            isInt = lexer.checkIntValue();
            if (isInt)
                numIndex = parseNum(true);
            else
                identIndex = lexer.obtain(Symbol.IDENT);
            lexer.accept(Symbol.RIGHTBRACKET);
        } else if (lexer.accept(Symbol.LEFTPAR)) {
            isFunc = true;
            if (lexer.check(Group.OR_LIST))
                orList = parseOrList();
            lexer.accept(Symbol.RIGHTPAR);            
        } else
            error.signal("Detalhes de átomo esperados");
        
        return new Details(isFunc, orList, isInt, numIndex, identIndex);
    }
    private Num parseNum(boolean intOnly) {
        boolean isInt = lexer.checkIntValue();
        int intValue = 1;
        float floatValue = 1;
        
        if (intOnly && !isInt)
            error.signal("Inteiro esperado");
        
        if (lexer.accept(Symbol.MINUS)) {
            intValue = -1;
            floatValue = -1;
        }
        
        if (lexer.checkIntValue())
            intValue *= lexer.getIntValue();
        else
            floatValue *= lexer.getFloatValue();
        
        lexer.nextToken();
        
        return new Num(isInt, intValue, floatValue);
    }
}
