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
	
	private Reader in;
	
	public int line, column;
	public char current;
	public boolean atEOF;
	
	/**
	 * Construct a Source wrapping the given Reader. Once constructed, the first
	 * character of the source (at line 1, column 1) will be available via
	 * current(), or else atEOF() will be true.
	 * 
	 * @param in
	 * @throws IOException 
	 */
	public Source(Reader in) throws IOException 
	{
		this.in = in;
		this.line = 0;
		this.column = 0;
		this.current = '\n';
		this.atEOF = false;
		this.advance();
	}
	
	 public char current() 
	 {
		 return this.current;
	 }
	 
	 public Position position() 
	 {
		 return new Position(this.line, this.column);
	 }
	 
	 public boolean atEOF() 
	 {
		 return this.atEOF;
	 }
	 
	 
	/**
	 * Advance to the next available character, if any. Either the new character
	 * will be available through current(), or atEOF() will be true.
	 */
	 public void advance() throws IOException 
	 {
		 if (this.atEOF) 
		 {
			 return;
	     }
		 
	     if (this.current == '\n') 
	     {
	    	 ++this.line;
	         this.column = 1;
	     }
	     
	     else 
	     {
	    	 ++this.column;
	     }
	     
	     int next = this.in.read();
	     if (next == -1) 
	     {
	    	 this.atEOF = true;
	     }
	     else 
	     {
	    	 this.current = (char)next;
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


}
