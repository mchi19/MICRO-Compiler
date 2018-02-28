import java.util.*;

public class SymbolTable
{
	String scope;
    ArrayList<Node> nodes;
    ArrayList<SymbolTable> children;
    SymbolTable parent = null;
    

    public SymbolTable(String scope)
    {
    	this.scope = scope;
    	this.nodes = new ArrayList<Node>();
    	this.children =  new ArrayList<SymbolTable>();
    }
    
    public String getNodeType(String name)
    {
    	for (Node x : nodes)
    		if(x.getName().equals(name))
    			return x.getType();
    	return "ERR";
    }
    
    public boolean addData(Node n)
    {
    	for (Node x : nodes)
    		if(n.getName().equals(x.getName()))
    			{
    				System.out.println("DECLARATION ERROR " + n.getName());
    				System.exit(0);
    				return false;
    			}
    	nodes.add(n);
    	return true;
    }
    
    public boolean addData0(Node n)
    {
    	for (Node x : nodes)
    		if(n.getName().equals(x.getName()))
    			{
    				System.out.println("DECLARATION ERROR " + n.getName());
    				System.exit(0);
    				return false;
    			}
    	nodes.add(0,n);
    	return true;
    }
    
    public void setParent(SymbolTable st)
    {
    	this.parent = st;
    	st.addChild(this);
    }
   
    public void addChild(SymbolTable st)
    {
    	this.children.add(st);
    }
    
    public boolean addData(ArrayList<Node> ns)
    {
    	for(Node n : ns)
    		{
        	for (Node x : nodes)
        		if(n.getName().equals(x.getName()))
        			{	
        			
        				System.out.println("DECLARATION ERROR " + n.getName());
        				System.exit(0);
        				return false;
        			}
    		nodes.add(n);
    		}
    	return true;
    }
    
    public void printTable()
    {
    	System.out.println("Symbol table " + this.scope);
    	for (Node n : nodes)
    	{
    		n.printNode();
    	}
    }
    
    public void printTree()
    {
    	//this.printTable();

    	for (SymbolTable st : children)
	    {	
		System.out.println();
    		st.printTree();   
	    } 	
    }

}
