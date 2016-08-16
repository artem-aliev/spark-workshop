name := "workshop"

version := "1.0"

scalaVersion := "2.10.4"

val sparkVersion = "1.6.2"

mainClass in (Compile, packageBin) := Some("edu.workshop.ScalaWordCount")

libraryDependencies += "org.apache.spark" %% "spark-core" % sparkVersion % "compile"
libraryDependencies += "org.apache.spark" %% "spark-sql" % sparkVersion % "compile"
libraryDependencies += "org.apache.spark" %% "spark-hive" % sparkVersion % "compile"
libraryDependencies += "org.apache.spark" %% "spark-streaming" % sparkVersion % "compile"
libraryDependencies += "org.apache.spark" %% "spark-mllib" % sparkVersion % "compile"

