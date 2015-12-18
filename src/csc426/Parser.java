package csc426;

import csc426.AST.*;
import static csc426.AST.*;
import static csc426.TokenType.*;

import java.util.ArrayList;
import java.util.List;

public class Parser {
	private Lookahead la;

	public Parser(Lookahead lookahead) {
		this.la = lookahead;
	}

	public Program parseProgram() throws ParseError {
		Position position = la.current().position;
		la.match(PROGRAM);
		String name = la.match(ID).lexeme;
		la.match(SEMI);
		Block block = parseBlock();
		la.match(PERIOD);
		la.match(EOF);

		return program(name, block, position);
	}

	public Block parseBlock() throws ParseError {
		Position position = la.current().position;
		List<ConstDecl> consts = parseConstDecls();
		List<VarDecl> vars = parseVarDecls();
		List<ProcDecl> procs = parseProcDecls();
		la.match(BEGIN);
		List<Stmt> stmts = parseStmts();
		la.match(END);
		return block(consts, vars, procs, stmts, position);
	}

	public List<ConstDecl> parseConstDecls() throws ParseError {
		List<ConstDecl> result = new ArrayList<>();
		while (la.check(CONST)) {
			result.add(parseConstDecl());
		}
		return result;
	}

	public ConstDecl parseConstDecl() throws ParseError {
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
		return constDecl(id, value, position);
	}

	public List<VarDecl> parseVarDecls() throws ParseError {
		List<VarDecl> result = new ArrayList<>();
		while (la.check(VAR)) {
			result.add(parseVarDecl());
		}
		return result;
	}

	public VarDecl parseVarDecl() throws ParseError {
		Position position = la.current().position;
		la.match(VAR);
		String id = la.match(ID).lexeme;
		la.match(COLON);
		Type type = parseType();
		la.match(SEMI);
		return varDecl(id, type, position);
	}

	public Type parseType() throws ParseError {
		if (la.check(INT)) {
			la.skip();
			return Type.Int;
		} else if (la.check(BOOL)) {
			la.skip();
			return Type.Bool;
		} else {
			throw new ParseError("Error: expected a type, found " + la.current());
		}
	}

	public List<ProcDecl> parseProcDecls() throws ParseError {
		List<ProcDecl> result = new ArrayList<>();
		while (la.check(PROC)) {
			result.add(parseProcDecl());
		}
		return result;
	}

	public ProcDecl parseProcDecl() throws ParseError {
		Position position = la.current().position;
		la.match(PROC);
		String id = la.match(ID).lexeme;
		List<Param> params = parseParamList();
		la.match(SEMI);
		Block block = parseBlock();
		la.match(SEMI);
		return procDecl(id, params, block, position);
	}

	public List<Param> parseParamList() throws ParseError {
		if (la.check(LPAREN)) {
			la.skip();
			List<Param> params = parseParams();
			la.match(RPAREN);
			return params;
		} else {
			return new ArrayList<>();
		}
	}

	public List<Param> parseParams() throws ParseError {
		List<Param> result = new ArrayList<>();
		result.add(parseParam());
		while (la.check(COMMA)) {
			la.skip();
			result.add(parseParam());
		}
		return result;
	}

	public Param parseParam() throws ParseError {
		Position position = la.current().position;
		if (la.check(VAR)) {
			la.skip();
			String id = la.match(ID).lexeme;
			la.match(COLON);
			Type type = parseType();
			return varParam(id, type, position);
		} else {
			String id = la.match(ID).lexeme;
			la.match(COLON);
			Type type = parseType();
			return valParam(id, type, position);
		}
	}

	public List<Stmt> parseStmts() throws ParseError {
		List<Stmt> result = new ArrayList<>();
		while (!la.check(END)) {
			result.add(parseStmt());
		}
		return result;
	}

	public Stmt parseStmt() throws ParseError {
		Position position = la.current().position;
		if (la.check(ID)) {
			String id = la.match(ID).lexeme;
			if (la.check(ASSIGN)) {
				la.skip();
				Expr expr = parseExpr();
				la.match(SEMI);
				return assignStmt(id, expr, position);
			} else {
				List<Expr> args = parseArgList();
				la.match(SEMI);
				return callStmt(id, args, position);
			}
		} else if (la.check(BEGIN)) {
			la.skip();
			List<Stmt> body = parseStmts();
			la.match(END);
			la.match(SEMI);
			return sequenceStmt(body, position);
		} else if (la.check(IF)) {
			la.skip();
			Expr test = parseExpr();
			la.match(THEN);
			Stmt trueClause = parseStmt();
			if (la.check(ELSE)) {
				la.skip();
				Stmt falseClause = parseStmt();
				return ifThenElseStmt(test, trueClause, falseClause, position);
			} else {
				return ifThenStmt(test, trueClause, position);
			}
		} else if (la.check(WHILE)) {
			la.skip();
			Expr test = parseExpr();
			la.match(DO);
			Stmt body = parseStmt();
			return whileStmt(test, body, position);
		} else if (la.check(PROMPT)) {
			la.skip();
			String message = la.match(STRING).lexeme;
			if (la.check(COMMA)) {
				la.skip();
				String id = la.match(ID).lexeme;
				la.match(SEMI);
				return prompt2Stmt(message, id, position);
			} else {
				la.match(SEMI);
				return promptStmt(message, position);
			}
		} else if (la.check(PRINT)) {
			la.skip();
			List<Item> items = parseItems();
			la.match(SEMI);
			return printStmt(items, position);
		} else {
			throw new ParseError("Error: expected a statement, found " + la.current());
		}
	}

	public List<Expr> parseArgList() throws ParseError {
		if (la.check(LPAREN)) {
			la.skip();
			List<Expr> args = parseArgs();
			la.match(RPAREN);
			return args;
		} else {
			return new ArrayList<>();
		}
	}

	public List<Expr> parseArgs() throws ParseError {
		List<Expr> result = new ArrayList<>();
		result.add(parseExpr());
		while (la.check(COMMA)) {
			la.skip();
			result.add(parseExpr());
		}
		return result;
	}

	public List<Item> parseItems() throws ParseError {
		List<Item> result = new ArrayList<>();
		result.add(parseItem());
		while (la.check(COMMA)) {
			la.skip();
			result.add(parseItem());
		}
		return result;
	}

	public Item parseItem() throws ParseError {
		Position position = la.current().position;
		if (la.check(STRING)) {
			String message = la.skip().lexeme;
			return stringItem(message, position);
		} else {
			Expr expr = parseExpr();
			return exprItem(expr, position);
		}
	}

	public Expr parseExpr() throws ParseError {
		Expr e1 = parseSimpleExpr();
		if (la.check(EQUAL) || la.check(NOTEQUAL) || la.check(LESSEQUAL)
				|| la.check(GREATEREQUAL) || la.check(LESS) || la.check(GREATER)) {
			Position position = la.current().position;
			Op2 op = parseRelOp();
			Expr e2 = parseSimpleExpr();
			return binOpExpr(e1, op, e2, position);
		}
		return e1;
	}
	
	public Op2 parseRelOp() throws ParseError {
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
			throw new ParseError("Error: expected a relational operator, found " + la.current());
		}
	}

	public Expr parseSimpleExpr() throws ParseError {
		Expr result = parseTerm();
		while (la.check(PLUS) || la.check(MINUS) || la.check(OR)) {
			Position position = la.current().position;
			Op2 op = parseAddOp();
			Expr t = parseTerm();
			result = binOpExpr(result, op, t, position);
		}
		return result;
	}
	
	public Op2 parseAddOp() throws ParseError {
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
			throw new ParseError("Error: expected an additive operator, found " + la.current());
		}
	}
	
	public Expr parseTerm() throws ParseError {
		Expr result = parseFactor();
		while (la.check(STAR) || la.check(DIV) || la.check(MOD) || la.check(AND)) {
			Position position = la.current().position;
			Op2 op = parseMulOp();
			Expr t = parseFactor();
			result = binOpExpr(result, op, t, position);
		}
		return result;
	}
	
	public Op2 parseMulOp() throws ParseError {
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
			throw new ParseError("Error: expected a multiplicative operator, found " + la.current());
		}
	}
	
	public Expr parseFactor() throws ParseError {
		Position position = la.current().position;
		if (la.check(NUM)) {
			String num = la.skip().lexeme;
			int value = Integer.parseInt(num);
			return numExpr(value, position);
		} else if (la.check(ID)) {
			String id = la.skip().lexeme;
			return idExpr(id, position);
		} else if (la.check(TRUE)) {
			la.skip();
			return trueExpr(position);
		} else if (la.check(FALSE)) {
			la.skip();
			return falseExpr(position);
		} else if (la.check(MINUS)) {
			la.skip();
			Expr expr = parseFactor();
			return unOpExpr(Op1.Neg, expr, position);
		} else if (la.check(NOT)) {
			la.skip();
			Expr expr = parseFactor();
			return unOpExpr(Op1.Not, expr, position);
		} else if (la.check(LPAREN)) {
			la.skip();
			Expr expr = parseExpr();
			la.match(RPAREN);
			return expr;
		} else {
			throw new ParseError("Error: expected an expression, found " + la.current());
		}
	}
}