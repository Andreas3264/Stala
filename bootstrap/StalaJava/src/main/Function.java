package main;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Function {
	
	public final String name;
	public final String label;
	public List<String> args = new ArrayList<String>();
	public List<String> locals = new ArrayList<String>();
	public Map<String, String> stringMap = new TreeMap<String, String>();
	
	public Token content;

	private static long uuid = 1;
	public Function(String name)
	{
		this.name = name;
		this.label = "f_" + uuid;
		uuid++;
	}
}
