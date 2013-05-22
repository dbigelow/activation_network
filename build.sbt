name := "ADN"

version := "0.0.1"

scalaVersion := "2.9.1"

// Twitter's repository
resolvers += "twitter.com" at "http://maven.twttr.com"

// Add Twitter dependencies
libraryDependencies ++= Seq(
  "com.twitter" % "finagle-core" % "1.9.0",
  "com.twitter" % "finagle-http" % "1.9.0"
)

// Add Akka dependency
resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

libraryDependencies += "com.typesafe.akka" % "akka-actor" % "2.0.3"

// Add Netty dependency
libraryDependencies += "org.jboss.netty" % "netty" % "3.2.1.Final"