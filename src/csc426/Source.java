package csc426;

import java.io.IOException;
import java.io.Reader;

/**
 * A Source object wraps a Reader (which will typically be a BufferedReader
 * wrapping another Reader connected to a file or an input stream) with the
 * ability to track the current line and column number, and to examine the
 * current character multiple times.
 * 
 * @author 
 * Provided as Part of Project Skeleton
 */
public class Source {
	/**
	 * Construct a Source wrapping the given Reader. Once constructed, the first
	 * character of the source (at line 1, column 1) will be available via
	 * current(), or else atEOF() will be true.
	 * 
	 * @param in
	 */
	public Source(Reader in) {
		this.in = in;
		this.line = 0;
		this.column = 0;
		this.current = '\n';
		this.atEOF = false;

		advance();
	}
	/**
	 * Advance to the next available character, if any. Either the new character
	 * will be available through current(), or atEOF() will be true.
	 */
	public void advance() {
		if (atEOF)
			return;

		if (current == '\n') {
			++line;
			column = 1;
		} else {
			++column;
		}

		try {
			int next = in.read();
			if (next == -1) {
				atEOF = true;
			} else {
				current = (char) next;
			}
		} catch (IOException e) {
			System.err.println("Error: " + e);
			System.exit(1);
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
	
	public int line, column;
	public char current;
	public boolean atEOF;
}
