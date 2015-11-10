package csc426.ast;

import csc426.parser.Position;

/**
 * Base class of expression ASTs.
 * 
 * @author bhoward
 */
public abstract class Expr extends AST {
	Expr(Position position) {
		super(position);
	}
}