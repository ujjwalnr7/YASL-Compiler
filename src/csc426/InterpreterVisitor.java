package csc426;

import java.io.PrintStream;
import java.util.ArrayList;
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

public class InterpreterVisitor implements ASTVisitor<Value> {
	private Scanner input;
	private PrintStream output;
	private SymbolTable<Value> table;

	public InterpreterVisitor(Scanner input, PrintStream output) {
		this.input = input;
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
			table.add(decl.id, new IntValue(decl.value));
		} catch (TableError e) {
			throw new InterpreterError(e.getMessage(), decl);
		}
		return null;
	}

	@Override
	public Value visit(VarDecl decl) {
		Value value = null;
		switch (decl.type) {
		case Bool:
			value = new BoolCell();
			break;
		case Int:
			value = new IntCell();
			break;
		}
		try {
			table.add(decl.id, value);
		} catch (TableError e) {
			throw new InterpreterError(e.getMessage(), decl);
		}
		return null;
	}

	@Override
	public Value visit(ProcDecl decl) {
		try {
			table.add(decl.id, new ProcValue(decl.params, decl.block));
		} catch (TableError e) {
			throw new InterpreterError(e.getMessage(), decl);
		}
		return null;
	}

	//Token Methods, They dont do anything
	@Override
	public Value visit(Val param) {
		return null;
	}

	@Override
	public Value visit(Var param) {
		return null;
	}

	@Override
	public Value visit(Assign stmt) {
		try {
			Value lhs = table.lookup(stmt.id);
			Value rhs = stmt.expr.accept(this);
			lhs.set(rhs);
		} catch (ValueError e) {
			throw new InterpreterError(e.getMessage(), stmt);
		} catch (TableError e) {
			throw new InterpreterError(e.getMessage(), stmt);
		}
		return null;
	}

	@Override
	public Value visit(Call stmt) {
		try {
			Value proc = table.lookup(stmt.id);
			List<Value> args = new ArrayList<>();
			for (Expr arg : stmt.args) {
				args.add(arg.accept(this));
			}
			
			table.enter(stmt.id);
			proc.call(args, this, table);
			table.exit();
		} catch (ValueError e) {
			throw new InterpreterError(e.getMessage(), stmt);
		} catch (TableError e) {
			throw new InterpreterError(e.getMessage(), stmt);
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
			throw new InterpreterError(e.getMessage(), stmt);
		}
		return null;
	}

	@Override
	public Value visit(IfThenElse stmt) {
		Value test = stmt.test.accept(this);
		try {
			if (test.boolValue()) {
				stmt.trueClause.accept(this);
			} else {
				stmt.falseClause.accept(this);
			}
		} catch (ValueError e) {
			throw new InterpreterError(e.getMessage(), stmt);
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
			throw new InterpreterError(e.getMessage(), stmt);
		}
		return null;
	}

	@Override
	public Value visit(Prompt stmt) {
		output.print(stmt.message);
		input.nextLine();
		return null;
	}

	@Override
	public Value visit(Prompt2 stmt) {
		try {
			Value lhs = table.lookup(stmt.id);
			output.print(stmt.message + " ");
			String line = input.nextLine();
			Value rhs = new IntValue(Integer.parseInt(line));
			lhs.set(rhs);
		} catch (NumberFormatException e) {
			throw new InterpreterError(e.getMessage(), stmt);
		} catch (ValueError e) {
			throw new InterpreterError(e.getMessage(), stmt);
		} catch (TableError e) {
			throw new InterpreterError(e.getMessage(), stmt);
		}
		return null;
	}

	@Override
	public Value visit(Print stmt) {
		for (Item item : stmt.items) {
			item.accept(this);
		}
		output.println();
		return null;
	}

	@Override
	public Value visit(ExprItem item) {
		Value value = item.expr.accept(this);
		try {
			output.print(value.intValue());
		} catch (ValueError e) {
			throw new InterpreterError(e.getMessage(), item);
		}
		return null;
	}

	@Override
	public Value visit(StringItem item) {
		output.print(item.message);
		return null;
	}

	@Override
	public Value visit(BinOp expr) {
		Value lhs = expr.left.accept(this);
		Value rhs = expr.right.accept(this);
		try {
			switch (expr.op) {
			case And:
				return new BoolValue(lhs.boolValue() && rhs.boolValue());
			case Div:
				return new IntValue(lhs.intValue() / rhs.intValue());
			case EQ:
				return new BoolValue(lhs.intValue() == rhs.intValue());
			case GE:
				return new BoolValue(lhs.intValue() >= rhs.intValue());
			case GT:
				return new BoolValue(lhs.intValue() > rhs.intValue());
			case LE:
				return new BoolValue(lhs.intValue() <= rhs.intValue());
			case LT:
				return new BoolValue(lhs.intValue() < rhs.intValue());
			case Minus:
				return new IntValue(lhs.intValue() - rhs.intValue());
			case Mod:
				return new IntValue(lhs.intValue() % rhs.intValue());
			case NE:
				return new BoolValue(lhs.intValue() != rhs.intValue());
			case Or:
				return new BoolValue(lhs.boolValue() || rhs.boolValue());
			case Plus:
				return new IntValue(lhs.intValue() + rhs.intValue());
			case Times:
				return new IntValue(lhs.intValue() * rhs.intValue());
			default:
				throw new InterpreterError("Unknown operator", expr);
			}
		} catch (ValueError e) {
			throw new InterpreterError(e.getMessage(), expr);
		}
	}

	@Override
	public Value visit(UnOp expr) {
		Value value = expr.expr.accept(this);
		try {
			switch (expr.op) {
			case Neg:
				return new IntValue(-value.intValue());
			case Not:
				return new BoolValue(!value.boolValue());
			default:
				throw new InterpreterError("Unknown operator", expr);
			}
		} catch (ValueError e) {
			throw new InterpreterError(e.getMessage(), expr);
		}
	}

	@Override
	public Value visit(Num expr) {
		return new IntValue(expr.value);
	}

	@Override
	public Value visit(Id expr) {
		try {
			return table.lookup(expr.id);
		} catch (TableError e) {
			throw new InterpreterError(e.getMessage(), expr);
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