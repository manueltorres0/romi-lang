
JAVAC      := javac
JAVA       := java
SRC_DIR    := src
BIN_DIR    := bin
MAIN_CLASS := Main


SOURCES := $(shell find $(SRC_DIR) -name "*.java")


JFLAGS := -g -d $(BIN_DIR) -sourcepath $(SRC_DIR)

.PHONY: all run clean


all: $(BIN_DIR)/.compiled

$(BIN_DIR)/.compiled: $(SOURCES)
	@mkdir -p $(BIN_DIR)
	$(JAVAC) $(JFLAGS) $(SOURCES)
	@touch $@


run: all
	$(JAVA) -cp $(BIN_DIR) $(MAIN_CLASS) $(ARGS)


clean:
	rm -rf $(BIN_DIR) dist