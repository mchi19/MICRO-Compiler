import org.antlr.v4.runtime.*;
import java.util.*;
import java.io.*;
import java.lang.*;

public class ASTOut
{
    public static void main (String[] args)
    {
		try
		{
			//Output File
			FileOutputStream outfile = new FileOutputStream(args[1]);
			System.setOut(new PrintStream(outfile));
			//Parser Build
			MICROLexer lexer = new MICROLexer(new ANTLRFileStream(args[0]));
			CommonTokenStream token = new CommonTokenStream(lexer);
			MICROParser parser = new MICROParser(token);
			ASTListener a = new ASTListener(parser.tree);
			parser.addParseListener(a);
			ParserRuleContext p = parser.program(); 
		}
		catch (IOException e){ }
    }
}
