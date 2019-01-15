FLAGS=-cp jipconsole.jar
JAVAC=javac $(FLAGS)
JAVA=java $(FLAGS)

all: generate run

generate:
	$(JAVAC) FactGenerator.java
	$(JAVA) FactGenerator

wrapper:
	$(JAVAC) Prolog.java
	$(JAVA) Prolog

run: world.pl next.pl lines.pl traffic.pl taxis.pl client.pl
	$(JAVAC) Graph.java
	$(JAVA) Graph

clean:
	rm -rf *.class
	rm -rf next.pl lines.pl traffic.pl taxis.pl client.pl
