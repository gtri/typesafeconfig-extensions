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

import static edu.gatech.gtri.typesafeconfigextensions.internal.Check.checkNotNull;

final class SimpleNamedConfigSource
implements NamedConfigSource {

    private final ConfigSource configSource;
    private final String name;

    SimpleNamedConfigSource(ConfigSource configSource, String name) {

        this.configSource = checkNotNull(configSource);
        this.name = checkNotNull(name);
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public NamedConfigSource named(String name) {
        return new SimpleNamedConfigSource(configSource, checkNotNull(name));
    }

    @Override
    public Config load(Bindings bindings) {
        return configSource.load(checkNotNull(bindings));
    }

    @Override
    public String toString() {
        return String.format("ConfigSource { %s }", name);
    }
}
