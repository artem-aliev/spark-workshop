#!/usr/bin/env bash
unzip spark-1.5.0-bin-hadoop2.6.zip
mv spark-1.5.0-bin-hadoop2.6 spark
cd spark
mv conf/log4j.properties.template conf/log4j.properties
#set log4j.rootCategory=ERROR, console
mv conf/spark-defaults.conf.template conf/spark-defaults.conf

./bin/spark-shell

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
