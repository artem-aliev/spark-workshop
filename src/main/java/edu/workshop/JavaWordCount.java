package edu.workshop;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.*;
import scala.Tuple2;

import java.util.Arrays;
import java.util.List;

public class JavaWordCount
{
  public static void main(String[] args) {
    SparkConf conf = new SparkConf().setAppName("Simple Spark Application").setMaster("local");
    JavaSparkContext sc = new JavaSparkContext(conf);

    JavaRDD<String> textFile = sc.textFile("README.md", 1);
    JavaPairRDD<String, Integer> wordCounts = textFile.flatMap(line -> Arrays.asList(line.split(" ")))
            .mapToPair(word -> new Tuple2<>(word, 1)).reduceByKey((a, b) -> a + b);

    List<Tuple2<String,Integer>> keys = Arrays.asList(new Tuple2<>("Spark", 1),
            new Tuple2<>("Hadoop", 2), new Tuple2<>("Streaming", 3));
    JavaPairRDD<String, Integer>  keysRDD= sc.parallelizePairs(keys);

    List<Tuple2<String, Tuple2<Integer, Integer>>> result = keysRDD.join(wordCounts).collect();

    result.forEach(System.out::println);

    sc.stop();

  }
}
