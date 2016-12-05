import sbt.Project.projectToRef

///////DISABLE FOR PRODUCTION
lazy val development = true

def commonSettings(pname: String) = Seq(
  version := "0.1-SNAPSHOT",
  organization := "xyz.hexagons",
  scalaVersion := "2.11.7",
  name := pname
)

//////////////////////////
// SERVER

lazy val clients = Seq(client)
lazy val server = (project in file("server")).settings(commonSettings("server"): _*).settings(
  scalaJSProjects := clients,
  pipelineStages := Seq(scalaJSProd, gzip),
  name := """hexagons-site""",
  resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases",
  libraryDependencies ++= Seq(
  	"com.vmunier" %% "play-scalajs-scripts" % "0.3.0",
    "org.webjars" % "jquery" % "1.11.1",
    "com.nimbusds" % "nimbus-jose-jwt" % "4.23",
    "com.google.guava" % "guava" % "19.0",
    "postgresql" % "postgresql" % "9.1-901-1.jdbc4",
    //"me.chrons" %% "boopickle" % "1.1.0",
    cache,
    ws,
    jdbc,
    specs2 % Test
  ),
  routesGenerator := InjectedRoutesGenerator,

  (compile in Compile) <<= (compile in Compile)
).enablePlugins(PlayScala).
  aggregate(clients.map(projectToRef): _*)
  .dependsOn(commonJvm)

//////////////////////////
// CLIENT

lazy val client = (project in file("client")).settings(commonSettings("client"): _*).settings(
  persistLauncher := true,
  persistLauncher in Test := false,
  emitSourceMaps in fullOptJS := development,
  libraryDependencies ++= Seq(
    "org.scala-js" %%% "scalajs-dom" % "0.8.0"
    //"me.chrons" %%% "boopickle" % "1.1.0"
  )
).enablePlugins(ScalaJSPlugin, ScalaJSPlay)
  .dependsOn(commonJs)

//////////////////////////
// GLOBAL COMMON

lazy val common = (crossProject.crossType(CrossType.Pure) in file("common")).
  settings(commonSettings("common"): _*).
  jsConfigure(_ enablePlugins ScalaJSPlay)

lazy val commonJvm = common.jvm.settings(
  libraryDependencies ++= Seq(
    //"me.chrons" %% "boopickle" % "1.1.0"
  )
)
lazy val commonJs = common.js.settings(
  libraryDependencies ++= Seq(
    //"me.chrons" %%% "boopickle" % "1.1.0"
  )
)

//////////////////////////
// OTHER

onLoad in Global := (Command.process("project server", _: State)) compose (onLoad in Global).value

