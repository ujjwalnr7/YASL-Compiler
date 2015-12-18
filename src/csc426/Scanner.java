package csc426;
import java.io.IOException;
import java.io.Reader;

public class Scanner {
	/**
	 * Construct the Scanner ready to read tokens from the given Reader.
	 * 
	 * @param in
	 * @throws IOException 
	 */
	public Scanner(Reader in) throws IOException {
		source = new Source(in);
	}

	/**
	 * Extract the next available token. When the input is exhausted, it will
	 * return an EOF token on all future calls.
	 * 
	 * @return the next Token object
	 * @throws IOException 
	 */
	public Token next() throws IOException {
		State state = State.INITIAL_STATE;

		for (;;) {
			state = state.step(source);
			if (state.done()) {
				break;
			}
			
			// Only advance if end of token not yet seen
			source.advance();
		}

		return state.token();
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
