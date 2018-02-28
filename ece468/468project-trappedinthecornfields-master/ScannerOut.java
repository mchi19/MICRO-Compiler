import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;
import java.io.*;
import java.util.*;

public class ScannerOut 
{

	public static void main (String[] args) 
	{
	    try
		{
		Lexer l = new MICROLexer(new ANTLRFileStream(args[0]));
		BufferedWriter outfile = new BufferedWriter(new FileWriter(args[1]));
		while(l.nextToken() != null && l.getToken().getText() != "<EOF>")
		    {
		    String tokType = l.getVocabulary().getSymbolicName(l.getToken().getType()); 
		    String dispType = tokType;
		    if (tokType.contains("OP"))
			dispType = "OPERATOR";
		    if(tokType.contains("KY"))
			dispType = "KEYWORD";
		    outfile.write("Token Type: " + dispType +"\n");
		    outfile.write("Value: " + l.getToken().getText() + "\n");
		    }
		//outfile.write(count.toString());
		outfile.close();
		}
	    catch (IOException ioE){ }
	}
	
}
