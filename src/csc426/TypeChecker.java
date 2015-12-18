package csc426;


import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import csc426.AST.*;
import csc426.AST.Expr.*;
import csc426.AST.Item.*;
import csc426.AST.Param.*;
import csc426.AST.Stmt.*;
import csc426.Value.BoolCell;
import csc426.Value.BoolValue;
import csc426.Value.IntCell;
import csc426.Value.IntValue;
import csc426.Value.ProcValue;

public class TypeChecker implements ASTVisitor<Value> {
	private PrintStream output;
	private SymbolTable<Value> table;
	
	public TypeChecker(Scanner input, PrintStream output) {
		this.output = output;
		this.table = new SymbolTable<>();
	}

	@Override
	public Value visit(Program program) {
		table.enter(program.name);
		program.block.accept(this);
		table.exit();
		return null;
	}

	@Override
	public Value visit(Block block) {
		for (ConstDecl decl : block.consts) {
			decl.accept(this);
		}
		for (VarDecl decl : block.vars) {
			decl.accept(this);
		}
		for (ProcDecl decl : block.procs) {
			try{
				table.add(decl.id, new ProcValue(decl.params, decl.block));
				
			} catch (TableError e){
				throw new TypeCheckError(e.getMessage(), decl);
			}
		}
		for (ProcDecl decl : block.procs) {
			decl.accept(this);
		}
		for (Stmt stmt : block.stmts) {
			stmt.accept(this);
		}
		return null;
	}

	@Override
	public Value visit(ConstDecl decl) {
		try {
			table.add(decl.id, new IntValue(0));
		} catch (TableError e) {
			throw new TypeCheckError(e.getMessage(), decl);
		}
		return null;
	}

	@Override
	public Value visit(VarDecl decl) {
		Value value = null;
		switch (decl.type) {
		case Bool:
			value = new BoolValue(false);
			break;
		case Int:
			value = new IntValue(0);
			break;
		}
		try {
			table.add(decl.id, value);
		} catch (TableError e) { throw new TypeCheckError(e.getMessage(), decl); }
		return null;
	}

	@Override
	public Value visit(ProcDecl decl) {
		table.enter(decl.id);
		for(Param param : decl.params){
			if(param.type == Type.Int){
				try{
					table.add(param.id, new IntValue(0));
				} catch (TableError e) { throw new TypeCheckError(e.getMessage(), decl); }
			} else {
				try{
					table.add(param.id, new BoolValue(false));
				} catch (TableError e) { throw new TypeCheckError(e.getMessage(), decl); }
			}
		}
		decl.block.accept(this);
		table.exit();
		return null;
	}

	//These are token methods
	@Override
	public Value visit(Val param) {
		return null;
	}

	@Override
	public Value visit(Var param) {
		return null;
	}

	//Pretty sure this is causing my problems with the typechecking.. not sure how to fix
	@Override
	public Value visit(Assign stmt) {
		try {
			Value lhs = table.lookup(stmt.id);
			Value rhs = stmt.expr.accept(this);
			if(lhs.getClass() == rhs.getClass()){
				return null;
			} else {
				throw new TypeCheckError("Not the same type.", stmt);
			}
		} catch (TableError e) {
			throw new TypeCheckError(e.getMessage(), stmt);
		}
	}

	@Override
	public Value visit(Call stmt) {
		List<Value> args = new ArrayList<>();
		try {
			Value proc = table.lookup(stmt.id);
			
			for (Expr arg : stmt.args) {
				args.add(arg.accept(this));
			}
			
			proc.match(args, this, table);
			
		} catch (TableError e) {
			throw new TypeCheckError(e.getMessage(), stmt);
		} catch (ValueError e) {
			throw new TypeCheckError(e.getMessage(), stmt);
		}
		
		return null;
	}

	
	@Override
	public Value visit(Sequence stmt) {
		for (Stmt s : stmt.body) {
			s.accept(this);
		}
		return null;
	}

	@Override
	public Value visit(IfThen stmt) {
		Value test = stmt.test.accept(this);
		try {
			if (test.boolValue()) {
				stmt.trueClause.accept(this);
			}
		} catch (ValueError e) {
			throw new TypeCheckError(e.getMessage(), stmt);
		}
		return null;
	}

	@Override
	public Value visit(IfThenElse stmt) {
		Value test = stmt.test.accept(this);
		try {
			if (test.boolValue()) {
				stmt.trueClause.accept(this);
				stmt.falseClause.accept(this);
			} 
		} catch (ValueError e) {
			throw new TypeCheckError(e.getMessage(), stmt);
		}
		return null;
	}

	@Override
	public Value visit(While stmt) {
		Value test = stmt.test.accept(this);
		try {
			while (test.boolValue()) {
				stmt.body.accept(this);
				test = stmt.test.accept(this);
			}
		} catch (ValueError e) {
			throw new TypeCheckError(e.getMessage(), stmt);
		}
		return null;
	}

	@Override
	public Value visit(Prompt stmt) {
		return null;
	}

	@Override
	public Value visit(Prompt2 stmt) {
		try {
			Value lhs = table.lookup(stmt.id);
			if(lhs instanceof IntValue){
				
			} else {
				throw new TypeCheckError("Prompt Statements need Int IDs", stmt);
			}
		} catch (NumberFormatException e) {
			throw new TypeCheckError(e.getMessage(), stmt);
		} catch (TableError e) {
			throw new TypeCheckError(e.getMessage(), stmt);
		}
		return null;
	}

	@Override
	public Value visit(Print stmt) {
		for (Item item : stmt.items) {
			if(item instanceof StringItem)
			{
				
			} 
			else if (item instanceof ExprItem){
				Value i = item.accept(this);
				if(i instanceof IntValue){
					throw new TypeCheckError("Not an Int.", stmt);
				}
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

	@Override
	public Value visit(BinOp expr) {
		Value lhs = expr.left.accept(this);
		Value rhs = expr.right.accept(this);
		switch (expr.op) {
		case And:
		case Or:
			if(lhs instanceof BoolValue && rhs instanceof BoolValue){
				return new BoolValue(false);
			} else {
				throw new TypeCheckError("Op Error", expr);
			}

		case EQ:
		case GE:
		case GT:
		case LE:
		case LT:
		case NE:
			if(lhs instanceof IntValue && rhs instanceof IntValue){
				return new BoolValue(false);
			} else {
				throw new TypeCheckError("Op Error", expr);
			}

		case Minus:
		case Mod:
		case Plus:
		case Times:
		case Div:
			if(lhs instanceof IntValue && rhs instanceof IntValue){
				return new IntValue(0);
			} else {
				throw new TypeCheckError("Op Error", expr);
			}

		default:
			throw new TypeCheckError("Unknown operator", expr);
		}

	}

	@Override
	public Value visit(UnOp expr) {
		Value value = expr.expr.accept(this);
		switch (expr.op) {
		case Neg:
			if(value instanceof IntValue){
				return new IntValue(0);
			} else {
				throw new TypeCheckError("Op Error", expr);
			}
		case Not:
			if(value instanceof IntValue){
				return new IntValue(0);
			} else {
				throw new TypeCheckError("Op Error", expr);
			}
		default:
			throw new TypeCheckError("Unknown operator", expr);
		}

	}

	@Override
	public Value visit(Num expr) {
		return new IntValue(0);
	}

	@Override
	public Value visit(Id expr) {
		try {
			return table.lookup(expr.id);
		} catch (TableError e) {
			throw new TypeCheckError(e.getMessage(), expr);
		}
	}

	@Override
	public Value visit(True expr) {
		return new BoolValue(true);
	}

	@Override
	public Value visit(False expr) {
		return new BoolValue(false);
	}
}