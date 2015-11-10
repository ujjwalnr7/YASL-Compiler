package csc426.ast;

import csc426.parser.Position;

public final class BoolExpr extends Expr {
	public final boolean value;
	
	public BoolExpr(boolean value, Position position) {
		super(position);
		this.value = value;
	}
	
	@Override
	public String toString() {
		if (value) {
			return "Boolean literal true at " + position;
		} else {
			return "Boolean literal false at " + position;
		}
	}

	public String render(String indent) {
		if (value) {
			return indent + "True\n";
		} else {
			return indent + "False\n";
		}
	}
}