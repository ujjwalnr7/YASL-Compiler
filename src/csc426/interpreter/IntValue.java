package csc426.interpreter;

public class IntValue implements Value {
	int val;
	public IntValue(int num) {
		val = num;
	}
	
	public void set(int num) throws InterpreterException {
		val = num;
	}
	
	public void set(String string) throws InterpreterException {
		throw new InterpreterException("Attempted to use a String to a Bool");
	}
	
	public int asInt() throws InterpreterException {
		return val;
	}
	public boolean asBool() throws InterpreterException {
		throw new InterpreterException("Attempted to use a Value as a Boolean");
	}
}