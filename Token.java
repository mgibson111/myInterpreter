package myInterpreter;

class Token {
	private TokenType type;
	private String lexeme;
	private int lineNumber;

	Token(TokenType type, String lexeme, int lineNumber) {
		this.type = type;
		this.lexeme = lexeme;
		this.lineNumber = lineNumber;
	}
	
	public TokenType getType() {return type;}
	
	public String getLexeme() {return lexeme;}
	
	public int getLineNumber() {return lineNumber;}

	//overrides Object.toString() so we can display a Token in the format we want
	public String toString() {return type + "     " + lexeme + "     line: " + lineNumber;}
	
}