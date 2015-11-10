package csc426.ast;

import csc426.parser.Position;

/**
 * Base class of statements.
 * 
 * @author bhoward
 */
public abstract class Stmt extends AST {
	Stmt(Position position) {
		super(position);
	}
}