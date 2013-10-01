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

package edu.gatech.gtri.typesafeconfigextensions.internal.equivalence;

/**
 * Equals and hashCode implementations for some type <tt>A</tt>.
 *
 * @param <A> The type for which equals and hashCode definitions are
 *   given by this instance.
 */
public interface Equivalence<A> {

    boolean equals(A a, A b);

    int hashCode(A obj);
}
