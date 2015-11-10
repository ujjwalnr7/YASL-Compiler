package csc426.ast;

import java.util.List;

import csc426.parser.Position;

public final class Block extends AST {
	public final List<ConstDecl> consts;
	public final List<VarDecl> vars;
	public final List<ProcDecl> procs;
	public final List<Stmt> stmts;

	public Block(List<ConstDecl> consts, List<VarDecl> vars, List<ProcDecl> procs, List<Stmt> stmts, Position position) {
		super(position);
		this.consts = consts;
		this.vars = vars;
		this.procs = procs;
		this.stmts = stmts;
	}

	@Override
	public String toString() {
		return "Block at " + position;
	}

	public String render(String indent) {
		String result = indent + "Block\n";
		for (ConstDecl decl : consts) {
			result += decl.render(indent + "  ");
		}
		for (VarDecl decl : vars) {
			result += decl.render(indent + "  ");
		}
		for (ProcDecl decl : procs) {
			result += decl.render(indent + "  ");
		}
		for (Stmt stmt : stmts) {
			result += stmt.render(indent + "  ");
		}
		return result;
	}
}