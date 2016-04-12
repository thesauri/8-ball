resolvers += Resolver.url("scalasbt snapshots", new URL("http://repo.scala-sbt.org/scalasbt/sbt-plugin-snapshots"))(Resolver.ivyStylePatterns)

addSbtPlugin("com.hanhuy.sbt" % "android-sdk-plugin" % "1.3.6")

addSbtPlugin("com.typesafe.sbteclipse" % "sbteclipse-plugin" % "4.0.0")

addSbtPlugin("org.ensime" % "ensime-sbt" % "0.4.0")

libraryDependencies += "org.scalatest" % "scalatest_2.10" % "2.0" % "test"

//addSbtPlugin("org.roboscala" % "sbt-robovm" % "1.12.0")
