package csc426;

import csc426.AST.*;

public interface ASTVisitor<T> {
	public T visit(Program program);

	public T visit(Block block);

	public T visit(ConstDecl decl);

	public T visit(VarDecl decl);

	public T visit(ProcDecl decl);
	
	public T visit(Param.Val param);
	
	public T visit(Param.Var param);

	public T visit(Stmt.Assign stmt);

	public T visit(Stmt.Call stmt);

	public T visit(Stmt.Sequence stmt);

	public T visit(Stmt.IfThen stmt);

	public T visit(Stmt.IfThenElse stmt);

	public T visit(Stmt.While stmt);

	public T visit(Stmt.Prompt stmt);

	public T visit(Stmt.Prompt2 stmt);

	public T visit(Stmt.Print stmt);

	public T visit(Item.ExprItem item);

	public T visit(Item.StringItem item);

	public T visit(Expr.BinOp expr);

	public T visit(Expr.UnOp expr);

	public T visit(Expr.Num expr);

	public T visit(Expr.Id expr);

	public T visit(Expr.True expr);

	public T visit(Expr.False expr);
}