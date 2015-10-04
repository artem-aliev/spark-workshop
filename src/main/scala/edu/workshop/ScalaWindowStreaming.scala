package edu.workshop

import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}


object ScalaWindowStreaming {
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

    val joinStream = wordCountStream.transform(rdd => rdd.join(keysRDD)
      .map{case (key,(count, id)) => (key,count)})


    val win = joinStream.window(Seconds(30), Seconds(10))

    def updateFunction(newValues: Seq[Int], runningCount: Option[Int]): Option[Int] = {
      val newCount: Int = runningCount.getOrElse(0) + newValues.sum
      Some(newCount)
    }
    val total = joinStream.updateStateByKey(updateFunction _)

    // debug info, first 10 events
    win.join(total).print()

    ssc
  }
}
