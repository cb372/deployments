
val circeVersion = "0.5.3"
val scalajsReactVersion = "0.11.2"
val reactVersion = "15.3.2"

val app = crossProject.settings(
  name := "deployments",
  organization := "com.ovoenergy",
  scalaVersion := "2.11.8",
  unmanagedSourceDirectories in Compile +=
    baseDirectory.value  / "shared" / "src" / "main" / "scala",
  libraryDependencies ++= Seq(
		"io.circe" %%% "circe-core" % circeVersion,
		"io.circe" %%% "circe-parser" % circeVersion,
		"io.circe" %%% "circe-generic" % circeVersion
  )
).jsSettings(
  libraryDependencies ++= Seq(
    "com.github.japgolly.scalajs-react" %%% "core" % scalajsReactVersion
  ),
  jsDependencies ++= Seq(
		"org.webjars.bower" % "react" % reactVersion
    /        "react-with-addons.js"
    commonJSName "React",

  "org.webjars.bower" % "react" % reactVersion
    /         "react-dom.js"
    dependsOn "react-with-addons.js"
    commonJSName "ReactDOM",

  "org.webjars.bower" % "react" % reactVersion
    /         "react-dom-server.js"
    dependsOn "react-dom.js"
    commonJSName "ReactDOMServer"
  )
).jvmSettings(
  libraryDependencies ++= Seq(
    "org.scala-js" %% "scalajs-library" % "0.6.8",
  	"com.typesafe.akka" %% "akka-http-experimental" % "2.4.11",
  	"io.getquill" %% "quill-jdbc" % "0.10.0"
  )
)
lazy val appJS = app.js
lazy val appJVM = app.jvm.settings(
  (resources in Compile) ++= Seq(
    (fastOptJS in (appJS, Compile)).value.data,
    (packageJSDependencies in (appJS, Compile)).value
  )
)
