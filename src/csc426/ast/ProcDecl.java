package csc426.ast;

import java.util.List;

import csc426.parser.Position;

public final class ProcDecl extends AST {
	public final String id;
	public final List<Param> params;
	public final Block block;

	public ProcDecl(String id, List<Param> params, Block block, Position position) {
		super(position);
		this.id = id;
		this.params = params;
		this.block = block;
	}

	@Override
	public String toString() {
		return "Procedure declaration " + id + " at " + position;
	}

	public String render(String indent) {
		String result = indent + "Proc " + id + "\n";
		for (Param param : params) {
			result += param.render(indent + "  ");
		}
		result += block.render(indent + "  ");
		return result;
	}
}