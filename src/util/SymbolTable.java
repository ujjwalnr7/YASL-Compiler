package util;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class SymbolTable<T> {
	private Stack<Map<String, T>> scopes;

	public SymbolTable() {
		scopes = new Stack<>();
	}

	/**
	 * Push a new local scope on top of this table.
	 */
	public void enter() {
		scopes.push(new HashMap<String, T>());
	}

	/**
	 * Discard the innermost local scope from this table.
	 */
	public void exit() {
		scopes.pop();
	}

	/**
	 * Add a binding from an id to a value in the innermost local scope. If
	 * there was a previous binding for the id in this scope, it is returned.
	 * 
	 * @param id
	 *            the name to be bound
	 * @param value
	 *            the corresponding value bound to the name
	 * @return the previous bound value, or null if there was none
	 */
	public T bind(String id, T value) {
		Map<String, T> local = scopes.peek();
		return local.put(id, value);
	}

	/**
	 * Return the first binding found for the given id, starting from the most
	 * local scope and working outwards.
	 * 
	 * @param id
	 *            the name to be searched
	 * @return the corresponding value, or null if none found
	 */
	public T lookup(String id) {
		for (int i = scopes.size() - 1; i >= 0; --i) {
			Map<String, T> scope = scopes.get(i);
			if (scope.containsKey(id)) {
				return scope.get(id);
			}
		}
		return null;
	}
}
