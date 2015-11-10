package csc426.ast;

import csc426.parser.*;

public final class IfThenElseStmt extends Stmt {
	public final Expr test;
	public final Stmt trueClause;
	public final Stmt falseClause;

	public IfThenElseStmt(Expr test, Stmt trueClause, Stmt falseClause, Position position) {
		super(position);
		this.test = test;
		this.trueClause = trueClause;
		this.falseClause = falseClause;
	}

	@Override
	public String toString() {
		return "If-then-else statement at " + position;
	}

	public String render(String indent) {
		String result = indent + "IfThenElse\n";
		result += test.render(indent + "  ");
		result += trueClause.render(indent + "  ");
		result += falseClause.render(indent + "  ");
		return result;
	}
}