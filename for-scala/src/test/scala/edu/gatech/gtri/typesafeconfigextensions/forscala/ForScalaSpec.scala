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

package edu.gatech.gtri.typesafeconfigextensions.forscala

import org.specs2.mutable._

class ForScalaSpec extends Specification {

  val a = "A { B: C }, D: E".toConfig
  val b = ("A" -> 7).toConfig                 ; "b" ! { b shouldEqual "A: 7".toConfig }
  val c = Seq("A" -> 4, "B" -> "C").toConfig  ; "c" ! { c shouldEqual "A: 4, B: C".toConfig }
  val d = c ++ b                              ; "d" ! { d shouldEqual "A: 7, B: C".toConfig }
  val e = b + ("F" -> "G")                    ; "e" ! { e shouldEqual "A: 7, F: G".toConfig }
  val f = a - "A"                             ; "f" ! { f shouldEqual "D: E".toConfig }

}
