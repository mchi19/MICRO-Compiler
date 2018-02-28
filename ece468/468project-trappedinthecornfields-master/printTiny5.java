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

public void printTiny(ArrayList<String> tACList) //will need access to symbol table 
{ 			//doesn't print tinycode for variable and string declarations
    //List<String> TinyList; //list for tiny code commands
    for(Node n : st.nodes)
	{
	    if(n.getType().equals("STRING")) //create a statement to deal with label# when encountered
		System.out.println("str " + n.getName() + " " + n.getValue());
	    else
		System.out.println("var "+ n.getName());
	}
    
    for(int i = 0; i < tACList.size(); i++)
	{ //3ACLen is the length of array for printing
	    String[] temp = tACList.get(i).split(" ");
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
		    
		    System.out.println("move " + convertTempToReg(temp[1]) + " " + convertTempToReg(temp[2]));
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
		    if (st.getNodeType(n.nodeValue).equals("INT")) //provided that the symbol table is global, or will need to have to pass 
			{
			    System.out.println("cmpi " + convertTempToReg(temp[1]) + " " + convertTempToReg(temp[2]));
			}
		    else
			{
			    System.out.println("cmpr " + convertTempToReg(temp[1]) + " " + convertTempToReg(temp[2]));
			}
		    System.out.println("jgt " + temp[3]);
		    break;
		case("GE"):
		    if (st.getNodeType(n.nodeValue).equals("INT")) //provided that the symbol table is global, or will need to have to pass 
			{
			    System.out.println("cmpi " + convertTempToReg(temp[1]) + " " + convertTempToReg(temp[2]));
			}
		    else
			{
			    System.out.println("cmpr " + convertTempToReg(temp[1]) + " " + convertTempToReg(temp[2]));
			}
		    System.out.println("jge " + temp[3]); 
		    break;
		case("LT"):
		    if (st.getNodeType(n.nodeValue).equals("INT")) //provided that the symbol table is global, or will need to have to pass 
			{
			    System.out.println("cmpi " + convertTempToReg(temp[1]) + " " + convertTempToReg(temp[2]));
			}
		    else
			{
			    System.out.println("cmpr " + convertTempToReg(temp[1]) + " " + convertTempToReg(temp[2]));
			}
		    System.out.println("jlt " + temp[3]); 
		    break;
		case("LE"):
		    if (st.getNodeType(n.nodeValue).equals("INT")) //provided that the symbol table is global, or will need to have to pass 
			{
			    System.out.println("cmpi " + convertTempToReg(temp[1]) + " " + convertTempToReg(temp[2]));
			}
		    else
			{
			    System.out.println("cmpr " + convertTempToReg(temp[1]) + " " + convertTempToReg(temp[2]));
			}
		    System.out.println("jle " + temp[3]); 
		    break;
		case("NE"):
		    if (st.getNodeType(n.nodeValue).equals("INT")) //provided that the symbol table is global, or will need to have to pass 
			{
			    System.out.println("cmpi " + convertTempToReg(temp[1]) + " " + convertTempToReg(temp[2]));
			}
		    else
			{
			    System.out.println("cmpr " + convertTempToReg(temp[1]) + " " + convertTempToReg(temp[2]));
			}
		    System.out.println("jne " + temp[3]); 
		    break;
		case("EQ"):
		    if (st.getNodeType(n.nodeValue).equals("INT")) //provided that the symbol table is global, or will need to have to pass 
			{
			    System.out.println("cmpi " + convertTempToReg(temp[1]) + " " + convertTempToReg(temp[2]));
			}
		    else
			{
			    System.out.println("cmpr " + convertTempToReg(temp[1]) + " " + convertTempToReg(temp[2]));
			}
		    System.out.println("jeq " + temp[3]); 
		    break;
		}
	}		
    System.out.println("sys halt");
}
