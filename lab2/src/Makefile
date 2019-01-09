FLAGS=-cp jipconsole.jar
JAVAC=javac $(FLAGS)
JAVA=java $(FLAGS)

generate:
	$(JAVAC) FactGenerator.java
	$(JAVA) FactGenerator

wrapper:
	$(JAVAC) Prolog.java
	$(JAVA) Prolog

run:
	$(JAVAC) Graph.java
	$(JAVA) Graph

prolog_example:
	$(JAVAC) PrologExample.java
	$(JAVA) PrologExample

clean:
	rm *.class

