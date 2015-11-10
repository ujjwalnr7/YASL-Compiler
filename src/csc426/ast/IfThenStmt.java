package csc426.ast;

import csc426.parser.*;

public final class IfThenStmt extends Stmt {
	public final Expr test;
	public final Stmt trueClause;

	public IfThenStmt(Expr test, Stmt trueClause, Position position) {
		super(position);
		this.test = test;
		this.trueClause = trueClause;
	}

	@Override
	public String toString() {
		return "If-then statement at " + position;
	}

	public String render(String indent) {
		String result = indent + "IfThen\n";
		result += test.render(indent + "  ");
		result += trueClause.render(indent + "  ");
		return result;
	}
}