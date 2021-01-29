SRC_DIR=./*.java
TARGET=TraverseSecond
HADOOP=$(HADOOP_HOME)/bin/hadoop
INPUT_DIR=final/input/
CACHE_DIR=final/cache/
TARGET_DIR=final/output/
INPUT_FILE=Wiki-Vote
INPUT_CACHE=

all:$(TARGET)

$(TARGET):$(SRC_DIR)
	$(HADOOP) com.sun.tools.javac.Main $(TARGET).java
	jar cf $(TARGET).jar $(TARGET)*.class
	$(HADOOP) fs -rm -r -f $(TARGET_DIR)

clean:
	rm -rf *.jar
	rm -rf *.class

test:
	$(HADOOP) fs -rm -r -f $(TARGET_DIR)
	$(HADOOP) jar $(TARGET).jar $(TARGET) $(INPUT_DIR)$(INPUT_FILE).txt $(TARGET_DIR)$(INPUT_FILE)

run:
	$(HADOOP) fs -rm -r -f $(TARGET_DIR)
	$(HADOOP) jar $(TARGET).jar $(TARGET) $(INPUT_DIR)$(INPUT_FILE).txt $(TARGET_DIR)$(INPUT_CACHE) $(CACHE_DIR)$(INPUT_CACHE).txt
	$(HADOOP) fs -get $(TARGET_DIR)$(INPUT_CACHE)/part-r-00000 seed/$(INPUT_CACHE)_out.txt
	cat seed/$(INPUT_CACHE).txt seed/$(INPUT_CACHE)_out.txt > graph/$(INPUT_CACHE).txt
	python3 SortGraph.py graph/$(INPUT_CACHE).txt
	python3 ExactAlgo.py graph/$(INPUT_CACHE).txt

get:
	$(HADOOP) fs -get $(TARGET_DIR)$(INPUT_FILE)/part-r-00000 ./$(INPUT_FILE)_out.txt

demo1:
	$(HADOOP) fs -rm -r -f $(TARGET_DIR)
	-$(HADOOP) jar SeedSelection.jar SeedSelection $(INPUT_DIR)test.txt $(TARGET_DIR)test
	-$(HADOOP) fs -get $(TARGET_DIR)test/part-r-00000 ./test_out.txt
	cat test_out.txt

demo2:
	cat test_out.txt test.txt > test_Selected.txt
	-$(HADOOP) fs -put ./test_Selected.txt $(INPUT_DIR)test_Selected.txt
	$(HADOOP) fs -rm -r -f $(TARGET_DIR)
	-$(HADOOP) jar TagSeed.jar TagSeed $(INPUT_DIR)test_Selected.txt $(TARGET_DIR)test
	-$(HADOOP) fs -get $(TARGET_DIR)test/part-r-00000 ./test_Seed.txt
	cat test_Seed.txt

demo3_1:
	-$(HADOOP) fs -put ./test_Seed.txt $(INPUT_DIR)test_Seed.txt
	$(HADOOP) fs -rm -r -f $(TARGET_DIR)
	-$(HADOOP) jar TraverseFirst.jar TraverseFirst $(INPUT_DIR)test_Seed.txt $(TARGET_DIR)test
	-$(HADOOP) fs -get $(TARGET_DIR)test/part-r-00000 ./test_Seed_out.txt
	cat test_Seed_out.txt
demo3_2:
	-$(HADOOP) fs -put ./test_Seed_out.txt $(CACHE_DIR)test_Seed_out.txt
	$(HADOOP) fs -rm -r -f $(TARGET_DIR)
	-$(HADOOP) jar $(TARGET).jar $(TARGET) $(INPUT_DIR)test.txt $(TARGET_DIR)test $(CACHE_DIR)test_Seed_out.txt
	-$(HADOOP) fs -get $(TARGET_DIR)test/part-r-00000 test_component.txt
	cat test_component.txt
demo4:
	cat test_Seed_out.txt test_component.txt > graph/test.txt
	python3 SortGraph.py graph/test.txt
	python3 ExactAlgo.py graph/test.txt
