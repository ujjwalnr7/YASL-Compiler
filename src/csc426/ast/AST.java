package csc426.ast;

import csc426.parser.Position;

/**
 * Base class of AST nodes. Keeps track of a position.
 * 
 * @author bhoward
 */
public abstract class AST {
	public Position position;

	AST(Position position) {
		this.position = position;
	}

	/**
	 * Return a rendering of the AST with the given initial indentation.
	 * 
	 * @param indent
	 *            String to be inserted at the beginning of each line
	 * @return a String (probably multiple lines) representing the tree
	 */
	public abstract String render(String indent);
}
