package csc426.ast;

import java.util.List;

import csc426.parser.Position;

public final class CallStmt extends Stmt {
	public final String id;
	public final List<Expr> args;

	public CallStmt(String id, List<Expr> args, Position position) {
		super(position);
		this.id = id;
		this.args = args;
	}

	@Override
	public String toString() {
		return "Call to " + id + " at " + position;
	}

	public String render(String indent) {
		String result = indent + "Call " + id + "\n";
		for (Expr arg : args) {
			result += arg.render(indent + "  ");
		}
		return result;
	}
}