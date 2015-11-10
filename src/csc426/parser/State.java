package csc426.parser;

import java.util.HashMap;
import java.util.Map;

/**
 * Another Java version, using an enumeration of states and a switch instead of
 * state objects and dynamic dispatch.
 * 
 * @author bhoward
 */
enum State {
	INITIAL_STATE, IDENT_STATE, NUM_STATE, EQUAL_STATE, LESS_STATE, GREATER_STATE, QUOTE_STATE, QUOTE2_STATE, SLASH_STATE, SLASH2_STATE, BRACE_STATE, FINAL_STATE;

	/**
	 * A String containing all of the characters that may start a single-char
	 * operator or punctuation.
	 */
	protected static final String OPCHARS = "+-*;.:(),";

	/**
	 * A Map from lexeme to the corresponding TokenType, for all of the
	 * "fixed lexeme" tokens.
	 */
	protected static Map<String, TokenType> tokenMap;

	// load tokens with fixed lexemes into tokenMap
	static {
		tokenMap = new HashMap<String, TokenType>();
		tokenMap.put("program", TokenType.PROGRAM);
		tokenMap.put("const", TokenType.CONST);
		tokenMap.put("begin", TokenType.BEGIN);
		tokenMap.put("print", TokenType.PRINT);
		tokenMap.put("end", TokenType.END);
		tokenMap.put("div", TokenType.DIV);
		tokenMap.put("mod", TokenType.MOD);
		tokenMap.put("var", TokenType.VAR);
		tokenMap.put("int", TokenType.INT);
		tokenMap.put("bool", TokenType.BOOL);
		tokenMap.put("proc", TokenType.PROC);
		tokenMap.put("if", TokenType.IF);
		tokenMap.put("then", TokenType.THEN);
		tokenMap.put("else", TokenType.ELSE);
		tokenMap.put("while", TokenType.WHILE);
		tokenMap.put("do", TokenType.DO);
		tokenMap.put("prompt", TokenType.PROMPT);
		tokenMap.put("and", TokenType.AND);
		tokenMap.put("or", TokenType.OR);
		tokenMap.put("not", TokenType.NOT);
		tokenMap.put("true", TokenType.TRUE);
		tokenMap.put("false", TokenType.FALSE);
		tokenMap.put("+", TokenType.PLUS);
		tokenMap.put("-", TokenType.MINUS);
		tokenMap.put("*", TokenType.STAR);
		tokenMap.put("=", TokenType.ASSIGN);
		tokenMap.put("==", TokenType.EQUAL);
		tokenMap.put("<>", TokenType.NOTEQUAL);
		tokenMap.put("<=", TokenType.LESSEQUAL);
		tokenMap.put(">=", TokenType.GREATEREQUAL);
		tokenMap.put("<", TokenType.LESS);
		tokenMap.put(">", TokenType.GREATER);
		tokenMap.put(";", TokenType.SEMI);
		tokenMap.put(".", TokenType.PERIOD);
		tokenMap.put(":", TokenType.COLON);
		tokenMap.put("(", TokenType.LPAREN);
		tokenMap.put(")", TokenType.RPAREN);
		tokenMap.put(",", TokenType.COMMA);
	}

	/**
	 * Compute the appropriate next state from the given state and the current
	 * character of the source.
	 * 
	 * @param state
	 * @param source
	 * @return the next State
	 */
	public static State doTransitionStep(State state, Source source, ErrorLog log) {
		switch (state) {
		case INITIAL_STATE:
			/*
			 * The starting state when trying to recognize a token. The type of
			 * token has not yet been determined, so white space and comments
			 * will be skipped until either a distinctive token-starting
			 * character is seen or the input is exhausted.
			 */
			if (source.atEOF()) {
				currentToken = new Token(source.position(), TokenType.EOF, null);
				return FINAL_STATE;
			} else if (Character.isLetter(source.current())) {
				position = source.position();
				lexeme = "" + source.current();
				source.advance();
				return IDENT_STATE;
			} else if (source.current() == '0') {
				position = source.position();
				lexeme = "0";
				source.advance();
				currentToken = new Token(position, TokenType.NUM, lexeme);
				return FINAL_STATE;
			} else if (Character.isDigit(source.current())) {
				position = source.position();
				lexeme = "" + source.current();
				source.advance();
				return NUM_STATE;
			} else if (OPCHARS.indexOf(source.current()) >= 0) {
				position = source.position();
				lexeme = "" + source.current();
				type = tokenMap.get(lexeme);
				source.advance();
				currentToken = new Token(position, type, lexeme);
				return FINAL_STATE;
			} else if (source.current() == '=') {
				position = source.position();
				source.advance();
				return EQUAL_STATE;
			} else if (source.current() == '<') {
				position = source.position();
				source.advance();
				return LESS_STATE;
			} else if (source.current() == '>') {
				position = source.position();
				source.advance();
				return GREATER_STATE;
			} else if (source.current() == '"') {
				position = source.position();
				lexeme = ""; // don't include the surrounding quotes
				source.advance();
				return QUOTE_STATE;
			} else if (Character.isWhitespace(source.current())) {
				source.advance();
				return INITIAL_STATE;
			} else if (source.current() == '/') {
				position = source.position();
				source.advance();
				return SLASH_STATE;
			} else if (source.current() == '{') {
				position = source.position();
				source.advance();
				return BRACE_STATE;
			} else {
				log.add("Error: Unexpected character (" + source.current() + ") at " + source.position());
				source.advance();
				return INITIAL_STATE;
			}

		case IDENT_STATE:
			/*
			 * The state while recognizing an identifier or keyword.
			 */
			if (!source.atEOF() && Character.isLetterOrDigit(source.current())) {
				lexeme += source.current();
				source.advance();
				return IDENT_STATE;
			} else {
				if (tokenMap.containsKey(lexeme)) {
					type = tokenMap.get(lexeme);
				} else {
					type = TokenType.ID;
				}
				currentToken = new Token(position, type, lexeme);
				return FINAL_STATE;
			}

		case NUM_STATE:
			/*
			 * The state while recognizing a non-zero integer literal.
			 */
			if (!source.atEOF() && Character.isDigit(source.current())) {
				lexeme += source.current();
				source.advance();
				return NUM_STATE;
			} else {
				currentToken = new Token(position, TokenType.NUM, lexeme);
				return FINAL_STATE;
			}

		case EQUAL_STATE:
			/*
			 * The state while recognizing = or ==
			 */
			if (!source.atEOF() && source.current() == '=') {
				source.advance();
				currentToken = new Token(position, TokenType.EQUAL, "==");
			} else {
				currentToken = new Token(position, TokenType.ASSIGN, "=");
			}
			return FINAL_STATE;

		case LESS_STATE:
			/*
			 * The state while recognizing <, <>, or <=
			 */
			if (!source.atEOF() && source.current() == '>') {
				source.advance();
				currentToken = new Token(position, TokenType.NOTEQUAL, "<>");
			} else if (!source.atEOF() && source.current() == '=') {
				source.advance();
				currentToken = new Token(position, TokenType.LESSEQUAL, "<=");
			} else {
				currentToken = new Token(position, TokenType.LESS, "<");
			}
			return FINAL_STATE;

		case GREATER_STATE:
			/*
			 * The state while recognizing > or >=
			 */
			if (!source.atEOF() && source.current() == '=') {
				source.advance();
				currentToken = new Token(position, TokenType.GREATEREQUAL, ">=");
			} else {
				currentToken = new Token(position, TokenType.GREATER, ">");
			}
			return FINAL_STATE;

		case QUOTE_STATE:
			/*
			 * The state while recognizing the contents of a string literal
			 */
			if (source.atEOF()) {
				log.add("Error: Unclosed string literal at " + position);
				return INITIAL_STATE;
			} else if (source.current() == '"') {
				source.advance();
				return QUOTE2_STATE;
			} else {
				lexeme += source.current();
				source.advance();
				return QUOTE_STATE;
			}

		case QUOTE2_STATE:
			/*
			 * The state when a quote is seen while recognizing the contents of
			 * a string literal. If followed by another quote, then treat it as
			 * an embedded quote; otherwise, it marks the end of the string
			 */
			if (!source.atEOF() && source.current() == '"') {
				lexeme += source.current();
				source.advance();
				return QUOTE_STATE;
			} else {
				currentToken = new Token(position, TokenType.STRING, lexeme);
				return FINAL_STATE;
			}

		case SLASH_STATE:
			/*
			 * The state while starting to recognize an end-of-line comment,
			 * starting with two forward slashes. It is an error to have only a
			 * single slash.
			 */
			if (!source.atEOF() && source.current() == '/') {
				source.advance();
				return SLASH2_STATE;
			} else {
				log.add("Error: Malformed comment at " + position);
				source.advance();
				return INITIAL_STATE;
			}

		case SLASH2_STATE:
			/*
			 * The state while recognizing the body of an end-of-line comment,
			 * starting with two forward slashes and continuing to the next
			 * newline.
			 */
			if (source.atEOF() || source.current() == '\n') {
				source.advance();
				return INITIAL_STATE;
			} else {
				source.advance();
				return SLASH2_STATE;
			}

		case BRACE_STATE:
			/*
			 * The state while recognizing the body of a brace-delimited
			 * comment, starting with a left curly brace ({) and continuing to
			 * the first right curly brace (}). It is an error to have an
			 * unclosed comment at the end of input.
			 */
			if (source.atEOF()) {
				log.add("Error: Unclosed comment at " + position);
				return INITIAL_STATE;
			} else if (source.current() == '}') {
				source.advance();
				return INITIAL_STATE;
			} else {
				source.advance();
				return BRACE_STATE;
			}

		case FINAL_STATE:
			/*
			 * The final state when a token has been recognized. This will
			 * happen when the current character of the source is one past the
			 * end of the token, so source.advance() should not be called before
			 * starting to recognize the next token.
			 */
			throw new IllegalStateException("No transitions out of final state");
		}

		throw new IllegalStateException("Unknown state");
	}

	/**
	 * @return the most recently scanned Token, or null if none available
	 */
	public static Token currentToken() {
		return currentToken;
	}

	private static Position position = new Position(0, 0);
	private static TokenType type = null;
	private static String lexeme = null;
	private static Token currentToken = null;
}
