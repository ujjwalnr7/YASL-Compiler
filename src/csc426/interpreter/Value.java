package csc426.interpreter;

public interface Value {

	public int asInt() throws InterpreterException;
	public boolean asBool() throws InterpreterException;
}