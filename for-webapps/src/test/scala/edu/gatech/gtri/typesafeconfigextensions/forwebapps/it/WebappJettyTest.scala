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

package edu.gatech.gtri.typesafeconfigextensions.forwebapps.it

import org.specs2.mutable._

import org.eclipse.jetty.plus.webapp._
import org.eclipse.jetty.webapp._
import org.eclipse.jetty.server.Server
import scalaj.http.{HttpOptions, Http}
import edu.gatech.gtri.typesafeconfigextensions.forscala._
import java.nio.file.{Path, Paths}

class WebappJettyTest extends Specification {

  val port: Int = 8082
  val webappPath: Path = Paths get "for-webapps/src/test/webapp"
  val jettyEnvPath: Path = webappPath resolve "WEB-INF/jetty-env.xml"

  "For-Webapps integration test with Jetty" ! {

    NoJettyLogging.disableJettyLogging()

    org.eclipse.jetty.util.log.Log.setLog(new NoJettyLogging())

    val server = new Server(port)

    server.setHandler({
      val context = new WebAppContext()
      context.setContextPath("/abc/def/ghi")
      context.setResourceBase(webappPath.toString)
      context.setConfigurations(Array[Configuration](
        new WebInfConfiguration,
        new WebXmlConfiguration,
        new EnvConfiguration {
          setJettyEnvXml(jettyEnvPath.toFile.toURI.toURL)
        },
        new PlusConfiguration,
        new JettyWebXmlConfiguration
      ))
      context
    })

    server.start()

    val response: String = try {
      Http("http://localhost:%d/abc/def/ghi/".format(port))
      .option(HttpOptions.connTimeout(2000)) { in =>
        io.Source.fromInputStream(in).getLines().mkString("\n")
      }
    } finally {
      server.stop()
      server.join()
    }

    response.toConfig shouldEqual "a: 1, b: 2, c: 3, d: 4, e: 5".toConfig
  }

}
