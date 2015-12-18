package csc426;

@SuppressWarnings("serial")
public class InterpreterError extends RuntimeException {
	public InterpreterError(String message, AST ast) {
		super(message + ": " + ast);
	}
}