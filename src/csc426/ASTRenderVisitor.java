package csc426;

import csc426.AST.*;

public class ASTRenderVisitor implements ASTVisitor<String> {
	private String indent = "";
	
	void indent() {
		indent += "  ";
	}
	
	void outdent() {
		indent = indent.substring(2);
	}
	
	String line(String text) {
		return indent + text + "\n";
	}
	
	public String visit(Program program) {
		String result = line("Program " + program.name);
		indent();
		result += program.block.accept(this);
		outdent();
		return result;
	}

	public String visit(Block block) {
		String result = line("Block");
		indent();
		for (ConstDecl decl : block.consts) {
			result += decl.accept(this);
		}
		for (VarDecl decl : block.vars) {
			result += decl.accept(this);
		}
		for (ProcDecl decl : block.procs) {
			result += decl.accept(this);
		}
		for (Stmt stmt : block.stmts) {
			result += stmt.accept(this);
		}
		outdent();
		return result;
	}

	public String visit(ConstDecl decl) {
		String result = line("Const " + decl.id + " = " + decl.value);
		return result;
	}

	public String visit(VarDecl decl) {
		String result = line("Var " + decl.id + " : " + decl.type);
		return result;
	}

	public String visit(ProcDecl decl) {
		String result = line("Proc " + decl.id);
		indent();
		for (Param param : decl.params) {
			result += param.accept(this);
		}
		result += decl.block.accept(this);
		outdent();
		return result;
	}

	public String visit(Param.Val param) {
		String result = line("Val " + param.id + " : " + param.type);
		return result;
	}

	public String visit(Param.Var param) {
		String result = line("Var " + param.id + " : " + param.type);
		return result;
	}

	public String visit(Stmt.Assign stmt) {
		String result = line("Assign " + stmt.id);
		indent();
		result += stmt.expr.accept(this);
		outdent();
		return result;
	}

	public String visit(Stmt.Call stmt) {
		String result = line("Call " + stmt.id);
		indent();
		for (Expr arg : stmt.args) {
			result += arg.accept(this);
		}
		outdent();
		return result;
	}

	public String visit(Stmt.Sequence stmt) {
		String result = line("Sequence");
		indent();
		for (Stmt stmt2 : stmt.body) {
			result += stmt2.accept(this);
		}
		outdent();
		return result;
	}

	public String visit(Stmt.IfThen stmt) {
		String result = line("IfThen");
		indent();
		result += stmt.test.accept(this);
		result += stmt.trueClause.accept(this);
		outdent();
		return result;
	}

	public String visit(Stmt.IfThenElse stmt) {
		String result = line("IfThenElse");
		indent();
		result += stmt.test.accept(this);
		result += stmt.trueClause.accept(this);
		result += stmt.falseClause.accept(this);
		outdent();
		return result;
	}

	public String visit(Stmt.While stmt) {
		String result = line("While");
		indent();
		result += stmt.test.accept(this);
		result += stmt.body.accept(this);
		outdent();
		return result;
	}

	public String visit(Stmt.Prompt stmt) {
		String result = line("Prompt \"" + stmt.message + "\"");
		return result;
	}

	public String visit(Stmt.Prompt2 stmt) {
		String result = line("Prompt2 \"" + stmt.message + "\", " + stmt.id);
		return result;
	}

	public String visit(Stmt.Print stmt) {
		String result = line("Print");
		indent();
		for (Item item : stmt.items) {
			result += item.accept(this);
		}
		outdent();
		return result;
	}

	public String visit(Item.ExprItem item) {
		String result = line("ExprItem");
		indent();
		result += item.expr.accept(this);
		outdent();
		return result;
	}

	public String visit(Item.StringItem item) {
		String result = line("StringItem \"" + item.message + "\"");
		return result;
	}

	public String visit(Expr.BinOp expr) {
		String result = line("BinOp " + expr.op);
		indent();
		result += expr.left.accept(this);
		result += expr.right.accept(this);
		outdent();
		return result;
	}

	public String visit(Expr.UnOp expr) {
		String result = line("UnOp " + expr.op);
		indent();
		result += expr.expr.accept(this);
		outdent();
		return result;
	}

	public String visit(Expr.Num expr) {
		String result = line("Num " + expr.value);
		return result;
	}

	public String visit(Expr.Id expr) {
		String result = line("Id " + expr.id);
		return result;
	}

	public String visit(Expr.True expr) {
		String result = line("True");
		return result;
	}

	public String visit(Expr.False expr) {
		String result = line("False");
		return result;
	}
}