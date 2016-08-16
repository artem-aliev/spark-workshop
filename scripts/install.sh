#!/usr/bin/env bash

http://spark.apache.org/downloads.html

tar xzvf spark-1.6.2-bin-hadoop2.6.tgz
mv spark-1.6.2-bin-hadoop2.6 spark

cd spark
mv conf/log4j.properties.template conf/log4j.properties
#set log4j.rootCategory=ERROR, console
mv conf/spark-defaults.conf.template conf/spark-defaults.conf

./bin/spark-shell

# first example
scala> val textFile = sc.textFile("README.md")
scala> textFile.count
scala> val wordCounts = textFile.flatMap(line => line.split(" ")).map(word => (word,1)).reduceByKey((a, b) => a+b)
scala> wordCounts.takeSample(true,3).foreach(println)

# join example

scala> val keys = Seq(("Spark",1), ("Hadoop",2), ("Streaming",3))
scala> val keysRDD= sc.parallelize(keys)
scala> keysRDD.join(wordCounts).collect.foreach(println)


./bin/spark-submit --class org.apache.spark.examples.SparkPi \
    --master yarn-cluster \
    --num-executors 3 \
    --driver-memory 4g \
    --executor-memory 2g \
    --executor-cores 1 \
    --queue thequeue \
    lib/spark-examples*.jar \
    10

./bin/spark-submit.cmd --class org.apache.spark.deploy.master.Master 1 -h localhost
# Goto http://localhost:8080 and get the master address
./bin/spark-submit.cmd --class org.apache.spark.deploy.worker.Worker 1 spark://localhost:7077

./sbin/start-master.sh -h localhost
./sbin/start-slave.sh  spark://localhost:7077

./bin/spark-shell --packages com.databricks:spark-csv_2.10:1.4.0

./bin/spark-sql --packages com.databricks:spark-csv_2.10:1.4.0 --hiveconf hive.metastore.warehouse.dir=file:/Users/artemaliev/git/workshop;

CREATE  TABLE iris (sepal_l double,sepal_w double,petal_l double,petal_w double, species string)
USING com.databricks.spark.csv
OPTIONS (path "data/iris.csv", header "true");

select species, avg(sepal_l) from iris group by species;

Iris-setosa	5.005999999999999
Iris-virginica	6.587999999999998
Iris-versicolor	5.935999999999999



./sbin/start-thriftserver.sh --packages com.databricks:spark-csv_2.10:1.2.0 --hiveconf hive.metastore.warehouse.dir=file:/Users/artemaliev/git/workshop
