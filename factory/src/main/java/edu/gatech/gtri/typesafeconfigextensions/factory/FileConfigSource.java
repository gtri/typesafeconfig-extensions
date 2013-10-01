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

import java.nio.file.Path;

import static edu.gatech.gtri.typesafeconfigextensions.internal.Check.checkNotNull;
import static edu.gatech.gtri.typesafeconfigextensions.factory.ConfigFactory.emptyConfig;

final class FileConfigSource
extends BaseConfigSource {

    private final PathSpecification pathSpecification;

    FileConfigSource(PathSpecification pathSpecification) {
        this.pathSpecification = checkNotNull(pathSpecification);
    }

    @Override
    public Config load(Bindings bindings) {

        checkNotNull(bindings);

        OptionalPath path = pathSpecification.path(bindings);

        if (path.isPresent()) {
            return parse(path.get(), bindings);
        } else {
            return emptyConfig();
        }
    }

    private Config parse(Path path, Bindings bindings) {

        return com.typesafe.config.ConfigFactory.parseFileAnySyntax(
            path.toFile(),
            configParseOptions(bindings)
        );
    }

    private ConfigParseOptions configParseOptions(Bindings bindings) {

        Binding<ConfigParseOptions> configParseOptionsBinding =
            bindings.get(ConfigParseOptions.class);

        if (configParseOptionsBinding.isPresent()) {
            return configParseOptionsBinding.get();
        } else {
            return ConfigParseOptions.defaults();
        }
    }

    @Override
    public String toString() {

        return String.format(
            "ConfigSource { file %s }",
            pathSpecification.name()
        );
    }
}
