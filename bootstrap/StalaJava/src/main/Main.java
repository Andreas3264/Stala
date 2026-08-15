package main;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import java.util.Map.Entry;

public class Main {

	public static String input = "";
	public static String output = "";
	public static String sources = "../:../stdlib:stdlib";
	
	public static boolean run = false;
	public static String[] progArgs = new String[0];
	
	public static void main(String[] args) throws Exception {

		Main.output = args[3];
		Main.input = args[1];
		
		Program prog = parseProgram(new Tokenizer(new FileCharacterIterator(
				new File(Main.input)), Main.input), Main.input, "../:../stdlib:stdlib".split(":"));
		
		PrintWriter progOut = new PrintWriter(new File(Main.output + ".asm"));
		printMacOSx86_64(prog, progOut);
		progOut.close();
		
	}
	
	private static Program parseProgram(Iterator<Token> source, String input, String[] paths)
	{
		Program prog = new Program();
		prog.paths = paths;
		prog.main = parseSource(source, input);
		prog.sourceMap.put(input, prog.main);
		return prog;
	}
	
	private static Source parseSource(Iterator<Token> tokenIter, String sourceName)
	{
		Source source = new Source();
		source.name = sourceName;
		
		while(tokenIter.hasNext())
		{
			tokenIter.next();
			Function func = parseFunction(tokenIter);
			source.functionMap.put(func.name, func);
		}
		return source;
	}
	
	private static Function parseFunction(Iterator<Token> tokenIter)
	{
		Token token = tokenIter.next();
		Function function = new Function(token.text);
		token = tokenIter.next();
		
		while(token.type != TOKEN_TYPE.DO)
		{
			String name = token.text;
			token = tokenIter.next();
			function.args.add(name);
			function.locals.add(name);
		}
		token = tokenIter.next();
		
		function.content = token;
		Stack<List<Token>> blockStack = new Stack<List<Token>>();

		while(true)
		{
			TOKEN_TYPE type = token.type;
			//String value = token.right;
			if(type == TOKEN_TYPE.NUMBER 
					|| type == TOKEN_TYPE.IDENTIFIER
					|| type == TOKEN_TYPE.RETURN
					)
			{
			}
			else if(type == TOKEN_TYPE.STRING)
			{
				String value = token.text;
				function.stringMap.put(token.label, value);
			}
			else if(type == TOKEN_TYPE.LET)
			{
				Token nextToken = tokenIter.next();
				token.text = nextToken.text;
				if(function.locals.indexOf(nextToken.text) == -1)
					function.locals.add(nextToken.text);
				
			}
			else if(type == TOKEN_TYPE.ASM)
			{
				Token nextToken = tokenIter.next();
				token.text = nextToken.text;
			}
			else if(type == TOKEN_TYPE.WHILE)
			{
				List<Token> chain = new ArrayList<Token>();
				chain.add(token);
				blockStack.push(chain);
			}
			else if(type == TOKEN_TYPE.DO)
			{
				blockStack.peek().add(token);
			}
			else if(type == TOKEN_TYPE.IF)
			{
				List<Token> chain = new ArrayList<Token>();
				chain.add(token);
				blockStack.push(chain);
			}
			else if(type == TOKEN_TYPE.ELIF)
			{
				List<Token> chain = blockStack.peek();
				chain.add(token);
			}
			else if(type == TOKEN_TYPE.ELSE)
			{
				List<Token> chain = blockStack.peek();
				chain.add(token);
			}
			else if(type == TOKEN_TYPE.BLOCK_END)
			{
				// end of function
				if(blockStack.size() == 0)
				{
					break;
				}
				else
				{
					List<Token> chain = blockStack.pop();
					
					if(chain.get(0).type == TOKEN_TYPE.WHILE)
					{
						chain.get(1).conditionalNext = token;
						token.conditionalNext = chain.get(0);
					}
					else
					{
						for(int i = 0; i < chain.size(); i++)
						{
							Token chainToken = chain.get(i);
							
							// set the next token to end or next
							Token nextChainToken = token;
							if(i < chain.size() - 1)
								nextChainToken = chain.get(i+1);
							
							if(chainToken.type == TOKEN_TYPE.ELSE)
							{
								chainToken.conditionalNext = token;
							}
							else if(chainToken.type == TOKEN_TYPE.IF
									&&(nextChainToken.type == TOKEN_TYPE.ELSE || nextChainToken.type == TOKEN_TYPE.BLOCK_END))
							{
								chainToken.conditionalNext = nextChainToken;
							}
							else if(chainToken.type == TOKEN_TYPE.ELIF
								&& (nextChainToken.type == TOKEN_TYPE.ELSE || nextChainToken.type == TOKEN_TYPE.BLOCK_END))
							{
								chainToken.conditionalNext = nextChainToken;
							}
						}
					}
				}
			}
			Token newToken = tokenIter.next();
			token.next = newToken;
			token = newToken;
			
		}
		
		return function;
	}
	
	private static final int BYTES_PER_REGISTER = 8;
	
	private static void printDataPush(PrintWriter out, String register)
	{
		out.println("mov [rbp], " + register);
		out.println("add rbp, " + BYTES_PER_REGISTER);
	}
	
	private static void printDataPop(PrintWriter out, String register)
	{
		out.println("sub rbp, " + BYTES_PER_REGISTER);
		out.println("mov " + register + ", [rbp]");
	}
	
	private static void printMacOSx86_64(Program program, PrintWriter out)
	{
		out.println("global _main");
		out.println("section .text");
		out.println("");
		
		out.println("_main:");
		out.println("mov [rel org_rsp], rsp");
		out.println("mov [rel org_rbp], rbp");
		
		out.println("lea rax, [rel mem]");
		out.println("mov [rel mem_ptr], rax");
		
		out.println("lea rbp, [rel dat_stack]");
		
		out.println("lea rax, [rel loc_stack]");
		out.println("mov [rel loc_stack_ptr], rax");

		printDataPush(out, "rdx"); // ENVP
		printDataPush(out, "rdi"); // ARGC
		printDataPush(out, "rsi"); // ARGV
		
		out.println("call " + program.main.functionMap.get("main").label);
		out.println("_exit:");
		out.println("mov rax, 0x2000001");	//TODO: OS agnostic exit
		out.println("mov rdi, 0");
		out.println("syscall");
		out.println("");
		
		for(Entry<String, Source> nameSource : program.sourceMap.entrySet())
		{
			for(Entry<String, Function> nameFunction : nameSource.getValue().functionMap.entrySet())
			{
				printFunctionMacOSx86_64(nameFunction.getValue(), nameSource.getValue(), program, out);
			}
		}
		
		out.println("section .data");
		
		for(Entry<String, Source> nameSource : program.sourceMap.entrySet())
		{
			for(Entry<String, Function> nameFunction : nameSource.getValue().functionMap.entrySet())
			{
				for(Entry<String, String> nameString : nameFunction.getValue().stringMap.entrySet())
				{
					out.print(nameString.getKey() + ": db ");
					byte[] bytes = StringUtill.unEscapeString(nameString.getValue()).getBytes();
					for(byte b : bytes)
					{
						out.print(b + ",");
					}
					out.println("0");
				}
			}
		}
		out.println("");
		
		out.println("section .bss");
		out.println(";; some global vars");
		out.println("org_rsp: resq 1");
		out.println("org_rbp: resq 1");
		out.println("");
		
		out.println(";; stack for local variables");
		out.println("loc_stack_ptr: resq 1");
		out.println("loc_stack: resq " + program.variableStackSize);
		out.println("loc_stack_end:");
		out.println("");
		
		out.println(";; stack for data elements");
		//out.println("dat_stack_ptr: resq 1");
		out.println("dat_stack: resq " + program.dataStackSize);
		out.println("dat_stack_end:");
		out.println("");
		
		out.println(";; memory region");
		out.println("mem_ptr: resq 1");
		out.println("mem: resb " + program.memorySize);
		out.println("mem_end:");
		out.println("");
	}
	
	private static void printFunctionMacOSx86_64(Function function, Source s, Program p, PrintWriter out)
	{
		out.println(function.label + ":");
		out.println("mov rax, [rel loc_stack_ptr]");
		out.println("add rax, " + (BYTES_PER_REGISTER*function.locals.size()));
		out.println("mov [rel loc_stack_ptr], rax");
		for(int i = function.args.size() - 1; i >= 0; i--)
		{
			printDataPop(out, "rax");
			out.println("mov rbx, [rel loc_stack_ptr]");
			out.println("mov [rbx - " + (BYTES_PER_REGISTER*i) + "], rax");
		}
		printTokensMacOSx86_64(function, s, p, out);
		out.println("end_" + function.label + ":");
		out.println("mov rax, [rel loc_stack_ptr]");
		out.println("sub rax, " + (BYTES_PER_REGISTER*function.locals.size()));
		out.println("mov [rel loc_stack_ptr], rax");
		out.println("ret");
		out.println("");
	}
	
	private static void printTokensMacOSx86_64(Function f, Source s, Program p, PrintWriter out)
	{
		Token token = f.content;
		
		while(token != null)
		{
			TOKEN_TYPE type = token.type;
			String value = token.text;
			
			if(type == TOKEN_TYPE.NUMBER)
			{
				out.println("mov rax, " + Long.decode(value));
				printDataPush(out, "rax");
			}
			else if(type == TOKEN_TYPE.STRING)
			{
				out.println("lea rax, [rel " + token.label + "]");
				printDataPush(out, "rax");
			}
			else if(type == TOKEN_TYPE.IF)
			{
				printDataPop(out, "rax");
				out.println("test rax, rax");
				Token toToken = token.conditionalNext;
				String toLable = toToken.label;
				out.println("jz " + toLable);
			}
			else if(type == TOKEN_TYPE.ELIF)
			{
				printDataPop(out, "rax");
				out.println("test rax, rax");
				Token toToken = token.conditionalNext;
				String toLable = toToken.label;
				out.println("jz " + toLable);
			}
			else if(type == TOKEN_TYPE.ELSE)
			{
				Token toToken = token.conditionalNext;
				String toLable = toToken.label;
				out.println("jmp " + toLable);
				String lable = token.label + ":";
				out.println(lable);
			}
			else if(type == TOKEN_TYPE.BLOCK_END)
			{
				if(token.conditionalNext != null)
				{
					Token toToken = token.conditionalNext;
					String toLable = toToken.label;
					out.println("jmp " + toLable);
				}
				String lable = token.label + ":";
				out.println(lable);
			}
			else if(type == TOKEN_TYPE.WHILE)
			{
				String toLable = token.label + ":";
				out.println(toLable);
			}
			else if(type == TOKEN_TYPE.DO)
			{
				printDataPop(out, "rax");
				out.println("test rax, rax");
				Token toToken = token.conditionalNext;
				String toLable = toToken.label;
				out.println("jz " + toLable);
			}
			else if(type == TOKEN_TYPE.RETURN)
			{
				out.println("jmp end_" + f.label);
			}
			else if(type == TOKEN_TYPE.LET)
			{
				int index = f.locals.indexOf(value);
				printDataPop(out, "rax");
				out.println("mov rbx, [rel loc_stack_ptr]");
				out.println("mov [rbx - " + (8*index) + "], rax");
			}
			else if(type == TOKEN_TYPE.ASM)
			{
				out.println(StringUtill.unEscapeString(token.text));
			}
			else if(type == TOKEN_TYPE.IDENTIFIER)
			{
				// Is a local variable
				if(f.locals.indexOf(value) != -1)
				{
					int index = f.locals.indexOf(value);
					
					out.println("mov rbx, [rel loc_stack_ptr]");
					out.println("mov rax, [rbx - " + (index * 8) + "]");
					printDataPush(out, "rax");
				}
				// Is a file local function
				else if(s.functionMap.containsKey(value))
				{
					out.println("call " + s.functionMap.get(value).label);
				}
				// Is function in external file
				else if(p.getExternalFunction(s, p, value, token) != null)
				{
					Function fn = p.getExternalFunction(s, p, value, token);
					out.println("call " + fn.label);
				}
			}
			token = token.next;
		}
	}

}
