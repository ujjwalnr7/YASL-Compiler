package csc426.ast;

import csc426.parser.Position;

public final class AssignStmt extends Stmt {
	public final String id;
	public final Expr expr;

	public AssignStmt(String id, Expr expr, Position position) {
		super(position);
		this.id = id;
		this.expr = expr;
	}

	@Override
	public String toString() {
		return "Assignment to " + id + " at " + position;
	}

	public String render(String indent) {
		String result = indent + "Assign " + id + "\n";
		result += expr.render(indent + "  ");
		return result;
	}
}