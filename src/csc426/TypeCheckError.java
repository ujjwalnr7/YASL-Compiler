package csc426;

@SuppressWarnings("serial")
public class TypeCheckError extends RuntimeException {
	public TypeCheckError(String message, AST ast) {
		super(message + ": " + ast);
	}
}
