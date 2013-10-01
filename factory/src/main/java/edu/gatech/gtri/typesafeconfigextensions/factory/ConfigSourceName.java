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
 * Something that can be used to identify a {@link ConfigSource} by
 * {@link #name() name}.
 *
 * <p>This interface is mostly used via its subtype {@link NamedConfigSource},
 * but you can also instantiate a {@link ConfigSourceName} without a
 * {@link ConfigSource} by calling
 * {@link ConfigFactory#configSourceNamed(String)}.</p>
 */
public interface ConfigSourceName {

    /**
     * A string that uniquely identifies a {@link ConfigSource} within
     * the scope of a {@link ConfigFactory}.
     */
    String name();
}
