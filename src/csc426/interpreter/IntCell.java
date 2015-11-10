package csc426.interpreter;

public class IntCell implements Value {
	int val;
	public IntCell(int num) {
		val = num;
	}
	public IntCell() {
	}
	public void set(int num){
		val = num;
	}
	public int asInt() throws InterpreterException {
		return val;
	}
	public boolean asBool() throws InterpreterException {
		throw new InterpreterException("Attempt to use a number as a boolean");
	}
}