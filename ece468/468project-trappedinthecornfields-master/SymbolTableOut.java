import java.util.*;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;
import java.io.*;
import java.lang.*;

public class SymbolTableOut
{
	public static void main(String[] args)
	{
		try
		{
			MICROLexer Lexer = new MICROLexer(new ANTLRFileStream(args[0]));
			CommonTokenStream Token = new CommonTokenStream(Lexer);
			MICROParser Parser = new MICROParser(Token);
			FileOutputStream outfile = new FileOutputStream(args[1]);
			System.setOut(new PrintStream(outfile));
			Parser.program();
		}
		catch (IOException e){ }	
		
	}
}
