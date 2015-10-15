package csc426;
import java.util.ArrayList;
import java.util.List;

public class Parser {
	private Wrapper la;

    public Parser(Wrapper lookahead) {
        this.la = lookahead;
    }

    public AST.Program parseProgram() throws ParseError {
        Position position = this.la.current().position;
        this.la.match(TokenType.PROGRAM);
        String name = this.la.match((TokenType)TokenType.ID).lexeme;
        this.la.match(TokenType.SEMI);
        AST.Block block = this.parseBlock();
        this.la.match(TokenType.PERIOD);
        this.la.match(TokenType.EOF);
        return AST.program(name, block, position);
    }

    public AST.Block parseBlock() throws ParseError {
        Position position = this.la.current().position;
        List<AST.ConstDecl> consts = this.parseConstDecls();
        List<AST.VarDecl> vars = this.parseVarDecls();
        List<AST.ProcDecl> procs = this.parseProcDecls();
        this.la.match(TokenType.BEGIN);
        List<AST.Stmt> stmts = this.parseStmts();
        this.la.match(TokenType.END);
        return AST.block(consts, vars, procs, stmts, position);
    }

    public List<AST.ConstDecl> parseConstDecls() throws ParseError {
        ArrayList<AST.ConstDecl> result = new ArrayList<AST.ConstDecl>();
        while (this.la.check(TokenType.CONST)) {
            result.add(this.parseConstDecl());
        }
        return result;
    }

    public AST.ConstDecl parseConstDecl() throws ParseError {
        Position position = this.la.current().position;
        this.la.match(TokenType.CONST);
        String id = this.la.match((TokenType)TokenType.ID).lexeme;
        this.la.match(TokenType.ASSIGN);
        int sign = 1;
        if (this.la.check(TokenType.MINUS)) {
            this.la.skip();
            sign = -1;
        }
        String num = this.la.match((TokenType)TokenType.NUM).lexeme;
        this.la.match(TokenType.SEMI);
        int value = sign * Integer.parseInt(num);
        return AST.constDecl(id, value, position);
    }

    public List<AST.VarDecl> parseVarDecls() throws ParseError {
        ArrayList<AST.VarDecl> result = new ArrayList<AST.VarDecl>();
        while (this.la.check(TokenType.VAR)) {
            result.add(this.parseVarDecl());
        }
        return result;
    }

    public AST.VarDecl parseVarDecl() throws ParseError {
        Position position = this.la.current().position;
        this.la.match(TokenType.VAR);
        String id = this.la.match((TokenType)TokenType.ID).lexeme;
        this.la.match(TokenType.COLON);
        AST.Type type = this.parseType();
        this.la.match(TokenType.SEMI);
        return AST.varDecl(id, type, position);
    }

    public AST.Type parseType() throws ParseError {
        if (this.la.check(TokenType.INT)) {
            this.la.skip();
            return AST.Type.Int;
        }
        if (this.la.check(TokenType.BOOL)) {
            this.la.skip();
            return AST.Type.Bool;
        }
        throw new ParseError("Error: expected a type, found " + this.la.current());
    }

    public List<AST.ProcDecl> parseProcDecls() throws ParseError {
        ArrayList<AST.ProcDecl> result = new ArrayList<AST.ProcDecl>();
        while (this.la.check(TokenType.PROC)) {
            result.add(this.parseProcDecl());
        }
        return result;
    }

    public AST.ProcDecl parseProcDecl() throws ParseError {
        Position position = this.la.current().position;
        this.la.match(TokenType.PROC);
        String id = this.la.match((TokenType)TokenType.ID).lexeme;
        List<AST.Param> params = this.parseParamList();
        this.la.match(TokenType.SEMI);
        AST.Block block = this.parseBlock();
        this.la.match(TokenType.SEMI);
        return AST.procDecl(id, params, block, position);
    }

    public List<AST.Param> parseParamList() throws ParseError {
        if (this.la.check(TokenType.LPAREN)) {
            this.la.skip();
            List<AST.Param> params = this.parseParams();
            this.la.match(TokenType.RPAREN);
            return params;
        }
        return new ArrayList<AST.Param>();
    }

    public List<AST.Param> parseParams() throws ParseError {
        ArrayList<AST.Param> result = new ArrayList<AST.Param>();
        result.add(this.parseParam());
        while (this.la.check(TokenType.COMMA)) {
            this.la.skip();
            result.add(this.parseParam());
        }
        return result;
    }

    public AST.Param parseParam() throws ParseError {
        Position position = this.la.current().position;
        if (this.la.check(TokenType.VAR)) {
            this.la.skip();
            String id = this.la.match((TokenType)TokenType.ID).lexeme;
            this.la.match(TokenType.COLON);
            AST.Type type = this.parseType();
            return AST.varParam(id, type, position);
        }
        String id = this.la.match((TokenType)TokenType.ID).lexeme;
        this.la.match(TokenType.COLON);
        AST.Type type = this.parseType();
        return AST.valParam(id, type, position);
    }

    public List<AST.Stmt> parseStmts() throws ParseError {
        ArrayList<AST.Stmt> result = new ArrayList<AST.Stmt>();
        while (!this.la.check(TokenType.END)) {
            result.add(this.parseStmt());
        }
        return result;
    }

    public AST.Stmt parseStmt() throws ParseError {
        Position position = this.la.current().position;
        if (this.la.check(TokenType.ID)) {
            String id = this.la.match((TokenType)TokenType.ID).lexeme;
            if (this.la.check(TokenType.ASSIGN)) {
                this.la.skip();
                AST.Expr expr = this.parseExpr();
                this.la.match(TokenType.SEMI);
                return AST.assignStmt(id, expr, position);
            }
            List<AST.Expr> args = this.parseArgList();
            this.la.match(TokenType.SEMI);
            return AST.callStmt(id, args, position);
        }
        if (this.la.check(TokenType.BEGIN)) {
            this.la.skip();
            List<AST.Stmt> body = this.parseStmts();
            this.la.match(TokenType.END);
            this.la.match(TokenType.SEMI);
            return AST.sequenceStmt(body, position);
        }
        if (this.la.check(TokenType.IF)) {
            this.la.skip();
            AST.Expr test = this.parseExpr();
            this.la.match(TokenType.THEN);
            AST.Stmt trueClause = this.parseStmt();
            if (this.la.check(TokenType.ELSE)) {
                this.la.skip();
                AST.Stmt falseClause = this.parseStmt();
                return AST.ifThenElseStmt(test, trueClause, falseClause, position);
            }
            return AST.ifThenStmt(test, trueClause, position);
        }
        if (this.la.check(TokenType.WHILE)) {
            this.la.skip();
            AST.Expr test = this.parseExpr();
            this.la.match(TokenType.DO);
            AST.Stmt body = this.parseStmt();
            return AST.whileStmt(test, body, position);
        }
        if (this.la.check(TokenType.PROMPT)) {
            this.la.skip();
            String message = this.la.match((TokenType)TokenType.STRING).lexeme;
            if (this.la.check(TokenType.COMMA)) {
                this.la.skip();
                String id = this.la.match((TokenType)TokenType.ID).lexeme;
                this.la.match(TokenType.SEMI);
                return AST.prompt2Stmt(message, id, position);
            }
            this.la.match(TokenType.SEMI);
            return AST.promptStmt(message, position);
        }
        if (this.la.check(TokenType.PRINT)) {
            this.la.skip();
            List<AST.Item> items = this.parseItems();
            this.la.match(TokenType.SEMI);
            return AST.printStmt(items, position);
        }
        throw new ParseError("Error: expected a statement, found " + this.la.current());
    }

    public List<AST.Expr> parseArgList() throws ParseError {
        if (this.la.check(TokenType.LPAREN)) {
            this.la.skip();
            List<AST.Expr> args = this.parseArgs();
            this.la.match(TokenType.RPAREN);
            return args;
        }
        return new ArrayList<AST.Expr>();
    }

    public List<AST.Expr> parseArgs() throws ParseError {
        ArrayList<AST.Expr> result = new ArrayList<AST.Expr>();
        result.add(this.parseExpr());
        while (this.la.check(TokenType.COMMA)) {
            this.la.skip();
            result.add(this.parseExpr());
        }
        return result;
    }

    public List<AST.Item> parseItems() throws ParseError {
        ArrayList<AST.Item> result = new ArrayList<AST.Item>();
        result.add(this.parseItem());
        while (this.la.check(TokenType.COMMA)) {
            this.la.skip();
            result.add(this.parseItem());
        }
        return result;
    }

    public AST.Item parseItem() throws ParseError {
        Position position = this.la.current().position;
        if (this.la.check(TokenType.STRING)) {
            String message = this.la.skip().lexeme;
            return AST.stringItem(message, position);
        }
        AST.Expr expr = this.parseExpr();
        return AST.exprItem(expr, position);
    }

    public AST.Expr parseExpr() throws ParseError {
        AST.Expr e1 = this.parseSimpleExpr();
        if (this.la.check(TokenType.EQUAL) || this.la.check(TokenType.NOTEQUAL) || this.la.check(TokenType.LESSEQUAL) || this.la.check(TokenType.GREATEREQUAL) || this.la.check(TokenType.LESS) || this.la.check(TokenType.GREATER)) {
            Position position = this.la.current().position;
            AST.Op2 op = this.parseRelOp();
            AST.Expr e2 = this.parseSimpleExpr();
            return AST.binOpExpr(e1, op, e2, position);
        }
        return e1;
    }

    public AST.Op2 parseRelOp() throws ParseError {
        if (this.la.check(TokenType.EQUAL)) {
            this.la.skip();
            return AST.Op2.EQ;
        }
        if (this.la.check(TokenType.NOTEQUAL)) {
            this.la.skip();
            return AST.Op2.NE;
        }
        if (this.la.check(TokenType.LESSEQUAL)) {
            this.la.skip();
            return AST.Op2.LE;
        }
        if (this.la.check(TokenType.GREATEREQUAL)) {
            this.la.skip();
            return AST.Op2.GE;
        }
        if (this.la.check(TokenType.LESS)) {
            this.la.skip();
            return AST.Op2.LT;
        }
        if (this.la.check(TokenType.GREATER)) {
            this.la.skip();
            return AST.Op2.GT;
        }
        throw new ParseError("Error: expected a relational operator, found " + this.la.current());
    }

    public AST.Expr parseSimpleExpr() throws ParseError {
        AST.Expr result = this.parseTerm();
        while (this.la.check(TokenType.PLUS) || this.la.check(TokenType.MINUS) || this.la.check(TokenType.OR)) {
            Position position = this.la.current().position;
            AST.Op2 op = this.parseAddOp();
            AST.Expr t = this.parseTerm();
            result = AST.binOpExpr(result, op, t, position);
        }
        return result;
    }

    public AST.Op2 parseAddOp() throws ParseError {
        if (this.la.check(TokenType.PLUS)) {
            this.la.skip();
            return AST.Op2.Plus;
        }
        if (this.la.check(TokenType.MINUS)) {
            this.la.skip();
            return AST.Op2.Minus;
        }
        if (this.la.check(TokenType.OR)) {
            this.la.skip();
            return AST.Op2.Or;
        }
        throw new ParseError("Error: expected an additive operator, found " + this.la.current());
    }

    public AST.Expr parseTerm() throws ParseError {
        AST.Expr result = this.parseFactor();
        while (this.la.check(TokenType.STAR) || this.la.check(TokenType.DIV) || this.la.check(TokenType.MOD) || this.la.check(TokenType.AND)) {
            Position position = this.la.current().position;
            AST.Op2 op = this.parseMulOp();
            AST.Expr t = this.parseFactor();
            result = AST.binOpExpr(result, op, t, position);
        }
        return result;
    }

    public AST.Op2 parseMulOp() throws ParseError {
        if (this.la.check(TokenType.STAR)) {
            this.la.skip();
            return AST.Op2.Times;
        }
        if (this.la.check(TokenType.DIV)) {
            this.la.skip();
            return AST.Op2.Div;
        }
        if (this.la.check(TokenType.MOD)) {
            this.la.skip();
            return AST.Op2.Mod;
        }
        if (this.la.check(TokenType.AND)) {
            this.la.skip();
            return AST.Op2.And;
        }
        throw new ParseError("Error: expected a multiplicative operator, found " + this.la.current());
    }

    public AST.Expr parseFactor() throws ParseError {
        Position position = this.la.current().position;
        if (this.la.check(TokenType.NUM)) {
            String num = this.la.skip().lexeme;
            int value = Integer.parseInt(num);
            return AST.numExpr(value, position);
        }
        if (this.la.check(TokenType.ID)) {
            String id = this.la.skip().lexeme;
            return AST.idExpr(id, position);
        }
        if (this.la.check(TokenType.TRUE)) {
            this.la.skip();
            return AST.trueExpr(position);
        }
        if (this.la.check(TokenType.FALSE)) {
            this.la.skip();
            return AST.falseExpr(position);
        }
        if (this.la.check(TokenType.MINUS)) {
            this.la.skip();
            AST.Expr expr = this.parseFactor();
            return AST.unOpExpr(AST.Op1.Neg, expr, position);
        }
        if (this.la.check(TokenType.NOT)) {
            this.la.skip();
            AST.Expr expr = this.parseFactor();
            return AST.unOpExpr(AST.Op1.Not, expr, position);
        }
        if (this.la.check(TokenType.LPAREN)) {
            this.la.skip();
            AST.Expr expr = this.parseExpr();
            this.la.match(TokenType.RPAREN);
            return expr;
        }
        throw new ParseError("Error: expected an expression, found " + this.la.current());
    }


}


