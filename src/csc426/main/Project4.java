package csc426.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Reader;
import java.util.Scanner;

import csc426.ast.*;
import csc426.parser.*;
import csc426.interpreter.*;

public class Project4 {
	public static void main(String[] args) {
		Scanner input = new Scanner(System.in);
		PrintStream output = System.out;
		ErrorLog log = new ErrorLog();

		File file = null;
		if (args.length < 1) {
			//output.print("Source file: ");
			// file = new File(input.nextLine());
			
			file = new File("demo4.yasl");
		} else {
			file = new File(args[0]);
		}

		Reader in = null;
		Lookahead lookahead = null;
		try {
			in = new BufferedReader(new FileReader(file));
			Lexer lexer = new Lexer(in, log);
			lookahead = new Lookahead(lexer, log);
			Parser parser = new Parser(lookahead);

			Program program = parser.parseProgram();
			
			Interpreter interpret = new Interpreter(input, output);
			
			interpret.run(program);
			
		} catch (IOException e) {
			log.add(e.getMessage());
		} finally {
			if (lookahead != null) {
				lookahead.close();
			}
			input.close();
		}

		if (log.nonEmpty()) {
			System.err.print(log);
		}
	}
}