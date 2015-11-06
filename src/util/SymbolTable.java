package util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import csc426.interpreter.IntValue;
import csc426.interpreter.InterpreterException;
import csc426.interpreter.Value;

public class SymbolTable {
	Stack<Map<String,Value>> stack;
	
	// Create a new table
	public SymbolTable(){
		stack = new Stack<Map<String,Value>>();
	}
	
	// Enter a new scope
	public void enter(){
		Map<String,Value> newMap = new HashMap<String,Value>();
		stack.push(newMap);
	}
	
	// Exit a scope
	public void exit(){
		stack.pop();
	}
	
	// Add a binding to the current scope
	public void add(String name, Value value){
		Map<String,Value> currentScope = stack.pop();
		currentScope.put(name, value);
		stack.add(currentScope);
	}
	
	// Find a binding: Check current scope, if not, check next scope down.
	public Value search(String name) throws InterpreterException{
		Stack<Map<String,Value>> copy = stack;
		Map<String,Value> currentScope = copy.pop();
		while(!copy.empty()){
			if(currentScope.get(name) != null){
				return currentScope.get(name);
			} else {
				currentScope = copy.pop();
			}
		} 
		if(copy.empty()){
			throw new InterpreterException("Symbol " + name + " not declared.");
		}
		return null;
	}
	
	// update a symbol by the name
	// TODO it's searching and updating a Copy
	// TODO check type
	// TODO make sure you change the intcell, not replace
	public Value update(String name, Value value) throws InterpreterException{
		Stack<Map<String,Value>> copy = stack;
		Map<String,Value> currentScope = copy.pop();
		while(!copy.empty()){
			if(currentScope.get(name) != null){
				if(currentScope.get(name) instanceof IntValue){
					throw new InterpreterException("Symbol " + name + " is a const.");
				}
				currentScope.put(name, value);
				return currentScope.get(name);
			} else {
				currentScope = copy.pop();
			}
		} 
		if(copy.empty()){
			throw new InterpreterException("Symbol " + name + " not declared.");
		}
		return null;
	}
}