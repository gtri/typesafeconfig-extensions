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

import scala.languageFeature.implicitConversions
import org.specs2.mutable._
import edu.gatech.gtri.typesafeconfigextensions.forscala._
import edu.gatech.gtri.typesafeconfigextensions.factory.ConfigFactory._
import JndiConfigSourceImpl._
import edu.gatech.gtri.typesafeconfigextensions.factory.Bindings
import edu.gatech.gtri.typesafeconfigextensions.internal.Function

class JndiConfigSourceImplSpec extends Specification {

  implicit def implicitFunctionConversion[A, B](f: A => B): Function[A, B] =
    new Function[A, B] { override def apply(a: A): B = f(a) }

  implicit def tupleToJndiPathMapping(t: (String, String)): JndiConfigSource.PathMapping =
    JndiConfigSource.PathMapping.jndiPathMapping(t._1, t._2)

  "JndiConfigSourceImpl" >> {

    """places JNDI configuration under the "jndi" path""" >> {

      val source: JndiConfigSource = defaultJndiConfigSource
        .withSupplier({ bindings: Bindings => "abc: def".toConfig })

      "with default bindings" ! ( source.load(defaultBindings) shouldEqual "jndi.abc: def".toConfig )

      "with no bindings"      ! ( source.load(noBindings)      shouldEqual "jndi.abc: def".toConfig )
    }

    """copies values from "jndi" to the root namespace as specified by path mappings""" ! {

      val source: JndiConfigSource = defaultJndiConfigSource
        .withSupplier({ bindings: Bindings => "abc: def, ghi: jkl".toConfig })
        .withPathMappings ("abc" -> "xyz", "jkl" -> "mno")

      source.load(defaultBindings) shouldEqual
        "jndi { abc: def, ghi: jkl }, xyz: def".toConfig
    }

    "name" ! ( defaultJndiConfigSource.name shouldEqual "jndi" )

    "toString" ! ( defaultJndiConfigSource.toString shouldEqual "ConfigSource { jndi }" )

    """named("xyz")""" >> {

      val source: JndiConfigSource = defaultJndiConfigSource named "xyz"

      "name" ! ( source.name shouldEqual "xyz" )

      "toString" ! ( source.toString shouldEqual "ConfigSource { xyz }" )
    }
  }
}
