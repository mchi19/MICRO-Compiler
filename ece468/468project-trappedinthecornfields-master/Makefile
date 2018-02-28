team:
	@echo "Team: trappedinthecornfields"
	@echo 
	@echo "Max Chi"
	@echo "mchi19"
	@echo
	@echo "Zach DeBernardo"
	@echo "deeeeeeebo"

compiler: 
	java org.antlr.v4.Tool MICRO.g4
	javac -bootclasspath /usr/lib/jvm/jre-1.7.0/lib/rt.jar -source 1.7 -target 1.7 SymbolTable.java	
	javac -bootclasspath /usr/lib/jvm/jre-1.7.0/lib/rt.jar -source 1.7 -target 1.7 Node.java 
	javac -bootclasspath /usr/lib/jvm/jre-1.7.0/lib/rt.jar -source 1.7 -target 1.7 ASTNode.java 
	javac -bootclasspath /usr/lib/jvm/jre-1.7.0/lib/rt.jar -source 1.7 -target 1.7 ASTOut.java 
	javac -bootclasspath /usr/lib/jvm/jre-1.7.0/lib/rt.jar -source 1.7 -target 1.7 ASTListener.java 
	javac -bootclasspath /usr/lib/jvm/jre-1.7.0/lib/rt.jar -source 1.7 -target 1.7 IRObj.java 
	javac -bootclasspath /usr/lib/jvm/jre-1.7.0/lib/rt.jar -source 1.7 -target 1.7 IRNode.java 
	chmod +x runme

clean:
	rm -f MICROLexer.*
	rm -f MICROBaseListener.java
	rm -f MICROParser.java
	rm -f MICROListener.java
	rm -f MICRO.tokens
	rm -f *.class
