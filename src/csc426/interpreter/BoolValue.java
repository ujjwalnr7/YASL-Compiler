package csc426.interpreter;

public class BoolValue implements Value {
	boolean bool_val;
	public BoolValue(boolean Bool){
		bool_val = Bool;
	}
	public boolean asBool() throws InterpreterException {
		return bool_val;
	}
	public int asInt() throws InterpreterException {
		throw new InterpreterException("Attempt to use a bool as an int");
	}
}