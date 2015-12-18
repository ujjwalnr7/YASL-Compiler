package csc426;

import java.util.List;

public abstract class AST {
	public Position position;
	
	AST(Position position) {
		this.position = position;
	}
	
	public abstract <T> T accept(ASTVisitor<T> visitor);
	
	// Factory methods for creating Abstract Syntax Tree nodes

	public static Program program(String name, Block block, Position position) {
		return new Program(name, block, position);
	}

	public static Block block(List<ConstDecl> consts, List<VarDecl> vars, List<ProcDecl> procs, List<Stmt> stmts, Position position) {
		return new Block(consts, vars, procs, stmts, position);
	}

	public static ConstDecl constDecl(String id, int value, Position position) {
		return new ConstDecl(id, value, position);
	}

	public static VarDecl varDecl(String id, Type type, Position position) {
		return new VarDecl(id, type, position);
	}

	public static ProcDecl procDecl(String id, List<Param> params, Block block, Position position) {
		return new ProcDecl(id, params, block, position);
	}

	public static Param valParam(String id, Type type, Position position) {
		return new Param.Val(id, type, position);
	}

	public static Param varParam(String id, Type type, Position position) {
		return new Param.Var(id, type, position);
	}

	public static Stmt assignStmt(String id, Expr expr, Position position) {
		return new Stmt.Assign(id, expr, position);
	}

	public static Stmt callStmt(String id, List<Expr> args, Position position) {
		return new Stmt.Call(id, args, position);
	}

	public static Stmt sequenceStmt(List<Stmt> body, Position position) {
		return new Stmt.Sequence(body, position);
	}

	public static Stmt ifThenStmt(Expr test, Stmt trueClause, Position position) {
		return new Stmt.IfThen(test, trueClause, position);
	}

	public static Stmt ifThenElseStmt(Expr test, Stmt trueClause, Stmt falseClause, Position position) {
		return new Stmt.IfThenElse(test, trueClause, falseClause, position);
	}

	public static Stmt whileStmt(Expr test, Stmt body, Position position) {
		return new Stmt.While(test, body, position);
	}

	public static Stmt promptStmt(String message, Position position) {
		return new Stmt.Prompt(message, position);
	}

	public static Stmt prompt2Stmt(String message, String id, Position position) {
		return new Stmt.Prompt2(message, id, position);
	}

	public static Stmt printStmt(List<Item> items, Position position) {
		return new Stmt.Print(items, position);
	}

	public static Item exprItem(Expr expr, Position position) {
		return new Item.ExprItem(expr, position);
	}

	public static Item stringItem(String message, Position position) {
		return new Item.StringItem(message, position);
	}

	public static Expr binOpExpr(Expr left, Op2 op, Expr right, Position position) {
		return new Expr.BinOp(left, op, right, position);
	}

	public static Expr unOpExpr(Op1 op, Expr expr, Position position) {
		return new Expr.UnOp(op, expr, position);
	}

	public static Expr numExpr(int value, Position position) {
		return new Expr.Num(value, position);
	}

	public static Expr idExpr(String id, Position position) {
		return new Expr.Id(id, position);
	}

	public static Expr trueExpr(Position position) {
		return new Expr.True(position);
	}

	public static Expr falseExpr(Position position) {
		return new Expr.False(position);
	}

	// Abstract Syntax Tree node classes

	public static final class Program extends AST {
		public final String name;
		public final Block block;

		Program(String name, Block block, Position position) {
			super(position);
			this.name = name;
			this.block = block;
		}

		public <T> T accept(ASTVisitor<T> visitor) {
			return visitor.visit(this);
		}
		
		@Override
		public String toString() {
			return "Program " + name + " at " + position;
		}
	}

	public static final class Block extends AST {
		public final List<ConstDecl> consts;
		public final List<VarDecl> vars;
		public final List<ProcDecl> procs;
		public final List<Stmt> stmts;

		Block(List<ConstDecl> consts, List<VarDecl> vars, List<ProcDecl> procs, List<Stmt> stmts, Position position) {
			super(position);
			this.consts = consts;
			this.vars = vars;
			this.procs = procs;
			this.stmts = stmts;
		}

		public <T> T accept(ASTVisitor<T> visitor) {
			return visitor.visit(this);
		}
		
		@Override
		public String toString() {
			return "Block at " + position;
		}

	}

	public static final class ConstDecl extends AST {
		public final String id;
		public final int value;

		ConstDecl(String id, int value, Position position) {
			super(position);
			this.id = id;
			this.value = value;
		}

		public <T> T accept(ASTVisitor<T> visitor) {
			return visitor.visit(this);
		}
		
		@Override
		public String toString() {
			return "Constant declaration " + id + " at " + position;
		}
	}

	public static final class VarDecl extends AST {
		public final String id;
		public final Type type;

		VarDecl(String id, Type type, Position position) {
			super(position);
			this.id = id;
			this.type = type;
		}

		public <T> T accept(ASTVisitor<T> visitor) {
			return visitor.visit(this);
		}
		
		@Override
		public String toString() {
			return "Variable declaration " + id + " at " + position;
		}
	}

	public static final class ProcDecl extends AST {
		public final String id;
		public final List<Param> params;
		public final Block block;

		ProcDecl(String id, List<Param> params, Block block, Position position) {
			super(position);
			this.id = id;
			this.params = params;
			this.block = block;
		}

		public <T> T accept(ASTVisitor<T> visitor) {
			return visitor.visit(this);
		}
		
		@Override
		public String toString() {
			return "Procedure declaration " + id + " at " + position;
		}
	}

	public static abstract class Param extends AST {
		public final String id;
		public final Type type;

		Param(String id, Type type, Position position) {
			super(position);
			this.id = id;
			this.type = type;
		}

		public static final class Val extends Param {
			Val(String id, Type type, Position position) {
				super(id, type, position);
			}

			public <T> T accept(ASTVisitor<T> visitor) {
				return visitor.visit(this);
			}
			
			@Override
			public String toString() {
				return "Value parameter " + id + " at " + position;
			}
		}

		public static final class Var extends Param {
			Var(String id, Type type, Position position) {
				super(id, type, position);
			}

			public <T> T accept(ASTVisitor<T> visitor) {
				return visitor.visit(this);
			}
			
			@Override
			public String toString() {
				return "Variable parameter " + id + " at " + position;
			}
		}
	}

	public static abstract class Stmt extends AST {
		Stmt(Position position) {
			super(position);
		}
		
		public static final class Assign extends Stmt {
			public final String id;
			public final Expr expr;

			Assign(String id, Expr expr, Position position) {
				super(position);
				this.id = id;
				this.expr = expr;
			}

			public <T> T accept(ASTVisitor<T> visitor) {
				return visitor.visit(this);
			}
			
			@Override
			public String toString() {
				return "Assignment to " + id + " at " + position;
			}
		}

		public static final class Call extends Stmt {
			public final String id;
			public final List<Expr> args;

			Call(String id, List<Expr> args, Position position) {
				super(position);
				this.id = id;
				this.args = args;
			}

			public <T> T accept(ASTVisitor<T> visitor) {
				return visitor.visit(this);
			}
			
			@Override
			public String toString() {
				return "Call to " + id + " at " + position;
			}
		}

		public static final class Sequence extends Stmt {
			public final List<Stmt> body;

			Sequence(List<Stmt> body, Position position) {
				super(position);
				this.body = body;
			}

			public <T> T accept(ASTVisitor<T> visitor) {
				return visitor.visit(this);
			}
			
			@Override
			public String toString() {
				return "Sequence at " + position;
			}
		}

		public static final class IfThen extends Stmt {
			public final Expr test;
			public final Stmt trueClause;

			IfThen(Expr test, Stmt trueClause, Position position) {
				super(position);
				this.test = test;
				this.trueClause = trueClause;
			}

			public <T> T accept(ASTVisitor<T> visitor) {
				return visitor.visit(this);
			}
			
			@Override
			public String toString() {
				return "If-then statement at " + position;
			}
		}

		public static final class IfThenElse extends Stmt {
			public final Expr test;
			public final Stmt trueClause;
			public final Stmt falseClause;

			IfThenElse(Expr test, Stmt trueClause, Stmt falseClause, Position position) {
				super(position);
				this.test = test;
				this.trueClause = trueClause;
				this.falseClause = falseClause;
			}

			public <T> T accept(ASTVisitor<T> visitor) {
				return visitor.visit(this);
			}
			
			@Override
			public String toString() {
				return "If-Then-Else statement at " + position;
			}
		}

		public static final class While extends Stmt {
			public final Expr test;
			public final Stmt body;

			While(Expr test, Stmt body, Position position) {
				super(position);
				this.test = test;
				this.body = body;
			}

			public <T> T accept(ASTVisitor<T> visitor) {
				return visitor.visit(this);
			}
			
			@Override
			public String toString() {
				return "While statement at " + position;
			}
		}

		public static final class Prompt extends Stmt {
			public final String message;

			Prompt(String message, Position position) {
				super(position);
				this.message = message;
			}

			public <T> T accept(ASTVisitor<T> visitor) {
				return visitor.visit(this);
			}
			
			@Override
			public String toString() {
				return "Prompt statement at " + position;
			}
		}

		public static final class Prompt2 extends Stmt {
			public final String message;
			public final String id;

			Prompt2(String message, String id, Position position) {
				super(position);
				this.message = message;
				this.id = id;
			}

			public <T> T accept(ASTVisitor<T> visitor) {
				return visitor.visit(this);
			}
			
			@Override
			public String toString() {
				return "Prompt of " + id + " at " + position;
			}
		}

		public static final class Print extends Stmt {
			public final List<Item> items;

			Print(List<Item> items, Position position) {
				super(position);
				this.items = items;
			}

			public <T> T accept(ASTVisitor<T> visitor) {
				return visitor.visit(this);
			}
			
			@Override
			public String toString() {
				return "Print statement at " + position;
			}
		}
	}

	public static abstract class Item extends AST {
		public Expr expr;
		public String message;

		Item(Position position) {
			super(position);
		}

		public static final class ExprItem extends Item {
			public final Expr expr;

			ExprItem(Expr expr, Position position) {
				super(position);
				this.expr = expr;
			}

			public <T> T accept(ASTVisitor<T> visitor) {
				return visitor.visit(this);
			}
			
			@Override
			public String toString() {
				return "Print expression item at " + position;
			}
		}

		public static final class StringItem extends Item {
			public final String message;

			StringItem(String message, Position position) {
				super(position);
				this.message = message;
			}

			public <T> T accept(ASTVisitor<T> visitor) {
				return visitor.visit(this);
			}
			
			@Override
			public String toString() {
				return "Print string item at " + position;
			}
		}
	}

	public static abstract class Expr extends AST {

		Expr(Position position) {
			super(position);
		}
		
		public static final class BinOp extends Expr {
			public final Expr left;
			public final Op2 op;
			public final Expr right;

			BinOp(Expr left, Op2 op, Expr right, Position position) {
				super(position);
				this.left = left;
				this.op = op;
				this.right = right;
			}

			public <T> T accept(ASTVisitor<T> visitor) {
				return visitor.visit(this);
			}
			
			@Override
			public String toString() {
				return "Binary operation " + op + " at " + position;
			}
		}

		public static final class UnOp extends Expr {
			public final Op1 op;
			public final Expr expr;

			UnOp(Op1 op, Expr expr, Position position) {
				super(position);
				this.op = op;
				this.expr = expr;
			}

			public <T> T accept(ASTVisitor<T> visitor) {
				return visitor.visit(this);
			}
			
			@Override
			public String toString() {
				return "Unary operation " + op + " at " + position;
			}
		}

		public static final class Num extends Expr {
			public final int value;

			Num(int value, Position position) {
				super(position);
				this.value = value;
			}

			public <T> T accept(ASTVisitor<T> visitor) {
				return visitor.visit(this);
			}
			
			@Override
			public String toString() {
				return "Integer literal " + value + " at " + position;
			}
		}

		public static final class Id extends Expr {
			public final String id;

			Id(String id, Position position) {
				super(position);
				this.id = id;
			}

			public <T> T accept(ASTVisitor<T> visitor) {
				return visitor.visit(this);
			}
			
			@Override
			public String toString() {
				return "Variable " + id + " at " + position;
			}
		}

		public static final class True extends Expr {
			True(Position position) {
				super(position);
			}
			
			public <T> T accept(ASTVisitor<T> visitor) {
				return visitor.visit(this);
			}
			
			@Override
			public String toString() {
				return "Boolean literal true at " + position;
			}
		}

		public static final class False extends Expr {
			False(Position position) {
				super(position);
			}
			
			public <T> T accept(ASTVisitor<T> visitor) {
				return visitor.visit(this);
			}
			
			@Override
			public String toString() {
				return "Boolean literal false at " + position;
			}
		}
	}

	public static enum Type 
	{
		Int, Bool
	}

	public static enum Op2 
	{
		EQ, NE, LE, GE, LT, GT, Plus, Minus, Times, Div, Mod, And, Or
	}

	public static enum Op1 
	{
		Neg, Not
	}
}