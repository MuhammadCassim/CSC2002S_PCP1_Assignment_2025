JAVAC = javac
SRC = $(windcard *.java)
CLASSES = $(SRC:.java=.class)

all: $(CLASSES)

%.class: %.java
	$(JAVAC) $<
run: all
	java Main
clean:
	rm -f *.class
