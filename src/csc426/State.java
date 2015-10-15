package csc426;
import java.util.HashMap;
import java.util.Map;

/**
 * Class for all the states of the state machine for lexical analysis of the YASL subset.
 * for project 1.  Starting with INITIAL_STATE, repeatedly call step(source) to get a
 * new state. If done() returns true, then the Token is given by token();
 * otherwise, do source.advance() and repeat (see Scanner.next()).
 * 
 * @author Ujjwal Nair
 */

public abstract class State {
	/**
	 * @param Source
	 * @return The next state of the machine
	 */
	public abstract State step(Source source);
	
	/**
	 * If done() returns true, then the Token is given by token();
	 * otherwise, do source.advance() and repeat.
	 * @return true if a token is ready to be emitted
	 * 
	 */
	public boolean done(){
		return false;
	}
	
	/**
	 * @return the emitted token if done() was true.
	 */
	public Token token() {
		throw new IllegalStateException("Token only available from final state");
	}
	
	public static State INITIAL_STATE = new InitialState();
	
	/**
	 * A String containing all of the characters that may start an operator or
	 * punctuation.
	 */	
	protected static final String OPCHARS = "+-*;.:(),";
	
	/**
	 * A Map from lexeme to the corresponding TokenType, for all of the
	 * "fixed lexeme" tokens.
	 */
	protected static Map<String, TokenType> tokenMap = new HashMap<String, TokenType>();

	// load tokens with fixed lexemes into tokenMap
	static {
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

class InitialState extends State 
{
    InitialState() {}

    @Override
    public State step(Source source) {
        if (source.atEOF()) {
            return new FinalState(source.position(), "<EOF>", TokenType.EOF);
        }
        if (Character.isLetter(source.current())) {
            return new IdentState(source);
        }
        if (source.current() == '0') {
            return new ZeroState(source);
        }
        if (Character.isDigit(source.current())) {
            return new NumState(source);
        }
        if ("+-*;.:(),".indexOf(source.current()) >= 0) {
            return new OpState(source);
        }
        if (source.current() == '=') {
            return new EqualState(source);
        }
        if (source.current() == '<') {
            return new LessState(source);
        }
        if (source.current() == '>') {
            return new GreaterState(source);
        }
        if (Character.isWhitespace(source.current())) {
            return INITIAL_STATE;
        }
        if (source.current() == '/') {
            return new SlashState(source);
        }
        if (source.current() == '{') {
            return new BraceState(source);
        }
        if (source.current() == '\"') {
            return new QuoteState(source);
        }
        System.err.print("Error: Unexpected character (" + source.current() + ") ");
        System.err.println("at " + source.position());
        return INITIAL_STATE;
    }
}



class FinalState extends State 
{
    private Token token;

    public FinalState(Position position, String lexeme, TokenType type) {
        this.token = new Token(position, type, lexeme);
    }

    @Override
    public State step(Source source) {
        return null;
    }

    @Override
    public boolean done() {
        return true;
    }

    @Override
    public Token token() {
        return this.token;
    }
}


abstract class MarkState extends State 
{
    protected Position position;
    protected StringBuilder buffer;

    public MarkState(Source source) {
        this.position = source.position();
        this.buffer = new StringBuilder();
        this.buffer.append(source.current());
    }
}

/**
 * The state while recognizing a non-zero integer literal. The same state object
 * is reused while the token is read; the "state" changes by appending
 * characters to the lexeme buffer.
 */
class NumState extends MarkState 
{
    public NumState(Source source) {
        super(source);
    }

    @Override
    public State step(Source source) {
        if (!source.atEOF() && Character.isDigit(source.current())) {
            this.buffer.append(source.current());
            return this;
        }
        return new FinalState(this.position, this.buffer.toString(), TokenType.NUM);
    }
}


class IdentState extends MarkState 
{
    public IdentState(Source source) {
        super(source);
    }

    @Override
    public State step(Source source) {
        if (!source.atEOF() && Character.isLetterOrDigit(source.current())) {
            this.buffer.append(source.current());
            return this;
        }
        String lexeme = this.buffer.toString();
        TokenType type = tokenMap.containsKey(lexeme) ? (TokenType)tokenMap.get(lexeme) : TokenType.ID;
        return new FinalState(this.position, lexeme, type);
    }
}



/**
 * The state while recognizing a zero integer literal (since no other literals
 * may start with a leading zero).
 */
class ZeroState extends MarkState 
{
    public ZeroState(Source source) {
        super(source);
    }

    @Override
    public State step(Source source) {
        return new FinalState(this.position, this.buffer.toString(), TokenType.NUM);
    }
}


/**
 * The state while recognizing an operator or punctuation. Currently, all of
 * these are single-character tokens, but it may be extended along the lines of
 * IdentState or NumState to handle multiple characters.
 */
class OpState extends MarkState 
{
    public OpState(Source source) {
        super(source);
    }

    @Override
    public State step(Source source) {
        String lexeme = this.buffer.toString();
        TokenType type = (TokenType)tokenMap.get(lexeme);
        return new FinalState(this.position, lexeme, type);
    }
}


/**
 * The state while starting to recognize an end-of-line comment, starting with
 * two forward slashes. It is an error to have only a single slash.
 */
class SlashState extends State {
	public SlashState(Source source) 
	{
		this.line = source.line;
		this.column = source.column;
	}

	public State step(Source source) {
		if (source.current == '/') 
		{
			return new Slash2State();
		} 
		
		else 
		{
			System.err.print("Error: Malformed comment ");
			System.err.println("at line " + line + ", column " + column);
			return INITIAL_STATE;
		}
	}

	public boolean done() 
	{
		return false;
	}

	private int line, column;
}

/**
 * The state while recognizing the body of an end-of-line comment, starting with
 * two forward slashes and continuing to the next newline.
 */
class Slash2State extends State {
	public State step(Source source) 
	{
		if (source.atEOF || source.current == '\n') 
		{
			return INITIAL_STATE;
		} 
		
		else 
		{
			return this;
		}
	}

	public boolean done() 
	{
		return false;
	}
}

class OpenCommaState extends State{
	
	public OpenCommaState(Source source) 
	{
		this.line = source.line;
		this.column = source.column;
	}
	
	public State step(Source source) 
	{
		if(source.current == '}')
		{
			return INITIAL_STATE;
		}
		
		else if (source.atEOF) 
		{
			System.err.print("Error: Malformed comment ");
			System.err.println("at line " + line + ", column " + column);
			return INITIAL_STATE;
		} 
		
		else 
		{
			return this;
		}
	}

	public boolean done() 
	{
		return false;
	}
	
	private int line, column;
}


class BraceState extends State {
    private Position position;

    public BraceState(Source source) {
        this.position = source.position();
    }

    @Override
    public State step(Source source) {
        if (source.atEOF()) {
            System.err.print("Error: Unclosed comment ");
            System.err.println("at " + this.position);
            return INITIAL_STATE;
        }
        if (source.current() == '}') {
            return INITIAL_STATE;
        }
        return this;
    }

    @Override
    public boolean done() {
        return false;
    }
}

class QuoteState extends MarkState {
    public QuoteState(Source source) {
        super(source);
    }

    @Override
    public State step(Source source) {
        if (source.atEOF()) {
            System.err.print("Error: Unclosed string literal ");
            System.err.println("at " + this.position);
            return INITIAL_STATE;
        }
        if (source.current() == '\"') {
            return new Quote2State(this);
        }
        this.buffer.append(source.current());
        return this;
    }
}

class Quote2State extends State {
    private QuoteState parent;

    public Quote2State(QuoteState parent) {
        this.parent = parent;
    }

    @Override
    public State step(Source source) {
        if (source.atEOF() || source.current() != '\"') {
            String lexeme = this.parent.buffer.substring(1);
            return new FinalState(this.parent.position, lexeme, TokenType.STRING);
        }
        this.parent.buffer.append(source.current());
        return this.parent;
    }
}

class EqualState
extends MarkState {
    public EqualState(Source source) {
        super(source);
    }

    @Override
    public State step(Source source) {
        if (!(source.atEOF() || source.current() != '=')) {
            return new EqualEqualState(this.position);
        }
        return new FinalState(this.position, "=", TokenType.ASSIGN);
    }
}

class EqualEqualState
extends State {
    private Position position;

    public EqualEqualState(Position position) {
        this.position = position;
    }

    @Override
    public State step(Source source) {
        return new FinalState(this.position, "==", TokenType.EQUAL);
    }
}


class GreaterState
extends MarkState {
    public GreaterState(Source source) {
        super(source);
    }

    @Override
    public State step(Source source) {
        if (!(source.atEOF() || source.current() != '=')) {
            return new GreaterEqualState(this.position);
        }
        return new FinalState(this.position, ">", TokenType.GREATER);
    }
}

class GreaterEqualState
extends State {
    private Position position;

    public GreaterEqualState(Position position) {
        this.position = position;
    }

    @Override
    public State step(Source source) {
        return new FinalState(this.position, ">=", TokenType.GREATEREQUAL);
    }
}

class LessState
extends MarkState {
    public LessState(Source source) {
        super(source);
    }

    @Override
    public State step(Source source) {
        if (!(source.atEOF() || source.current() != '=')) {
            return new LessEqualState(this.position);
        }
        if (!(source.atEOF() || source.current() != '>')) {
            return new LessGreaterState(this.position);
        }
        return new FinalState(this.position, "<", TokenType.LESS);
    }
}


class LessEqualState
extends State {
    private Position position;

    public LessEqualState(Position position) {
        this.position = position;
    }

    @Override
    public State step(Source source) {
        return new FinalState(this.position, "<=", TokenType.LESSEQUAL);
    }
}

class LessGreaterState
extends State {
    private Position position;

    public LessGreaterState(Position position) {
        this.position = position;
    }

    @Override
    public State step(Source source) {
        return new FinalState(this.position, "<>", TokenType.NOTEQUAL);
    }
}




