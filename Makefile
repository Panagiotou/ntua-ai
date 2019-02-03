all: lab1 lab2

lab1:
	cd lab1/src && $(MAKE) all

lab2:
	cd lab2/src && $(MAKE) all
