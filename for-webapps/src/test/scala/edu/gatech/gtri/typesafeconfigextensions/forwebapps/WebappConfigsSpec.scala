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
import edu.gatech.gtri.typesafeconfigextensions.factory.NamedConfigSource

class WebappConfigsSpec extends Specification {

  implicit class RichString(s: String) {
    import java.nio.file._
    def toPath: Path = Paths.get(s)
  }

  "WebappConfigs" >> {

    import WebappConfigs._

    "servletContextDirectory" >> {

      "byKey" >> {

        val configSource: NamedConfigSource =
          servletContextDirectory.byKey("web.conf")

        "name" ! ( configSource.name shouldEqual
          "servlet context directory by key: web.conf" )

        "toString" ! ( configSource.toString shouldEqual
          "ConfigSource { servlet context directory by key: web.conf }" )
      }

      "byPath" >> {

        val configSource: NamedConfigSource =
          servletContextDirectory.byPath("/etc/www.conf".toPath)

        "name" ! ( configSource.name shouldEqual
          "servlet context directory by path: /etc/www.conf" )

        "toString" ! ( configSource.toString shouldEqual
          "ConfigSource { servlet context directory by path: /etc/www.conf }" )
      }
    }

    "jndi" >> {

      "name" ! ( jndi.name shouldEqual "jndi" )

      "toString" ! ( jndi.toString shouldEqual "ConfigSource { jndi }" )
    }
  }
}
