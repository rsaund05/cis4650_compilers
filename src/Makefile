JAVA=java
JAVAC=javac -nowarn
JFLEX=jflex
CLASSPATH=-classpath ../java_cup/java-cup-11b.jar:.
#CUP=$(JAVA) $(CLASSPATH) java_cup.Main <
CUP=cup

all: CM.class

CM.class: c1Absyn/*.java parser.java sym.java Lexer.java ShowTreeVisitor.java Scanner.java CM.java 

%.class: %.java
	$(JAVAC) $(CLASSPATH)  $^

Lexer.java: cminus.flex
	$(JFLEX) cminus.flex

parser.java: cminus.cup
	#$(CUP) -dump -expect 3 cminus.cup
	$(CUP) cminus.cup

clean:
	rm -f parser.java Lexer.java sym.java *.class c1Absyn/*.class *~
