val projectName = "hummingbird"
name := projectName

inThisBuild(Seq(
  version := "0.1",
  scalaVersion := "2.13.6"
))

lazy val commonSettings = Seq(
  libraryDependencies ++= Seq(
    "org.typelevel" %% "cats-effect" % "2.5.1",
  ),
  addCompilerPlugin("org.typelevel" % "kind-projector" % "0.13.2" cross CrossVersion.full)
)

lazy val core = project.in(file("core"))
  .settings(commonSettings)
  .settings(
    name := s"$projectName-core"
  )

lazy val interop_monix = project.in(file("interop/monix"))
  .settings(commonSettings)
  .settings(
    name := s"$projectName-interop_monix",
    libraryDependencies ++= Seq(
      "io.monix" %% "monix" % "3.4.0",
      "io.monix" %% "monix-reactive" % "3.4.0",
    )
  )
  .dependsOn(core)

lazy val root = project.in(file("."))
  .settings(
    name := s"$projectName-root"
  ).aggregate(core, interop_monix)