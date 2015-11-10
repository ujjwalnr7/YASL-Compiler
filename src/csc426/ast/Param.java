package csc426.ast;

import csc426.parser.Position;

public final class Param extends AST {
	public final String id;
	public final Type type;
	public final boolean isVar;

	public Param(String id, Type type, Position position, boolean isVar) {
		super(position);
		this.id = id;
		this.type = type;
		this.isVar = isVar;
	}

	@Override
	public String toString() {
		if (isVar) {
			return "Variable parameter " + id + " at " + position;
		} else {
			return "Value parameter " + id + " at " + position;
		}
	}

	public String render(String indent) {
		String result = indent;
		if (isVar) {
			result += "Var ";
		} else {
			result += "Val ";
		}
		result += id + " : " + type + "\n";
		return result;
	}
}