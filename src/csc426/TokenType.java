package csc426;

/**
 * Enumeration of the different kinds of tokens in the YASL subset.
 * 
 * @author Ujjwal Nair
 */
public enum TokenType {
	NUM, // numeric literal
	SEMI, // semicolon (;)
	PLUS, // plus operator (+)
	MINUS, // minus operator (-)
	STAR, // times operator (*)
	ASSIGN, //equals operator (=)
	DIV, // division operator (/)
	MOD, // modulus operator (%)
	PERIOD, // period (.)
	EOF // end-of-file
	// TODO add more token types here
}
