package csc426.ast;

import csc426.parser.*;

public final class UnOpExpr extends Expr {
	public final Op1 op;
	public final Expr expr;

	public UnOpExpr(Op1 op, Expr expr, Position position) {
		super(position);
		this.op = op;
		this.expr = expr;
	}

	@Override
	public String toString() {
		return "Unary operation " + op + " at " + position;
	}

	public String render(String indent) {
		String result = indent + "UnOp " + op + "\n";
		result += expr.render(indent + "  ");
		return result;
	}
}