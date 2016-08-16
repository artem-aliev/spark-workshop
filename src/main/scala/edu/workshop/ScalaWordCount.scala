package edu.workshop

import org.apache.spark.{SparkConf, SparkContext}

object ScalaWordCount {
  def main(args: Array[String]) {

     val conf = new SparkConf().setAppName("Simple Spark Application")
     val sc = new SparkContext(conf)
     val textFile = sc.textFile("README.md")
     val wordCounts = textFile.flatMap(line => line.split(" ")).map(word => (word, 1)).reduceByKey((a, b) => a + b)
     val keys = Seq(("Spark",1), ("Hadoop",2), ("Streaming",3))
     val keysRDD= sc.parallelize(keys).cache

     keysRDD.join(wordCounts).collect.foreach(println)
    sc.stop()

  }
}
