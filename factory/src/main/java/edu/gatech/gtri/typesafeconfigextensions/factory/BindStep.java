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
 * This type is used for {@link ConfigFactory}'s DSL API when
 * {@link ConfigFactory#bind(Class) binding or unbinding} instances.
 *
 * @param <R> The return type of these methods.
 * @param <A> The type that is being bound or unbound.
 */
public interface BindStep<R, A> {

    R toInstance(A instance);

    R toNothing();
}
