package csc426.parser;

/**
 * Enumeration of the different kinds of tokens in YASL.
 * 
 * @author bhoward
 */
public enum TokenType {
	ID, // identifier, such as a variable name
	NUM, // numeric literal
	STRING, // string literal
	PROGRAM, // "program" keyword
	CONST, // "const" keyword
	BEGIN, // "begin" keyword
	PRINT, // "print" keyword
	END, // "end" keyword
	DIV, // "div" operator keyword
	MOD, // "mod" operator keyword
	VAR, // "var" keyword
	INT, // "int" type keyword
	BOOL, // "bool" type keyword
	PROC, // "proc" keyword
	IF, // "if" keyword
	THEN, // "then" keyword
	ELSE, // "else" keyword
	WHILE, // "while" keyword
	DO, // "do" keyword
	PROMPT, // "prompt" keyword
	AND, // "and" operator keyword
	OR, // "or" operator keyword
	NOT, // "not" operator keyword
	TRUE, // "true" constant keyword
	FALSE, // "false" constant keyword
	SEMI, // semicolon (;)
	PERIOD, // period (.)
	COLON, // colon (:)
	LPAREN, // left-parenthesis (()
	RPAREN, // right-parenthesis ())
	COMMA, // comma (,)
	PLUS, // plus operator (+)
	MINUS, // minus operator (-)
	STAR, // times operator (*)
	ASSIGN, // assignment operator (=)
	EQUAL, // equality operator (==)
	NOTEQUAL, // inequality operator (<>)
	LESSEQUAL, // less-or-equal operator (<=)
	GREATEREQUAL, // greater-or-equal operator (>=)
	LESS, // less-than operator (<)
	GREATER, // greater-than operator (>)
	EOF // end-of-file
}
