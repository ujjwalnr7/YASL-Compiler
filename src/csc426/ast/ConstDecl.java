package csc426.ast;

import csc426.parser.Position;

public final class ConstDecl extends AST {
	public final String id;
	public final int value;

	public ConstDecl(String id, int value, Position position) {
		super(position);
		this.id = id;
		this.value = value;
	}

	@Override
	public String toString() {
		return "Constant declaration " + id + " at " + position;
	}

	public String render(String indent) {
		return indent + "Const " + id + " = " + value + "\n";
	}
}