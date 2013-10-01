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

package edu.gatech.gtri.typesafeconfigextensions.jndi.it

import javax.servlet.http.{HttpServlet, HttpServletRequest, HttpServletResponse}
import java.io.{Closeable, PrintWriter}
import edu.gatech.gtri.typesafeconfigextensions.forscala._
import edu.gatech.gtri.typesafeconfigextensions.jndi.JndiContexts.jndiContext
import scala.util.control.NonFatal

/** The server responds to any HTTP request with a dump of its JNDI configuration.
  */
final class JndiServlet extends HttpServlet {

  override def doGet(request: HttpServletRequest, response: HttpServletResponse): Unit =
    respond(response, config.root.render)

  def config: Config = jndiContext.toConfig - "__"

  def respond(response: HttpServletResponse, str: => String): Unit =
    useThenClose(response.getWriter) { out => printingExceptionsTo(out) { out write str } }

  def useThenClose[R <: Closeable](resource: R)(f: R => Unit): Unit =
    try f(resource) finally resource.close()

  def printingExceptionsTo(w: PrintWriter)(f: => Unit): Unit =
    try f catch { case NonFatal(e) => w write (
      "exception" -> s"${e.toString}\n${e.getStackTraceString}"
    ).toConfig.root.render }

}
