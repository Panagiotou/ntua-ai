FLAGS=-cp jipconsole.jar
JAVAC=javac $(FLAGS)
JAVA=java $(FLAGS)
TOLERANCES=0.0 0.001 0.01 # Various tolerances for evaluation

all: generate run

# Generate prolog files
generate:
	$(JAVAC) FactGenerator.java
	$(JAVA) FactGenerator

# Test the wrapper
wrapper:
	$(JAVAC) Prolog.java
	$(JAVA) Prolog

# Run the assignment
run: world.pl next.pl lines.pl traffic.pl taxis.pl client.pl Graph.java
	$(JAVAC) Graph.java
	$(JAVA) Graph

# Evaluate for various tolerences defined in $(TOLERANCES)
evaluate: world.pl next.pl lines.pl traffic.pl taxis.pl client.pl Graph.java
	$(JAVAC) Graph.java
	$(foreach tol, $(TOLERANCES), $(JAVA) Graph 1 $(tol);)

# Clean unused and auto-generated files
clean:
	rm -rf *.class *.kml
	rm -rf next.pl lines.pl traffic.pl taxis.pl client.pl nodes.pl
	cd ../results && rm -rf *.kml
