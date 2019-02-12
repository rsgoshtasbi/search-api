javacOptions ++= Seq("-source", "1.8", "-target", "1.8", "-Xlint")
name := "search-api"
version := "0.1"
scalaVersion := "2.12.8"
retrieveManaged := true

libraryDependencies += "com.amazonaws" % "aws-lambda-java-core" % "1.2.0"
libraryDependencies += "com.amazonaws" % "aws-lambda-java-events" % "2.2.5"
libraryDependencies += "com.amazonaws" % "aws-java-sdk-kinesis" % "1.11.492"
libraryDependencies += "com.amazonaws" % "aws-java-sdk-core" % "1.11.275"
libraryDependencies += "com.amazonaws" % "aws-java-sdk-dynamodb" % "1.11.494"
libraryDependencies += "io.spray" %%  "spray-json" % "1.3.5"

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case x => MergeStrategy.first
}