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

import org.specs2.mutable._
import edu.gatech.gtri.typesafeconfigextensions.forscala._
import edu.gatech.gtri.typesafeconfigextensions.factory.ConfigFactory.noBindings

class FileConfigSourceSpec extends Specification {

  "FileConfigSource" >> {

    "byKey" >> {

      val source = new FileConfigSource(PathSpecifications.byKey("some.key"))

      "toString" ! ( source.toString shouldEqual "ConfigSource { file by key: some.key }" )

      "with nothing bound" ! ( source.load(noBindings) shouldEqual Nil.toConfig )
    }
  }
}
