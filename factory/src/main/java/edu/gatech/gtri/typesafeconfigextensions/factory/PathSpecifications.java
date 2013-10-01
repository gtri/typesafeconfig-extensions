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
import com.typesafe.config.ConfigException;

import java.nio.file.Path;
import java.nio.file.Paths;

import static edu.gatech.gtri.typesafeconfigextensions.internal.Check.checkNotNull;

/**
 * Static methods for constructing {@link PathSpecification}s.
 */
public final class PathSpecifications {

    private PathSpecifications() { }

    /**
     * A trivial {@link PathSpecification} with a constant
     * {@link PathSpecification#path(Bindings) path}
     * given as this method's {@code path} parameter.
     *
     * <p>The {@link PathSpecification#name() name} of the returned
     * path specification is "by path: _", where "_" is substituted
     * by the string representation of the {@code path} parameter.</p>
     */
    public static PathSpecification byPath(final Path path) {

        checkNotNull(path);

        return new PathSpecification() {

            @Override
            public OptionalPath path(Bindings bindings) {

                checkNotNull(bindings);

                return new SomePath(path);
            }

            @Override
            public String name() {
                return String.format("by path: %s", path);
            }
        };
    }

    /**
     * A {@link PathSpecification} that uses a {@link Config} to
     * {@link Config#getString(String) get} a path.
     *
     * <p>The {@link PathSpecification#name() name} of the returned
     * path specification is "by key: _", where "_" is substituted
     * by the {@code key} parameter.</p>
     *
     * @param key
     *   The <i>path</i> (in the {@link Config} object) at which to look
     *   for a string representing a <i>path</i> (on the filesystem).
     */
    public static PathSpecification byKey(final String key) {

        checkNotNull(key);

        return new PathSpecification() {

            @Override
            public OptionalPath path(Bindings bindings) {

                checkNotNull(bindings);

                Binding<Config> config = bindings.get(Config.class);

                if (config.isPresent()) {

                    try {

                        return new SomePath(
                            Paths.get(config.get().getString(key))
                        );

                    } catch (ConfigException.Missing ignored) { }
                }

                return NO_PATH;
            }

            @Override
            public String name() {
                return String.format("by key: %s", key);
            }
        };
    }

    private static final class SomePath implements OptionalPath {

        private final Path path;

        private SomePath(Path path) {
            this.path = path;
        }

        @Override
        public Path get() {
            return path;
        }

        @Override
        public boolean isPresent() {
            return true;
        }
    }

    private static final OptionalPath NO_PATH = new OptionalPath() {

        @Override
        public Path get() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isPresent() {
            return false;
        }
    };
}
