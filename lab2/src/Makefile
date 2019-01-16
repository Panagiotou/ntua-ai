FLAGS=-cp jipconsole.jar
JAVAC=javac $(FLAGS)
JAVA=java $(FLAGS)
TOLERANCES=0.0 0.001 0.01 # Various tolerances for evaluation

all: generate run

generate:
	$(JAVAC) FactGenerator.java
	$(JAVA) FactGenerator

wrapper:
	$(JAVAC) Prolog.java
	$(JAVA) Prolog

run: world.pl next.pl lines.pl traffic.pl taxis.pl client.pl Graph.java
	$(JAVAC) Graph.java
	$(JAVA) Graph

evaluate: world.pl next.pl lines.pl traffic.pl taxis.pl client.pl Graph.java
	$(JAVAC) Graph.java
	$(foreach tol, $(TOLERANCES), $(JAVA) Graph 1 $(tol);)

clean:
	rm -rf *.class
	rm -rf next.pl lines.pl traffic.pl taxis.pl client.pl
