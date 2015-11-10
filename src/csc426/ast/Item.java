package csc426.ast;

import csc426.parser.*;

/**
 * Base class of print items.
 * 
 * @author bhoward
 */
public abstract class Item extends AST {
	public String message;
	public Expr expr;

	Item(Position position) {
		super(position);
	}

	public String type() {
		// TODO Auto-generated method stub
		return null;
	}
}