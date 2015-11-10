package csc426.parser;

import java.io.IOException;
import java.io.Reader;

/**
 * A Lexical Analyzer for full YASL. Uses a (Mealy) state machine to
 * extract the next available token from the input each time next() is called.
 * Input comes from a Reader, which will generally be a BufferedReader wrapped
 * around a FileReader or InputStreamReader (though for testing it may also be
 * simply a StringReader).
 * 
 * @author bhoward
 */
public class Lexer {
	/**
	 * Construct the Lexer ready to read tokens from the given Reader.
	 * 
	 * @param in
	 */
	public Lexer(Reader in, ErrorLog log) {
		source = new Source(in);
		this.log = log;
	}

	/**
	 * Extract the next available token. When the input is exhausted, it will
	 * return an EOF token on all future calls.
	 * 
	 * @return the next Token object
	 */
	public Token next() {
		State state = State.INITIAL_STATE;

		while (state != State.FINAL_STATE) {
			state = State.doTransitionStep(state, source, log);
		}

		return State.currentToken();
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
	private ErrorLog log;
}
