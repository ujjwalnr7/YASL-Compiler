package csc426.parser;

import static csc426.parser.TokenType.*;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import csc426.ast.*;

/**
 * A recursive-descent parser for full YASL.
 * 
 * @author bhoward
 */
public class Parser {
	private static final EnumSet<TokenType> FIRST_FACTOR = EnumSet.of(NUM, ID, TRUE, FALSE, MINUS, NOT, LPAREN);
	private static final EnumSet<TokenType> FOLLOW_FACTOR = EnumSet.of(RPAREN, COMMA, SEMI, THEN, DO, EOF);
	private static final EnumSet<TokenType> FIRST_STMT = EnumSet.of(ID, BEGIN, IF, WHILE, PROMPT, PRINT);
	private static final EnumSet<TokenType> FOLLOW_STMT = EnumSet.of(END, EOF);
	
	private Lookahead la;

	public Parser(Lookahead lookahead) {
		this.la = lookahead;
	}

	public Program parseProgram() {
		Position position = la.current().position;
		la.match(PROGRAM);
		String name = la.match(ID).lexeme;
		la.match(SEMI);
		Block block = parseBlock();
		la.match(PERIOD);
		la.match(EOF);
		return new Program(name, block, position);
	}

	public Block parseBlock() {
		Position position = la.current().position;
		List<ConstDecl> consts = parseConstDecls();
		List<VarDecl> vars = parseVarDecls();
		List<ProcDecl> procs = parseProcDecls();
		la.match(BEGIN);
		List<Stmt> stmts = parseStmts();
		la.match(END);
		return new Block(consts, vars, procs, stmts, position);
	}

	public List<ConstDecl> parseConstDecls() {
		List<ConstDecl> result = new ArrayList<>();
		while (la.check(CONST)) {
			result.add(parseConstDecl());
		}
		return result;
	}

	public ConstDecl parseConstDecl() {
		Position position = la.current().position;
		la.match(CONST);
		String id = la.match(ID).lexeme;
		la.match(ASSIGN);
		int sign = 1;
		if (la.check(MINUS)) {
			la.skip();
			sign = -1;
		}
		String num = la.match(NUM).lexeme;
		la.match(SEMI);
		int value = sign * Integer.parseInt(num);
		return new ConstDecl(id, value, position);
	}

	public List<VarDecl> parseVarDecls() {
		List<VarDecl> result = new ArrayList<>();
		while (la.check(VAR)) {
			result.add(parseVarDecl());
		}
		return result;
	}

	public VarDecl parseVarDecl() {
		Position position = la.current().position;
		la.match(VAR);
		String id = la.match(ID).lexeme;
		la.match(COLON);
		Type type = parseType();
		la.match(SEMI);
		return new VarDecl(id, type, position);
	}

	public Type parseType() {
		if (la.check(INT)) {
			la.skip();
			return Type.Int;
		} else if (la.check(BOOL)) {
			la.skip();
			return Type.Bool;
		} else {
			la.logError("Error: expected a type, found " + la.current());
			return Type.Int; // assume int if missing
		}
	}

	public List<ProcDecl> parseProcDecls() {
		List<ProcDecl> result = new ArrayList<>();
		while (la.check(PROC)) {
			result.add(parseProcDecl());
		}
		return result;
	}

	public ProcDecl parseProcDecl() {
		Position position = la.current().position;
		la.match(PROC);
		String id = la.match(ID).lexeme;
		List<Param> params = parseParamList();
		la.match(SEMI);
		Block block = parseBlock();
		la.match(SEMI);
		return new ProcDecl(id, params, block, position);
	}

	public List<Param> parseParamList() {
		if (la.check(LPAREN)) {
			la.skip();
			List<Param> params = parseParams();
			la.match(RPAREN);
			return params;
		} else {
			return new ArrayList<>();
		}
	}

	public List<Param> parseParams() {
		List<Param> result = new ArrayList<>();
		result.add(parseParam());
		while (la.check(COMMA)) {
			la.skip();
			result.add(parseParam());
		}
		return result;
	}

	public Param parseParam() {
		Position position = la.current().position;
		boolean isVar = false;

		if (la.check(VAR)) {
			la.skip();
			isVar = true;
		}

		String id = la.match(ID).lexeme;
		la.match(COLON);
		Type type = parseType();
		return new Param(id, type, position, isVar);
	}

	public List<Stmt> parseStmts() {
		List<Stmt> result = new ArrayList<>();
		while (!la.check(END)) {
			Stmt stmt = parseStmt();
			if (stmt == null) {
				// Unable to find a statement; abort
				break;
			} else {
				result.add(stmt);
			}
		}
		return result;
	}

	public Stmt parseStmt() {
		la.synchronize(FIRST_STMT, FOLLOW_STMT);
		Position position = la.current().position;
		if (la.check(ID)) {
			String id = la.match(ID).lexeme;
			if (la.check(ASSIGN)) {
				la.skip();
				Expr expr = parseExpr();
				la.match(SEMI);
				return new AssignStmt(id, expr, position);
			} else {
				List<Expr> args = parseArgList();
				la.match(SEMI);
				return new CallStmt(id, args, position);
			}
		} else if (la.check(BEGIN)) {
			la.skip();
			List<Stmt> body = parseStmts();
			la.match(END);
			la.match(SEMI);
			return new SequenceStmt(body, position);
		} else if (la.check(IF)) {
			la.skip();
			Expr test = parseExpr();
			la.match(THEN);
			Stmt trueClause = parseStmt();
			if (la.check(ELSE)) {
				la.skip();
				Stmt falseClause = parseStmt();
				return new IfThenElseStmt(test, trueClause, falseClause, position);
			} else {
				return new IfThenStmt(test, trueClause, position);
			}
		} else if (la.check(WHILE)) {
			la.skip();
			Expr test = parseExpr();
			la.match(DO);
			Stmt body = parseStmt();
			return new WhileStmt(test, body, position);
		} else if (la.check(PROMPT)) {
			la.skip();
			String message = la.match(STRING).lexeme;
			if (la.check(COMMA)) {
				la.skip();
				String id = la.match(ID).lexeme;
				la.match(SEMI);
				return new Prompt2Stmt(message, id, position);
			} else {
				la.match(SEMI);
				return new PromptStmt(message, position);
			}
		} else if (la.check(PRINT)) {
			la.skip();
			List<Item> items = parseItems();
			la.match(SEMI);
			return new PrintStmt(items, position);
		} else {
			la.logError("Error: expected a statement, found " + la.current());
			return null;
		}
	}

	public List<Expr> parseArgList() {
		if (la.check(LPAREN)) {
			la.skip();
			List<Expr> args = parseArgs();
			la.match(RPAREN);
			return args;
		} else {
			return new ArrayList<>();
		}
	}

	public List<Expr> parseArgs() {
		List<Expr> result = new ArrayList<>();
		result.add(parseExpr());
		while (la.check(COMMA)) {
			la.skip();
			result.add(parseExpr());
		}
		return result;
	}

	public List<Item> parseItems() {
		List<Item> result = new ArrayList<>();
		result.add(parseItem());
		while (la.check(COMMA)) {
			la.skip();
			result.add(parseItem());
		}
		return result;
	}

	public Item parseItem() {
		Position position = la.current().position;
		if (la.check(STRING)) {
			String message = la.skip().lexeme;
			return new StringItem(message, position);
		} else {
			Expr expr = parseExpr();
			return new ExprItem(expr, position);
		}
	}

	public Expr parseExpr() {
		Expr e1 = parseSimpleExpr();
		if (la.check(EQUAL) || la.check(NOTEQUAL) || la.check(LESSEQUAL) || la.check(GREATEREQUAL) || la.check(LESS)
				|| la.check(GREATER)) {
			Position position = la.current().position;
			Op2 op = parseRelOp();
			Expr e2 = parseSimpleExpr();
			return new BinOpExpr(e1, op, e2, position);
		}
		return e1;
	}

	public Op2 parseRelOp() {
		if (la.check(EQUAL)) {
			la.skip();
			return Op2.EQ;
		} else if (la.check(NOTEQUAL)) {
			la.skip();
			return Op2.NE;
		} else if (la.check(LESSEQUAL)) {
			la.skip();
			return Op2.LE;
		} else if (la.check(GREATEREQUAL)) {
			la.skip();
			return Op2.GE;
		} else if (la.check(LESS)) {
			la.skip();
			return Op2.LT;
		} else if (la.check(GREATER)) {
			la.skip();
			return Op2.GT;
		} else {
			return null; // this should not happen
		}
	}

	public Expr parseSimpleExpr() {
		Expr result = parseTerm();
		while (la.check(PLUS) || la.check(MINUS) || la.check(OR)) {
			Position position = la.current().position;
			Op2 op = parseAddOp();
			Expr t = parseTerm();
			result = new BinOpExpr(result, op, t, position);
		}
		return result;
	}

	public Op2 parseAddOp() {
		if (la.check(PLUS)) {
			la.skip();
			return Op2.Plus;
		} else if (la.check(MINUS)) {
			la.skip();
			return Op2.Minus;
		} else if (la.check(OR)) {
			la.skip();
			return Op2.Or;
		} else {
			return null; // this should not happen
		}
	}

	public Expr parseTerm() {
		Expr result = parseFactor();
		while (la.check(STAR) || la.check(DIV) || la.check(MOD) || la.check(AND)) {
			Position position = la.current().position;
			Op2 op = parseMulOp();
			Expr t = parseFactor();
			result = new BinOpExpr(result, op, t, position);
		}
		return result;
	}

	public Op2 parseMulOp() {
		if (la.check(STAR)) {
			la.skip();
			return Op2.Times;
		} else if (la.check(DIV)) {
			la.skip();
			return Op2.Div;
		} else if (la.check(MOD)) {
			la.skip();
			return Op2.Mod;
		} else if (la.check(AND)) {
			la.skip();
			return Op2.And;
		} else {
			return null; // this should not happen
		}
	}

	public Expr parseFactor() {
		la.synchronize(FIRST_FACTOR, FOLLOW_FACTOR);
		Position position = la.current().position;
		if (la.check(NUM)) {
			String num = la.skip().lexeme;
			int value = Integer.parseInt(num);
			return new NumExpr(value, position);
		} else if (la.check(ID)) {
			String id = la.skip().lexeme;
			return new IdExpr(id, position);
		} else if (la.check(TRUE)) {
			la.skip();
			return new BoolExpr(true, position);
		} else if (la.check(FALSE)) {
			la.skip();
			return new BoolExpr(false, position);
		} else if (la.check(MINUS)) {
			la.skip();
			Expr expr = parseFactor();
			return new UnOpExpr(Op1.Neg, expr, position);
		} else if (la.check(NOT)) {
			la.skip();
			Expr expr = parseFactor();
			return new UnOpExpr(Op1.Not, expr, position);
		} else if (la.check(LPAREN)) {
			la.skip();
			Expr expr = parseExpr();
			la.match(RPAREN);
			return expr;
		} else {
			la.logError("Error: expected an expression, found " + la.current());
			return new NumExpr(0, position); // insert a zero as part of error recovery
		}
	}
}
