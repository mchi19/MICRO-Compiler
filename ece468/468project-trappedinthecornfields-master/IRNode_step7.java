import java.util.*;

public class IRNode
{
    String opCode;
    String op1;
    String op2;
    String destination;
    private ArrayList<IRNode> successor;
    private ArrayList<IRNode> predecessor;
    private Set<String> genSet;
    private Set<String> killSet;
    private Set<String> inSet;
    private Set<String> outSet;
    private boolean isLeader;

    public IRNode(String opCode, String op1, String op2, String destination)
    {
	    this.opCode = opCode;
	    this.op1 = op1;
	    this.op2 = op2;
	    this.destination = destination;
	    this.successor = new ArrayList<IRNode>();
	    this.predecessor = new ArrayList<IRNode>();
	    this.genSet = new HashSet<String>();
	    this.killSet = new HashSet<String>();
	    this.inSet = new HashSet<String>();
	    this.outSet = new HashSet<String>();
	    this.isLeader = false;
    }

    //newly added step 7 attributes
    public void addSucessor(IRNode irnode)
    {
	this.successor.add(irnode);
    }
    public ArrayList<IRNode> getSuccessor() 
    {
	return this.successor;
    }

    public void addPredecessor(IRNode irnode)
    {
	this.predecessor.add(irnode);
    }
    public ArrayList<IRNode> getPredecessor()
    {
	return this.predecessor;
    }

    public void addGenSet(String dest)
    {
	this.genSet.add(dest);
    }
    public Set<String> getGenSet()
    {
	return this.genSet;
    }

    public void addKillSet(String dest)
    {
	this.killSet.add(dest);
    }
    public Set<String> getKillSet()
    {
	return this.killSet;
    }

    public void addInSet(String temp)
    {
	this.inSet.add(temp);
    }
    public Set<String> getInSet()
    {
	return this.inSet;
    }

    public void addOutSet(String temp)
    {
	this.outSet.add(temp);
    }
    public Set<String> getOutSet()
    {
	return this.outSet;
    }

    public void setInSet(Set<String> inSet)
    {
	this.inSet = inSet;
    }

    public void setOutSet(Set<String> outSet)
    {
	this.outSet = outSet;
    }

    public void setGenSet(Set<String> genSet)
    {
	this.genSet = genSet;
    }

    public void setKillSet(Set<String> killSet)
    {
	this.killSet = killSet;
    }

    public boolean getLeader() 
    {
	return this.isLeader;
    }

    public void setLeader()
    {
	this.isLeader = true;
    }

    
    //original irnode attributes for just opCodes
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
}

