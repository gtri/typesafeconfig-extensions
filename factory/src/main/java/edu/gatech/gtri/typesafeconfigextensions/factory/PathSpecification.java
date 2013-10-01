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
 * Strategy for finding a filesystem {@link java.nio.file.Path Path}.
 *
 * <p>This type is intended for use as a field on a {@link ConfigSource}
 * implementation to help implement {@link ConfigSource#load(Bindings)}.</p>
 *
 * <p>Some default implementations are constructed by the static methods
 * available on {@link PathSpecifications}.</p>
 */
public interface PathSpecification {

    OptionalPath path(Bindings bindings);

    /**
     * A name that can be used to derive
     * {@link ConfigSourceName#name() the name of a ConfigSource}.
     */
    String name();
}
