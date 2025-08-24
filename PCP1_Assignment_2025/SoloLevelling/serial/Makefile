
JAVAC=javac
SRC = $(wildcard *.java)
CLASSES = $(SRC:.java=.class)

ARGS ?= 100 0.2 0

all: $(CLASSES)

%.class: %.java
	$(JAVAC) $<
run: all
	java DungeonHunter $(ARGS) 

clean:
	rm -f $(SRC)/*.class
