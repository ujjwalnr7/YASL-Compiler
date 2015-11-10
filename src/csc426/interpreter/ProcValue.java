package csc426.interpreter;
import csc426.ast.Block;
import csc426.ast.Param;
import java.util.List;

public class ProcValue implements Value {
	List<Param> params;
	Block block;
	
	public ProcValue(List<Param> params, Block block){
		this.params = params;
		this.block = block;
	}

	public int asInt() throws InterpreterException {
		// TODO Auto-generated method stub
		return 0;
	}

	public boolean asBool() throws InterpreterException {
		// TODO Auto-generated method stub
		return false;
	}

}
