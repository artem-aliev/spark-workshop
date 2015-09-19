__author__ = 'artemaliev'

from pyspark import SparkContext

sc = SparkContext("local", "Simple App")
textFile = sc.textFile("README.md")
wordCounts = textFile.flatMap(lambda line: line.split(" "))\
    .map(lambda word: (word, 1))\
    .reduceByKey(lambda a,b: a + b)

for row in wordCounts.takeSample(True,3):
    print("(%s, %i)" % row)

