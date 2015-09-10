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
	DIV, // DIV Keyword
	MOD, //  MOD Keyword
	PERIOD, // period (.)
	PROGRAM, // Keyword Program
	CONST, // Keyword Const
	BEGIN, // Keyword Begin
	PRINT, // Keyword Print
	END, // Keyword End
	ID, // Identifier
	EOF // end-of-file
}
