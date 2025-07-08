ThisBuild / version := "0.1.0"

ThisBuild / scalaVersion := "3.6.4"

lazy val root = (project in file("."))
  .settings(
    name := "storch-pickle"
  )

import sbt.*
import Keys.*
import sbt.Def.settings

import scala.collection.Seq

//lazy val root = project
//  .enablePlugins(NoPublishPlugin)
//  .in(file("."))
////  .aggregate(core, vision, examples, docs)
//  .settings(
//    javaCppVersion := (ThisBuild / javaCppVersion).value,
////    csrCacheDirectory := file("D:\\coursier"),
//  )

ThisBuild / tlBaseVersion := "0.0" // your current series x.y
//ThisBuild / CoursierCache := file("D:\\coursier")
ThisBuild / organization := "io.github.mullerhai" //"dev.storch"
ThisBuild / organizationName := "storch.dev"
ThisBuild / startYear := Some(2024)
ThisBuild / licenses := Seq(License.Apache2)
ThisBuild / developers := List(
  // your GitHub handle and name
  tlGitHubDev("mullerhai", "mullerhai")
)
ThisBuild / version := "0.1.0"

ThisBuild / scalaVersion := "3.6.4"
ThisBuild / tlSonatypeUseLegacyHost := false

import xerial.sbt.Sonatype.sonatypeCentralHost
ThisBuild / sonatypeCredentialHost := sonatypeCentralHost

import ReleaseTransformations._
releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies,
  inquireVersions,
  runClean,
  runTest,
  setReleaseVersion,
  commitReleaseVersion,
  tagRelease,
  releaseStepCommandAndRemaining("+publishSigned"),
  releaseStepCommandAndRemaining("sonatypeBundleRelease"),
  setNextVersion,
  commitNextVersion,
  pushChanges,
)


//ThisBuild / version := "0.1.0-SNAPSHOT"


ThisBuild / tlSitePublishBranch := Some("main")

ThisBuild / apiURL := Some(new URL("https://storch.dev/api/"))
ThisBuild / tlSonatypeUseLegacyHost := false

// publish website from this branch
ThisBuild / tlSitePublishBranch := Some("main")
ThisBuild / homepage := Some(new URL("https://storch.dev/api/"))
ThisBuild / scmInfo := Some( ScmInfo( url( "https://github.com/mullerhai/storch-pandas" ), "scm:git:https://github.com/mullerhai/storch-pandas.git" ) )
//ThisBuild / scmInfo := Some(new ScmInfo("https://github.com/mullerhai/storch-k8s.git"))
//val scrImageVersion = "4.3.0" //4.0.34
//libraryDependencies +=   "dev.storch" % "core_3" % "0.2.6-1.15.1"
libraryDependencies += "io.github.mullerhai" % "storch-numpy_3" % "0.1.0"

ThisBuild  / assemblyMergeStrategy := {
  case v if v.contains("module-info.class")   => MergeStrategy.discard
  case v if v.contains("UnusedStub")          => MergeStrategy.first
  case v if v.contains("aopalliance")         => MergeStrategy.first
  case v if v.contains("inject")              => MergeStrategy.first
  case v if v.contains("jline")               => MergeStrategy.discard
  case v if v.contains("scala-asm")           => MergeStrategy.discard
  case v if v.contains("asm")                 => MergeStrategy.discard
  case v if v.contains("scala-compiler")      => MergeStrategy.deduplicate
  case v if v.contains("reflect-config.json") => MergeStrategy.discard
  case v if v.contains("jni-config.json")     => MergeStrategy.discard
  case v if v.contains("git.properties")      => MergeStrategy.discard
  case v if v.contains("reflect.properties")      => MergeStrategy.discard
  case v if v.contains("compiler.properties")      => MergeStrategy.discard
  case v if v.contains("scala-collection-compat.properties")      => MergeStrategy.discard
  case x =>
    val oldStrategy = (assembly / assemblyMergeStrategy).value
    oldStrategy(x)
}