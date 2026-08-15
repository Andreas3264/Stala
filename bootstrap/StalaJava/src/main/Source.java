package main;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Source {
	
	public String name;
	public Map<String, Function> functionMap = new TreeMap<String, Function>();
	public List<String> includes = new ArrayList<String>();
}
