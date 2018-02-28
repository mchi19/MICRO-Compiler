import java.io.*;
import java.util.*;
public class IRObj
{
	ArrayList<IRNode> IRNodeList;
	String result;
	String resultType;
	
	public IRObj(ArrayList<IRNode> ins, String result, String resultType)
	{
		this.IRNodeList = ins;
		this.result = result;
		this.resultType = resultType;
	}
}
