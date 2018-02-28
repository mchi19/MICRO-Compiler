import java.util.*;

public String get3acOp(String opCode, String type){
    switch (opCode){
    case "+":{
	if type.equals("INT"){
		return "ADDI";
	    }
	else{
	    return "ADDF";
	}
	break;
    }
    case "-":{
	if type.equals("INT"){
		return "SUBI";
	    }
	else {
	    return "SUBF";
	}
	break;
    }
    case "*":{
	if type.equals("INT"){
		return "MULI";
	    }
	else {
	    return "MULF";
	}
	break;
    }
    case "/":{
	if type.equals("INT"){
		return "DIVI";
	    }
	else{
	    return "DIVF";
	}
	break;
    }
    case "store":{
	if type.equals("INT"){
		return "STOREI";
	    }
	else{
	    return "STOREF";
	}
    }
    case "writre":{
	if type.equals("INT"){
		return "WRITEI";
	    }
	else{
	    return "WRITEF";
	}
    }
    case "read":{
	if type.equals("INT"){
		return "READI";
	    }
	else{
	    return "READF";
	}
    }
    }

public List postwalkAST(ASTNode Anode){ //must pass root of AST node, basically last expr of each line
    if (Anode.ctx == null){ //if Anode is empty of anything
	return (Anode(null, null, null, "Null", "Null"));
    }
    postwalkAST(Anode.left);
    postwalkAST(Anode.right);
    new List<String> = temp;
    temp.append(ASTNode.nodeType.text, ASTNode.nodeValue.text);
    return temp;  
}

////////////////////////////
//print3AC does not print for function blocks yet so doesn't print ;LABEL main, ;LINK, and ;RET yet
////////////////////////////

//assume that the list is in this form
//ex: d := a + b * c
//[INT, b, INT, c, mulExpr, *, INT, a, addExpr, +, INT, d, storeExpr, store] 
//                         ^$T1                                              
//                                              ^$T2
public String print3AC(List<AST> astList){ //astList contains a list of AST Nodes
    int temp = 1;
    String opCode, op1, op2, dest;
    List<String> 3ACList; //list of 3AC commands that will be referenced for generating and printing tiny code
    System.out.println(";IR code");
    for (int i = 0; i < astList.size(); i++){ 
	new List<String> x = postwalkAST(astList[i]);
	for (int j = 0; j < (x.size() - 1); j++){
	    if (x[j].contains("Expr")){
		    opCode = get3acOp(x[j+1], x[j-2]);
		    if (opCode.contains("STORE")){
			op1 = x[j-3];
			op2 = "null";
			dest = x[j-1];
		    }
		    else if ((opCode.contains("WRITE")) || (opCode.contains("READ"))){
			op1 = "null";
			op2 = "null";
			dest = x[j-1];
			}
		    else {
			op1 = x[j-3];
			op2 = x[j-1];
			dest = "$T" + (String)temp;
			temp++;
		    }
		    new IRNode Inode(opCode, op1, op2, dest);
		    3ACList.append(Inode.printIRNode());
		    System.out.println(";" + Inode.printIRNode());
		    x[j+1] = dest;
	    }
	}
    }
    System.out.println(";tiny code");
    return 3ACList
}

//Converts temporary values to numbered regiters
public String convertTempToReg(String temp){
    if (temp.contains("$T")){
	    int x = Integer.parseInt(temp.replaceAll("\\D", "")); //extracts the number of the temporary
	    int tx = x - 1;
	    return ("r" + (String) tx);
    }
    else {
	return temp;
    }
}

/////////////////////////////////////////////////////////////////////////////////
//Need to add tinycode for variable declarations printed before and string declarations
/////////////////////////////////////////////////////////////////////////////////

public void printTiny(List<String> 3ACList){ //doesn't print tinycode for variable and string declarations
    //List<String> TinyList; //list for tiny code commands
    for(int i = 0; i < 3ACList.size(); i++){ //3ACLen is the length of array for printing
	String[] temp = 3ACList.split(" ");
	switch(temp[0]){
	case("ADDI"):
	case("ADDF"):{
	    if ((temp[1].contains("$T")) && (temp[2].contains("$T"))){
		System.out.println("move " + convertTempToReg(temp[1]) + " " + convertTempToReg(temp[3]));
		if(temp[0].contains("F")){
		    System.out.println("addf " + convertTempToReg(temp[2]) + " " + convertTempToReg(temp[3]));
		}
		else{
		    System.out.println("addi " + convertTempToReg(temp[2]) + " " + convertTempToReg(temp[3]));
		}
	    }
	    else if (temp[1].contains("$T")){
		System.out.println("move " + convertTempToReg(temp[1]) + " " + convertTempToReg(temp[3]));
		if(temp[0].contains("F")){
		    System.out.println("addf " + temp[2] + " " + convertTempToReg(temp[3]));
		}
		else{
		    System.out.println("addi " + temp[2] + " " + convertTempToReg(temp[3]));
		}
	    }
	    else if (temp[2].contains("$T")){
		System.out.println("move " + temp[1] + " " + convertTempToReg(temp[3]));
		if(temp[0].contains("F")){
		    System.out.println("addf " + convertTempToReg(temp[2]) + " " + convertTempToReg(temp[3]));
		}
		else {
		    System.out.println("addi " + temp[2] + " " + convertTempToReg(temp[3]));
		}
	    }
	    else{
		System.out.println("move " + temp[1] + " " + convertTempToReg(temp[3]));
		if(temp[0].contains("F")){
		    System.out.println("addf " + temp[2] + " " + convertTempToReg(temp[3]));
		}
		else {
		    System.out.println("addi " + temp[2] + " " + convertTempToReg(temp[3]));
		}
	    }
	    break;
	}
	case("SUBI"):
	case("SUBF"):{
	    if ((temp[1].contains("$T")) && (temp[2].contains("$T"))){
		System.out.println("move " + convertTempToReg(temp[1]) + " " + convertTempToReg(temp[3]));
		if(temp[0].contains("F")){
		    System.out.println("subf " + convertTempToReg(temp[2]) + " " + convertTempToReg(temp[3]));
		}
		else{
		    System.out.println("subi " + convertTempToReg(temp[2]) + " " + convertTempToReg(temp[3]));
		}
	    }
	    else if (temp[1].contains("$T")){
		System.out.println("move " + convertTempToReg(temp[1]) + " " + convertTempToReg(temp[3]));
		if(temp[0].contains("F")){
		    System.out.println("subf " + temp[2] + " " + convertTempToReg(temp[3]));
		}
		else{
		    System.out.println("subi " + temp[2] + " " + convertTempToReg(temp[3]));
		}
	    }
	    else if (temp[2].contains("$T")){
		System.out.println("move " + temp[1] + " " + convertTempToReg(temp[3]));
		if(temp[0].contains("F")){
		    System.out.println("subf " + convertTempToReg(temp[2]) + " " + convertTempToReg(temp[3]));
		}
		else {
		    System.out.println("subi " + temp[2] + " " + convertTempToReg(temp[3]));
		}
	    }
	    else{
		System.out.println("move " + temp[1] + " " + convertTempToReg(temp[3]));
		if(temp[0].contains("F")){
		    System.out.println("subf " + temp[2] + " " + convertTempToReg(temp[3]));
		}
		else {
		    System.out.println("subi " + temp[2] + " " + convertTempToReg(temp[3]));
		}
	    }
	    break;
	}
	case("MULI"):
	case("MULF"):{
	    if ((temp[1].contains("$T")) && (temp[2].contains("$T"))){
		System.out.println("move " + convertTempToReg(temp[1]) + " " + convertTempToReg(temp[3]));
		if(temp[0].contains("F")){
		    System.out.println("mulf " + convertTempToReg(temp[2]) + " " + convertTempToReg(temp[3]));
		}
		else{
		    System.out.println("muli " + convertTempToReg(temp[2]) + " " + convertTempToReg(temp[3]));
		}
	    }
	    else if (temp[1].contains("$T")){
		System.out.println("move " + convertTempToReg(temp[1]) + " " + convertTempToReg(temp[3]));
		if(temp[0].contains("F")){
		    System.out.println("mulf " + temp[2] + " " + convertTempToReg(temp[3]));
		}
		else{
		    System.out.println("muli " + temp[2] + " " + convertTempToReg(temp[3]));
		}
	    }
	    else if (temp[2].contains("$T")){
		System.out.println("move " + temp[1] + " " + convertTempToReg(temp[3]));
		if(temp[0].contains("F")){
		    System.out.println("mulf " + convertTempToReg(temp[2]) + " " + convertTempToReg(temp[3]));
		}
		else {
		    System.out.println("muli " + temp[2] + " " + convertTempToReg(temp[3]));
		}
	    }
	    else{
		System.out.println("move " + temp[1] + " " + convertTempToReg(temp[3]));
		if(temp[0].contains("F")){
		    System.out.println("mulf " + temp[2] + " " + convertTempToReg(temp[3]));
		}
		else {
		    System.out.println("muli " + temp[2] + " " + convertTempToReg(temp[3]));
		}
	    }
	    break;
	}
	case("DIVI"):
	case("DIVF"):{
	    if ((temp[1].contains("$T")) && (temp[2].contains("$T"))){
		System.out.println("move " + convertTempToReg(temp[1]) + " " + convertTempToReg(temp[3]));
		if(temp[0].contains("F")){
		    System.out.println("divf " + convertTempToReg(temp[2]) + " " + convertTempToReg(temp[3]));
		}
		else{
		    System.out.println("divi " + convertTempToReg(temp[2]) + " " + convertTempToReg(temp[3]));
		}
	    }
	    else if (temp[1].contains("$T")){
		System.out.println("move " + convertTempToReg(temp[1]) + " " + convertTempToReg(temp[3]));
		if(temp[0].contains("F")){
		    System.out.println("divf " + temp[2] + " " + convertTempToReg(temp[3]));
		}
		else{
		    System.out.println("divi " + temp[2] + " " + convertTempToReg(temp[3]));
		}
	    }
	    else if (temp[2].contains("$T")){
		System.out.println("move " + temp[1] + " " + convertTempToReg(temp[3]));
		if(temp[0].contains("F")){
		    System.out.println("divf " + convertTempToReg(temp[2]) + " " + convertTempToReg(temp[3]));
		}
		else {
		    System.out.println("divi " + temp[2] + " " + convertTempToReg(temp[3]));
		}
	    }
	    else{
		System.out.println("move " + temp[1] + " " + convertTempToReg(temp[3]));
		if(temp[0].contains("F")){
		    System.out.println("divf " + temp[2] + " " + convertTempToReg(temp[3]));
		}
		else {
		    System.out.println("divi " + temp[2] + " " + convertTempToReg(temp[3]));
		}
	    }
	    break;
	}
	case("STOREI"):
	case("STOREF"):{
	    if(temp[1].contains("$T")){
		System.out.println("move " + convertTempToReg(temp[1]) + " " + temp[2]);
	    }
	    else{
		System.out.println("move " + temp[1] + " " + convertTempToReg(temp[2]));
	    }
	    break;
	}
	case("READI"):
	case("READF"):{
	    if (temp[0].contains("F")){
		System.out.println("sys readf " + temp[1]);
	    }
	    else {
		System.out.println("sys readi " + temp[1]);
	    }
	    break;
	}
	case("WRITEI"):
	case("WRITEF"):
	case("WRITES"):{
	    if (temp[0].contains("F")){
		System.out.println("sys writef " + temp[1]);
	    }
	    else if (temp[0].contains("S")){
		System.out.println("sys writes " + temp[1]);
	    }
	    else {
		System.out.println("sys writei " + temp[1]);
	    }
	    break;
	}
    }
    System.out.println("sys halt");
}
