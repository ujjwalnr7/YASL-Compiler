package csc426.ast;

import csc426.parser.Position;

public final class StringItem extends Item {
	public final String message;

	public StringItem(String message, Position position) {
		super(position);
		this.message = message;
	}

	@Override
	public String toString() {
		return "Print string item at " + position;
	}

	public String render(String indent) {
		return indent + "StringItem \"" + message + "\"\n";
	}
}