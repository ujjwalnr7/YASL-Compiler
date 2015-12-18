package csc426;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Reader;

import csc426.AST.Program;

public class Project5 {
	public static void main(String[] args) throws IOException {
		java.util.Scanner input = new java.util.Scanner(System.in);
		PrintStream output = System.out;
		PrintWriter fileout = new PrintWriter("output.yasm", "UTF-8");

		File file = null;
		file = new File(args[0]);

		Reader in = null;
		try {
			in = new BufferedReader(new FileReader(file));
			Lookahead lookahead = new Lookahead(new Scanner(in));
			
			Parser parser = new Parser(lookahead);
			Program program = parser.parseProgram();
			
			
			ASTVisitor<Value> generator = new CodeGenerator(input, output, fileout);
			program.accept(generator);
		} catch (ParseError pe) {
			System.err.println(pe.getMessage());
			System.exit(1);
		} catch (InterpreterError ie) {
			System.err.println(ie.getMessage());
			System.exit(1);
		} finally {
			if (in != null) {
				in.close();
			}
			input.close();
			output.println("Execution Complete");
			output.println("Success!!!"); //check line
		}
	}
}