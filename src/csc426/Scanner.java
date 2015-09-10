package csc426;

import java.io.IOException;
import java.io.Reader;

/**
 * A Lexical Analyzer for a subset of YASL. Uses a (Mealy) state machine to
 * extract the next available token from the input each time next() is called.
 * Input comes from a Reader, which will generally be a BufferedReader wrapped
 * around a FileReader or InputStreamReader (though for testing it may also be
 * simply a StringReader).
 * 
 * @author bhoward
 */
public class Scanner {
	/**
	 * Construct the Scanner ready to read tokens from the given Reader.
	 * 
	 * @param in
	 */
	public Scanner(Reader in) {
		source = new Source(in);
	}

	/**
	 * Extract the next available token. When the input is exhausted, it will
	 * return an EOF token on all future calls.
	 * 
	 * @return the next Token object
	 */
	public Token next() {
		// TODO implement the state machine here:
		// - have a "state" variable start in the initial state
		// - repeatedly look at source.current (the current character),
		//   perform an appropriate action based on them, and assign
		//   a new state until the end of a token is seen
		// - call source.advance() on each state transition, until you
		//   see the first character after the token
		// - if source.atEOF is true, then return an EOF token:
		//     new Token(source.line, source.column, TokenType.EOF, null)
		
		return null; // TODO replace this with an actual Token object
	}

	/**
	 * Close the underlying Reader.
	 * 
	 * @throws IOException
	 */
	public void close() throws IOException {
		source.close();
	}

	private Source source;
}
