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

import static com.typesafe.config.ConfigFactory.parseString;
import static edu.gatech.gtri.typesafeconfigextensions.internal.Check.checkNotNull;

final class StringConfigSource
extends BaseConfigSource {

    private final String configString;

    StringConfigSource(String configString) {
        this.configString = checkNotNull(configString);
    }

    @Override
    public Config load(Bindings bindings) {

        checkNotNull(bindings);

        Binding<ConfigParseOptions> parseOptions =
            bindings.get(ConfigParseOptions.class);

        if (parseOptions.isPresent()) {
            return parseString(configString, parseOptions.get());
        }

        return parseString(configString);
    }

    @Override
    public String toString() {
        return String.format(
            "ConfigSource { string: %s }",
            ellipsize(configString)
        );
    }

    private static final int MAX_TOSTRING_LENGTH = 40;

    private static String ellipsize(String str) {

        String ellipsis = " ...";

        if (str.length() > MAX_TOSTRING_LENGTH) {
            return str.substring(0, MAX_TOSTRING_LENGTH - ellipsis.length())
                + ellipsis;
        } else {
            return str;
        }
    }
}
