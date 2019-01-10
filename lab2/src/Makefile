FLAGS=-cp jipconsole.jar
JAVAC=javac $(FLAGS)
JAVA=java $(FLAGS)

all: 
	$(MAKE) generate
	$(MAKE) run

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

