package main;

public class Logger {
	
	public static void failToken(String msg, Token token)
	{
		System.err.println(msg);
		System.err.println("in file: \"" + token.source + "\" on row: " + token.row);
		System.err.println("type: " + token.type + " value: " + token.text);
		throw new RuntimeException("error");
	}
	
	public static void failTokenize(String msg, String file, int row, int col)
	{
		System.err.println(msg);
		System.err.println("in file: \"" + file + "\" on row: " + row + " in column: " + col);
		throw new RuntimeException("error");
	}
	
	public static void fail(String msg)
	{
		System.err.println(msg);
		throw new RuntimeException("error");
	}

}
