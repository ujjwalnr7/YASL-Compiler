package csc426.ast;

import csc426.parser.Position;

/**
 * Base class of print items.
 * 
 * @author bhoward
 */
public abstract class Item extends AST {
	Item(Position position) {
		super(position);
	}
}