package csc426;

public interface ASTVisitor<T> {
	public T visit(AST.Program var1);

    public T visit(AST.Block var1);

    public T visit(AST.ConstDecl var1);

    public T visit(AST.VarDecl var1);

    public T visit(AST.ProcDecl var1);

    public T visit(AST.Param.Val var1);

    public T visit(AST.Param.Var var1);

    public T visit(AST.Stmt.Assign var1);

    public T visit(AST.Stmt.Call var1);

    public T visit(AST.Stmt.Sequence var1);

    public T visit(AST.Stmt.IfThen var1);

    public T visit(AST.Stmt.IfThenElse var1);

    public T visit(AST.Stmt.While var1);

    public T visit(AST.Stmt.Prompt var1);

    public T visit(AST.Stmt.Prompt2 var1);

    public T visit(AST.Stmt.Print var1);

    public T visit(AST.Item.ExprItem var1);

    public T visit(AST.Item.StringItem var1);

    public T visit(AST.Expr.BinOp var1);

    public T visit(AST.Expr.UnOp var1);

    public T visit(AST.Expr.Num var1);

    public T visit(AST.Expr.Id var1);

    public T visit(AST.Expr.True var1);

    public T visit(AST.Expr.False var1);

}
