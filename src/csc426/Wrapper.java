package csc426;

import java.io.IOException;
import java.io.PrintStream;

/**
 * Wrapper class has its own scanner, check and match methods 
 * @author Ujjwal
 *
 */

public class Wrapper 
{
	private Scanner scanner;
	private Token current;
	
	public Wrapper(Scanner scanner)
	{
		this.scanner = scanner;
		this.current = scanner.next();
	}


	public Token current() 
	{
	    return this.current;
	}
	
	// Check Method looks checks whether the required token type is the current token type
	public boolean check(TokenType type) 
	{
	    if (this.current.type == type) 
	    {
	        return true;
	    }
	    return false;
	}
	
	// Match method passes the token type to check and if they dont't match throws an error
	public Token match(TokenType type) 
	{
		 Token token = this.current;
	     	if (token.type == type) 
	     	{
	            this.current = this.scanner.next();
	        }
	     	
	     	else 
	     	{
	            System.err.println("Error: Expected a " + (Object)type + ", found " + token);
	            System.exit(1);
	        }
	        return token;
	}

	// Skip method advances the scanner
	public Token skip() 
	{
		Token token = this.current;
	    this.current = this.scanner.next();
	    return token;
	}

	//Closes the file after reading
	public void close() throws IOException
	{
		this.scanner.close();
	}
}