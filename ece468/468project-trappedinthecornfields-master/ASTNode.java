import java.util.*;

public class ASTNode
{
	ASTNode left = null;
	ASTNode right =  null;
	ASTNode parent =  null; 
	String nodeType = null;
	String nodeValue =  null;
	
	//IF Children
	ArrayList<ASTNode> ifChild = null;
	ArrayList<ASTNode> elseChild = null;
	
	
	public ASTNode(ASTNode left, ASTNode right, ASTNode parent, String nodeType, String nodeValue)
	{
		this.left =  left;
		this.right = right;
		this.parent =  parent;
		this.nodeType = nodeType;
		this.nodeValue = nodeValue;
		this.ifChild = null;
		this.elseChild = null;
	}
	
	public void printAST(Integer level)
	{
		System.out.println(level.toString()+ ": NodeType: " + nodeType + " NodeValue: " + nodeValue);
		
		if(nodeType.equals("IF"))
		{
			left.printAST(level+1);
			System.out.println("IF CHILDREN " + level);
			for(ASTNode as : ifChild)
				as.printAST(level+1);
			if(elseChild.size() != 0)
			{
				System.out.println("ELSE CHILDREN " + level);
				for(ASTNode as : elseChild)
					as.printAST(level+1);
			}
		}
		else if(nodeType.equals("FOR"))
		{
			if(left != null)
				{
					System.out.println("INIT CHILD " + level);
					left.printAST(level+1);
				}
			if(right != null)
				{
					System.out.println("COND CHILD " + level);
					right.printAST(level+1);
				}
			if(parent != null)
			{
				System.out.println("INCR CHILD " + level);
				parent.printAST(level+1);
			}
			System.out.println("FOR CHILDREN " + level);
			for(ASTNode as : ifChild)
				as.printAST(level+1);
		}
		else
		{
			if(left != null)
				left.printAST(level+1);
			if(right != null)
				right.printAST(level+1);
			if(level == 0)
				System.out.println();
		}
	}
}
