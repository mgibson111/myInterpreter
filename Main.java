package myInterpreter;

import java.io.IOException;

public class Main {

	private static final String FILENAME = "C:\\Users\\lue\\Documents\\Concepts\\eclipse-workspace\\Interpreter\\inputfile.txt";

	public static void main(String[] args) throws IOException {
		LexicalAnalyzer test = new LexicalAnalyzer(FILENAME);
		test.scanAllTokens();
		test.printTokens();
	}

}