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
	
	public void set(int whatever) throws InterpreterException {
		throw new InterpreterException("Attempt to store an int to a bool");
	}
	
	public void set(String string) throws InterpreterException {
		throw new InterpreterException("Attempt to use an int to a bool");
	}
	public boolean asBool() throws InterpreterException {
		return bool_val;
	}
	public int asInt() throws InterpreterException {
		throw new InterpreterException("Attempt to use a bool as an int");
	}
}