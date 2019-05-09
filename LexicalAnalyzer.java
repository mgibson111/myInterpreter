package myInterpreter;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

import static myInterpreter.TokenType.*;

class LexicalAnalyzer {

	private Map<String, TokenType> keywords = new HashMap<>();
	private ArrayList<Token> tokens = new ArrayList<Token>();
	private String sourceCode = null;
	private int startIndex = 0, currentIndex = 0, lineNumber = 1;
	
	public LexicalAnalyzer(String filename) throws IOException {
		addKeywords();
		byte[] bytes = Files.readAllBytes(Paths.get(filename));
		sourceCode = new String(bytes, Charset.defaultCharset());
	}

	public ArrayList<Token> scanAllTokens() {
		while (currentIndex < sourceCode.length()) {			//keep scanning tokens until we are at the end of the sourceCode
			startIndex = currentIndex;
			scanToken();
		}
		tokens.add(new Token(TokenType.EOF, "", lineNumber));	//add EOF token once we get to the end of sourceCode
		return tokens;
	}
	
	//Prints all tokens in the list
	public void printTokens() {
		for (int i = 0; i < tokens.size(); i++) {System.out.println(tokens.get(i));}
	}
	
	//returns the character at the currentIndex
	private char currentChar() {
		if (currentIndex >= sourceCode.length()) {return '\0';}
		return sourceCode.charAt(currentIndex);
	}

	//identifies the next token and adds it to the tokens list
	private void scanToken() {
		char c = currentChar();									//assign the character at currentIndex to c
		currentIndex++;											//advance currentIndex
		switch (c) {
		case ',':
			addToken(COMMA);
			break;
		case '.':
			addToken(DOT);
			break;
		case '(':
			addToken(LEFT_PARENTHESIS);
			break;
		case ')':
			addToken(RIGHT_PARENTHESIS);
			break;
		case '[':
			addToken(LEFT_BRACKET);
			break;
		case ']':
			addToken(RIGHT_BRACKET);
			break;
		case '+':
			addToken(PLUS_OPERATOR);
			break;
		case '-':
			addToken(SUBTRACT_OPERATOR);
			break;
		case '*':
			addToken(MULTIPLY_OPERATOR);
			break;

		//for the following characters we have to check the next character as well to determine what TokenType it is
		case '~':
			if (currentChar() == '=') {currentIndex++;   addToken(NOT_EQUALS_OPERATOR);}
			else {System.out.println("Unexpected character error on Line: " + lineNumber);}
			break;
		case '=':
			if (currentChar() == '=') {currentIndex++;   addToken(EQUALS_OPERATOR);}
			else {addToken(ASSIGNMENT_OPERATOR);}
			break;
		case '<':
			if (currentChar() == '=') {currentIndex++;   addToken(LESSTHAN_EQUAL_OPERATOR);}
			else {addToken(LESSTHAN_OPERATOR);}
			break;
		case '>':
			if (currentChar() == '=') {currentIndex++;   addToken(GREATERTHAN_EQUAL_OPERATOR);}
			else {addToken(GREATERTHAN_OPERATOR);}
			break;

		case '/':
			if (currentChar() == '/') {																	//if we get a 2nd '/' it is a comment line
				currentIndex++;
				while (currentChar() != '\n' && currentIndex < sourceCode.length()) {currentIndex++;;}	//keep advancing currentIndex until the next line
			} else {
				addToken(DIVIDE_OPERATOR);																//otherwise it is a divide operator
			}
			break;

		//skip through all whitespace characters
		case ' ':
		case '\t':
		case '\r':
			break;

		case '\n':
			lineNumber++;
			break;

		case '"':			//quotation mark indicates a string is starting
			string();
			break;

		default:													//if none of the above then check if character is a letter or is a digit
			if (isLetter(c)) {identifier();}
			else if (isDigit(c)) {integer();}
			else {System.out.println("Unexpected character error on Line: " + lineNumber);}	//if neither then give an unexpected character error
			break;
		}
	}
	
	private void string() {
		while (currentIndex < sourceCode.length() && currentChar() != '"') {								//keep advancing currentIndex as long as the current character is not a closing quotation
			if (currentChar() == '\n') {lineNumber++;}
			currentIndex++;;
		}

		//give an error if the string is never closed
		if (currentIndex >= sourceCode.length()) {
			System.out.println("Unterminated string error on Line: " + lineNumber);
			return;
		}

		currentIndex++;;											//advance 1 more time past the closing quotation mark
		addToken(STRING_LITERAL);
	}

	private void integer() {
		while (isDigit(currentChar())) {currentIndex++;;}			//keep advancing as long as the current character is a digit
		addToken(INTEGER_LITERAL);
	}

	private void identifier() {
		while (isLetterOrDigit(currentChar())) {currentIndex++;;}	//keep advancing as long as the current character is a letter or digit or underscore

		String word = sourceCode.substring(startIndex, currentIndex);

		//Checks if the word is a reserved word. If type is null then the word is an identifier.
		TokenType type = keywords.get(word);
		if (type == null) {type = IDENTIFIER;}
		addToken(type);
	}

	private boolean isDigit(char c) {return c >= '0' && c <= '9';}

	private boolean isLetter(char c) {return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');}

	private boolean isLetterOrDigit(char c) {
		if(isLetter(c) || isDigit(c) || c == '_') {return true;}	//underscore can also be included in an identifier as long as it is not the first character
		else return false;
	}

	//Generates the lexeme for a token using startIndex and currentIndex, then adds the token to the list
	private void addToken(TokenType type) {
		String lexeme = sourceCode.substring(startIndex, currentIndex);
		//if(type == TokenType.STRING_LITERAL) {lexeme = sourceCode.substring(startIndex + 1, currentIndex - 1);}	//store a STRING_LITERAL type token's lexeme without the quotation marks surrounding it
		tokens.add(new Token(type, lexeme, lineNumber));
	}
	
	//Adds all the language's keywords to the HashMap keywords
	private void addKeywords() {
		keywords.put("specifications", SPECIFICATIONS);
		keywords.put("symbol", SYMBOL);
		keywords.put("forward", FORWARD);
		keywords.put("references", REFERENCES);
		keywords.put("function", FUNCTION);
		keywords.put("pointer", POINTER);
		keywords.put("array", ARRAY);
		keywords.put("type", TYPE);
		keywords.put("struct", STRUCT);
		keywords.put("integer", INTEGER);
		keywords.put("enum", ENUM);
		keywords.put("global", GLOBAL);
		keywords.put("declarations", DECLARATIONS);
		keywords.put("implementations", IMPLEMENTATIONS);
		keywords.put("main", MAIN);
		keywords.put("parameters", PARAMETERS);
		keywords.put("constant", CONSTANT);
		keywords.put("begin", BEGIN);
		keywords.put("endfun", ENDFUN);
		keywords.put("if", IF);
		keywords.put("then", THEN);
		keywords.put("else", ELSE);
		keywords.put("endif", ENDIF);
		keywords.put("repeat", REPEAT);
		keywords.put("until", UNTIL);
		keywords.put("endrepeat", ENDREPEAT);
		keywords.put("display", DISPLAY);
		keywords.put("set", SET);
		keywords.put("return", RETURN);
		keywords.put("variables", VARIABLES);
		keywords.put("define", DEFINE);
		keywords.put("exit", EXIT);
		keywords.put("import", IMPORT);
		keywords.put("do", DO);
		keywords.put("to", TO);
		keywords.put("constants", CONSTANTS);
	}

}