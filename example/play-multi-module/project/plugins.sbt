// Comment to get more information during initialization
logLevel := Level.Warn

// The Typesafe repository 
resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

resolvers += "JohnsonUSM repository" at "http://johnsonusm.com:8020/nexus/content/repositories/releases/"

resolvers += "JohnsonUSM snapshots" at "http://johnsonusm.com:8020/nexus/content/repositories/snapshots/"

resolvers ++= Seq("Sonatype snapshots repository" at "https://oss.sonatype.org/content/repositories/snapshots/",
                "Pablo repo" at "https://raw.github.com/fernandezpablo85/scribe-java/mvn-repo/")

resolvers += Resolver.mavenLocal

// Use the Play sbt plugin for Play projects
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.3.3")

addSbtPlugin("com.typesafe.sbt" % "sbt-less" % "1.0.0")

addSbtPlugin("com.typesafe.sbt" % "sbt-coffeescript" % "1.0.0")

addSbtPlugin("com.github.inthenow" % "sbt-scalajs" % "0.55.1")