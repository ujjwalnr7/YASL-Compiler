package csc426.interpreter;

public class IntValue implements Value {
	int val;
	public IntValue(int num) {
		val = num;
	}
	public int asInt() throws InterpreterException {
		return val;
	}
	public boolean asBool() throws InterpreterException {
		throw new InterpreterException("Attempt to use a number as a boolean");
	}
}