/**
 * Define a grammar called MICRO
 */ 
grammar MICRO;

//Import statements
@header{import java.util.*;}
//Define global stack, tree, counter
@members{public SymbolTable tree = new SymbolTable("GLOBAL"); public ArrayList<SymbolTable> ststack = new ArrayList<SymbolTable>(); public Integer blocknum = 1; public Boolean noError = true;}

//Program
program : KYPROGRAM id KYBEGIN {ststack.add(0,tree);} pgm_body KYEND {if(noError) tree.printTree();};
id : IDENTIFIER;
pgm_body : decl func_declarations;
decl : string_decl decl | var_decl decl | empty;

//Global String Declaration
string_decl returns [Node s]: KYSTRING id OP1 str OP12 {$s = new Node((String)$id.text,(String)$str.text, "STRING");
														ArrayList<Node> data = new ArrayList<Node>();
														data.add($s);
														//System.out.println($s.getName() + " " + $s.getType() + " "+ $s.getValue());
														if(!ststack.get(0).addData(data)) noError = false;
														};
str : STRINGLITERAL;

//Variable Declaration
var_decl returns [ArrayList<Node> nodeList]: var_type id_list OP12 
											{
												$nodeList = new ArrayList<Node>(); 
												for(String name: $id_list.idList)
												{
													$nodeList.add(new Node(name , null, $var_type.t));
													//System.out.println(name + " " + $var_type.t);
												}
												if(!ststack.get(0).addData($nodeList)) noError = false;
											};
var_type returns [String t] : KYFLOAT {$t = "FLOAT";}| KYINT {$t = "INT";};
any_type : var_type | KYVOID;
id_list returns [ArrayList<String> idList]: id id_tail {$idList = $id_tail.idTailList;
														$idList.add(0,$id.text);};
id_tail returns [ArrayList<String> idTailList] : OP13 id id_tail 
												{
													$idTailList = $id_tail.idTailList;
													$idTailList.add(0,$id.text);
												} 
												| empty {$idTailList = new ArrayList<String>();};

//Function Parameter List
param_decl_list : param_decl param_decl_tail | empty;
param_decl returns [Node n] : var_type id {$n = new Node((String)$id.text, null, $var_type.t);
											if(!ststack.get(0).addData($n)) noError = false;};
											//System.out.println($id.text + " " + $var_type.t);};
param_decl_tail : OP13  param_decl param_decl_tail | empty;

//Function Declarations
func_declarations : func_decl func_declarations | empty;
func_decl returns [SymbolTable st] : KYFUNCTION any_type id {$st = new SymbolTable($id.text); $st.setParent(ststack.get(0)); ststack.add(0,$st);}
									 OP10 param_decl_list OP11 KYBEGIN func_body KYEND {ststack.remove(0);};
func_body : decl stmt_list;

//Statement List
stmt_list : stmt stmt_list | empty;
stmt : base_stmt | if_stmt | for_stmt;
base_stmt : assign_stmt | read_stmt | write_stmt | return_stmt;

//Basic statements
assign_stmt : assign_expr OP12;
assign_expr : id OP1 expr;
read_stmt : KYREAD OP10 id_list OP11 OP12;
write_stmt : KYWRITE OP10 id_list OP11 OP12;
return_stmt : KYRETURN expr OP12;

//Expressions
expr : expr_prefix factor;
expr_prefix : expr_prefix factor addop | empty;
factor : factor_prefix postfix_expr;
factor_prefix : factor_prefix postfix_expr mulop | empty;
postfix_expr : primary | call_expr;
call_expr : id OP10 expr_list OP11;
expr_list : expr expr_list_tail | empty;
expr_list_tail : OP13 expr expr_list_tail | empty;
primary : OP10 expr OP11 | id | INTLITERAL | FLOATLITERAL;
addop : OP2 | OP3;
mulop : OP4 | OP5;

//Complex Statements and Condition
if_stmt returns [SymbolTable st] : KYIF {$st = new SymbolTable("BLOCK "+ blocknum.toString()); blocknum++; $st.setParent(ststack.get(0)); ststack.add(0,$st);} OP10 cond OP11 decl stmt_list else_part KYFI {ststack.remove(0);};
else_part returns [SymbolTable st] : KYELSE {$st = new SymbolTable("BLOCK "+ blocknum.toString()); blocknum++; $st.setParent(ststack.get(0)); ststack.add(0,$st);} decl stmt_list {ststack.remove(0);} | empty;
cond : expr compop expr;
compop : OP6 | OP7 | OP8 | OP9 | OP14 | OP15;

init_stmt : assign_expr | empty;
incr_stmt : assign_expr | empty;

for_stmt returns [SymbolTable st]: KYFOR {$st = new SymbolTable("BLOCK "+ blocknum.toString()); blocknum++; $st.setParent(ststack.get(0)); ststack.add(0,$st);} OP10 init_stmt OP12 cond OP12 incr_stmt OP11 decl stmt_list KYROF {ststack.remove(0);};

//Define empty
empty : ;

//Token Defintions
//Keywords
KYPROGRAM : 'PROGRAM';
KYBEGIN : 'BEGIN';
KYEND : 'END';
KYFUNCTION : 'FUNCTION';
KYREAD : 'READ';
KYWRITE : 'WRITE';
KYIF : 'IF';
KYELSE : 'ELSE';
KYFI : 'FI';
KYFOR : 'FOR';
KYROF : 'ROF';
KYRETURN : 'RETURN';
KYINT : 'INT';
KYVOID : 'VOID';
KYSTRING : 'STRING';
KYFLOAT : 'FLOAT';

//Types
IDENTIFIER : [a-zA-Z][a-zA-Z0-9]*;
INTLITERAL : [0-9]+;
FLOATLITERAL : [0-9]*'.'[0-9]+;
STRINGLITERAL : '"'.*?'"';
COMMENT : '--'~[\n]*'\n' -> skip;
WS : [ \t\r\n]+ -> skip ; // skip spaces, tabs, newlines

//Operators
OP1 : ':=';
OP2 : '+';
OP3 : '-';
OP4 : '*';
OP5 : '/';
OP6 : '=';
OP7 : '!=';
OP8 : '<';
OP9 : '>';
OP10 : '(';
OP11 : ')';
OP12 : ';';
OP13 : ',';
OP14 : '<=';
OP15 : '>=';
