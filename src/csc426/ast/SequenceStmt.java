package csc426.ast;

import java.util.List;

import csc426.parser.Position;

public final class SequenceStmt extends Stmt {
	public final List<Stmt> body;

	public SequenceStmt(List<Stmt> body, Position position) {
		super(position);
		this.body = body;
	}

	@Override
	public String toString() {
		return "Sequence at " + position;
	}

	public String render(String indent) {
		String result = indent + "Sequence\n";
		for (Stmt stmt : body) {
			result += stmt.render(indent + "  ");
		}
		return result;
	}
}