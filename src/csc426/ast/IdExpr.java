package csc426.ast;

import csc426.parser.*;

public final class IdExpr extends Expr {
	public final String id;

	public IdExpr(String id, Position position) {
		super(position);
		this.id = id;
	}

	@Override
	public String toString() {
		return "Variable " + id + " at " + position;
	}

	public String render(String indent) {
		return indent + "Id " + id + "\n";
	}
}