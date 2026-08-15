package main;

import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

public class Program {
	
	public final long dataStackSize = 256*256;
	public final long variableStackSize = 256*256;
	public final long memorySize = 256*256*256;
	
	public String[] paths;
	public Source main;
	
	public Map<String, Source> sourceMap = new TreeMap<String, Source>();
	
	public Function getExternalFunction(Source s, Program p, String name, Token t)
	{
		Function ret = null;
		for(Entry<String, Source> ss : p.sourceMap.entrySet())
		{
			Function f = ss.getValue().functionMap.get(name);
			if(f != null)
			{
				if(ret == null)
					ret = f;
				else
					Logger.failToken("multiple external functions of name: \"" + name + "\"", t);
			}
		}
		return ret;
	}
	
}
