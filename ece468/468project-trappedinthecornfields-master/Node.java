import java.util.*;

public class Node
{
    String name;
    String type;
    String value;

    public Node(String name, String value, String type)
    {
    	this.name = name;
    	this.value = value;
    	this.type = type;	
    }
    
    public String getName()
    {
    	return this.name;
    }
    public String getValue()
    {
    	return this.value;
    }
    public String getType()
    {
    	return this.type;
    }
    
    public void printNode()
    {
    	if (this.type.equals("STRING"))
		{
		    System.out.println("name " + this.name + " type " + this.type + " value " + this.value);
		}
    	else
		{
		    System.out.println("name " + this.name + " type " + this.type);
		}
    }
}
