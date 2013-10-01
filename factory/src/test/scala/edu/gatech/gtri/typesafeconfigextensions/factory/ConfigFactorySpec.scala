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

package edu.gatech.gtri.typesafeconfigextensions.factory

import scala.languageFeature.implicitConversions
import org.specs2.mutable._
import edu.gatech.gtri.typesafeconfigextensions.forscala._
import ConfigFactory._
import com.typesafe.config.ConfigException

class ConfigFactorySpec extends Specification {

  implicit class RichString(s: String) {
    import java.nio.file._
    def toPath: Path = Paths.get(s)
  }

  "ConfigFactory" >> {

    "loads classpath resources in precedence order" ! {

      val factory: ConfigFactory =
        emptyConfigFactory
          .bindDefaults
          .withSources(
            classpathResource("config-factory-test-reference"),
            classpathResource("config-factory-test-application")
          ).fromLowestToHighestPrecedence

      factory.load shouldEqual "abc: def, ghi: xyz".toConfig
    }

    "config source modification" >> {

      "insert" >> {

        val c1 = configString("a: 1, b: 1")             named "c1"
        val c2 = configString("      b: 2, c: 2")       named "c2"
        val c3 = configString("            c: 3, d: 3") named "c3"
        val c4 = configString("                  d: 4") named "c4"

        val correctFactory: ConfigFactory = emptyConfigFactory.bindDefaults
          .withSources(c4, c3, c2, c1).fromHighestToLowestPrecedence

        "one" ! {

          val factory: ConfigFactory = emptyConfigFactory.bindDefaults
            .withSources(c1, c2, c4).fromLowestToHighestPrecedence
            .insertSource(c3).withHigherPrecedenceThan(configSourceNamed("c2"))

          factory.load shouldEqual correctFactory.load
        }

        "two" ! {

          val factory: ConfigFactory = emptyConfigFactory.bindDefaults
            .withSources(c1, c4).fromLowestToHighestPrecedence
            .insertSources(c3, c2).fromHighestToLowestPrecedence.withHigherPrecedenceThan(c1)

          factory.load shouldEqual correctFactory.load
        }

        "replacement by same id" >> {

          val factory: ConfigFactory = emptyConfigFactory.bindDefaults
            .withSources(c1, c4, c3).fromLowestToHighestPrecedence
            .insertSource(c2 named "c4").replacing(configSourceNamed("c4"))
            .insertSource(c4 named "x").withHighestPrecedence

          factory.load shouldEqual correctFactory.load
        }

        "no such id" >> {

          val factory: ConfigFactory = emptyConfigFactory.bindDefaults
            .withSources(c1, c2).fromLowestToHighestPrecedence

          "with higher precedence" ! (
            factory.insertSource(c4).withHigherPrecedenceThan(c3)
              should throwAn[IllegalArgumentException]
          )

          "replacing" ! (
            factory.insertSource(c4).replacing(c3)
              should throwAn[IllegalArgumentException]
          )
        }

        "duplicate id" >> {

          val factory: ConfigFactory = emptyConfigFactory.bindDefaults
            .withSources(c1, c2, c3).fromLowestToHighestPrecedence

          "one" ! (
            { factory.insertSource (c3).replacing(c2)
            } should throwAn[IllegalArgumentException]
          )

          "two" ! (
            { factory.insertSources(c3, c4)
              .fromHighestToLowestPrecedence
              .withHigherPrecedenceThan(c1)
            } should throwAn[IllegalArgumentException]
          )
        }

      }

      "remove" ! {

        val factory: ConfigFactory = emptyConfigFactory.bindDefaults
          .withSources(
            configString("a: 1") named "one",
            configString("b: 2") named "two"
          ).fromHighestToLowestPrecedence
          .removeSource(configSourceNamed("two"))

        factory.load shouldEqual "a: 1".toConfig
      }

    }

    "load Configs derived from other Configs by loading in two phases" ! {

      val factory: ConfigFactory =
        emptyConfigFactory
          .bindDefaults
          .withSources(

            new BaseConfigSource {
              def load(bindings: Bindings): Config = {
                val config: Config = bindings.get(classOf[Config]).get

                def missingAsNone[A](f: => A): Option[A] =
                    try { Some(f) }
                    catch { case e: ConfigException.Missing => None }

                (
                  for (
                    key <- missingAsNone(config.getString("c"));
                    value <- missingAsNone(config.getValue(key))
                  ) yield ("f" -> value).toConfig

                ).getOrElse(Nil.toConfig)
              }
            } named "Alpha",

            configString("b: 3, f: 9, d: 7") named "Beta",

            configString("a: 1, b: 2, c: d") named "Delta"

          ).fromHighestToLowestPrecedence

      // In the first phase, the key `c` is not bound, so Alpha is empty.
      // In the second phase, `c` is bound to `d`, and `d` is bound to `7`, so
      // Alpha binds `f` to `7`, which overrides the value of `f` from Beta.

      factory.load shouldEqual "a: 1, b: 3, c: d, d: 7, f: 7".toConfig
    }

    "configFile" >> {

      "byKey" >> {

        val source: NamedConfigSource = configFile byKey "my.config.file"

        """name is "file by key: _"""" !
          ( source.name shouldEqual "file by key: my.config.file" )

        "toString" ! ( source.toString shouldEqual
          "ConfigSource { file by key: my.config.file }" )
      }

      "byPath" >> {

        val source: NamedConfigSource = configFile byPath "/etc/myapp/app.conf".toPath

        """name is "file by path: _"""" !
          ( source.name shouldEqual "file by path: /etc/myapp/app.conf" )

        "toString" ! ( source.toString shouldEqual
          "ConfigSource { file by path: /etc/myapp/app.conf }" )
      }
    }

  }

}
