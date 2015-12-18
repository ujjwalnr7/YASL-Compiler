package csc426;


import java.io.IOException;
import java.io.Reader;

public class Source {
	/**
	 * Construct a Source wrapping the given Reader. Once constructed, the first
	 * character of the source (at line 1, column 1) will be available via
	 * current(), or else atEOF() will be true.
	 * 
	 * @param in
	 * @throws IOException 
	 */
	public Source(Reader in) throws IOException {
		this.in = in;
		this.line = 0;
		this.column = 0;
		this.current = '\n';
		this.atEOF = false;

		advance();
	}

	/**
	 * @return the current character, unless atEOF() true
	 */
	public char current() {
		return current;
	}

	/**
	 * @return the current Position
	 */
	public Position position() {
		return new Position(line, column);
	}

	/**
	 * @return true if input has been exhausted
	 */
	public boolean atEOF() {
		return atEOF;
	}

	/**
	 * Advance to the next available character, if any. Either the new character
	 * will be available through current(), or atEOF() will be true.
	 */
	public void advance() throws IOException {
		if (atEOF)
			return;

		if (current == '\n') {
			++line;
			column = 1;
		} else {
			++column;
		}

		int next = in.read();
		if (next == -1) {
			atEOF = true;
		} else {
			current = (char) next;
		}
	}

	/**
	 * Close the underlying Reader.
	 * 
	 * @throws IOException
	 */
	public void close() throws IOException {
		in.close();
	}

	private Reader in;
	private int line, column;
	private char current;
	private boolean atEOF;
}