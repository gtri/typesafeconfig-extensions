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

class PathSpecificationsSpec extends Specification {

  implicit class RichString(s: String) {
    import java.nio.file._
    def toPath: Path = Paths.get(s)
  }

  "PathSpecifications" >> {

    import PathSpecifications._

    "byKey" >> {

      val pathSpecification: PathSpecification = byKey("config.key")

      """name is "by key: _"""" ! ( pathSpecification.name shouldEqual "by key: config.key" )
    }

    "byPath" >> {

      val pathSpecification: PathSpecification = byPath("/tmp/configs".toPath)

      """name is "by path: _"""" ! ( pathSpecification.name shouldEqual "by path: /tmp/configs" )
    }
  }

}
