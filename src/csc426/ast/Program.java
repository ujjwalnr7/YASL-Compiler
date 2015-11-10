package csc426.ast;

import csc426.parser.*;

public final class Program extends AST {
	public final String name;
	public final Block block;

	public Program(String name, Block block, Position position) {
		super(position);
		this.name = name;
		this.block = block;
	}

	@Override
	public String toString() {
		return "Program " + name + " at " + position;
	}

	public String render(String indent) {
		String result = indent + "Program " + name + "\n";
		result += block.render(indent + "  ");
		return result;
	}
}