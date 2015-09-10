package csc426;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Main class for Project 1 -- Scanner for a Subset of YASL (Fall 2015). Scans
 * tokens from standard input and prints the token stream to standard output.
 * 
 * @author bhoward
 */
public class Project1 {
	public static void main(String[] args) throws IOException {
		Scanner scanner = new Scanner(new BufferedReader(new InputStreamReader(System.in)));

		Token token;
		do {
			token = scanner.next();
			System.out.println(token);
		} while (token.type != TokenType.EOF);

		scanner.close();
	}
}
