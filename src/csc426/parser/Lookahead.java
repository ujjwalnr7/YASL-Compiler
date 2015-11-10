package csc426.parser;

import java.io.IOException;
import java.util.EnumSet;

/**
 * Wrapper class around a Lexer that provides the check and match functions used
 * in the parsers discussed in class. Error messages are collected in an
 * ErrorLog.
 * 
 * @author bhoward
 */
public class Lookahead {
	public Lookahead(Lexer lexer, ErrorLog log) {
		this.lexer = lexer;
		this.log = log;
		this.current = lexer.next();
	}

	/**
	 * @return the token under current examination
	 */
	public Token current() {
		return current;
	}

	/**
	 * Check whether the current token has the given type.
	 * 
	 * @param type
	 * @return true if the TokenType matches the current token
	 */
	public boolean check(TokenType type) {
		return current.type == type;
	}

	/**
	 * If the current token has the given type, skip to the next token and
	 * return the matched token. Otherwise, return a fake token of the desired
	 * type, to attempt error recovery.
	 * 
	 * @param type
	 * @return the matched token if successful; a fake token otherwise
	 */
	public Token match(TokenType type) {
		if (current.type == type) {
			return skip();
		} else {
			log.add("Error: Expected a " + type + ", found " + current);
			return new Token(current.position, type, "");
		}
	}

	/**
	 * Skip to the next token and return the skipped token.
	 * 
	 * @return the skipped token
	 */
	public Token skip() {
		Token token = current;
		current = lexer.next();
		return token;
	}

	/**
	 * Close the Lexer (which in turn will close its underlying Source and
	 * Reader)
	 */
	public void close() {
		try {
			lexer.close();
		} catch (IOException e) {
			log.add(e.getMessage());
		}
	}

	public void logError(String message) {
		log.add(message);
	}

	/**
	 * Check that the current token's type is in a set of valid TokenTypes. If
	 * not, log the error and attempt to skip tokens until either a valid token
	 * or a stop token is seen. EOF should always be included in the stop set.
	 * 
	 * @param valid
	 *            the valid TokenTypes at this point
	 * @param stop
	 *            the TokenTypes that signal an end to the synchronization
	 *            attempt
	 */
	public void synchronize(EnumSet<TokenType> valid, EnumSet<TokenType> stop) {
		if (valid.contains(current.type)) {
			return;
		}
		log.add("Error: Unexpected token " + current);
		while (!valid.contains(current.type) && !stop.contains(current.type)) {
			current = lexer.next();
		}
	}

	private Lexer lexer;
	private ErrorLog log;
	private Token current;
}
