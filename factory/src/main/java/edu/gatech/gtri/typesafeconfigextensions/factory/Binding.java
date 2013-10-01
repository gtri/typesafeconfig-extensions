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

package edu.gatech.gtri.typesafeconfigextensions.factory;

/**
 * An optional instance to which a type is bound in a {@link Bindings}
 * collection.
 *
 * @param <A> The type to which an instance is or is not bound.
 */
public interface Binding<A> {

    /**
     * True is an instance is bound, false if no instance is bound.
     */
    boolean isPresent();

    /**
     * The instance that is bound to type {@code A} if such a binding exists.
     *
     * @throws UnsupportedOperationException
     *   if no binding {@link #isPresent() is present}.
     */
    A get();
}
