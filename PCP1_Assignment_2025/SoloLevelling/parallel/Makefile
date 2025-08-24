JAVAC = javac
SRC = $(wildcard *.java)
CLASSES = $(SRC:.java=.class)

all: $(CLASSES)

%.class: %.java
	$(JAVAC) $<
ARGS ?= 100 0.2 0
run: all
	java DungeonHunterParallel $(ARGS)
clean:
	rm -f *.class
