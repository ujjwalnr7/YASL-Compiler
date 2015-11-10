package csc426.ast;

import csc426.parser.Position;

public final class Prompt2Stmt extends Stmt {
	public final String message;
	public final String id;

	public Prompt2Stmt(String message, String id, Position position) {
		super(position);
		this.message = message;
		this.id = id;
	}

	@Override
	public String toString() {
		return "Prompt of " + id + " at " + position;
	}

	public String render(String indent) {
		return indent + "Prompt2 \"" + message + "\", " + id + "\n";
	}
}