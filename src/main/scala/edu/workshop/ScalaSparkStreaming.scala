package edu.workshop

import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}


object ScalaSparkStreaming {
  val checkpoint = "streaming_checkpoint"
  val dataDirectory = "tmp_data"

  def main(args: Array[String]): Unit = {
    // restore after failure
    // remove streaming_checkpoint from current directory if the code was changed
    val ssc = StreamingContext.getOrCreate(checkpoint, createStreamingContext _)
    ssc.start() // Start the computation
    ssc.awaitTermination() // Wait for the computation to terminate

  }


  def createStreamingContext: StreamingContext = {
    val conf = new SparkConf().setAppName("ShipmentStream")
      .set("spark.streaming.receiver.writeAheadLog.enable", "true")
      .set("spark.streaming.receiver.maxRate","1")
      .setMaster("local[*]")

    val ssc = new StreamingContext(conf, Seconds(10))
    ssc.checkpoint(checkpoint)

    val stream =  ssc.textFileStream(dataDirectory)

    val keys = Seq(("Spark",1), ("Hadoop",2), ("Streaming",3))
    val keysRDD= ssc.sparkContext.parallelize(keys).cache
    val wordCountStream = stream.flatMap(_ split " " ).map((_, 1)).reduceByKey(_ + _)

    val joinStream = wordCountStream.transform(rdd => rdd.join(keysRDD))

    // debug info, first 10 events
    joinStream.print()

    // store data in files
    stream.repartition(1).saveAsTextFiles("stream/s-")
    joinStream.foreachRDD((rdd, time) =>
      if (!rdd.isEmpty())rdd.saveAsTextFile("stream1/s-" + time.milliseconds)
    )
    ssc
  }
}
