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
import edu.gatech.gtri.typesafeconfigextensions.internal.Function
import edu.gatech.gtri.typesafeconfigextensions.internal.equivalence._

class ListInsertionStepSpec extends Specification {

  implicit def implicitFunctionConversion[A, B](f: A => B): Function[A, B] =
    new Function[A, B] { override def apply(a: A): B = f(a) }

  "ListInsertionStep" >> {

    import scala.collection.convert.wrapAll._

    def insert(xs: String*) =

      new ListInsertionStep[List[String], String, Int](
        new ListInsertionStepStrategy[List[String], String, Int] {

          override def currentElements(): java.util.List[String] = List("1", "2", "3")

          override def elementsToInsert(): java.util.List[String] = xs

          override def identifierEquivalence(): Equivalence[Int] =
            new EqualsEquivalence[Int].map({ i: Int => i % 10 })

          override def identifierFor(element: String): Int = element.toInt

          override def wrap(list: java.util.List[String]): List[String] = list.toList
        }
      )

    "4,5 at beginning" ! ( insert("4", "5").atBeginning  shouldEqual List("4", "5", "1", "2", "3") )
    "4,5 at end"       ! ( insert("4", "5").atEnd        shouldEqual List("1", "2", "3", "4", "5") )
    "4,5 before 1"     ! ( insert("4", "5") before 1     shouldEqual insert("4", "5").atBeginning )
    "4,5 after 3"      ! ( insert("4", "5") after 3      shouldEqual insert("4", "5").atEnd )
    "4,5 after 2"      ! ( insert("4", "5") after 2      shouldEqual List("1", "2", "4", "5", "3") )
    "4,5 before 2"     ! ( insert("4", "5") before 2     shouldEqual List("1", "4", "5", "2", "3") )
    "4,5 replacing 2"  ! ( insert("4", "5") replacing 2  shouldEqual List("1", "4", "5", "3") )
    "4,5 after 12"     ! ( insert("4", "5") after 12     shouldEqual insert("4", "5").after(2) )
    "4,5 after 8"      ! ( insert("4", "5") after 8      should throwAn[IllegalArgumentException] )
    "2,4 after 3"      ! ( insert("2", "4") after 3      should throwAn[IllegalArgumentException] )

  }
}
