package csc426.ast;

import csc426.parser.Position;

public final class WhileStmt extends Stmt {
	public final Expr test;
	public final Stmt body;

	public WhileStmt(Expr test, Stmt body, Position position) {
		super(position);
		this.test = test;
		this.body = body;
	}

	@Override
	public String toString() {
		return "While statement at " + position;
	}

	public String render(String indent) {
		String result = indent + "While\n";
		result += test.render(indent + "  ");
		result += body.render(indent + "  ");
		return result;
	}
}