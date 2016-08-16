package edu.workshop

import org.apache.spark.sql.SQLContext
import org.apache.spark.sql.types._
import org.apache.spark.{SparkConf, SparkContext}


object ScalaSparkSQL {
  def main(args: Array[String]) {

    val conf = new SparkConf().setAppName("Simple Spark Application") //.setMaster("local")
    val sc = new SparkContext(conf)
    val sqlContext = new SQLContext(sc)
    val df = sqlContext.read.format("com.databricks.spark.csv")
      .option("header", "true").option("inferSchema", "true")load("iris.csv")
    df.show
    val irisSchema = StructType(Array(
      StructField("sepal_l",DoubleType,false),
      StructField("sepal_w",DoubleType,false),
      StructField("petal_l",DoubleType,false),
      StructField("petal_w",DoubleType,false),
      StructField("species",StringType,true)))
    val dfS = sqlContext.read.schema(irisSchema).format("com.databricks.spark.csv").option("header", "true").load("iris.csv")
      dfS.groupBy("species").avg("petal_w").show

    dfS.registerTempTable("species")
    import sqlContext._
    sql("select species, avg(sepal_l) from iris group by species").show
  }
}