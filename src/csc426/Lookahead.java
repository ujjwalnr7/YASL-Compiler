package csc426;


import java.io.IOException;

public class Lookahead {
	public Lookahead(Scanner scanner) throws IOException {
		this.scanner = scanner;
		this.current = scanner.next();
	}

	public Token current() {
		return current;
	}

	public boolean check(TokenType type) {
		return current.type == type;
	}

	public Token match(TokenType type) throws ParseError {
		Token token = current;
		
		if (token.type == type) {
			try {
				current = scanner.next();
			} catch (IOException e) {
				throw new ParseError(e.getMessage());
			}
			return token;
		} else {
			throw new ParseError("Error: Expected " + type + ", found " + token);
		}
	}

	public Token skip() throws ParseError {
		Token token = current;
		try {
			current = scanner.next();
		} catch (IOException e) {
			throw new ParseError(e.getMessage());
		}
		return token;
	}

	public void close() throws IOException {
		scanner.close();
	}

	private Scanner scanner;
	private Token current;
}