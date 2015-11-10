package csc426.ast;

import java.util.List;

import csc426.parser.Position;

public final class PrintStmt extends Stmt {
	public final List<Item> items;

	public PrintStmt(List<Item> items, Position position) {
		super(position);
		this.items = items;
	}

	@Override
	public String toString() {
		return "Print statement at " + position;
	}

	public String render(String indent) {
		String result = indent + "Print\n";
		for (Item item : items) {
			result += item.render(indent + "  ");
		}
		return result;
	}
}