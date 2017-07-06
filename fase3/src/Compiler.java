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
    private String funcName = "";
    private int numArgs = 0;
    
    public Program compile(char []input, String fileName) {
        SymbolTable.globalTable = new HashMap<>();   
        SymbolTable.returnTypeTable = new HashMap<>();
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
        
        lexer.expect(Symbol.END);
        
        return new Program(name, funcDefs);
    }
    private FuncDef parseFuncDef() {
        lexer.expect(Symbol.DEF);
        
        String name = lexer.obtain(Symbol.IDENT);
        funcName = name;
        
        lexer.expect(Symbol.LEFTPAR);
        
        ArgsList argsList = null;
        if (lexer.check(Group.TYPE)) {
            argsList = parseArgsList();
            numArgs = argsList.arguments.size();
        }
        
        lexer.expect(Symbol.RIGHTPAR);
        lexer.expect(Symbol.COLON);
        
        String type = lexer.obtain(Group.TYPE);
        SymbolTable.returnTypeTable.put(name, type);
        
        lexer.expect(Symbol.LEFTBRACE);
        
        Body body = parseBody();                
        
        lexer.expect(Symbol.RIGHTBRACE);
                
        SymbolTable.globalTable.put(name, (HashMap)SymbolTable.localTable.clone());
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
                for (int i = 0; i < idList.nameArrays.size(); ++i)
                    if (SymbolTable.localTable.put(idList.nameArrays.get(i).name, new Variable(type, idList.nameArrays.get(i).length)) != null)
                        error.signal("Variável " + idList.nameArrays.get(i).name + " já declarada");
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
        String varType = SymbolTable.localTable.get(name).getType();
        
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
            
            String valType = getType(list);
            if (!varType.equals(valType))
                error.signal("Atribuição com tipo incorreto");
        } else {
            value = parseOrTest();
            
            String valType = getType(value);
            if (!varType.equals(valType))
                error.signal("Atribuição com tipo incorreto");
        }
        
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
        
        String retType = SymbolTable.returnTypeTable.get(funcName);
        
        if (value != null && !retType.equals(getType(value)))
            error.signal("Retorno de tipo incorreto");
        
        lexer.expect(Symbol.SEMICOLON);                                
        
        return new ReturnStmt(value);
    }
    private SimpleStmt parseFuncStmt(String name) {
        lexer.expect(Symbol.LEFTPAR);
        
        OrList param = null;
        if (lexer.check(Group.OR_LIST))
            param = parseOrList();
        
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
            default:
                lexer.nextToken();
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
        
        andTests.add(parseAndTest());
        if (lexer.check(Symbol.OR) && !"boolean".equals(getType(andTests.get(0))))
            error.signal("OR com valor não-booleano");
        while (lexer.accept(Symbol.OR)) {
            andTests.add(parseAndTest());
            if (!"boolean".equals(getType(andTests.get(andTests.size() - 1))))
                error.signal("OR com valor não-booleano");
        }
        
        return new OrTest(andTests);
    }
    private AndTest parseAndTest() {
        ArrayList<NotTest> notTests = new ArrayList<>();
        
        notTests.add(parseNotTest());
        if (lexer.check(Symbol.AND) && !"boolean".equals(getType(notTests.get(0))))
            error.signal("OR com valor não-booleano");
        while (lexer.accept(Symbol.OR)) {
            notTests.add(parseNotTest());
            if (!"boolean".equals(getType(notTests.get(notTests.size() - 1))))
                error.signal("OR com valor não-booleano");
        }
        
        return new AndTest(notTests);
    }
    private NotTest parseNotTest() {
        boolean not = lexer.accept(Symbol.NOT);
        
        Comp comp = parseComparison();
        
        if (not && !"boolean".equals(getType(comp)))
            error.signal("NOT com valor não-booleano");
        
        return new NotTest(not, comp);
    }
    private Comp parseComparison() {
        ArrayList<Expr> exprs = new ArrayList<>();
        ArrayList<String> opers = new ArrayList<>();
        
        exprs.add(parseExpr());
        String type1 = getType(exprs.get(0));
        while (lexer.check(Group.COMP_OP)) {
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
            
            exprs.add(parseExpr());
            String type2 = getType(exprs.get(exprs.size() - 1));
            if (!type1.equals(type2))
                error.signal("Comparação de tipos diferentes");
            type1 = type2;
        }
        
        return new Comp(exprs, opers);
    }
    private Expr parseExpr() {
        ArrayList<Term> terms = new ArrayList<>();
        ArrayList<String> opers = new ArrayList<>();
        
        terms.add(parseTerm());
        if (lexer.check(Group.TERM_OP) &&
            !"int".equals(getType(terms.get(0))) &&
            !"float".equals(getType(terms.get(0))))
            error.signal("Termo com valor não-númerico");
        while (lexer.check(Group.TERM_OP)) {
            opers.add(lexer.getToken() == Symbol.PLUS ? "+" : "-");
            lexer.nextToken();
            
            if (!"int".equals(getType(terms.get(terms.size() - 1))) &&
                !"float".equals(getType(terms.get(terms.size() - 1))))
                error.signal("Termo com valor não-númerico");
            terms.add(parseTerm());
        }
                return new Expr(terms, opers);
    }
    private Term parseTerm() {
        ArrayList<Factor> factors = new ArrayList<>();
        ArrayList<String> opers = new ArrayList<>();
        
        factors.add(parseFactor());
        if (lexer.check(Group.FACTOR_OP) &&
            !"int".equals(getType(factors.get(0))) &&
            !"float".equals(getType(factors.get(0))))
            error.signal("Fator com valor não-númerico");
        while (lexer.check(Group.FACTOR_OP)) {
            opers.add(lexer.getToken() == Symbol.MULT ? "*" : "/");
            lexer.nextToken();
            
            if (!"int".equals(getType(factors.get(factors.size() - 1))) &&
                !"float".equals(getType(factors.get(factors.size() - 1))))
                error.signal("Fator com valor não-númerico");
            factors.add(parseFactor());
        }
        
        return new Term(factors, opers);
    }
    private Factor parseFactor() {
        String signal = "";
        if (lexer.check(Group.SIGNAL))
            signal = lexer.obtain(Group.SIGNAL);
        
        AtomExpr atomExpr = parseAtomExpr();
        
        String type = getType(atomExpr);
        
        if (!"".equals(signal) && !"int".equals(type) && !"float".equals(type))
            error.signal("Sinal em valor não-numérico");
        
        Factor exponent = null;
        if (lexer.accept(Symbol.POW))
            exponent = parseFactor();
        
        if (exponent != null && !"int".equals(type) && !"float".equals(type))
            error.signal("Exponente em valor não-numérico");
        
        return new Factor(signal, atomExpr, exponent);
    }
    private AtomExpr parseAtomExpr() {
        Atom atom = parseAtom();
        
        Details details = null;
        if (lexer.check(Group.DETAILS)) {
            details = parseDetails();
            
            if (atom.type != Symbol.IDENT)
                error.signal("Índice em não-identificador");
            
            // Não função
            if (SymbolTable.localTable.containsKey(atom.name)) {
                Num length = SymbolTable.localTable.get(atom.name).length;

                if (length == null || length.intValue <= 0)
                    error.signal("Índice em não-array");

                if (!details.isFunc && details.isInt &&
                    details.numIndex.intValue > length.intValue)
                    error.signal("Índice inválido no array");
            }
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
    
    private String getType(OrList orList) {
        return getType(orList.orTests.get(0)) + "[" + orList.orTests.size() + "]";
    }
    private String getType(OrTest orTest) {
        return getType(orTest.andTests.get(0));
    }
    private String getType(AndTest andTest) {
        return getType(andTest.notTests.get(0));
    }
    private String getType(NotTest notTest) {
        return getType(notTest.comp);
    }
    private String getType(Comp comp) {
        return getType(comp.exprs.get(0));
    }
    private String getType(Expr expr) {
        return getType(expr.terms.get(0));
    }
    private String getType(Term term) {
        return getType(term.factors.get(0));
    }
    private String getType(Factor factor) {
        return getType(factor.atomExpr);
    }
    private String getType(AtomExpr atomExpr) {
        return getType(atomExpr.atom);
    }
    private String getType(Atom atom) {
        switch (atom.type) {
        case IDENT:
            if (SymbolTable.localTable.containsKey(atom.name)) {
                Variable var = SymbolTable.localTable.get(atom.name);
                String result = var.type;
                if (var.length != null && var.length.isInt && var.length.intValue > 0)
                    result += "[" + var.length.intValue + "]";
                return result;
            }
            return SymbolTable.returnTypeTable.get(atom.name);
        case NUMBER:
            if (atom.number.isInt)
                return "int";
            else
                return "float";
        case STRINGLIT:
            return "string";
        case TRUE:
            return "boolean";
        case FALSE:
            return "boolean";
        }
        error.signal("Tipo inválido de átomo");
        return null;
    }
}
