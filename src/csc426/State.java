package csc426;


import java.util.HashMap;
import java.util.Map;


public abstract class State {
	/**
	 * Compute the appropriate next state from this state and the current
	 * character of the source.
	 * 
	 * @param source
	 * @return the next State
	 */
	public abstract State step(Source source);

	/**
	 * @return true if a token is ready to be emitted.
	 */
	public boolean done() {
		return false;
	}

	/**
	 * @return the emitted token if done() was true.
	 */
	public Token token() {
		throw new IllegalStateException("Token only available from final state");
	}

	/**
	 * The starting state to recognize a token.
	 */
	public static final State INITIAL_STATE = new InitialState();

	/**
	 * A String containing all of the characters that may start an operator or
	 * punctuation.
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
}

/**
 * The starting state when trying to recognize a token. The type of token has
 * not yet been determined, so white space and comments will be skipped until
 * either a distinctive token-starting character is seen or the input is
 * exhausted.
 */
class InitialState extends State {
	public State step(Source source) {
		if (source.atEOF()) {
			return new FinalState(source.position(), "<EOF>", TokenType.EOF);
		} else if (Character.isLetter(source.current())) {
			return new IdentState(source);
		} else if (source.current() == '0') {
			return new ZeroState(source);
		} else if (Character.isDigit(source.current())) {
			return new NumState(source);
		} else if (OPCHARS.indexOf(source.current()) >= 0) {
			return new OpState(source);
		} else if (source.current() == '=') {
			return new EqualState(source);
		} else if (source.current() == '<') {
			return new LessState(source);
		} else if (source.current() == '>') {
			return new GreaterState(source);
		} else if (Character.isWhitespace(source.current())) {
			return INITIAL_STATE;
		} else if (source.current() == '/') {
			return new SlashState(source);
		} else if (source.current() == '{') {
			return new BraceState(source);
		} else if (source.current() == '"') {
			return new QuoteState(source);
		} else {
			System.err.print("Error: Unexpected character (" + source.current() + ") ");
			System.err.println("at " + source.position());
			return INITIAL_STATE;
		}
	}
}

/**
 * The final state when a token has been recognized. This will happen when the
 * current character of the source is one past the end of the token, so
 * source.advance() should not be called before starting to recognize the next
 * token.
 */
class FinalState extends State {
	public FinalState(Position position, String lexeme, TokenType type) {
		this.token = new Token(position, type, lexeme);
	}

	//No reason for this to be called
	public State step(Source source) {
		return null;
	}

	@Override
	public boolean done() {
		return true;
	}

	@Override
	public Token token() {
		return token;
	}

	private Token token;
}

/**
 * Base class of the states corresponding to the body of a token. Marks the
 * current position when created, and starts a lexeme buffer with the current
 * character from the source.
 */
abstract class MarkState extends State {
	public MarkState(Source source) {
		this.position = source.position();
		this.buffer = new StringBuilder();

		buffer.append(source.current());
	}

	protected Position position;
	protected StringBuilder buffer;
}

/**
 * The state while recognizing an identifier or keyword. The same state object
 * is reused while the token is read; the "state" changes by appending
 * characters to the lexeme buffer.
 */
class IdentState extends MarkState {
	public IdentState(Source source) {
		super(source);
	}

	public State step(Source source) {
		if (!source.atEOF() && Character.isLetterOrDigit(source.current())) {
			buffer.append(source.current());
			return this;
		} else {
			String lexeme = buffer.toString();
			TokenType type;

			if (tokenMap.containsKey(lexeme)) {
				type = tokenMap.get(lexeme);
			} else {
				type = TokenType.ID;
			}
			return new FinalState(position, lexeme, type);
		}
	}
}

/**
 * The state while recognizing a non-zero integer literal. The same state object
 * is reused while the token is read; the "state" changes by appending
 * characters to the lexeme buffer.
 */
class NumState extends MarkState {
	public NumState(Source source) {
		super(source);
	}

	public State step(Source source) {
		if (!source.atEOF() && Character.isDigit(source.current())) {
			buffer.append(source.current());
			return this;
		} else {
			return new FinalState(position, buffer.toString(), TokenType.NUM);
		}
	}
}

/**
 * The state while recognizing a zero integer literal (since no other literals
 * may start with a leading zero).
 */
class ZeroState extends MarkState {
	public ZeroState(Source source) {
		super(source);
	}

	public State step(Source source) {
		return new FinalState(position, buffer.toString(), TokenType.NUM);
	}
}

/**
 * The state while recognizing a single-character operator or punctuation.
 */
class OpState extends MarkState {
	public OpState(Source source) {
		super(source);
	}

	public State step(Source source) {
		String lexeme = buffer.toString();
		TokenType type = tokenMap.get(lexeme);

		return new FinalState(position, lexeme, type);
	}
}

/**
 * The state while recognizing an operator that starts with =.
 */
class EqualState extends MarkState {
	public EqualState(Source source) {
		super(source);
	}

	public State step(Source source) {
		if (!source.atEOF() && source.current() == '=') {
			return new EqualEqualState(position);
		} else {
			return new FinalState(position, "=", TokenType.ASSIGN);
		}
	}
}

/**
 * The state while recognizing the == operator.
 */
class EqualEqualState extends State {
	public EqualEqualState(Position position) {
		this.position = position;
	}

	public State step(Source source) {
		return new FinalState(position, "==", TokenType.EQUAL);
	}

	private Position position;
}

/**
 * The state while recognizing an operator that starts with <.
 */
class LessState extends MarkState {
	public LessState(Source source) {
		super(source);
	}

	public State step(Source source) {
		if (!source.atEOF() && source.current() == '=') {
			return new LessEqualState(position);
		} else if (!source.atEOF() && source.current() == '>') {
			return new LessGreaterState(position);
		} else {
			return new FinalState(position, "<", TokenType.LESS);
		}
	}
}

/**
 * The state while recognizing the <= operator.
 */
class LessEqualState extends State {
	public LessEqualState(Position position) {
		this.position = position;
	}

	public State step(Source source) {
		return new FinalState(position, "<=", TokenType.LESSEQUAL);
	}

	private Position position;
}

/**
 * The state while recognizing the <> operator.
 */
class LessGreaterState extends State {
	public LessGreaterState(Position position) {
		this.position = position;
	}

	public State step(Source source) {
		return new FinalState(position, "<>", TokenType.NOTEQUAL);
	}

	private Position position;
}

/**
 * The state while recognizing an operator that starts with >.
 */
class GreaterState extends MarkState {
	public GreaterState(Source source) {
		super(source);
	}

	public State step(Source source) {
		if (!source.atEOF() && source.current() == '=') {
			return new GreaterEqualState(position);
		} else {
			return new FinalState(position, ">", TokenType.GREATER);
		}
	}
}

/**
 * The state while recognizing the >= operator.
 */
class GreaterEqualState extends State {
	public GreaterEqualState(Position position) {
		this.position = position;
	}

	public State step(Source source) {
		return new FinalState(position, ">=", TokenType.GREATEREQUAL);
	}

	private Position position;
}

/**
 * The state while starting to recognize an end-of-line comment, starting with
 * two forward slashes. It is an error to have only a single slash.
 */
class SlashState extends State {
	public SlashState(Source source) {
		this.position = source.position();
	}

	public State step(Source source) {
		if (source.current() == '/') {
			return new Slash2State();
		} else {
			System.err.print("Error: Malformed comment ");
			System.err.println("at " + position);
			return INITIAL_STATE;
		}
	}

	public boolean done() {
		return false;
	}

	private Position position;
}

/**
 * The state while recognizing the body of an end-of-line comment, starting with
 * two forward slashes and continuing to the next newline.
 */
class Slash2State extends State {
	public State step(Source source) {
		if (source.atEOF() || source.current() == '\n') {
			return INITIAL_STATE;
		} else {
			return this;
		}
	}

	public boolean done() {
		return false;
	}
}

/**
 * The state while recognizing the body of a brace-delimited comment, starting
 * with a left curly brace ({) and continuing to the first right curly brace
 * (}). It is an error to have an unclosed comment at the end of input.
 */
class BraceState extends State {
	public BraceState(Source source) {
		this.position = source.position();
	}

	public State step(Source source) {
		if (source.atEOF()) {
			System.err.print("Error: Unclosed comment ");
			System.err.println("at " + position);
			return INITIAL_STATE;
		} else if (source.current() == '}') {
			return INITIAL_STATE;
		} else {
			return this;
		}
	}

	public boolean done() {
		return false;
	}

	private Position position;
}

/**
 * The state while recognizing a string literal, starting with a double-quote
 * (") and continuing to the next double-quote that is not paired with an
 * immediately following double-quote (such pairs are reduced to a single
 * double-quote as part of the string). It is an error to have an unclosed
 * string literal at the end of input.
 */
class QuoteState extends MarkState {
	public QuoteState(Source source) {
		super(source);
	}

	public State step(Source source) {
		if (source.atEOF()) {
			System.err.print("Error: Unclosed string literal ");
			System.err.println("at " + position);
			return INITIAL_STATE;
		} else if (source.current() == '"') {
			return new Quote2State(this);
		} else {
			buffer.append(source.current());
			return this;
		}
	}
}

/**
 * The state when a double-quote is seen while processing a string literal. If
 * another double-quote follows it, then the literal continues and just one
 * double-quote is added to the string; otherwise, it is the end of the string.
 */
class Quote2State extends State {
	public Quote2State(QuoteState parent) {
		this.parent = parent;
	}

	public State step(Source source) {
		if (source.atEOF() || source.current() != '"') {
			String lexeme = parent.buffer.substring(1);
			return new FinalState(parent.position, lexeme, TokenType.STRING);
		} else {
			parent.buffer.append(source.current());
			return parent;
		}
	}

	private QuoteState parent;
}