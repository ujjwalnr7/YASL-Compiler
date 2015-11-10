package csc426.interpreter;

public class BoolCell implements Value {
	boolean bool_val;
	public BoolCell(boolean Bool){
		bool_val = Bool;
	}
	public BoolCell(){
	}
	public void set(boolean Bool){
		bool_val = Bool;
	}
	public boolean asBool() throws InterpreterException {
		return bool_val;
	}
	public int asInt() throws InterpreterException {
		throw new InterpreterException("Attempt to use a bool as an int");
	}
}