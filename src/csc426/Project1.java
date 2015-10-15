package csc426;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Stack;

/**
 * Main class for Project 2 -- 
 * Unfortunately its still called Project 1 instead of something more conventional because refactoring seems to break things
 * Scanner for a Subset of YASL (Fall 2015). Scans
 * tokens from standard input and prints the token stream to standard output.
 * 
 * @author Ujjwal Nair
 */
public class Project1 {

	public static void main(String[] args) throws IOException {
		 try {
	            Wrapper lookahead = new Wrapper(new Scanner(new BufferedReader(new InputStreamReader(System.in))));
	            Parser parser = new Parser(lookahead);
	            AST.Program program = parser.parseProgram();
	            ASTRenderVisitor visitor = new ASTRenderVisitor();
	            String result = (String)program.accept(visitor);
	            System.out.println(result);
	        }
	        catch (ParseError pe) {
	            System.err.println(pe.getMessage());
	            System.exit(1);
	        }
	    }

	
}
