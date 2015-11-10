package csc426.ast;

import csc426.parser.Position;

public final class BinOpExpr extends Expr {
	public final Expr left;
	public final Op2 op;
	public final Expr right;

	public BinOpExpr(Expr left, Op2 op, Expr right, Position position) {
		super(position);
		this.left = left;
		this.op = op;
		this.right = right;
	}

	@Override
	public String toString() {
		return "Binary operation " + op + " at " + position;
	}

	public String render(String indent) {
		String result = indent + "BinOp " + op + "\n";
		result += left.render(indent + "  ");
		result += right.render(indent + "  ");
		return result;
	}
}