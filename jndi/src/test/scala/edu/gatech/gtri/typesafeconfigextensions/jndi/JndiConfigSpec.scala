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

package edu.gatech.gtri.typesafeconfigextensions.jndi

import scala.languageFeature.implicitConversions
import org.specs2.mutable._
import com.typesafe.config.ConfigParseOptions
import edu.gatech.gtri.typesafeconfigextensions.jndi
import edu.gatech.gtri.typesafeconfigextensions.forscala._

class JndiConfigSpec extends Specification {

  class Context extends org.eclipse.jetty.jndi.NamingContext {
    setNameParser(new org.eclipse.jetty.jndi.java.javaNameParser())
  }
  object Context {
    def apply(bindings: (String, Object)*): Context = new Context {
      bindings foreach { case (key, value) => bind(key, value) }
    }
  }

  implicit def wrapContext(context: Context):
    jndi.JndiContext = JndiContexts.context(context)

  "JndiConfig" >> {

    "create an empty Config from an empty Context" ! {
      new Context().toConfig shouldEqual Nil.toConfig
    }

    "conversion to Config" >> {

      val context = Context(
        "magicword" -> "xyzzy",
        "ultimate" -> new Context,
        "ultimate/question" -> "unknown",
        "ultimate/answer" -> "42"
      )

      "from nested Contexts" ! ( context.toConfig shouldEqual
        "magicword: xyzzy, ultimate { question: unknown, answer: 42 }".toConfig )

      "from a subcontext" ! ( context.getContext("ultimate").toConfig shouldEqual
        "question: unknown, answer: 42".toConfig )
    }

    "parse config references without immediately resolving them" ! {

      val context = Context("a" -> "${b}")

      (context.toConfig + ("b" -> "c")).resolve shouldEqual "a: c, b: c".toConfig
    }

    "origin description on config values" >> {

      val config = Context("a" -> "b")
        .withParseOptions(ConfigParseOptions.defaults.setOriginDescription("hello"))
        .toConfig

      "root"  ! { config.root.origin.description shouldEqual "hello" }
      "value" ! { config.getValue("a").origin.description shouldEqual "hello: 1" }
    }
  }
}
