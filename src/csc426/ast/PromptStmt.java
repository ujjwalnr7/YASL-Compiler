package csc426.ast;

import csc426.parser.Position;

public final class PromptStmt extends Stmt {
	public final String message;

	public PromptStmt(String message, Position position) {
		super(position);
		this.message = message;
	}

	@Override
	public String toString() {
		return "Prompt statement at " + position;
	}

	public String render(String indent) {
		return indent + "Prompt \"" + message + "\"\n";
	}
}