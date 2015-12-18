package csc426;


import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import csc426.AST.*;
import csc426.AST.Expr.*;
import csc426.AST.Item.*;
import csc426.AST.Param.*;
import csc426.AST.Stmt.*;
import csc426.AST.Expr.True;
import csc426.OffsetInformation.*;

public class CodeGenerator implements ASTVisitor<Value> {
	private PrintStream output;
	private SymbolTable<OffsetInformation> table;
	private PrintWriter fileout;
	private int currentLabel;
	
	public CodeGenerator(Scanner input, PrintStream output, PrintWriter fileout) {
		this.output = output;
		this.table = new SymbolTable<>();
		this.fileout = fileout;
	}

	@Override
	public Value visit(Program program) {
		this.currentLabel = 0; 		
		table.enter(program.name);
		program.block.accept(this);
		table.exit();
		fileout.println("HALT");
		return null;
	}

	@Override
	public Value visit(Block block) {
		int s = currentLabel;
		currentLabel++;
		
		fileout.println("BRANCH _" + s);
		
		for (ConstDecl decl : block.consts) { decl.accept(this); }
		table.setOffset(0);
		for (VarDecl decl : block.vars) { decl.accept(this); }
		for(ProcDecl decl : block.procs){
			String label = "_" + currentLabel;
			currentLabel++;
			try {
				table.add(decl.id, new ProcInfo(label, decl.params));
			} catch (TableError e) {
				throw new CodeGeneratorError(e.getMessage(), decl);
			}
		}
		for(ProcDecl decl : block.procs){ decl.accept(this); }
		
		fileout.println("LABEL _" + s);
		int n = block.vars.size();
		int l = table.level();
		fileout.println("ENTER " + l);
		fileout.println("RESERVE " + n);
		
		for(Stmt stmt : block.stmts){
			stmt.accept(this);
		}
		
		fileout.println("DROP " + n);
		fileout.println("EXIT " + l);
		
		return null;
	}

	@Override
	public Value visit(ConstDecl decl) {
		try {
			table.add(decl.id, new ConstInfo(decl.value));
		} catch (TableError e) {
			throw new CodeGeneratorError(e.getMessage(), decl);
		}
		return null;
	}

	@Override
	public Value visit(VarDecl decl) {
		try {
			table.setOffset(table.getOffset()-1);
			
			table.add(decl.id, new VarInfo(table.level(), table.getOffset()));
		} catch (TableError e) {
			throw new CodeGeneratorError(e.getMessage(), decl);
		}
		return null;
	}

	@Override
	public Value visit(ProcDecl decl) {
		try {
			OffsetInformation temp = table.lookup(decl.id);
			table.enter(decl.id);
			table.setParamOffset(1);

			List<Param> reverse = decl.params;
			Collections.reverse(reverse);
			
			for(Param param : reverse){
				param.accept(this);
			}
			
			try {
				fileout.println("LABEL " + temp.label());
			} catch (OffsetInformationError e) {
				throw new CodeGeneratorError(e.getMessage(), decl);
			}
			
			decl.block.accept(this);
			
			fileout.println("RETURN");
			table.exit();
			
		} catch (TableError e) {
			throw new CodeGeneratorError(e.getMessage(), decl);
		}
		
		return null;
	}

	@Override
	public Value visit(Val param) {
		table.setParamOffset(table.getParamOffset() + 1);
		try {
			table.add(param.id, new VarInfo(table.level(), table.getParamOffset()));
		} catch (TableError e) {
			throw new CodeGeneratorError(e.getMessage(), param);
		}
		return null;
	}
	
	private void lvalue(String ID){
		try {
			OffsetInformation i = table.lookup(ID);
			fileout.println("ADDRESS " + i.levelValue() + ", " + i.offsetValue());
			
			if(i instanceof RefInfo){
				fileout.println("LOAD");
			}
		} catch (TableError | OffsetInformationError e) {
			throw new CodeGeneratorError(e.getMessage() + " in lvalue on " + ID, null);
		}
	}
	
	@Override
	public Value visit(Var param) {
		table.setParamOffset(table.getParamOffset() + 1);
		try {
			table.add(param.id, new RefInfo(table.level(), table.getParamOffset()));
		} catch (TableError e) {
			throw new CodeGeneratorError(e.getMessage(), param);
		}
		return null;
	}

	@Override
	public Value visit(Assign stmt) {
		stmt.expr.accept(this);
		lvalue(stmt.id);
		fileout.println("STORE");
		return null;
	}
	
	public void setup(List<Param> params, List<Expr> args){
		if (args.size() == 0 && params.size() == 0) {
			return;
		}
		Iterator<Expr> it = args.iterator();
		for (Param param : params) {
			Expr arg = it.next();
			
			if(param instanceof Val){
				arg.accept(this);
			} else if (param instanceof Var) {
				lvalue(param.id);
			} else {
				throw new CodeGeneratorError("Operation Broke for some reason", param);
			}
		}
	}
	
	@Override
	public Value visit(Call stmt) {
		try {
			OffsetInformation i = table.lookup(stmt.id);
			setup(i.params(), stmt.args);
			fileout.println("CALL " + i.label());
			fileout.print("DROP " + i.params().size());
		} catch (TableError | OffsetInformationError e) {
			throw new CodeGeneratorError(e.getMessage(), stmt);
		}
		
		return null;
	}

	
	@Override
	public Value visit(Sequence stmt) {
		for(Stmt s : stmt.body){
			s.accept(this);
		}
		return null;
	}

	@Override
	public Value visit(IfThen stmt) {
		String y = "_" + currentLabel;
		currentLabel++;
		String n = "_" + currentLabel;
		currentLabel++;
		
		stmt.test.accept(this);
		fileout.println("LABEL " + y);
		stmt.trueClause.accept(this);
		fileout.println("LABEL " + n);
		return null;
	}

	@Override
	public Value visit(IfThenElse stmt) {
		
		String y = "_" + currentLabel;
		currentLabel++;
		String n = "_" + currentLabel;
		currentLabel++;
		String s = "_" + currentLabel;
		currentLabel++;
		
		fileout.println("LABEL " + y);
		stmt.trueClause.accept(this);
		fileout.println("BRANCH " + s);
		fileout.println("LABEL " + n);
		stmt.falseClause.accept(this);
		fileout.println("LABEL " + s);
		
		return null;
	}

	@Override
	public Value visit(While stmt) {
		String y = "_" + currentLabel;
		currentLabel++;
		String n = "_" + currentLabel;
		currentLabel++;
		String s = "_" + currentLabel;
		currentLabel++;
				
		fileout.println("LABEL " + s);
		fileout.println("LABEL " + y);
		stmt.body.accept(this);
		fileout.println("BRANCH " + s);
		fileout.println("LABEL " + n);
		
		return null;
	}

	public void print(String s){
		for(char c : s.toCharArray()){
			fileout.println("CONSTANT " + c);
			fileout.println("WRITECHAR");
		}
	}
	
	@Override
	public Value visit(Prompt stmt) {
		print(stmt.message);
		fileout.println("READLINE");
		return null;
	}

	@Override
	public Value visit(Prompt2 stmt) {
		print(stmt.message + " ");
		fileout.println("READINT");
		lvalue(stmt.id);
		fileout.println("STORE");
		return null;
	}
	
	@Override
	public Value visit(Print stmt) {
		for(Item i : stmt.items){
			if(i instanceof ExprItem){
				i.expr.accept(this);
			} else if (i instanceof StringItem) {
				print(i.message);
			}
		}
		
		return null;
	}

	@Override
	public Value visit(ExprItem item) {
		return null;
	}

	@Override
	public Value visit(StringItem item) {
		return null;
	}
	
	public Value BoolBinOp(BinOp expr, String y, String n){
		Expr lhs = expr.left;
		Expr rhs = expr.right;
		String s;
		switch (expr.op) {
		
		case And:
			s = "_" + currentLabel;
			currentLabel++;
			expr(expr.left, s, n);
			output.println("LABEL " + s);
			expr(expr.right, y, n);
			break;
		case Or:
			s = "_" + currentLabel;
			currentLabel++;
			expr(expr.left, y, s);
			output.println("LABEL " + s);
			expr(expr.left, y, n);
			break;
		case EQ:
			expr(lhs);
			expr(rhs);
			output.println("SUB");
			output.println("BRANCHZERO " + y);
			output.println("BRANCH " + n);
			break;
		case NE:
			expr(lhs);
			expr(rhs);
			output.println("SUB");
			output.println("BRANCHZERO " + n);
			output.println("BRANCH " + y);
			break;
		case LT:
			expr(lhs);
			expr(rhs);
			output.println("SUB");
			output.println("BRANCHNEG " + y);
			output.println("BRANCH " + n);
			break;
		case GE:
			expr(lhs);
			expr(rhs);
			output.println("SUB");
			output.println("BRANCHNEG " + n);
			output.println("BRANCH " + y);
			break;
		case GT:
			expr(lhs);
			expr(rhs);
			output.println("SUB");
			output.println("BRANCHNEG " + y);
			output.println("BRANCH " + n);
			break;
		case LE:
			expr(lhs);
			expr(rhs);
			output.println("SUB");
			output.println("BRANCHNEG " + n);
			output.println("BRANCH " + y);
		default:
			throw new CodeGeneratorError("Unknown operator", expr);
		}
		return null;
	}

	@Override
	public Value visit(BinOp expr) {
		Expr lhs = expr.left;
		Expr rhs = expr.right;
		expr(lhs);
		expr(rhs);
		switch(expr.op){
		case Plus: 
			output.println("ADD");
			break;
		case Minus: 
			output.println("SUB");
			break;
		case Times:
			output.println("MUL");
			break;
		case Div:
			output.println("DIV");
			break;
		case Mod:
			output.println("MOD");
			break;
		default: 
			throw new CodeGeneratorError("Unknown operator", expr);
		}
		
		return null;
	}

	@Override
	public Value visit(UnOp expr) {
		return null;

	}

	@Override
	public Value visit(Num expr) {
		
		return null;
	}

	@Override
	public Value visit(Id expr) {
		return null;
	}

	public Value expr(Expr expr, String y, String n){
		if(expr instanceof True){
			output.println("BRANCH " + y);
		} else if (expr instanceof False){
			output.println("BRANCH " + n);
		} else if (expr instanceof UnOp){
			output.println("CONSTANT 0");
			expr.accept(this);
			output.println("SUB");
		}
		return null;
	}
	public Value expr(Expr expr){
		return null;
	}
	
	@Override
	public Value visit(True expr) {
		output.println("CONSTANT 1");
		return null;
	}

	@Override
	public Value visit(False expr) {
		output.println("CONSTANT 0");
		return null;
	}
}