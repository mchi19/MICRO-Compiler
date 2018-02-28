//this should just be added into the astListener.java file

private void step7lol()
{
    createCRF();
    createGen_Kill();
    createIN_OUT();
    initReg();
    allocateReg();
}

private String [] reg4List = new String[4];
private boolean[] dirtyReg = new boolean[4];

private void initReg()
{
    for (int i = 0; i < 4; i++)
    {
	reg4List[i] = null;
	dirtyReg[i] = false;
    }
}

private void freeReg(int i)
{
    if(dirtyReg[i])
    {
	//call tiny register
    }
    reg4List[i] = null;
    dirtyReg[i] = false;
}

private void createCFG(){
    for (int i = 0; i < ACList.size(); i++){
	IRNode irnode = AClist.get(i);
	if (irnode.getOpCode() == null) {
	    continue;
	}
	if (irnode.getOpCode().equals("JUMP")){
	    for(IRNode destNode : ACList) {
		if(destNode.getOpCode() == null){
		    continue;
		}
		if(destNode.getOpCode().equals("LABEL") && irnode.destination().equals(destNode.getResult())){
		    irnode.addSuccessor(destNode); //add this to the irnode init
		}
	    }
	}
	else if (irnode.getOpCode().equals("LE") || irnode.getOpCode().equals("GE") || irnode.getOpCode().equals("NE") || irnode.getOpCode().equals("GT") || irnode.getOpCode().equals("LT") || irnode.getOpCode().equals("EQ")) {
	    for (IRNode destNode : ACList) {
		if(destNode.getOpCode() == null){
		    continue;
		}
		if(destNode.getOpCode().equals("LABEL") && irnode.destination().equals(destNode.getResult())){
		    irnode.addSuccessor(destNode); //add this to the irnode init
		}
	    }
	    irnode.addSuccessor(ACList.get(i+1));
	    
	}
	else {
	    if(i+1 != ACList.size()){
		irnode.addSucessor(ACList.get(i+1));
	    }
	}
    }
    for (int i = 0; i < ACLIst.size(); i++){
	IRNode irnode = ACList.get(i);
	if(irnode.getOpCode() == null){
	    continue;
	}
	ArrayList<IRNode> successor = irnode.getSuccessor();
	for(IRNode destNode : successor){
	    ArrayList<IRNode> predecessor = destNode.getPredecessor(); //add in this function call for irnode class
	    if(!predecessor.contains(irnode)){
		destNode.addPredecessor(irnode); //add this to the irnode init
	    }
	}	
    }
}

private void createGen_Kill(){
    for(IRNode irnode: ACList) {
	String opCode = irnode.getOpCode();
	String op1 = irnode.getOp1();
	String op2 = irnode.getOp2();
	String dest = irnode.getDestination();
	if (opCode == null) {
	    continue;
	}
	if(opCode.equals("PUSH") || opCode.contains("WRITE")){
	    if(dest != null) {
		irnode.addGenSet(dest); //add this into irnode constructor
	    }
	}
	else if (opCode.equals("POP") || opCode.contains("READ")){
	    if(dest != null) {
		irnode.addKillSet(dest); //add this into irnode constructor
	    }
	}
	else if (opCode.equals("GT") || opCode.equals("LT") || opCode.equals("GE") || opCode.equals("LE") || opCode.equals("NE") || opCode.equals("EQ")){
	    irnode.addGenSet(op1);
	    irnode.addGenSet(op2);
	}
	else if (opCode.equals("JSR")) {
	    for(String str: globalVar) { //add all the global variables to the gen set, but requires a list of strings for global variables
		irnode.addGenSet(str);
	    }
	}
	else if (opCode.equals("STOREI") || opCode.equals("STOREF")){
	    if(op1 != null && !(op1.matches("[0-9]+") || op1.matches("[0-9]*\\.[0-9]+"))){
		irnode.addGenSet(op1);
	    }
	    if(dest != null) {
		irnode.addKillSet(dest);
	    }
	}
	else {
	    if (opCode.equals("JUMP") || opCode.equals("LABEL") || opcode.equals("LINK")){
		/////???? not sure what this should do, potentiall add to gen set and then kill immediately after
		continue;
	    }
	    else{
		if(op1 != null) {
		    irnode.addGenSet(op1);
		}
		if(op2 != null) {
		    irnode.addGenSet(op2);
		}
		if(dest != null) {
		    irnode.addGenSet(dest);
		}
	    }
	}
    }
}
//add leader list to top
public ArrayList<Integer> leaderList = new ArrayList<Integer>();

private void createLeader(){
    //initialize leaderList to be all 0s
    for(int i = 0; i < ACList.size(); i++){
	leaderList.add(0);
    }
    for(int i = 0; i < ACList.size(); i++){
	IRNode irnode = ACList.get(i);
	Set<String> inSet = new HashSet<String>();
	Set<String> outSet = new HashSet<String>();
	irnode.setInSet(inSet); //add this to the IRNode constructor
	irnode.setOutSet(outSet); //add this to the IRNode constructor
	ArrayList<IRNode> predList = irnode.getPredecessor();
	if(predList.size() == 0) {
	    irnode.setLeader(); //add this to the IRNode constructor
	    leaderList.set(i,1);
	}
	else{
	    for(IRNode predecessor : predList){
		switch(predecessor.getOpCode()) {
		case "JUMP":
		case "EQ":
		case "NE":
		case "GT":
		case "LT":
		case "GE":
		case "NE": {
		    irnode.setLeader();
		    leaderList.set(i,1);
		    break;
		}
		}
	    }
	}
    }
}

private void createIN_OUT(){
    creatLeader();
    ArrayList<Integer> indexList = new ArrayList<Integer>();
    for (int i = 0; i < ACList.size(); i++){
	indexList.add(i);
    }
    while(indexList.isEmpty() != true) {
	int i = indexList.remove(indexList.size()-1);
	IRNode irnode = ACList.get(i);
	set<String> prevInSet = irnode.getInSet();
	set<String> prevOutSet = irnode.getOutSet();
	set<String> useSet = irnode.getGenSet();
	set<String> defSet = irnode.getKillSet();
	if(irnode.getOpCode() == null) {
	    continue;
	}
	if(irnode.getOpCode().equals("RET")){
	    Set<String> inSet = new HashSet<String>(useSet);
	    Set<String> outSet = new HashSet<String>(prevOutSet);
	    for (String def : defSet) {
		outSet.remove(def);
	    }
	    for (String out : outSet) {
		inSet.add(out);
	    }
	    irnode.setInSet(inSet);
	}
	else {
	    Set<String> outSet = new HashSet<String>(prevOutSet);
	    for (IRNode successor: irnode.getSuccessor()) {
		for(String in : successor.getInSet()){
		    outSet.add(in);
		}
	    }
	    irnode.setOutSet(outSet);
	    Set<String> inSet = new HashSet<String>(useSet);
	    Set<String> tempOut = new HashSet<String>(outSet);
	    for (String def: defSet) {
		tempOut.remove(def);
	    }
	    for (String out: tempOut) {
		inSet.add(out);
	    }
	    irnode.setInSet(inSet); //add this function to IRNode constructor
	    if(!inSet.containsAll(prevInSet) || !prevInSet.containsAll(inSet)){
		for (IRNode predecessor: irnode.getPredecessor()){
		    int idx = ACList.indexOf(predecessor);
		    if(!indexList.contains(idx)){
			indexList.add(idx);
		    }
		}
	    }
	}
    }
}

