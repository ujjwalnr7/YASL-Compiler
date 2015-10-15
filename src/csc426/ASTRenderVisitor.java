package csc426;
import java.util.List;

public class ASTRenderVisitor implements ASTVisitor<String>{
	private String indent = "";

    void indent() {
        this.indent = String.valueOf(this.indent) + "  ";
    }

    void outdent() {
        this.indent = this.indent.substring(2);
    }

    String line(String text) {
        return String.valueOf(this.indent) + text + "\n";
    }

    @Override
    public String visit(AST.Program program) {
        String result = this.line("Program " + program.name);
        this.indent();
        result = String.valueOf(result) + (String)program.block.accept(this);
        this.outdent();
        return result;
    }

    @Override
    public String visit(AST.Block block) {
        String result = this.line("Block");
        this.indent();
        for (AST.ConstDecl decl22 : block.consts) {
            result = String.valueOf(result) + (String)decl22.accept(this);
        }
        for (AST.VarDecl decl : block.vars) {
            result = String.valueOf(result) + (String)decl.accept(this);
        }
        for (AST.ProcDecl decl2 : block.procs) {
            result = String.valueOf(result) + (String)decl2.accept(this);
        }
        for (AST.Stmt stmt : block.stmts) {
            result = String.valueOf(result) + (String)stmt.accept(this);
        }
        this.outdent();
        return result;
    }

    @Override
    public String visit(AST.ConstDecl decl) {
        String result = this.line("Const " + decl.id + " = " + decl.value);
        return result;
    }

    @Override
    public String visit(AST.VarDecl decl) {
        String result = this.line("Var " + decl.id + " : " + (Object)decl.type);
        return result;
    }

    @Override
    public String visit(AST.ProcDecl decl) {
        String result = this.line("Proc " + decl.id);
        this.indent();
        for (AST.Param param : decl.params) {
            result = String.valueOf(result) + (String)param.accept(this);
        }
        result = String.valueOf(result) + (String)decl.block.accept(this);
        this.outdent();
        return result;
    }

    @Override
    public String visit(AST.Param.Val param) {
        String result = this.line("Val " + param.id + " : " + (Object)param.type);
        return result;
    }

    @Override
    public String visit(AST.Param.Var param) {
        String result = this.line("Var " + param.id + " : " + (Object)param.type);
        return result;
    }

    @Override
    public String visit(AST.Stmt.Assign stmt) {
        String result = this.line("Assign " + stmt.id);
        this.indent();
        result = String.valueOf(result) + (String)stmt.expr.accept(this);
        this.outdent();
        return result;
    }

    @Override
    public String visit(AST.Stmt.Call stmt) {
        String result = this.line("Call " + stmt.id);
        this.indent();
        for (AST.Expr arg : stmt.args) {
            result = String.valueOf(result) + (String)arg.accept(this);
        }
        this.outdent();
        return result;
    }

    @Override
    public String visit(AST.Stmt.Sequence stmt) {
        String result = this.line("Sequence");
        this.indent();
        for (AST.Stmt stmt2 : stmt.body) {
            result = String.valueOf(result) + (String)stmt2.accept(this);
        }
        this.outdent();
        return result;
    }

    @Override
    public String visit(AST.Stmt.IfThen stmt) {
        String result = this.line("IfThen");
        this.indent();
        result = String.valueOf(result) + (String)stmt.test.accept(this);
        result = String.valueOf(result) + (String)stmt.trueClause.accept(this);
        this.outdent();
        return result;
    }

    @Override
    public String visit(AST.Stmt.IfThenElse stmt) {
        String result = this.line("IfThenElse");
        this.indent();
        result = String.valueOf(result) + (String)stmt.test.accept(this);
        result = String.valueOf(result) + (String)stmt.trueClause.accept(this);
        result = String.valueOf(result) + (String)stmt.falseClause.accept(this);
        this.outdent();
        return result;
    }

    @Override
    public String visit(AST.Stmt.While stmt) {
        String result = this.line("While");
        this.indent();
        result = String.valueOf(result) + (String)stmt.test.accept(this);
        result = String.valueOf(result) + (String)stmt.body.accept(this);
        this.outdent();
        return result;
    }

    @Override
    public String visit(AST.Stmt.Prompt stmt) {
        String result = this.line("Prompt \"" + stmt.message + "\"");
        return result;
    }

    @Override
    public String visit(AST.Stmt.Prompt2 stmt) {
        String result = this.line("Prompt2 \"" + stmt.message + "\", " + stmt.id);
        return result;
    }

    @Override
    public String visit(AST.Stmt.Print stmt) {
        String result = this.line("Print");
        this.indent();
        for (AST.Item item : stmt.items) {
            result = String.valueOf(result) + (String)item.accept(this);
        }
        this.outdent();
        return result;
    }

    @Override
    public String visit(AST.Item.ExprItem item) {
        String result = this.line("ExprItem");
        this.indent();
        result = String.valueOf(result) + (String)item.expr.accept(this);
        this.outdent();
        return result;
    }

    @Override
    public String visit(AST.Item.StringItem item) {
        String result = this.line("StringItem \"" + item.message + "\"");
        return result;
    }

    @Override
    public String visit(AST.Expr.BinOp expr) {
        String result = this.line("BinOp " + (Object)expr.op);
        this.indent();
        result = String.valueOf(result) + (String)expr.left.accept(this);
        result = String.valueOf(result) + (String)expr.right.accept(this);
        this.outdent();
        return result;
    }

    @Override
    public String visit(AST.Expr.UnOp expr) {
        String result = this.line("UnOp " + (Object)expr.op);
        this.indent();
        result = String.valueOf(result) + (String)expr.expr.accept(this);
        this.outdent();
        return result;
    }

    @Override
    public String visit(AST.Expr.Num expr) {
        String result = this.line("Num " + expr.value);
        return result;
    }

    @Override
    public String visit(AST.Expr.Id expr) {
        String result = this.line("Id " + expr.id);
        return result;
    }

    @Override
    public String visit(AST.Expr.True expr) {
        String result = this.line("True");
        return result;
    }

    @Override
    public String visit(AST.Expr.False expr) {
        String result = this.line("False");
        return result;
    }


}
