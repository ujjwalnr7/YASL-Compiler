package csc426.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Reader;
import java.util.Scanner;

import csc426.ast.Program;
import csc426.parser.ErrorLog;
import csc426.parser.Lexer;
import csc426.parser.Lookahead;
import csc426.parser.Parser;

public class Project3 {
	public static void main(String[] args) {
		Scanner input = new Scanner(System.in);
		PrintStream output = System.out;
		ErrorLog log = new ErrorLog();

		File file = null;
		if (args.length < 1) {
			output.print("Source file: ");
			file = new File(input.nextLine());
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

			String result = program.render("");

			output.print(result);
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