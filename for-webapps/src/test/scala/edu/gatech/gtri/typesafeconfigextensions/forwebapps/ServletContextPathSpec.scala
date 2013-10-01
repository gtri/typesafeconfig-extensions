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

package edu.gatech.gtri.typesafeconfigextensions.forwebapps

import org.specs2.mutable.Specification
import scala.collection.JavaConversions._

class ServletContextPathSpec extends Specification {

  "ServletContextPath" >> {

    "parse" >> {

      def parseAsList(s: String): List[String] =
        ServletContextPath.parse(s).toList.toList

      "empty string -> empty list" !
        ( parseAsList("") shouldEqual Nil )

      """"/abc" -> as List("abc")""" !
        ( parseAsList("/abc") shouldEqual List("abc") )

      "ignore duplicate slashes" !
        ( parseAsList("//abc////def/ghi//") shouldEqual List("abc", "def", "ghi") )
    }

    "equals" >> {

      "== same path" ! (
        new ServletContextPath(List("foo", "bar"))
        shouldEqual
        new ServletContextPath(List("foo", "bar"))
      )

      "!= different path" ! (
        new ServletContextPath(List("foo", "bar"))
        shouldNotEqual
        new ServletContextPath(List("foo", "baz"))
      )
    }

    "toString" >> {

      "empty" ! (
        new ServletContextPath(Nil).toString shouldEqual
        "ServletContextPath { (root) }" )

      "one component" ! (
        new ServletContextPath(List("foo")).toString shouldEqual
        "ServletContextPath { /foo }" )

      "two components" ! (
        new ServletContextPath(List("foo", "bar")).toString shouldEqual
        "ServletContextPath { /foo/bar }" )
    }
  }
}
