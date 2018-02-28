import java.util.*;

public class TinyNode
{
    String opCode;
    String op1;
    String op2;

    public TinyNode(String opCode, String op1, String op2)
    {
	    this.opCode = opCode;
	    this.op1 = op1;
	    this.op2 = op2;
    }

    public String getopCode()
    {
	    return this.opCode;
    }
    
    public String getop1()
    {
	    return this.op1;
    }

    public String getop2()
    {
	    return this.op2;
    }

    public void printTinyNode()
    {
	    String t_op1;
	    String t_op2;
	
	    if (op1 != null)
	    {
	        t_op1 = " " + op1;
	    }
	    else 
	    {
	        t_op1 = "";
	    }
	
	    if (op2 != null)
	    {
	        t_op2 = " " + op2;
      }
	    else
	    {
	        t_op2 = "";
	    }
	    System.out.println(opCode + t_op1 + t_op2);
    }
}
