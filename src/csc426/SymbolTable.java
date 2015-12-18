package csc426;


import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class SymbolTable<T> {
	private Stack<Map<String, T>> scopes;
	private Stack<Integer> offsets;
	private Stack<Integer> paramOffsets;
	private int level;

	public SymbolTable() {
		this.scopes = new Stack<>();
		this.offsets = new Stack<>();
		this.paramOffsets = new Stack<>();
		level = 0;
	}

	public void enter(String name) {
		scopes.push(new HashMap<String, T>());
		offsets.push(-1);
		paramOffsets.push(2);
		level++;
	}

	public void exit() {
		scopes.pop();
		offsets.pop();
		paramOffsets.pop();
		level--;
	}

	public void add(String id, T binding) throws TableError {
		Map<String, T> local = scopes.peek();
		if (local.containsKey(id)) {
			throw new TableError("Duplicate definition of " + id);
		}
		local.put(id, binding);
	}

	public T lookup(String id) throws TableError {
		for (int i = scopes.size() - 1; i >= 0; --i) {
			Map<String, T> scope = scopes.get(i);
			if (scope.containsKey(id)) {
				return scope.get(id);
			}
		}
		throw new TableError("Unknown identifier " + id);
	}
	
	public int getOffset(){
		return offsets.peek();
	}
	public int getParamOffset(){
		return paramOffsets.peek();
	}
	public void setOffset(int i){
		offsets.pop();
		offsets.push(i);
	}
	public void setParamOffset(int i){
		paramOffsets.pop();
		paramOffsets.push(i);
	}
	public int level(){
		return level;
	}
}