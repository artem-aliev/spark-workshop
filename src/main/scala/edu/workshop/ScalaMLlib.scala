package edu.workshop

import org.apache.spark.mllib.classification.NaiveBayes
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.sql.types.{DoubleType, StringType, StructField, StructType}
import org.apache.spark.sql.{DataFrame, SQLContext}
import org.apache.spark.{SparkConf, SparkContext}

class ScalaMLlib {
  def main(args: Array[String]) {

    val conf = new SparkConf().setAppName("Simple Spark Application") //.setMaster("local")
    val sc = new SparkContext(conf)
    val sqlContext = new SQLContext(sc)
    val df = loadIris("iris.csv",sqlContext)
    val class2id = df.select("species").distinct.map(_.getString(0)).collect.zipWithIndex.map{case (k,v)=>(k, v.toDouble)}.toMap
    val id2class = class2id.map(_.swap)
    val parsedData = df.map {r => LabeledPoint(class2id(r.getString(4)),
      Vectors.dense(r.getDouble(0), r.getDouble(2), r.getDouble(2), r.getDouble(3)
      ))}

    val splits = parsedData.randomSplit(Array(0.6, 0.4), seed = 11L)
    val training = splits(0)
    val test = splits(1)

    val model = NaiveBayes.train(training, lambda = 1.0, modelType = "multinomial")
    val predictionAndLabel = test.map(p => (model.predict(p.features), p.label))
    val accuracy = 1.0 * predictionAndLabel.filter(x => x._1 == x._2).count() / test.count()


  }

  def loadIris(file: String, sql: SQLContext): DataFrame = {
    val irisSchema = StructType(Array(
      StructField("sepal_l",DoubleType,false),
      StructField("sepal_w",DoubleType,false),
      StructField("petal_l",DoubleType,false),
      StructField("petal_w",DoubleType,false),
      StructField("species",StringType,true)))
    sql.read.schema(irisSchema).format("com.databricks.spark.csv").option("header", "true")
      .load(file)
  }

}
