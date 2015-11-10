package csc426.ast;

import csc426.parser.Position;

public final class ExprItem extends Item {
	public final Expr expr;

	public ExprItem(Expr expr, Position position) {
		super(position);
		this.expr = expr;
	}

	@Override
	public String toString() {
		return "Print expression item at " + position;
	}

	public String render(String indent) {
		String result = indent + "ExprItem\n";
		result += expr.render(indent + "  ");
		return result;
	}
}