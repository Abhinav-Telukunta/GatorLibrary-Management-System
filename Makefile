# Makefile for running GatorLibrary

# Compiler to use
JC = javac

# Name of the main class
MAIN_CLASS = gatorLibrary

# Compile the main class
$(MAIN_CLASS).class: $(MAIN_CLASS).java
	$(JC) $(MAIN_CLASS).java

# Clean up compiled class files
clean:
	rm -f *.class