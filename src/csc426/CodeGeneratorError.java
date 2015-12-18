package csc426;

@SuppressWarnings("serial")
public class CodeGeneratorError extends RuntimeException {
	public CodeGeneratorError(String message, AST ast) {
		super(message + ": " + ast);
	}
}