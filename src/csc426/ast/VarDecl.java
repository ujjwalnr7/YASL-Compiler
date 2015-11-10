package csc426.ast;

import csc426.parser.*;

public final class VarDecl extends AST {
	public final String id;
	public final Type type;

	public VarDecl(String id, Type type, Position position) {
		super(position);
		this.id = id;
		this.type = type;
	}

	@Override
	public String toString() {
		return "Variable declaration " + id + " at " + position;
	}

	public String render(String indent) {
		return indent + "Var " + id + " : " + type + "\n";
	}
}