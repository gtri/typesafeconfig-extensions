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
import com.typesafe.config.ConfigParseOptions;

import static com.typesafe.config.ConfigFactory.parseResourcesAnySyntax;
import static edu.gatech.gtri.typesafeconfigextensions.internal.Check.checkNotNull;

final class ClasspathResourceConfigSource
extends BaseConfigSource {

    private final String resourceBasename;

    ClasspathResourceConfigSource(String resourceBasename) {
        this.resourceBasename = checkNotNull(resourceBasename);
    }

    @Override
    public Config load(Bindings bindings) {

        checkNotNull(bindings);

        Binding<ClassLoader> loader = bindings.get(ClassLoader.class);

        Binding<ConfigParseOptions> parseOptions =
            bindings.get(ConfigParseOptions.class);

        if (loader.isPresent() && parseOptions.isPresent()) {

            return parseResourcesAnySyntax(
                loader.get(),
                resourceBasename,
                parseOptions.get()
            );
        }

        if (loader.isPresent()) {

            return parseResourcesAnySyntax(
                loader.get(),
                resourceBasename
            );
        }

        if (parseOptions.isPresent()) {

            return parseResourcesAnySyntax(
                resourceBasename,
                parseOptions.get()
            );
        }

        return parseResourcesAnySyntax(resourceBasename);
    }

    @Override
    public String toString() {

        return String.format(
            "ConfigSource { classpath: %s }",
            resourceBasename
        );
    }
}
