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
	protected static final String OPCHARS = ";.+-*=";
	
	/**
	 * A Map from lexeme to the corresponding TokenType, for all of the
	 * "fixed lexeme" tokens.
	 */
	protected static Map<String, TokenType> tokenMap;

	// load tokens with fixed lexemes into tokenMap
	static {
		tokenMap = new HashMap<String, TokenType>();
		tokenMap.put("+", TokenType.PLUS);
		tokenMap.put("-", TokenType.MINUS);
		tokenMap.put("*", TokenType.STAR);
		tokenMap.put(";", TokenType.SEMI);
		tokenMap.put(".", TokenType.PERIOD);
		tokenMap.put("=", TokenType.ASSIGN);	
		tokenMap.put("print", TokenType.PRINT);
		tokenMap.put("const", TokenType.CONST);
		tokenMap.put("begin", TokenType.BEGIN);
		tokenMap.put("end", TokenType.END);
		tokenMap.put("div", TokenType.DIV);
		tokenMap.put("mod", TokenType.MOD);
		tokenMap.put("program", TokenType.PROGRAM);
		tokenMap.put("id", TokenType.ID);
	}
	
}

class InitialState extends State{

	@Override
	public State step(Source source) {
		if (source.atEOF)
		{
			return new FinalState(source.line, source.column, "<EOF>", TokenType.EOF);
		}
		
		else if(source.current == '0')
		{
			return new ZeroState(source);
		}
		
		else if (Character.isDigit(source.current))
		{
			return new NumState(source);
		}
		
		else if (OPCHARS.indexOf(source.current) >= 0)
		{
			return new OpState(source);
		}
		
		else if (Character.isWhitespace(source.current))
		{
			return INITIAL_STATE;
		}
		
		else if (Character.isAlphabetic(source.current))
		{
			return new IdentState(source);
		}
		
		else if(source.current == '/')
		{
			return new SlashState(source);
		}
		
		else if(source.current == '{')
		{
			return new OpenCommaState(source);
		}
		
		
		else
		{
			System.err.print("Error: Unexpected character (" + source.current + ") ");
			System.err.println("at line " + source.line + ", column " + source.column);
			return INITIAL_STATE;
		}
		
	}
	
}


class FinalState extends State {
	public FinalState(int line, int column, String lexeme, TokenType type) {
		this.token = new Token(line, column, type, lexeme);
	}
	
	/**
	 * There should be no reason for this to be called
	 */
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

abstract class MarkState extends State{
	public MarkState(Source source) 
	{
		this.line = source.line;
		this.column = source.column;
		this.buffer = new StringBuilder();
		buffer.append(source.current);
	}

	protected int line, column;
	protected StringBuilder buffer;
	
}

/**
 * The state while recognizing a non-zero integer literal. The same state object
 * is reused while the token is read; the "state" changes by appending
 * characters to the lexeme buffer.
 */
class NumState extends MarkState {
	public NumState(Source source) 
	{
		super(source);
	}

	public State step(Source source) 
	{
		if (Character.isDigit(source.current)) 
		{
			buffer.append(source.current);
			return this;
		} 
		else 
		{
			return new FinalState(line, column, buffer.toString(), TokenType.NUM);
		}
	}
}

class IdentState extends MarkState{
	public IdentState(Source source)
	{
		super(source);
	}
	
	public State step(Source source)
	{
		if (Character.isLetter(source.current) || Character.isDigit(source.current))
		{
			buffer.append(source.current);
			return this;
		}
		
		else
		{
			String anything = buffer.toString();
			if (((anything).equals("program")) || ((anything).equals("const")) ||((anything).equals("begin")) ||((anything).equals("print")) ||((anything).equals("div")) ||((anything).equals("mod")) || ((anything).equals("end")))
			{
				TokenType type = tokenMap.get(anything);
				return new FinalState(line, column, anything, type);
			}
			
			else
			{
				String id = "id";
				TokenType type = tokenMap.get(id);
				return new FinalState(line, column, anything, type);
			}
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
		return new FinalState(line, column, buffer.toString(), TokenType.NUM);
	}
}

/**
 * The state while recognizing an operator or punctuation. Currently, all of
 * these are single-character tokens, but it may be extended along the lines of
 * IdentState or NumState to handle multiple characters.
 */
class OpState extends MarkState {
	public OpState(Source source) 
	{
		super(source);
	}

	public State step(Source source) {
		String lexeme = buffer.toString();
		TokenType type = tokenMap.get(lexeme);

		return new FinalState(line, column, lexeme, type);
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

	// this is a very sneaky comment
	public boolean done() 
	{
		return false;
	}
	
	private int line, column;
}


