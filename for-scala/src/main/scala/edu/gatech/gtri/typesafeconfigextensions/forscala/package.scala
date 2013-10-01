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

package edu.gatech.gtri.typesafeconfigextensions

import com.typesafe.config.{ConfigException, ConfigFactory, ConfigValue, ConfigValueFactory}
import scala.math.ScalaNumber

/**
 * This package adds some sugar atop the Typesafe Config API for usage
 * in Scala.
 *
 * <h2>Example usage</h2>
 *
 * {{{
 * import edu.gatech.gtri.typesafeconfigextensions.forscala._
 *
 * val a = "A { B: C }, D: E".toConfig          // A { B: C }, D: E
 * val b = ("A" -> 7).toConfig                  // A: 7
 * val c = Seq("A" -> 4, "B" -> "C").toConfig   // A: 4, B: C
 * val d = c ++ b                               // A: 7, B: C
 * val e = b + ("F" -> "G")                     // A: 7, F: G
 * val f = a - "A"                              // D: E
 * }}}
 */
package object forscala {

  type Config = com.typesafe.config.Config

  implicit class AnyEnrichedForScalaConfig(a: Any) {

    /** `ConfigValueFactory.fromAnyRef` accepts "Boolean, Number, String, Map, Iterable, or null".
      * This method handles Scala and Java types for Boolean, Number, String, and null. It neglects
      * Map and Iterable, although support for these may be added in future versions of this library.
      */
    val toConfigValue: ConfigValue = a match {

      case null =>
        ConfigValueFactory fromAnyRef null

      case x if x.isInstanceOf[ConfigValue] =>
        x.asInstanceOf[ConfigValue]

      case x if x.isInstanceOf[java.lang.Boolean]
             || x.isInstanceOf[java.lang.Number]
             || x.isInstanceOf[java.lang.String] =>
        ConfigValueFactory fromAnyRef x

      case x if x.isInstanceOf[Boolean] =>
        ConfigValueFactory fromAnyRef (x.asInstanceOf[Boolean]: java.lang.Boolean)

      case x if x.isInstanceOf[Int] =>
        ConfigValueFactory fromAnyRef (x.asInstanceOf[Int]: java.lang.Integer)

      case x if x.isInstanceOf[Long] =>
        ConfigValueFactory fromAnyRef (x.asInstanceOf[Long]: java.lang.Long)

      case x if x.isInstanceOf[Float] =>
        ConfigValueFactory fromAnyRef (x.asInstanceOf[Float]: java.lang.Float)

      case x if x.isInstanceOf[Double] =>
        ConfigValueFactory fromAnyRef (x.asInstanceOf[Double]: java.lang.Double)

      case x if x.isInstanceOf[Byte] =>
        ConfigValueFactory fromAnyRef (x.asInstanceOf[Byte]: java.lang.Byte)

      case x if x.isInstanceOf[Short] =>
        ConfigValueFactory fromAnyRef (x.asInstanceOf[Short]: java.lang.Short)

      case x if x.isInstanceOf[ScalaNumber] =>
        ConfigValueFactory fromAnyRef (x.asInstanceOf[ScalaNumber]: java.lang.Number)
    }
  }

  implicit class ConfigEnrichedForScalaConfig(config: Config) {

    def +(entry: (String, Any)): Config = entry match {
      case (path, value) => config.withValue(path, value.toConfigValue)
    }

    /** Combines this config with another config. The config on the left-hand side
      * of the operation is overridden by the config on the right-hand side.
      * In other words, `a ++ b` is equivalent to `b withFallback a`.
      */
    def ++(config: Config): Config = config withFallback this.config

    /** `a - b` is equivalent to `a withoutPath b`.
      */
    def -(path: String): Config = config.withoutPath(path)
  }

  implicit class Tuple2EnrichedForScalaConfig(tuple: (String, Any)) {

    def toConfig: Config = ConfigFactory.empty + (tuple._1 -> tuple._2)
  }

  implicit class SeqOfStringAndAnyEnrichedForScalaConfig(seq: Seq[(String, Any)]) {

    def toConfig: Config = seq.foldLeft(ConfigFactory.empty)(_+_)
  }

  implicit class StringEnrichedForScalaConfig(s: String) {

    def toConfig: Config = ConfigFactory.parseString(s)
  }
}
