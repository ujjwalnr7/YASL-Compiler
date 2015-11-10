package util;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import csc426.interpreter.*;

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

		
		Stack<Map<String,Value>> copy = (Stack<Map<String,Value>>)stack.clone();
		if(copy.empty()){
			throw new InterpreterException("Table is empty.");
		}
		Map<String,Value> currentScope = copy.pop();
		do{
			if(currentScope.containsKey(name)){
				return currentScope.get(name);
			} else {
				currentScope = copy.pop();
			}
		} while (!copy.empty());
		throw new InterpreterException("Symbol " + name + " not found in search.");
	}
	
	public Value update(String name, Value value) throws InterpreterException{


		Stack<Map<String,Value>> popped = new Stack<Map<String,Value>>();
		Map<String,Value> currentScope;
		while (!stack.empty()) {
			currentScope = stack.pop();
			popped.add(currentScope);
			if(currentScope.containsKey(name)){
				if(currentScope.get(name) instanceof IntValue){
					throw new InterpreterException("Symbol " + name + " is a const.");
				}
				currentScope.put(name, value);
				return currentScope.get(name);
			}
		}
		
		while(!popped.empty()){
			stack.add(popped.pop());
		}
		
		throw new InterpreterException("Symbol " + name + " not found in update.");
	}
	
	public void printTable(){
		Stack<Map<String,Value>> copy = (Stack<Map<String,Value>>)stack.clone();
		if (!copy.empty()){
			Map<String,Value> currentScope = copy.pop();
			System.out.print("{");
			for (String key : currentScope.keySet()) {
			    System.out.print(key + " ");
			}
			System.out.println("} ");
		}
	}
}
