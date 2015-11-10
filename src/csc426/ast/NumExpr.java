package csc426.ast;

import csc426.parser.*;

public final class NumExpr extends Expr {
	public final int value;

	public NumExpr(int value, Position position) {
		super(position);
		this.value = value;
	}

	@Override
	public String toString() {
		return "Integer literal " + value + " at " + position;
	}

	public String render(String indent) {
		return indent + "Num " + value + "\n";
	}
}