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

public class ParserOut
{
    public static void main (String[] args)
    {
		try
		{
			MICROLexer Lexer = new MICROLexer(new ANTLRFileStream(args[0]));
			CommonTokenStream Token = new CommonTokenStream(Lexer);
			MICROParser Parser = new MICROParser(Token);
			ANTLRErrorStrategy Err = new NewErrorStrategy(args);
			Parser.setErrorHandler(Err);
			Parser.program();
			BufferedWriter outfile = new BufferedWriter(new FileWriter(args[1]));
			outfile.write("Accepted");
			outfile.close();
		}
		catch (IOException e){ }
    }
}

class NewErrorStrategy extends DefaultErrorStrategy 
{
    String[] args;
    
        public NewErrorStrategy(String[] inargs) 
        { 
	    args = inargs;
        } 

	public void reportError (Parser recognizer, RecognitionException e)
	{
	    try{
	        BufferedWriter outfile = new BufferedWriter(new FileWriter(args[1]));
		outfile.write("Not accepted");
		outfile.close();
		System.exit(0);
	       } catch(IOException ioE) {}
	}	
}
