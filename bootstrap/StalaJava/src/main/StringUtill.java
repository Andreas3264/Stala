package main;

public class StringUtill {
	
	public static String unEscapeString(String s)
	{
		if(s.length() < 2)
			throw new RuntimeException("mallformed string: " + s);
		
		if(!s.startsWith("\"") || !s.endsWith("\"") )
			throw new RuntimeException("mallformed string: " + s);
		
		String ret = "";
		boolean escaped = false;
		for(int i = 1; i < s.length()-1; i++)
		{
			char ch = s.charAt(i);
			
			if(escaped)
			{
				escaped = false;
				if		(ch == '\\') {ret += "\\";}
				else if	(ch == '\"') {ret += "\"";}
				else if	(ch == 'n') {ret += "\n";}
				else if	(ch == 't') {ret += "\t";}
				else if	(ch == 'b') {ret += "\b";}
				else if	(ch == 'r') {ret += "\r";}
				else {throw new RuntimeException("forbidden escape character: " + ch);}
			}
			else
			{
				if(ch == '\\') 
					escaped = true;
				else if(isUpper(ch) || isLower(ch) || isNumber(ch) || isMisc(ch)) 
					ret += ch + "";
				else 
					throw new RuntimeException("forbidden character: " + ch); //TODO: propper error reporting
			}
			
		}
		return ret;
	}
	
	public static boolean isUpper(char c)
	{
		return isInString(c, "ABCDEFGHIJKLMNOPQRSTUVWXYZ");
	}
	
	public static boolean isLower(char c)
	{
		return isInString(c, "abcdefghijklmnopqrstuvwxyz");
	}
	
	public static boolean isAlpha(char c)
	{
		return isUpper(c) || isLower(c);
	}
	
	public static boolean isNumber(char c)
	{
		return isInString(c, "0123456789");
	}
	
	public static boolean isAlphaNum(char c)
	{
		return isAlpha(c) || isNumber(c);
	}
	
	public static boolean isMisc(char c)
	{
		return isInString(c, ",;.:_'!#%&()=?+-*/<>[](){}@$^~| ");
	}
	
	public static boolean isSpacer(char c)
	{
		return isInString(c, "\s\n\t");
	}
	
	public static boolean isEscaped(char c)
	{
		return isInString(c, "\t\n\b\r\\\"");
	}
	
	public static boolean validSourceFileCharacter(char ch)
	{
		return isAlphaNum(ch) || isMisc(ch) || isEscaped(ch);
	}
	
	public static boolean isInString(char c, String s)
	{
		for(char ch : s.toCharArray())
		{
			if(ch == c)
				return true;
		}
		return false;
	}

}
