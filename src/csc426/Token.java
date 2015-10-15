package csc426;

/**
 * A Token represents one lexical unit of a YASL source program. A Token object
 * stores a position (line and column numbers, each starting from 1), a
 * TokenType, and a lexeme (string value -- for the fixed tokens, this is
 * redundant, but for identifiers and numbers it specifies which particular one
 * it is).
 * 
 * @author Ujjwal Nair
 */
public class Token {
	/**
	 * Construct a Token object given its components.
	 * 
	 * @param line the line number (starting from 1) where the token was found
	 * @param column the column number (starting from 1) where the token started
	 * @param type the TokenType of the token
	 * @param lexeme the string value of the token
	 */
	public Token(Position position, TokenType type, String lexeme) {
		this.position = position;
		this.type = type;
		this.lexeme = lexeme;
	}

	// Override the default toString() for use in development and debugging.
	public String toString() {
        StringBuilder result = new StringBuilder(this.type.toString());
        if (this.type == TokenType.ID || this.type == TokenType.NUM || this.type == TokenType.STRING) {
            result.append(" ").append(this.lexeme);
        }
        result.append(" ").append(this.position);
        return result.toString();
    }

	public final Position position;
	public final TokenType type;
	public final String lexeme;
}
