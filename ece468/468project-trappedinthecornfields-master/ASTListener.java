import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import java.util.*;

public class ASTListener extends MICROBaseListener
{
	Integer tempNum, labelNum;
	SymbolTable st;
	HashMap<ParseTree, ASTNode> nodeMap;
	HashMap<ParseTree, ArrayList<ASTNode>> nodeArrayMap;
	ArrayList<ASTNode> nodes;
	ArrayList<IRNode> ACList;
	ArrayList<String> labelStack;
	ParseTreeProperty<String> nodeValues;
	ParseTreeProperty<String> nodeTypes;
	ParseTreeProperty<ArrayList<String>> idList;
	public ASTListener(SymbolTable st)
	{
		this.nodeMap = new HashMap<ParseTree, ASTNode>();
		this.nodeArrayMap = new HashMap<ParseTree, ArrayList<ASTNode>>();
		this.nodes = new ArrayList<ASTNode>();
		this.nodeValues = new ParseTreeProperty();
		this.nodeTypes =  new ParseTreeProperty();
		this.idList = new ParseTreeProperty();
		this.ACList = new ArrayList<IRNode>();
		this.st = st;
		this.labelStack = new ArrayList<String>();
		tempNum = 1;
		labelNum = 1;
	}
	
	@Override public void exitFunc_decl(MICROParser.Func_declContext ctx)
	{

	/*	System.out.println("----------------AST-----------------");
		System.out.println("FUNCTION START");
		for(ASTNode as : nodes)
		{
			as.printAST(0);
		}
	
		System.out.println("----------------3AC-----------------");
	*/	
		System.out.println(";LABEL main");
		System.out.println(";LINK");
		for(ASTNode as : nodes)
		{
			String rootType = "";
			if(!as.nodeType.equals("FOR") && !as.nodeType.equals("IF"))
				rootType = st.getNodeType(as.left.nodeValue);
			for(IRNode inode : ASTto3AC(as,rootType).IRNodeList)
				{
					inode.printIRNode();
					ACList.add(inode);
				}
		}
		System.out.println(";RET");

		//System.out.println("----------------Tiny----------------");
		printTiny(ACList);
	
	}
	
	@Override public void exitFor_stmt(MICROParser.For_stmtContext ctx)
	{
		ASTNode initNode = nodeMap.get(ctx.getChild(2));
		ASTNode condNode = nodeMap.get(ctx.getChild(4));
		ASTNode incNode = nodeMap.get(ctx.getChild(6));
		ASTNode node =  new ASTNode(initNode, condNode, incNode, "FOR", "FOR");
		node.ifChild = nodeArrayMap.get(ctx.getChild(9));
		nodeMap.put(ctx, node);
		ArrayList<ASTNode> aNode = new ArrayList<ASTNode>();
		aNode.add(node);
		nodeArrayMap.put(ctx, aNode);
	}
	
	@Override public void exitInit_stmt(MICROParser.Init_stmtContext ctx)
	{
		if(ctx.getChild(0).getChild(0) != null && nodeTypes.get(ctx.getChild(0)).equals("ASSIGN"))
		{
			nodeMap.put(ctx, nodeMap.get(ctx.getChild(0)));
		}
	}
	
	@Override public void exitIncr_stmt(MICROParser.Incr_stmtContext ctx)
	{
		if(ctx.getChild(0).getChild(0) != null && nodeTypes.get(ctx.getChild(0)).equals("ASSIGN"))
		{
			nodeMap.put(ctx, nodeMap.get(ctx.getChild(0)));
		}
	}
	
	@Override public void exitFunc_body(MICROParser.Func_bodyContext ctx)
	{
		nodes = nodeArrayMap.get(ctx.getChild(1));
	}
	
	@Override public void exitStmt_list(MICROParser.Stmt_listContext ctx)
	{
		//EMPTY
		if(ctx.getChild(1) != null)
		{
			//STMT_LIST == EMPTY
			if(ctx.getChild(1).getChild(1) == null)
			{
				nodeArrayMap.put(ctx, nodeArrayMap.get(ctx.getChild(0)));
			}
			else
			{
				ArrayList<ASTNode> as = nodeArrayMap.get(ctx.getChild(1));
				if(nodeArrayMap.get(ctx.getChild(0)) != null)
					as.addAll(0, nodeArrayMap.get(ctx.getChild(0)));
					
				nodeArrayMap.put(ctx, as);
			}
		}
	}
	
	@Override public void exitBase_stmt(MICROParser.Base_stmtContext ctx)
	{
		nodeArrayMap.put(ctx, nodeArrayMap.get(ctx.getChild(0)));
	}
	
	@Override public void exitStmt(MICROParser.StmtContext ctx)
	{
		nodeArrayMap.put(ctx, nodeArrayMap.get(ctx.getChild(0)));
	}
	
	@Override public void exitAssign_stmt(MICROParser.Assign_stmtContext ctx)
	{
		nodeArrayMap.put(ctx, nodeArrayMap.get(ctx.getChild(0)));
	}
	
	@Override public void exitAssign_expr(MICROParser.Assign_exprContext ctx)
	{
		nodeValues.put(ctx, ":=");
		nodeTypes.put(ctx, "ASSIGN");
		
		createASTNode(ctx, nodeMap.get(ctx.getChild(0)), nodeMap.get(ctx.getChild(2)));
		
		ArrayList<ASTNode> as = new ArrayList<ASTNode>();
		as.add(0,nodeMap.get(ctx));
		nodeArrayMap.put(ctx, as);
		//nodes.add(nodeMap.get(ctx));
	}
	
	@Override public void exitWrite_stmt(MICROParser.Write_stmtContext ctx)
	{
		ArrayList<ASTNode> as = new ArrayList<ASTNode>();
		for(String id : idList.get(ctx.getChild(2)))
			{
				//System.out.println(id);
				ASTNode idNode = new ASTNode(null,null,null,"ID",id);
				as.add(new ASTNode(idNode,null,null,"WRITE","WRITE"));
				//nodes.add(new ASTNode(idNode,null,null,"WRITE","WRITE"));
			}
		nodeArrayMap.put(ctx, as);
	}
	
	@Override public void exitId_list(MICROParser.Id_listContext ctx)
	{
		if(ctx.getChild(1) == null)
		{
			ArrayList<String> ids = new ArrayList<String>();
			ids.add(ctx.getChild(0).getText());
			idList.put(ctx, ids);
		}
		else
		{
			ArrayList<String> ids = idList.get(ctx.getChild(1));
			ids.add(0, ctx.getChild(0).getText());
			idList.put(ctx, ids);
		}
	}
	
	@Override public void exitId_tail(MICROParser.Id_tailContext ctx)
	{
		if(ctx.getChild(1) != null)	//Not Empty
		{
			if(idList.get(ctx.getChild(2)) == null)
			{
				ArrayList<String> ids = new ArrayList<String>();
				ids.add(ctx.getChild(1).getText());
				idList.put(ctx, ids);
			}
			else
			{
				ArrayList<String> ids = idList.get(ctx.getChild(2));
				ids.add(0, ctx.getChild(1).getText());
				idList.put(ctx, ids);
			}
		}
		else
		{
			ArrayList<String> ids = new ArrayList<String>();
			idList.put(ctx, ids);
		}
	}
	
	@Override public void exitRead_stmt(MICROParser.Read_stmtContext ctx)
	{
		ArrayList<ASTNode> as = new ArrayList<ASTNode>();
		for(String id : idList.get(ctx.getChild(2)))
		{
			
			ASTNode idNode = new ASTNode(null,null,null,"ID",id);
			as.add(new ASTNode(idNode,null,null,"READ","READ"));
			//nodes.add(new ASTNode(idNode,null,null,"READ","READ"));
		}
		nodeArrayMap.put(ctx, as);
	}
	
	@Override public void exitExpr(MICROParser.ExprContext ctx)
	{
		//EXPR: EXPR_PREFIX FACTOR
		String op = nodeValues.get(ctx.getChild(0));
		nodeValues.put(ctx, op);
		nodeTypes.put(ctx, op);
		
		//EXPR_PREFIX NULL
		if(nodeValues.get(ctx.getChild(0).getChild(1)) == null)
			nodeMap.put(ctx, nodeMap.get(ctx.getChild(1)));
		//EXPR_PREFIX
		else
			createASTNode(ctx, nodeMap.get(ctx.getChild(0)), nodeMap.get(ctx.getChild(1)));
	}
	
	@Override public void exitExpr_prefix(MICROParser.Expr_prefixContext ctx)
	{
		String op = "NULL";
		
		if(ctx.getChild(1) != null) //Not Empty
			{
				if(nodeValues.get(ctx.getChild(0).getChild(1)) == null) //EXPR_PREFIX == EMPTY
				{
					if(ctx.getChild(2) != null)
					{
						op = nodeValues.get(ctx.getChild(2));
					}	
					nodeValues.put(ctx, op);
					nodeTypes.put(ctx, op);
					
					nodeMap.put(ctx, nodeMap.get(ctx.getChild(1)));
				}
				else
				{
					if(ctx.getChild(2) != null)
					{
						op = nodeValues.get(ctx.getChild(0));
					}	
					nodeValues.put(ctx, op);
					nodeTypes.put(ctx, op);
					createASTNode(ctx, nodeMap.get(ctx.getChild(0)), nodeMap.get(ctx.getChild(1)));
					op = nodeValues.get(ctx.getChild(2));
					nodeValues.put(ctx, op);
					nodeTypes.put(ctx, op);
				}
			}
	}
	
	@Override public void exitFactor(MICROParser.FactorContext ctx)
	{
		//FACTOR : FACTOR_PREFIX POSTFIX_EXPR
		
		String op = nodeValues.get(ctx.getChild(0));
		nodeValues.put(ctx, op);
		nodeTypes.put(ctx, op);
		
		//Factor_Prefix NULL
		if(nodeValues.get(ctx.getChild(0).getChild(0)) == null)
			nodeMap.put(ctx, nodeMap.get(ctx.getChild(1)));
		//Factor_Prefix  
		else
			createASTNode(ctx, nodeMap.get(ctx.getChild(0)), nodeMap.get(ctx.getChild(1)));
		
	}
	
	@Override public void exitFactor_prefix(MICROParser.Factor_prefixContext ctx)
	{
		String op = "NULL";
		if(ctx.getChild(2) != null)
			op = ctx.getChild(2).getText();
		
		nodeValues.put(ctx, op);
		nodeTypes.put(ctx, op);
		
		if(ctx.getChild(1) != null)
		{
			if(nodeValues.get(ctx.getChild(0).getChild(0)) == null)
				nodeMap.put(ctx, nodeMap.get(ctx.getChild(1))); 
			else
				createASTNode(ctx, nodeMap.get(ctx.getChild(0)), nodeMap.get(ctx.getChild(1)));
		}
	}
	
	@Override public void exitPostfix_expr(MICROParser.Postfix_exprContext ctx)
	{
		nodeValues.put(ctx, "NULL");
		nodeTypes.put(ctx, "POSTFIX EXPRESSION");
		
		nodeMap.put(ctx, nodeMap.get(ctx.getChild(0)));
	}
	
	@Override public void exitAddop(MICROParser.AddopContext ctx)
	{
		nodeValues.put(ctx, ctx.getText());
		nodeTypes.put(ctx, "ADDOP");
	}
	
	@Override public void exitMulop(MICROParser.MulopContext ctx)
	{
		nodeValues.put(ctx, ctx.getText());
		nodeTypes.put(ctx, "MULOP");
	}
	
	@Override public void exitId(MICROParser.IdContext ctx)
	{
		nodeValues.put(ctx, ctx.getText());
		nodeTypes.put(ctx, "ID");
		createASTNode(ctx,null,null);
	}
	
	@Override public void exitPrimary(MICROParser.PrimaryContext ctx)
	{
		if(ctx.getChild(0).getText().equals("("))
			nodeMap.put(ctx, nodeMap.get(ctx.getChild(1)));
		else
		{
			nodeValues.put(ctx, ctx.getText());
			nodeTypes.put(ctx, "PRIMARY");
			createASTNode(ctx, null, null);
		}
	}
	
	@Override public void exitCond(MICROParser.CondContext ctx)
	{
		nodeValues.put(ctx, ctx.getChild(1).getChild(0).getText());
		nodeTypes.put(ctx, "COND");
		createASTNode(ctx, nodeMap.get(ctx.getChild(0)), nodeMap.get(ctx.getChild(2)));
	}
	
	@Override public void exitIf_stmt(MICROParser.If_stmtContext ctx)
	{
		nodeValues.put(ctx, "IF");
		nodeTypes.put(ctx, "IF");
		ArrayList<ASTNode> elseBlock = nodeArrayMap.get(ctx.getChild(6));
		ArrayList<ASTNode> codeBlock = nodeArrayMap.get(ctx.getChild(5));
		ASTNode condNode = nodeMap.get(ctx.getChild(2));
		ASTNode node =  new ASTNode(condNode, null, null, "IF", "IF");
		node.ifChild = codeBlock;
		node.elseChild = elseBlock;
		ArrayList<ASTNode> nodeA = new ArrayList<ASTNode>();
		nodeA.add(node);
		nodeMap.put(ctx, node);
		nodeArrayMap.put(ctx, nodeA);
	}
	
	@Override public void exitElse_part(MICROParser.Else_partContext ctx)
	{
		//NOT EMPTY
		if(ctx.getChild(1) != null)
		{
			nodeArrayMap.put(ctx, nodeArrayMap.get(ctx.getChild(2)));
		}
		else
		{
			nodeArrayMap.put(ctx, new ArrayList<ASTNode>());
		}
	}
	
	//Creates an AST for the specified Parse Tree Node (Stored in Node Map)
	public void createASTNode(ParseTree ctx, ASTNode left, ASTNode right)
	{
		String type = nodeTypes.get(ctx);
		String value = nodeValues.get(ctx);
		nodeMap.put(ctx, new ASTNode(left,right,null,type,value));
	}
	
	//---------------------3AC------------------------------------------------
	
	//Converts Specified AST to 3AC List
	public IRObj ASTto3AC(ASTNode ast, String rootType)
	{
		IRObj lhs = null;
		IRObj rhs = null;
		IRObj phs = null;
		IRObj codeObject = new IRObj(null,null,null);
		//RECURSIVE CALLS (POSTORDER WALK)
		if(ast.nodeType.equals("FOR"))
		{
			String rootTypef, tempLabel, loopLabel;
			ArrayList<IRNode> tempList = new ArrayList<IRNode>();
			//INIT
			if(ast.left != null)
			{
				rootTypef = st.getNodeType(ast.left.left.nodeValue);
				lhs = ASTto3AC(ast.left, rootTypef);
				tempList.addAll(lhs.IRNodeList);
			}
			
			//LABELS
			tempLabel = "label"+labelNum;
			labelNum++;
			labelStack.add(0,tempLabel);
			loopLabel = "label"+labelNum;
			labelNum++;
			
			tempList.add(new IRNode("LABEL",loopLabel,null,null));
			
			//COND	
			rhs = ASTto3AC(ast.right, "FORCOND");	
			tempList.addAll(rhs.IRNodeList);
			
			//STMT_LIST	
			for (ASTNode n : ast.ifChild)
			{
				rootTypef = "";
				if(!n.nodeType.equals("FOR") && !n.nodeType.equals("IF"))
					rootTypef = st.getNodeType(n.left.nodeValue);
				tempList.addAll(ASTto3AC(n, rootTypef).IRNodeList); //Need to update root type param for these
			}
			//INCR	
			if(ast.parent != null)	
			{
				rootTypef = st.getNodeType(ast.parent.left.nodeValue);
				phs = ASTto3AC(ast.parent, rootTypef);
				tempList.addAll(phs.IRNodeList);
			}
			tempList.add(new IRNode("JUMP",loopLabel,null,null));
			tempList.add(new IRNode("LABEL",tempLabel,null,null));
			
			codeObject = new IRObj(tempList,null,null);
			
		}
		else if(ast.nodeType.equals("IF"))
		{
			ArrayList<IRNode> tempList = new ArrayList<IRNode>();
			String rootTypet;
			//Append (NOT IF) Label
			String tempLabel = "label"+labelNum;
			labelNum++;
			labelStack.add(0,tempLabel);
			//COND
			lhs = ASTto3AC(ast.left, "");
			tempList.addAll(lhs.IRNodeList);
			
			
			String endLabel = "label"+labelNum;
			labelNum++;
			labelStack.add(0,endLabel);
			//IF Code Block
			for (ASTNode n : ast.ifChild)
				{
					rootTypet = "";
					if(!n.nodeType.equals("FOR") && !n.nodeType.equals("IF"))
						rootTypet = st.getNodeType(n.left.nodeValue);
					tempList.addAll(ASTto3AC(n, rootTypet).IRNodeList); //Need to update root type param for these
				}
			tempList.add(new IRNode("JUMP", endLabel,null, null));
			tempList.add(new IRNode("LABEL",tempLabel,null,null));
			
			//Else Code Block
			if(ast.elseChild != null && ast.elseChild.size() != 0)
				{
					for (ASTNode n : ast.elseChild)
					{
						rootTypet = "";
						if(!n.nodeType.equals("FOR") && !n.nodeType.equals("IF"))
							rootTypet = st.getNodeType(n.left.nodeValue);
						tempList.addAll(ASTto3AC(n, rootTypet).IRNodeList); //Need to update root type param for these
					}
				}
			tempList.add(new IRNode("LABEL",endLabel,null,null));
			codeObject = new IRObj(tempList,null,null);
		}
		else
		{	
		if(ast.right != null)
			rhs = ASTto3AC(ast.right, rootType);
		if(ast.left != null)
			lhs = ASTto3AC(ast.left, rootType);
		}
		
		
		IRNode inode;
		switch(ast.nodeType)
		{
			case "FOR": break;
			
			case "IF": break;
			
			case "COND" :
			{
				switch(ast.nodeValue)
				{
					case "<": 	
							if(lhs != null && rhs != null)
							{
								String newType = lhs.resultType;
								ArrayList<IRNode> iList;
								iList = rhs.IRNodeList;
								iList.addAll(lhs.IRNodeList);
								
								//RHS Type Unkown
								if(!(rhs.resultType.equals("INT")||rhs.resultType.equals("FLOAT")))
								  {
									  if(lhs.resultType.equals("INT"))
										  inode = new IRNode("STOREI",rhs.resultType,null,rhs.result);
									  else
										  inode = new IRNode("STOREF",rhs.resultType,null,rhs.result);
									  iList.add(inode); 
								  }
								//LHS Type Unkown
								else if(!(lhs.resultType.equals("INT")||lhs.resultType.equals("FLOAT")))
								  {
									  if(rhs.resultType.equals("INT"))
										  inode = new IRNode("STOREI",lhs.resultType,null,lhs.result);
									  else
										  inode = new IRNode("STOREF",lhs.resultType,null,lhs.result);
									  newType = rhs.resultType;
									  iList.add(inode); 
								  }
								
								inode = new IRNode("GE",lhs.result,rhs.result,labelStack.get(0));
								inode.typeOp = newType;
								labelStack.remove(0);
								iList.add(inode);
								codeObject =  new IRObj(iList, null, newType);
							}
							break;
				case "<=": 	
							if(lhs != null && rhs != null)
							{
								ArrayList<IRNode> iList;
								iList = rhs.IRNodeList;
								iList.addAll(lhs.IRNodeList);
								String newType = lhs.resultType;
								
								//RHS Type Unkown
								if(!(rhs.resultType.equals("INT")||rhs.resultType.equals("FLOAT")))
								  {
									  if(lhs.resultType.equals("INT"))
										  inode = new IRNode("STOREI",rhs.resultType,null,rhs.result);
									  else
										  inode = new IRNode("STOREF",rhs.resultType,null,rhs.result);
									  iList.add(inode); 
								  }
								
								//LHS Type Unkown
								else if(!(lhs.resultType.equals("INT")||lhs.resultType.equals("FLOAT")))
								  {
									  if(rhs.resultType.equals("INT"))
										  inode = new IRNode("STOREI",lhs.resultType,null,lhs.result);
									  else
										  inode = new IRNode("STOREF",lhs.resultType,null,lhs.result);
									  newType = rhs.resultType;
									  iList.add(inode); 
								  }
								
								inode = new IRNode("GT",lhs.result,rhs.result,labelStack.get(0));
								inode.typeOp = newType;
								labelStack.remove(0);
								iList.add(inode);
								codeObject =  new IRObj(iList, null, newType);
							}
							break;
		
				case ">": 	
							if(lhs != null && rhs != null)
							{
								ArrayList<IRNode> iList;
								iList = rhs.IRNodeList;
								iList.addAll(lhs.IRNodeList);
								String newType = lhs.resultType;
								
								//RHS Type Unkown
								if(!(rhs.resultType.equals("INT")||rhs.resultType.equals("FLOAT")))
								  {
									  if(lhs.resultType.equals("INT"))
										  inode = new IRNode("STOREI",rhs.resultType,null,rhs.result);
									  else
										  inode = new IRNode("STOREF",rhs.resultType,null,rhs.result);
									  iList.add(inode); 
								  }
								//LHS Type Unkown
								else if(!(lhs.resultType.equals("INT")||lhs.resultType.equals("FLOAT")))
								  {
									  if(rhs.resultType.equals("INT"))
										  inode = new IRNode("STOREI",lhs.resultType,null,lhs.result);
									  else
										  inode = new IRNode("STOREF",lhs.resultType,null,lhs.result);
									  newType = rhs.resultType;
									  iList.add(inode); 
								  }
								
								inode = new IRNode("LE",lhs.result,rhs.result,labelStack.get(0));
								inode.typeOp = newType;
								labelStack.remove(0);
								iList.add(inode);
								codeObject =  new IRObj(iList, null, newType);
							}
							break;
				
				case ">=": 	
							if(lhs != null && rhs != null)
							{
								String newType = lhs.resultType;
								ArrayList<IRNode> iList;
								iList = rhs.IRNodeList;
								iList.addAll(lhs.IRNodeList);
								
								//RHS Type Unkown
								if(!(rhs.resultType.equals("INT")||rhs.resultType.equals("FLOAT")))
								  {
									  if(lhs.resultType.equals("INT"))
										  inode = new IRNode("STOREI",rhs.resultType,null,rhs.result);
									  else
										  inode = new IRNode("STOREF",rhs.resultType,null,rhs.result);
									  iList.add(inode); 
								  }
								
								//LHS Type Unkown
								else if(!(lhs.resultType.equals("INT")||lhs.resultType.equals("FLOAT")))
								  {
									  if(rhs.resultType.equals("INT"))
										  inode = new IRNode("STOREI",lhs.resultType,null,lhs.result);
									  else
										  inode = new IRNode("STOREF",lhs.resultType,null,lhs.result);
									  newType = rhs.resultType;
									  iList.add(inode); 
								  }
								
								inode = new IRNode("LT",lhs.result,rhs.result,labelStack.get(0));
								inode.typeOp = newType;
								labelStack.remove(0);
								iList.add(inode);
								codeObject =  new IRObj(iList, null, newType);
							}
							break;
				case "=": 	
							if(lhs != null && rhs != null)
							{
								ArrayList<IRNode> iList;
								iList = rhs.IRNodeList;
								iList.addAll(lhs.IRNodeList);
								String newType = lhs.resultType;
								
								//RHS Type Unkown
								if(!(rhs.resultType.equals("INT")||rhs.resultType.equals("FLOAT")))
								  {
									  if(lhs.resultType.equals("INT"))
										  inode = new IRNode("STOREI",rhs.resultType,null,rhs.result);
									  else
										  inode = new IRNode("STOREF",rhs.resultType,null,rhs.result);
									  iList.add(inode); 
								  }
								
								//LHS Type Unkown
								else if(!(lhs.resultType.equals("INT")||lhs.resultType.equals("FLOAT")))
								  {
									  if(rhs.resultType.equals("INT"))
										  inode = new IRNode("STOREI",lhs.resultType,null,lhs.result);
									  else
										  inode = new IRNode("STOREF",lhs.resultType,null,lhs.result);
									  newType = rhs.resultType;
									  iList.add(inode); 
								  }
								
								inode = new IRNode("NE",lhs.result,rhs.result,labelStack.get(0));
								inode.typeOp = newType;
								labelStack.remove(0);
								iList.add(inode);
								codeObject =  new IRObj(iList, null, newType);
							}
							break;
				case "!=": 	
							if(lhs != null && rhs != null)
							{
								ArrayList<IRNode> iList;
								iList = rhs.IRNodeList;
								iList.addAll(lhs.IRNodeList);
								String newType = lhs.resultType;
								
								//RHS Type Unkown
								if(!(rhs.resultType.equals("INT")||rhs.resultType.equals("FLOAT")))
								  {
									  if(lhs.resultType.equals("INT"))
										  inode = new IRNode("STOREI",rhs.resultType,null,rhs.result);
									  else
										  inode = new IRNode("STOREF",rhs.resultType,null,rhs.result);
									  iList.add(inode); 
								  }
								
								//LHS Type Unkown
								else if(!(lhs.resultType.equals("INT")||lhs.resultType.equals("FLOAT")))
								  {
									  if(rhs.resultType.equals("INT"))
										  inode = new IRNode("STOREI",lhs.resultType,null,lhs.result);
									  else
										  inode = new IRNode("STOREF",lhs.resultType,null,lhs.result);
									  newType = rhs.resultType;
									  iList.add(inode); 
								  }
								
								inode = new IRNode("EQ",lhs.result,rhs.result,labelStack.get(0));
								inode.typeOp = newType;
								labelStack.remove(0);
								iList.add(inode);
								codeObject =  new IRObj(iList, null, newType);
							}
							break;
						}
				break;
			}
			
			case "PRIMARY": 	
							if(st.getNodeType(ast.nodeValue).equals("ERR"))
								{
									codeObject = new IRObj(new ArrayList<IRNode>(),"$T"+tempNum,ast.nodeValue); 
									tempNum++;
								}
							else if (st.getNodeType(ast.nodeValue).equals("INT"))
								{
									codeObject = new IRObj(new ArrayList<IRNode>(),ast.nodeValue,"INT"); 
								}
							else
								{
									codeObject = new IRObj(new ArrayList<IRNode>(),ast.nodeValue,"FLOAT"); 
								}
							break;
			case "ID": 
						if(st.getNodeType(ast.nodeValue).equals("INT"))
							codeObject = new IRObj(new ArrayList<IRNode>(),ast.nodeValue,"INT"); 
						else if(st.getNodeType(ast.nodeValue).equals("FLOAT"))
							codeObject = new IRObj(new ArrayList<IRNode>(),ast.nodeValue,"FLOAT"); 
						else
							codeObject = new IRObj(new ArrayList<IRNode>(),ast.nodeValue,"STRING"); 
						break;
			case "+": 	
						if(lhs != null && rhs != null)
						{
							String newType = lhs.resultType;
							ArrayList<IRNode> iList;
							iList = rhs.IRNodeList;
							iList.addAll(lhs.IRNodeList);
							if(!(rhs.resultType.equals("INT")||rhs.resultType.equals("FLOAT")))
							  {
								  if(lhs.resultType.equals("INT"))
									  inode = new IRNode("STOREI",rhs.resultType,null,rhs.result);
								  else
									  inode = new IRNode("STOREF",rhs.resultType,null,rhs.result);
								  iList.add(inode); 
							  }
							
							else if(!(lhs.resultType.equals("INT")||lhs.resultType.equals("FLOAT")))
							  {
								  if(rhs.resultType.equals("INT"))
									  inode = new IRNode("STOREI",lhs.resultType,null,lhs.result);
								  else
									  inode = new IRNode("STOREF",lhs.resultType,null,lhs.result);
								  newType = rhs.resultType;
								  iList.add(inode); 
							  }
							
							if(lhs.resultType.equals("INT")||rhs.resultType.equals("INT"))
								inode = new IRNode("ADDI",lhs.result,rhs.result,"$T"+tempNum);
							else
								inode = new IRNode("ADDF",lhs.result,rhs.result,"$T"+tempNum);
							tempNum++;
							iList.add(inode);
							codeObject = new IRObj(iList,inode.destination, newType); 
						}
						break;
			case "-":	
						if(lhs != null && rhs != null)
						  {
							  String newType = lhs.resultType;
							  ArrayList<IRNode> iList;
							  iList = rhs.IRNodeList;
							  iList.addAll(lhs.IRNodeList);
							  if(!(rhs.resultType.equals("INT")||rhs.resultType.equals("FLOAT")))
							  {
								  if(lhs.resultType.equals("INT"))
									  inode = new IRNode("STOREI",rhs.resultType,null,rhs.result);
								  else
									  inode = new IRNode("STOREF",rhs.resultType,null,rhs.result);
								  iList.add(inode); 
							  }
							  
							  else if(!(lhs.resultType.equals("INT")||lhs.resultType.equals("FLOAT")))
							  {
								  if(rhs.resultType.equals("INT"))
									  inode = new IRNode("STOREI",lhs.resultType,null,lhs.result);
								  else
									  inode = new IRNode("STOREF",lhs.resultType,null,lhs.result);
								  newType = rhs.resultType;
								  iList.add(inode); 
							  }
							  
							  if(lhs.resultType.equals("INT")||rhs.resultType.equals("INT"))
								  inode = new IRNode("SUBI",lhs.result,rhs.result,"$T"+tempNum);
							  else
								  inode = new IRNode("SUBF",lhs.result,rhs.result,"$T"+tempNum);
							  tempNum++;
							  iList.add(inode);
							  codeObject = new IRObj(iList,inode.destination, newType); 
						  }
						break;
			case "*": 	
						if(lhs != null && rhs != null)
						{
							String newType = lhs.resultType;
							ArrayList<IRNode> iList;
							iList = rhs.IRNodeList;
							iList.addAll(lhs.IRNodeList);
							  if(!(rhs.resultType.equals("INT")||rhs.resultType.equals("FLOAT")))
							  {
								  if(lhs.resultType.equals("INT"))
									  inode = new IRNode("STOREI",rhs.resultType,null,rhs.result);
								  else
									  inode = new IRNode("STOREF",rhs.resultType,null,rhs.result);
								  iList.add(inode); 
							  }
							  
							  if(!(lhs.resultType.equals("INT")||lhs.resultType.equals("FLOAT")))
							  {
								  if(rhs.resultType.equals("INT"))
									  inode = new IRNode("STOREI",lhs.resultType,null,lhs.result);
								  else
									  inode = new IRNode("STOREF",lhs.resultType,null,lhs.result);
								  newType = rhs.resultType;
								  iList.add(inode); 
							  }
							  
							if(lhs.resultType.equals("INT"))
								inode = new IRNode("MULI",lhs.result,rhs.result,"$T"+tempNum);
							else
								inode = new IRNode("MULF",lhs.result,rhs.result,"$T"+tempNum);
							tempNum++;

							iList.add(inode);
							codeObject = new IRObj(iList, inode.destination, rootType); 
						}
						break;
			case "/": 	
						if(lhs != null && rhs != null)
						  {
							  String newType = lhs.resultType;
							  ArrayList<IRNode> iList;
							  iList = rhs.IRNodeList;
							  iList.addAll(lhs.IRNodeList);
							  if(!(rhs.resultType.equals("INT")||rhs.resultType.equals("FLOAT")))
							  {
								  if(lhs.resultType.equals("INT"))
									  inode = new IRNode("STOREI",rhs.resultType,null,rhs.result);
								  else
									  inode = new IRNode("STOREF",rhs.resultType,null,rhs.result);
								  iList.add(inode); 
							  }
							  
							  if(!(lhs.resultType.equals("INT")||lhs.resultType.equals("FLOAT")))
							  {
								  if(rhs.resultType.equals("INT"))
									  inode = new IRNode("STOREI",lhs.resultType,null,lhs.result);
								  else
									  inode = new IRNode("STOREF",lhs.resultType,null,lhs.result);
								  newType = rhs.resultType;
								  iList.add(inode); 
							  }
							  
							  if(lhs.resultType.equals("INT"))
								  inode = new IRNode("DIVI",lhs.result,rhs.result,"$T"+tempNum);
							  else
								  inode = new IRNode("DIVF",lhs.result,rhs.result,"$T"+tempNum);
							  tempNum++;

							  iList.add(inode);
							  codeObject = new IRObj(iList,inode.destination, rootType); 
						  }
						break;
			case "ASSIGN": 	
							if(lhs != null && rhs != null)
							{	
								  ArrayList<IRNode> iList;
								  iList = rhs.IRNodeList;
								  iList.addAll(lhs.IRNodeList);
								  if(!(rhs.resultType.equals("INT")||rhs.resultType.equals("FLOAT")))
								  {
									  if(lhs.resultType.equals("INT"))
										  inode = new IRNode("STOREI",rhs.resultType,null,rhs.result);
									  else
										  inode = new IRNode("STOREF",rhs.resultType,null,rhs.result);
									  iList.add(inode); 
								  }
								  
								  if(lhs.resultType.equals("INT"))
									  inode = new IRNode("STOREI",rhs.result,null,lhs.result);
								  else
									  inode = new IRNode("STOREF",rhs.result,null,lhs.result);
								  iList.add(inode);
								  codeObject = new IRObj(iList,inode.destination, lhs.resultType); 
							}
							break;
			case "READ":	
							if(lhs != null)
							{
								  if(lhs.resultType.equals("INT"))
									  inode = new IRNode("READI",lhs.result,null,null);
								  else
									  inode = new IRNode("READF",lhs.result,null,null);
								  ArrayList<IRNode> iList;
								  iList = lhs.IRNodeList;
								  iList.add(inode);
								  codeObject = new IRObj(iList, inode.destination, lhs.resultType); 
							}
							break;
			case "WRITE": 	
							if(lhs != null)
							{
								  if(lhs.resultType.equals("INT"))
									  inode = new IRNode("WRITEI",lhs.result,null,null);
								  else if(lhs.resultType.equals("FLOAT"))
									  inode = new IRNode("WRITEF",lhs.result,null,null);
								  else
									  inode = new IRNode("WRITES",lhs.result,null,null);
								  ArrayList<IRNode> iList;
								  iList = lhs.IRNodeList;
								  iList.add(inode);
								  codeObject = new IRObj(iList, inode.destination, lhs.resultType); 
							}
							break;
		}
		
		return codeObject;
	}

	//---------------------TINY-----------------------------------------------
	
	public String convertTempToReg(String temp)
	{
	    if (temp.contains("$T"))
	    {
		    int x = Integer.parseInt(temp.replaceAll("\\D", "")); //extracts the number of the temporary
		    Integer tx = x - 1;
		    return ("r" + tx.toString());
	    }
	    else 
	    {
	    	return temp;
	    }
	}

	public void printTiny(ArrayList<IRNode> tACList)
		{ 
	    			
			for(Node n : st.nodes)
			{
				if(n.getType().equals("STRING"))
					System.out.println("str " + n.getName() + " " + n.getValue());
				else
					System.out.println("var "+ n.getName());
			}
			
		
	    	for(int i = 0; i < tACList.size(); i++)
	    		{ 
	    			String[] temp = tACList.get(i).toString().split(" ");
	    			switch(temp[0])
	    			{
	    			case("ADDI"):
	    			case("ADDF"):
	    							System.out.println("move " + convertTempToReg(temp[1]) + " " + convertTempToReg(temp[3]));
		    						if(temp[0].contains("F"))
		    						{
		    							System.out.println("addr " + convertTempToReg(temp[2]) + " " + convertTempToReg(temp[3]));
		    						}
		    						else
		    						{
		    							System.out.println("addi " + convertTempToReg(temp[2]) + " " + convertTempToReg(temp[3]));
		    						}
		    						break;
	    			case("SUBI"):
	    			case("SUBF"):
	    							System.out.println("move " + convertTempToReg(temp[1]) + " " + convertTempToReg(temp[3]));
	    							if(temp[0].contains("F"))
	    								{
	    								System.out.println("subr " + convertTempToReg(temp[2]) + " " + convertTempToReg(temp[3]));
	    								}
	    							else
	    								{
	    								System.out.println("subi " + convertTempToReg(temp[2]) + " " + convertTempToReg(temp[3]));
	    								}
	    							break;
	    			case("MULI"):
	    			case("MULF"):
	    							System.out.println("move " + convertTempToReg(temp[1]) + " " + convertTempToReg(temp[3]));
	    							if(temp[0].contains("F"))
	    								{
	    								System.out.println("mulr " + convertTempToReg(temp[2]) + " " + convertTempToReg(temp[3]));
	    								}
	    							else
	    								{
	    								System.out.println("muli " + convertTempToReg(temp[2]) + " " + convertTempToReg(temp[3]));
	    								}
	    							break;
	    			case("DIVI"):
	    			case("DIVF"):
	    							System.out.println("move " + convertTempToReg(temp[1]) + " " + convertTempToReg(temp[3]));
	    							if(temp[0].contains("F"))
	    								{
	    								System.out.println("divr " + convertTempToReg(temp[2]) + " " + convertTempToReg(temp[3]));
	    								}
	    							else
	    								{
	    								System.out.println("divi " + convertTempToReg(temp[2]) + " " + convertTempToReg(temp[3]));
	    								}
	    							break;
	    		  case("STOREI"):
	    		  case("STOREF"):
	    			  				if(!tACList.get(i).op1.contains("$T") && !tACList.get(i).destination.contains("$T"))
	    			  				{
	    			  					String newTempReg = "$T"+tempNum;
	    			  					tempNum++;
	    			  					System.out.println("move " + convertTempToReg(temp[1]) + " " + convertTempToReg(newTempReg));
	    			  					System.out.println("move " + convertTempToReg(newTempReg) + " " + convertTempToReg(temp[2]));
	    			  				}
	    			  				else
	    			  				{
	    			  					System.out.println("move " + convertTempToReg(temp[1]) + " " + convertTempToReg(temp[2]));
	    			  				}
	    			  				break;
	    		   case("READI"):
	    		   case("READF"):
	    			   				if (temp[0].contains("F"))
	    			   				{
	    			   					System.out.println("sys readr " + temp[1]);
	    			   				}
	    			   				else 
	    			   				{
	    			   					System.out.println("sys readi " + temp[1]);
	    			   				}
	    			   				break;
				  case("WRITEI"):
				  case("WRITEF"):
				  case("WRITES"):
						  			if (temp[0].contains("F"))
						  				{
						  				System.out.println("sys writer " + temp[1]);
						  				}
						  			else if (temp[0].contains("S"))
						  				{
						  				System.out.println("sys writes " + temp[1]);
						  				}
						  			else 
						  				{
						  				System.out.println("sys writei " + temp[1]);
						  				}
						  			break;
					case("LABEL"):
					    			System.out.println("label " + temp[1]); //temp[1] is label
					    			break;
					case("JUMP"):
					    			System.out.println("jmp " + temp[1]); 
					    			break;
					case("GT"):
								    if (tACList.get(i).typeOp.equals("INT")) //provided that the symbol table is global, or will need to have to pass 
									{
								    	if(!tACList.get(i).op1.contains("$T") && !tACList.get(i).destination.contains("$T"))
		    			  				{
		    			  					String newTempReg = "$T"+tempNum;
		    			  					tempNum++;
		    			  					System.out.println("move " + convertTempToReg(temp[2]) + " " + convertTempToReg(newTempReg));
										    System.out.println("cmpi " + convertTempToReg(temp[1]) + " " + convertTempToReg(newTempReg));
		    			  				}
		    			  				else
		    			  					System.out.println("cmpi " + convertTempToReg(temp[1]) + " " + convertTempToReg(temp[2]));
									}
								    else
									{
								    	if(!tACList.get(i).op1.contains("$T") && !tACList.get(i).destination.contains("$T"))
		    			  				{
		    			  					String newTempReg = "$T"+tempNum;
		    			  					tempNum++;
		    			  					System.out.println("move " + convertTempToReg(temp[2]) + " " + convertTempToReg(newTempReg));
										    System.out.println("cmpr " + convertTempToReg(temp[1]) + " " + convertTempToReg(newTempReg));
		    			  				}
		    			  				else
		    			  					System.out.println("cmpr " + convertTempToReg(temp[1]) + " " + convertTempToReg(temp[2]));
									}
								    System.out.println("jgt " + temp[3]);
								    break;
					case("GE"):
								    if (tACList.get(i).typeOp.equals("INT")) //provided that the symbol table is global, or will need to have to pass 
									{
								    	if(!tACList.get(i).op1.contains("$T") && !tACList.get(i).destination.contains("$T"))
		    			  				{
		    			  					String newTempReg = "$T"+tempNum;
		    			  					tempNum++;
		    			  					System.out.println("move " + convertTempToReg(temp[2]) + " " + convertTempToReg(newTempReg));
										    System.out.println("cmpi " + convertTempToReg(temp[1]) + " " + convertTempToReg(newTempReg));
		    			  				}
		    			  				else
		    			  					System.out.println("cmpi " + convertTempToReg(temp[1]) + " " + convertTempToReg(temp[2]));
									}
								    else
									{
								    	if(!tACList.get(i).op1.contains("$T") && !tACList.get(i).destination.contains("$T"))
		    			  				{
		    			  					String newTempReg = "$T"+tempNum;
		    			  					tempNum++;
		    			  					System.out.println("move " + convertTempToReg(temp[2]) + " " + convertTempToReg(newTempReg));
										    System.out.println("cmpr " + convertTempToReg(temp[1]) + " " + convertTempToReg(newTempReg));
		    			  				}
		    			  				else
		    			  					System.out.println("cmpr " + convertTempToReg(temp[1]) + " " + convertTempToReg(temp[2]));
									}
								    System.out.println("jge " + temp[3]); 
								    break;
					case("LT"):
								    if (tACList.get(i).typeOp.equals("INT")) //provided that the symbol table is global, or will need to have to pass 
									{
								    	if(!tACList.get(i).op1.contains("$T") && !tACList.get(i).destination.contains("$T"))
		    			  				{
		    			  					String newTempReg = "$T"+tempNum;
		    			  					tempNum++;
		    			  					System.out.println("move " + convertTempToReg(temp[2]) + " " + convertTempToReg(newTempReg));
										    System.out.println("cmpi " + convertTempToReg(temp[1]) + " " + convertTempToReg(newTempReg));
		    			  				}
		    			  				else
		    			  					System.out.println("cmpi " + convertTempToReg(temp[1]) + " " + convertTempToReg(temp[2]));
									}
								    else
									{
								    	if(!tACList.get(i).op1.contains("$T") && !tACList.get(i).destination.contains("$T"))
		    			  				{
		    			  					String newTempReg = "$T"+tempNum;
		    			  					tempNum++;
		    			  					System.out.println("move " + convertTempToReg(temp[2]) + " " + convertTempToReg(newTempReg));
										    System.out.println("cmpr " + convertTempToReg(temp[1]) + " " + convertTempToReg(newTempReg));
		    			  				}
		    			  				else
		    			  					System.out.println("cmpr " + convertTempToReg(temp[1]) + " " + convertTempToReg(temp[2]));
									}
								    System.out.println("jlt " + temp[3]); 
								    break;
					case("LE"):
								    if (tACList.get(i).typeOp.equals("INT")) //provided that the symbol table is global, or will need to have to pass 
									{
								    	if(!tACList.get(i).op1.contains("$T") && !tACList.get(i).destination.contains("$T"))
		    			  				{
		    			  					String newTempReg = "$T"+tempNum;
		    			  					tempNum++;
		    			  					System.out.println("move " + convertTempToReg(temp[2]) + " " + convertTempToReg(newTempReg));
										    System.out.println("cmpi " + convertTempToReg(temp[1]) + " " + convertTempToReg(newTempReg));
		    			  				}
								    	else
								    		System.out.println("cmpi " + convertTempToReg(temp[1]) + " " + convertTempToReg(temp[2]));
									}
								    else
									{
								    	if(!tACList.get(i).op1.contains("$T") && !tACList.get(i).destination.contains("$T"))
		    			  				{
		    			  					String newTempReg = "$T"+tempNum;
		    			  					tempNum++;
		    			  					System.out.println("move " + convertTempToReg(temp[2]) + " " + convertTempToReg(newTempReg));
										    System.out.println("cmpr " + convertTempToReg(temp[1]) + " " + convertTempToReg(newTempReg));
		    			  				}
		    			  				else
		    			  					System.out.println("cmpr " + convertTempToReg(temp[1]) + " " + convertTempToReg(temp[2]));
									}
								    System.out.println("jle " + temp[3]); 
								    break;
					case("NE"):
								    if (tACList.get(i).typeOp.equals("INT")) //provided that the symbol table is global, or will need to have to pass 
									{
								    	if(!tACList.get(i).op1.contains("$T") && !tACList.get(i).destination.contains("$T"))
		    			  				{
		    			  					String newTempReg = "$T"+tempNum;
		    			  					tempNum++;
		    			  					System.out.println("move " + convertTempToReg(temp[2]) + " " + convertTempToReg(newTempReg));
										    System.out.println("cmpi " + convertTempToReg(temp[1]) + " " + convertTempToReg(newTempReg));
		    			  				}
		    			  				else
		    			  					System.out.println("cmpi " + convertTempToReg(temp[1]) + " " + convertTempToReg(temp[2]));
									}
								    else
									{
								    	if(!tACList.get(i).op1.contains("$T") && !tACList.get(i).destination.contains("$T"))
		    			  				{
		    			  					String newTempReg = "$T"+tempNum;
		    			  					tempNum++;
		    			  					System.out.println("move " + convertTempToReg(temp[2]) + " " + convertTempToReg(newTempReg));
										    System.out.println("cmpr " + convertTempToReg(temp[1]) + " " + convertTempToReg(newTempReg));
		    			  				}
		    			  				else
		    			  					System.out.println("cmpr " + convertTempToReg(temp[1]) + " " + convertTempToReg(temp[2]));
									}
								    System.out.println("jne " + temp[3]); 
								    break;
					case("EQ"):
								    if (tACList.get(i).typeOp.equals("INT")) //provided that the symbol table is global, or will need to have to pass 
									{
								    	if(!tACList.get(i).op1.contains("$T") && !tACList.get(i).destination.contains("$T"))
		    			  				{
		    			  					String newTempReg = "$T"+tempNum;
		    			  					tempNum++;
		    			  					System.out.println("move " + convertTempToReg(temp[2]) + " " + convertTempToReg(newTempReg));
										    System.out.println("cmpi " + convertTempToReg(temp[1]) + " " + convertTempToReg(newTempReg));
		    			  				}
		    			  				else
		    			  					System.out.println("cmpi " + convertTempToReg(temp[1]) + " " + convertTempToReg(temp[2]));
									}
								    else
									{
								    	if(!tACList.get(i).op1.contains("$T") && !tACList.get(i).destination.contains("$T"))
		    			  				{
		    			  					String newTempReg = "$T"+tempNum;
		    			  					tempNum++;
		    			  					System.out.println("move " + convertTempToReg(temp[2]) + " " + convertTempToReg(newTempReg));
										    System.out.println("cmpr " + convertTempToReg(temp[1]) + " " + convertTempToReg(newTempReg));
		    			  				}
		    			  				else
		    			  					System.out.println("cmpr " + convertTempToReg(temp[1]) + " " + convertTempToReg(temp[2]));
									}
								    System.out.println("jeq " + temp[3]); 
								    break;
	    			}
	    	}		
	    System.out.println("sys halt");
	}
	
}
