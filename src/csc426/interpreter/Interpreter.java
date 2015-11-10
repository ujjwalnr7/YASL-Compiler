package csc426.interpreter;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import util.SymbolTable;
import csc426.ast.*;

public class Interpreter {
	private SymbolTable table;
	private PrintStream consoleOutput;
	private Scanner consoleInput;

	
	public Interpreter(Scanner consoleInput, PrintStream consoleOutput) {
		this.table = new SymbolTable();
		this.consoleInput = consoleInput;
		this.consoleOutput = consoleOutput;
	}
	
	// loop through things in program and parse
	public void run(Program program) {
		table.enter();
		interpretBlock(program.block);
		table.exit();
	}
	
	// unpack the statement and dispatch it to the relevant method
	public void interpretStmt(Stmt s){
		if(s instanceof AssignStmt){
			assignID(((AssignStmt) s).id, ((AssignStmt) s).expr);
		} else if(s instanceof CallStmt){
			interpretProc(((CallStmt) s).id, ((CallStmt) s).args);
		} else if(s instanceof SequenceStmt){
			for(Stmt subStmt : ((SequenceStmt) s).body){
				interpretStmt(subStmt);
			}
		} else if(s instanceof IfThenStmt){
			ifthen(((IfThenStmt) s).test, ((IfThenStmt) s).trueClause);
		} else if(s instanceof IfThenElseStmt){
			ifThenElse(((IfThenElseStmt) s).test, ((IfThenElseStmt) s).trueClause, ((IfThenElseStmt) s).falseClause);
		} else if(s instanceof WhileStmt){
			whileDo(((WhileStmt) s).test, ((WhileStmt) s).body);
		} else if(s instanceof PromptStmt){
			prompt(((PromptStmt) s).message);
		} else if(s instanceof Prompt2Stmt){
			prompt(((Prompt2Stmt) s).message, ((Prompt2Stmt) s).id);
		} else if(s instanceof PrintStmt){
			printItems(((PrintStmt) s).items);
		}
	}
	
	// for each const, var, proc, stmts, interpret
	public void interpretBlock(Block b){
		for(ConstDecl c : b.consts){
			assignConst(c.id, c.value);
		}
		for(VarDecl v : b.vars){
			switch(v.type){
			case Bool:
				declareVar(v.id, "bool");
				break;
			case Int:
				declareVar(v.id, "int");
				break;
			}
		}
		for(ProcDecl p : b.procs){
			declareProc(p.id, p.params, p.block);
		}
		for(Stmt s : b.stmts){
			interpretStmt(s);
		}
	}
	
	/*
	 	case class BinOp(left: Expr, op: Op2, right: Expr) extends Expr
		case class UnOp(op: Op1, expr: Expr) extends Expr
		case class Num(value: Int) extends Expr
		case class Id(id: String) extends Expr
		case object True extends Expr
		case object False extends Expr
	 */
	public Value interpretExpr(Expr e){
		if(e instanceof BinOpExpr){
			return exprOpExpr(((BinOpExpr) e).left, ((BinOpExpr) e).op, ((BinOpExpr) e).right);
		} else if (e instanceof UnOpExpr){
			return OpExpr(((UnOpExpr) e).expr, ((UnOpExpr) e).op);
		} else if (e instanceof NumExpr){
			return new IntValue(((NumExpr) e).value);
		} else if (e instanceof IdExpr){
			return id(((IdExpr) e).id);
		} else if (e instanceof BoolExpr){
			if(((BoolExpr) e).value){
				return trueValue();
			} else {
				return falseValue();
			}
		} else if (e == null){
			throw new InterpreterException("Null expr in interpretExpr");
		}
		
		
		throw new InterpreterException("Expr not caught in interpretExpr");
	}
	

	public void assignConst(String lhs, int val){
		try{
			table.add(lhs, new IntValue(val));
		} catch (Exception e) {
			throw new InterpreterException("Attempt to use a non-int const");
		}
	}
	

	public void declareVar(String id, String type){
		if(type.equals("bool")){
			table.add(id, new BoolCell(false));
		} else if (type.equals("int")){
			table.add(id, new IntCell(0));
		} else {
			throw new InterpreterException("Weird error in declareVar");
		}
	}

	public void declareProc(String id, List<Param> params, Block block){
		table.add(id, new ProcValue(id, params, block));
	}
	

	public void assignID(String id, Expr expr){
		Value lhs;

		try{
			lhs = table.search(id);
		} catch (InterpreterException e) {
			System.exit(0);
		}
		Value rhs = interpretExpr(expr);
		
		table.update(id, rhs);
	}
	
	
	// ID Args ;
	// Lookup the proc in the current scope
	// for each expression, turn it into a list of values
	// enter new scope
	// run the call function below
	// exit the scope
	public void interpretProc(String id, List<Expr> expressions){
		Value procVal = table.search(id);
		
		List<Value> args = new ArrayList<Value>();
		for(Expr e : expressions){
			args.add(interpretExpr(e));
		}
		
		table.enter();
		callProc(procVal.params, procVal.block, args);
		table.exit();
	}
	// match the cases in the semantics, and do what's needed
	public void callProc(List<Param> params, Block block, List<Value> args){
		if(params == null && args.isEmpty()){
			interpretBlock(block);
		} else if (params != null && !args.isEmpty()){
			Param p = params.remove(0);
			Value v = args.remove(0);
			if(p.type == Type.Int){
				table.add(p.id, new IntCell(v.asInt()));
			} else if (p.type == Type.Bool) {
				table.add(p.id, new BoolCell(v.asBool()));
			}
			callProc(params, block, args);
		} else if (params != null && (params.get(0).type == Type.Int) && !args.isEmpty() && (args.get(0) instanceof IntCell)){ // if arg is an IntCell
			Param p = params.remove(0);
			Value v = args.remove(0);
			table.add(p.id, v);
			callProc(params, block, args);
		} else if (params != null && (params.get(0).type == Type.Bool) && !args.isEmpty()  && (args.get(0) instanceof BoolCell)){ // if arg is a BoolCell
			Param p = params.remove(0);
			Value v = args.remove(0);
			table.add(p.id, v);
			callProc(params, block, args);
		}
	}
	
	// begin Stmts end ;
	public void compoundStatement(List<Stmt> stmts){
		for(Stmt s : stmts){
			interpretStmt(s);
		}
	}
	
	// if Expr then Stmt
	public void ifthen(Expr expr, Stmt stmt){
		Value result = interpretExpr(expr);
		if(result instanceof BoolValue){
			if(result.asBool()){
				interpretStmt(stmt);
			}
		} else {
			throw new InterpreterException("Not a bool, bye!");
		}
	}
	
	// if Expr then Stmt1 else Stmt2
	public void ifThenElse(Expr ifExpr, Stmt thenStmt, Stmt elseStmt){
		Value result = interpretExpr(ifExpr);
		if(result instanceof BoolValue){
			if(result.asBool()){
				interpretStmt(thenStmt);
			} else {
				interpretStmt(elseStmt);
			}
		} else {
			throw new InterpreterException("Not a bool, bye!");
		}
	}
	
	// while Expr do Stmt
	public void whileDo(Expr expr, Stmt stmt){
		Value result = interpretExpr(expr);
		if(result instanceof BoolValue){
			while(result.asBool()){
				interpretStmt(stmt);
				result = interpretExpr(expr);
			}
		}else {
			throw new InterpreterException("Not a bool, bye!");
		}
	}
	
	// prompt STRING ;
	public void prompt(String string){
		consoleOutput.print(string);
		String s = consoleInput.nextLine();
	}
	
	// prompt STRING , ID ;
	public void prompt(String string, String ID){
		Value lhs = null;
		try{
			lhs = table.search(ID);
		} catch (InterpreterException e) {
			System.out.println(e);
			table.printTable();
			System.out.println("prompt");
			System.exit(1);
		}
		consoleOutput.print(string + " ");
		String input = consoleInput.nextLine();
		try{
			lhs.set(Integer.parseInt(input));
		} catch (Exception e) {
			System.out.println(e);
			System.exit(0);
		}
	}
	
	// print Items ;
	public void printItems(List<Item> items){
		for(Item i : items){
			if(i.type().equals("string")){
				System.out.print("<s>");
				consoleOutput.println(i.message); // had to add message to Item
			} else if (i.type().equals("expr")){
				System.out.print("<e>");
				Value exprInterp = interpretExpr(i.expr);
				if(exprInterp instanceof IntValue){
					consoleOutput.println(exprInterp.asInt());
				} else if(exprInterp instanceof BoolValue){
					consoleOutput.println(exprInterp.asBool());
				} else {
					consoleOutput.println(exprInterp);
				}
			}
		}
	}
	
	// Expr1 op Expr2
	public Value exprOpExpr(Expr expr1, Op2 op, Expr expr2){
		Value lhs = interpretExpr(expr1);
		Value rhs = interpretExpr(expr2);
		
		switch (op){
		case EQ:
			return new BoolValue(lhs.asInt() == rhs.asInt());
		case NE:
			return new BoolValue(lhs.asInt() != rhs.asInt());
		case LE:
			return new BoolValue(lhs.asInt() <= rhs.asInt());
		case GE:
			return new BoolValue(lhs.asInt() >= rhs.asInt());
		case LT:
			return new BoolValue(lhs.asInt() < rhs.asInt());
		case GT:
			return new BoolValue(lhs.asInt() > rhs.asInt());
		case Plus:
			return new IntValue(lhs.asInt() + rhs.asInt());
		case Minus:
			return new IntValue(lhs.asInt() - rhs.asInt());
		case Times:
			return new IntValue(lhs.asInt() * rhs.asInt());
		case Div:
			return new IntValue(lhs.asInt() / rhs.asInt());
		case Mod:
			return new IntValue(lhs.asInt() % rhs.asInt());
		case And:
			return new BoolValue(lhs.asBool() && rhs.asBool());
		case Or:
			return new BoolValue(lhs.asBool() || rhs.asBool());
		}
		
		return null;
	}
	
	// op Expr
	public Value OpExpr(Expr expr1, Op1 op){
		Value lhs = interpretExpr(expr1);
		
		switch (op){
			case Neg:
				return new IntValue(0 - lhs.asInt());
			case Not:
				return new BoolValue(!lhs.asBool());
		}
		
		return null;
	}
	
	// NUM
	public Value num(int num){
		return new IntValue(num);
	}
	
	// true
	public Value trueValue(){
		return new BoolValue(true);
	}
	
	// false
	public Value falseValue(){
		return new BoolValue(false);
	}
	
	// id
	public Value id(String ID){
		return table.search(ID);
	}
}