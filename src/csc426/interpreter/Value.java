package csc426.interpreter;

import java.util.List;

import csc426.ast.*;

public interface Value {

	Block block = null;
	List<Param> params = null;
	public int asInt() throws InterpreterException;
	public boolean asBool() throws InterpreterException;
	public void set(int newInt);
	public void set(String newString);
}