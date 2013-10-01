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

import com.typesafe.config.Config;
import edu.gatech.gtri.typesafeconfigextensions.factory.BaseConfigSource;
import edu.gatech.gtri.typesafeconfigextensions.factory.Binding;
import edu.gatech.gtri.typesafeconfigextensions.factory.Bindings;
import edu.gatech.gtri.typesafeconfigextensions.factory.ConfigFactory;
import edu.gatech.gtri.typesafeconfigextensions.factory.OptionalPath;
import edu.gatech.gtri.typesafeconfigextensions.factory.PathSpecification;

import java.nio.file.Path;

import static edu.gatech.gtri.typesafeconfigextensions.internal.Check.checkNotNull;

final class ServletContextDirectoryConfigSource
extends BaseConfigSource {

    private final PathSpecification pathSpecification;

    ServletContextDirectoryConfigSource(PathSpecification pathSpecification) {
        this.pathSpecification = checkNotNull(pathSpecification);
    }

    @Override
    public Config load(Bindings bindings) {

        checkNotNull(bindings);

        Path path;
        ServletContextPath servletContextPath;

        {
            OptionalPath optionalPath = pathSpecification.path(bindings);

            if (optionalPath.isPresent()) {
                path = optionalPath.get();
            } else {
                return com.typesafe.config.ConfigFactory.empty();
            }
        }

        {
            Binding<ServletContextPath> servletContextPathBinding =
                bindings.get(ServletContextPath.class);

            if (servletContextPathBinding.isPresent()) {
                servletContextPath = servletContextPathBinding.get();
            } else {
                return com.typesafe.config.ConfigFactory.empty();
            }
        }

        return load(path, servletContextPath, bindings);
    }

    private Config load(
        Path basePath,
        ServletContextPath servletContextPath,
        Bindings bindings
    ) {
        Path path = basePath;

        for (String pathComponent : servletContextPath.toList()) {
            path = path.resolve(pathComponent);
        }

        return ConfigFactory.configFile().byPath(path).load(bindings);
    }

    @Override
    public String toString() {

        return String.format(
            "ConfigSource { servlet context relative to directory %s }",
            pathSpecification.name()
        );
    }
}
