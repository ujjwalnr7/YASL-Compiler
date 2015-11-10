package csc426.interpreter;
import csc426.ast.*;
import java.util.List;

public class ProcValue implements Value {
	List<Param> params;
	Block block;
	String name;
	
	public ProcValue(String name, List<Param> params, Block block){
		this.name = name;
		this.params = params;
		this.block = block;
	}

	public void set(int whatever) throws InterpreterException {
		throw new InterpreterException("Attempted to store an Int to a Procedure");
	}
	
	public void set(String string) throws InterpreterException {
		throw new InterpreterException("Attempted to use an Int to a Procedure");
	}
	
	public int asInt() throws InterpreterException {
		throw new InterpreterException("Attempted to get a Procedure as an Int");
	}

	public boolean asBool() throws InterpreterException {
		throw new InterpreterException("Attempted to get a Procedure as a Boolean");
	}

}
