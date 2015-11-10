package csc426.interpreter;

import java.io.IOException;
import java.io.PrintStream;
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
	
	public void run(Stmt program) {
		table.enter();
		
		table.exit();
	}
	
	// TODO have this evalute what statement and dispatch it to the correct method
	public void interpretStmt(Stmt s){
		
	}
	
	// const ID = NUM ;
	public void assignConst(String lhs, int num){
		table.add(lhs, new IntValue(num));
	}
	
	// var ID : int ;
	// var ID : bool ;
	public void declareVar(String id, String type){
		if(type == "bool"){
			table.add(id, new BoolCell(false));
		} else if (type == "int"){
			table.add(id, new IntCell(0));
		}
	}
	
	// proc ID Params ; Block ;
	// TODO what parameters and how to handle them
	public void declareProc(String id, List<Param> params, Block block){
		table.add(id, new ProcValue(params, block));
	}
	
	// ID = Expr ;
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
	
	// TODO this, and have it call the relevant method
	public Value interpretExpr(Expr expr){
		return null;
	}
	
	// ID Args ;
	// TODO this seems hard
	public void interpretProc(String id){
		Value procVal = table.search(id);
		
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
	
	// while Expr do Stmt
	
	// prompt STRING ;
	// prompt STRING , ID ;
	
	// print Items ;
	
	// Expr1 op Expr2
	
	// op Expr
	
	// NUM
	// true
	// false
	
	// id
}