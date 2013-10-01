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

import com.typesafe.config.Config;

/**
 * Strategy of obtaining {@link Config}uration data.
 *
 * <p>A {@link ConfigFactory} is composed of a sequence of
 *   {@link ConfigSource}s.</p>
 *
 * <p>Some examples:</p>
 * <ul>
 *   <li>{@link ConfigFactory#systemProperties() System properties}</li>
 *   <li>{@link ConfigFactory#configFile() A file}</li>
 *   <li>{@link ConfigFactory#classpathResource(String)
 *     A resource on the classpath}</li>
 * </ul>
 *
 * <p>{@code ConfigSource} should generally be implemented by extending
 * {@link BaseConfigSource}.</p>
 */
public interface ConfigSource {

    Config load(Bindings bindings);

    /**
     * Assigns a name to this config source.
     *
     * <p>This method is intended to provide a fluent API that mimics the
     * syntax of an English phrase like "a person named Alice":
     * <pre>{@code
     * aPerson()
     *     .named("Alice")
     * }</pre>
     */
    NamedConfigSource named(String name);
}
