/*
 * Copyright 2013 Georgia Tech Applied Research Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import sbt._
import Keys._

object Build extends Build {

  lazy val root: Project = (
    Project("root", file("."))
    aggregate (internal, jndi, factory, forWebapps, forScala)
    settings(baseSettings ++ unpublished ++ Seq(
      name := "typesafeconfig-extensions",
      description := """
        |Libraries to help utilize the Typesafe Config library,
        |particularly designed to support servlet-based web applications.
      """.stripMargin.trim
    ): _*)
  )

  lazy val internal: Project = (
    Project("internal", file("internal"))
    settings(baseSettings ++ javaSettings ++ published ++ Seq(
      name := "typesafeconfig-internal",
      description := """
        |Code common to various typesafeconfig-extensions projects.
        |Other projects should not use this project directly, and
        |it should not be considered part of the public API.
      """.stripMargin.trim
    ): _*)
  )

  lazy val jndi: Project = (
    Project("jndi", file("jndi"))
    dependsOn(internal, forScala % "test")
    settings(baseSettings ++ javaSettings ++ published ++ Seq(
      name := "typesafeconfig-jndi",
      libraryDependencies += typesafeConfig,
      libraryDependencies ++= Seq(jettyPlus, scalajHttp).map(_ % "test")
    ): _*)
  )

  lazy val factory: Project = (
    Project("factory", file("factory"))
    dependsOn(internal, forScala % "test")
    settings(baseSettings ++ javaSettings ++ published ++ Seq(
      name := "typesafeconfig-factory",
      description := """
        |An immutable, adaptable factory for Configs.
      """.stripMargin.trim,
      libraryDependencies += typesafeConfig
    ): _*)
  )

  lazy val forWebapps: Project = (
    Project("for-webapps", file("for-webapps"))
    dependsOn (internal, factory, jndi, forScala % "test")
    settings(baseSettings ++ javaSettings ++ published ++ Seq(
      name := "typesafeconfig-for-webapps",
      description := """
        |A sensible default configuration factory for a web application.
      """.stripMargin.trim,
      libraryDependencies ++= Seq(
        typesafeConfig,
        servletApi % "provided,optional",
        jettyPlus % "test",
        scalajHttp % "test"
      )
    ): _*)
  )

  lazy val forScala: Project = (
    Project("for-scala", file("for-scala"))
    settings(baseSettings ++ published ++ Seq(
      name := "typesafeconfig-for-scala",
      description := """
        |Adds some sugar atop the Typesafe Config API for usage in Scala.
      """.stripMargin.trim,
      libraryDependencies += typesafeConfig
    ): _*)
  )

  lazy val baseSettings: Seq[Setting[_]] = Seq(
    version := "1.1.1-SNAPSHOT",
    organization := "edu.gatech.gtri.typesafeconfig-extensions",
    organizationName := "Georgia Tech Research Institute, CTISL",
    organizationHomepage := Some(url("http://gtri.gatech.edu/ctisl")),
    homepage := Some(url("https://github.com/gtri/typesafeconfig-extensions")),
    licenses := Seq("The Apache Software License, Version 2.0" ->
      url("http://www.apache.org/licenses/LICENSE-2.0.txt")),
    scalaVersion := "2.10.0",
    javacOptions ++= Seq("-source", "1.7")
  ) ++ specs2

  lazy val javaSettings: Seq[Setting[_]] = Seq(
    crossPaths := false,
    autoScalaLibrary := false
  ) ++ {
    import de.johoop.findbugs4sbt.{FindBugs, ReportType, Effort}, FindBugs._
    FindBugs.findbugsSettings ++ Seq[Setting[_]](
      findbugsReportType := Some(ReportType.Html),
      findbugsEffort := Effort.High,
      test <<= (test in Test) dependsOn findbugs
    )
  }

  lazy val typesafeConfig = "com.typesafe" % "config" % "1.0.2"

  lazy val servletApi = "javax.servlet" % "servlet-api" % "2.5"

  lazy val specs2: Seq[Setting[_]] = Seq(
    libraryDependencies += "org.specs2" % "specs2_2.10" % "2.2.1" % "test",
    scalacOptions in Test ++= Seq("-Yrangepos", "-feature")
  )

  def jettyModule(artifactId: String) =
    "org.eclipse.jetty" % artifactId % "7.3.0.v20110203"

  lazy val jettyWebapp = jettyModule("jetty-webapp")

  lazy val jettyPlus = jettyModule("jetty-plus")

  lazy val scalajHttp = "org.scalaj" % "scalaj-http_2.10" % "0.3.10"

  lazy val unpublished: Seq[Setting[_]] = {
    Seq(
      publishArtifact := false,
      publishArtifact in makePom := false,
      publish := {},
      publishLocal := {}
    )
  }

  lazy val published: Seq[Setting[_]] = Seq(
    publishMavenStyle := true,
    publish <<= com.typesafe.sbt.pgp.PgpKeys.publishSigned,
    publishTo := {
      val nexus = "https://oss.sonatype.org/"
      if (version.value.trim.endsWith("SNAPSHOT"))
        Some("snapshots" at nexus + "content/repositories/snapshots")
      else
        Some("releases"  at nexus + "service/local/staging/deploy/maven2")
    },
    pomExtra := {
      <scm>
        <url>https://github.com/gtri/typesafeconfig-extensions</url>
        <connection>https://github.com/gtri/typesafeconfig-extensions.git</connection>
        <developerConnection>git@github.com:gtri/typesafeconfig-extensions.git</developerConnection>
      </scm>
      <developers>
        <developer>
          <name>Chris Martin</name>
          <email>chris.martin@gatech.edu</email>
        </developer>
      </developers>
      <contributors>
        <contributor>
          <name>Kelsey Francis</name>
          <email>francis@gatech.edu</email>
        </contributor>
      </contributors>
    }
  )

}
