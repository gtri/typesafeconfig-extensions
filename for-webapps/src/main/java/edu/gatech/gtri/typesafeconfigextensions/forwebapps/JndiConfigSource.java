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

package edu.gatech.gtri.typesafeconfigextensions.forwebapps;

import edu.gatech.gtri.typesafeconfigextensions.factory.ConfigSource;
import edu.gatech.gtri.typesafeconfigextensions.factory.NamedConfigSource;

import java.util.List;

import static edu.gatech.gtri.typesafeconfigextensions.internal.Check.checkNotNull;

/**
 * A {@link ConfigSource} that loads configuration from JNDI variables.
 */
public interface JndiConfigSource
extends NamedConfigSource {

    /**
     * Copies a specific value from the JNDI namespace into the root namespace.
     *
     * <p>Example: Suppose we start with a config source that produces</p>
     * <pre>{@code
     * jndi { a: 1, b: 2 }
     * }</pre>
     * <p>Applying {@code mapPath ("b", "c")}, we get a new config that
     *   produces</p>
     * <pre>{@code
     * jndi { a: 1, b: 2 }, c: 2
     * }</pre>
     *
     * @see #mapPath(JndiConfigSource.PathMapping)
     */
    JndiConfigSource mapPath(String fromJndiPath, String toRootPath);

    /**
     * Equivalent to
     * {@code mapPath(pathMapping.fromJndiPath, pathMapping.toRootPath)}.
     *
     * @see #mapPath(String, String)
     */
    JndiConfigSource mapPath(PathMapping pathMapping);

    /**
     * Replaces all existing mappings with the one provided by the method
     * argument.
     */
    JndiConfigSource withPathMapping(PathMapping onlyPathMapping);

    /**
     * Replaces all existing mappings with those provided by the method
     * arguments.
     */
    JndiConfigSource withPathMappings(
        PathMapping firstPathMapping,
        PathMapping secondPathMapping,
        PathMapping... morePathMappings
    );

    /**
     * Replaces all existing mappings with those provided by the given
     * collection.
     */
    JndiConfigSource withPathMappings(List<PathMapping> mappings);

    /**
     * Indicates that the JNDI value named {@link #fromJndiPath()}
     * should be copied into the root Config namespace at path
     * {@link #toRootPath()}.
     */
    final class PathMapping {

        private final String fromJndiPath;
        private final String toRootPath;

        private PathMapping(
            String fromJndiPath,
            String toRootPath
        ) {
            this.fromJndiPath = checkNotNull(fromJndiPath);
            this.toRootPath = checkNotNull(toRootPath);
        }

        public static PathMapping jndiPathMapping(
            String fromJndiPath,
            String toRootPath
        ) {
            return new PathMapping(
                checkNotNull(fromJndiPath),
                checkNotNull(toRootPath)
            );
        }

        public String fromJndiPath() {
            return fromJndiPath;
        }

        public String toRootPath() {
            return toRootPath;
        }
    }
}
