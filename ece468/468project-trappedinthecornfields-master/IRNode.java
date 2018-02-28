import java.util.*;

public class IRNode
{
    String opCode;
    String op1;
    String op2;
    String destination;
    String typeOp;

    public IRNode(String opCode, String op1, String op2, String destination)
    {
	    this.opCode = opCode;
	    this.op1 = op1;
	    this.op2 = op2;
	    this.destination = destination;
	    this.typeOp = null;
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

    public String getdestination()
    {
	    return this.destination;
    }
    
    public void printIRNode()
    {
	    String t_op1;
	    String t_op2;
	    String t_destination;
	
	    if (op1 == null)
	    {
	        t_op1 = "";
	    }
	    else
	    {
	        t_op1 = " " + op1;
	    }
    
	    if (op2 == null)
	    {
	        t_op2 = "";
	    }
	    else
	    {
	        t_op2 = " " + op2;
	    }

	    if (destination == null)
	    {
	        t_destination = "";
	    }
	    else
	    {
	        t_destination = " " + destination;
	    }
    
	    System.out.println(";" + opCode + t_op1 + t_op2 + t_destination);
   }
    
    public String toString()
    {
    	 String t_op1;
 	    String t_op2;
 	    String t_destination;
 	
 	    if (op1 == null)
 	    {
 	        t_op1 = "";
 	    }
 	    else
 	    {
 	        t_op1 = " " + op1;
 	    }
     
 	    if (op2 == null)
 	    {
 	        t_op2 = "";
 	    }
 	    else
 	    {
 	        t_op2 = " " + op2;
 	    }

 	    if (destination == null)
 	    {
 	        t_destination = "";
 	    }
 	    else
 	    {
 	        t_destination = " " + destination;
 	    }
     
 	    return opCode + t_op1 + t_op2 + t_destination;
    }
}
