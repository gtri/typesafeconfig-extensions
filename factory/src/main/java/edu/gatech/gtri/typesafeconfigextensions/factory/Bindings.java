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

import java.util.Map;

/**
 * A {@link ConfigFactory}'s {@code Bindings} contains options that are made
 * available to {@link ConfigSource#load(Bindings)}.
 *
 * <p>See
 *   {@link ConfigFactory#defaultBindings() ConfigFactory.defaultBindings()}
 *   for a list of some commonly-used bindings.</p>
 */
public interface Bindings {

    /**
     * Equivalent to {@link #asMap()}.{@link Map#get(Object) get(type)}
     * if {@code T} is bound. Otherwise, throws
     * {@link IllegalArgumentException}.
     */
    <T> Binding<T> get(Class<T> type);

    /**
     * An unmodifiable map containing all of the bindings.
     */
    Map<Class<?>, Object> asMap();
}
