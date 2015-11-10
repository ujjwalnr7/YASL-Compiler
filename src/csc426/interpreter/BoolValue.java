package csc426.interpreter;

public class BoolValue implements Value {
	boolean bool_val;
	public BoolValue(boolean Bool){
		bool_val = Bool;
	}
	public boolean asBool() throws InterpreterException {
		return bool_val;
	}
	
	public void set(int whatever) throws InterpreterException {
		throw new InterpreterException("Attempted to store an int in a bool");
	}
	
	public void set(String string) throws InterpreterException {
		throw new InterpreterException("Attempted to use an int in a bool");
	}
	
	public int asInt() throws InterpreterException {
		throw new InterpreterException("Attempted to use a bool as an int");
	}
}