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
import com.typesafe.config.ConfigParseOptions;
import com.typesafe.config.ConfigValue;
import edu.gatech.gtri.typesafeconfigextensions.factory.Binding;
import edu.gatech.gtri.typesafeconfigextensions.factory.Bindings;
import edu.gatech.gtri.typesafeconfigextensions.internal.Function;
import edu.gatech.gtri.typesafeconfigextensions.jndi.JndiContext;

import java.util.List;

import static edu.gatech.gtri.typesafeconfigextensions.forwebapps.JndiConfigSource.PathMapping.jndiPathMapping;
import static edu.gatech.gtri.typesafeconfigextensions.internal.Check.checkNotNull;
import static edu.gatech.gtri.typesafeconfigextensions.internal.Lists.listConcat;
import static edu.gatech.gtri.typesafeconfigextensions.internal.Lists.listOfOne;
import static edu.gatech.gtri.typesafeconfigextensions.internal.Lists.listOfTwoOrMore;
import static edu.gatech.gtri.typesafeconfigextensions.jndi.JndiContexts.jndiContext;
import static java.util.Arrays.asList;

final class JndiConfigSourceImpl
implements JndiConfigSource {

    private final List<PathMapping> mappings;

    private final Function<? super Bindings, ? extends Config>
        jndiConfigSupplier;

    private final String name;

    static final List<PathMapping> DEFAULT_MAPPINGS = asList();

    static final Function<Bindings, Config> DEFAULT_JNDI_CONFIG_SUPPLIER =
        new Function<Bindings, Config>() {

            @Override
            public Config apply(Bindings bindings) {

                checkNotNull(bindings);

                JndiContext context = jndiContext();

                Binding<ConfigParseOptions> parseOptionsBinding =
                    bindings.get(ConfigParseOptions.class);

                if (parseOptionsBinding.isPresent()) {
                    context = context
                        .withParseOptions(parseOptionsBinding.get());
                }

                return context.toConfig();
            }
        };

    static final String DEFAULT_NAME = "jndi";

    static JndiConfigSourceImpl defaultJndiConfigSource() {

        return new JndiConfigSourceImpl(
            DEFAULT_MAPPINGS,
            DEFAULT_JNDI_CONFIG_SUPPLIER,
            DEFAULT_NAME
        );
    }

    JndiConfigSourceImpl withSupplier(
        Function<? super Bindings, ? extends Config> jndiConfigSupplier
    ) {
        return new JndiConfigSourceImpl(
            mappings,
            checkNotNull(jndiConfigSupplier),
            name
        );
    }

    JndiConfigSourceImpl(
        List<PathMapping> mappings,
        Function<? super Bindings, ? extends Config> jndiConfigSupplier,
        String name
    ) {
        this.mappings = checkNotNull(mappings);
        this.jndiConfigSupplier = checkNotNull(jndiConfigSupplier);
        this.name = checkNotNull(name);
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public Config load(Bindings bindings) {

        checkNotNull(bindings);

        Config jndiConfig = jndiConfigSupplier.apply(bindings);
        Config config = com.typesafe.config.ConfigFactory.empty();

        for (PathMapping mapping : mappings) {

            ConfigValue value = jndiConfig.root().get(mapping.fromJndiPath());

            if (value != null) {
                config = config.withValue(mapping.toRootPath(), value);
            }
        }

        return config.withFallback(jndiConfig.atPath("jndi"));
    }

    @Override
    public JndiConfigSource mapPath(String fromJndiPath, String toRootPath) {

        return withPathMappings(listConcat(
            mappings,
            listOfOne(jndiPathMapping(
                checkNotNull(fromJndiPath),
                checkNotNull(toRootPath)
            ))
        ));
    }

    @Override
    public JndiConfigSource mapPath(PathMapping pathMapping) {

        checkNotNull(pathMapping);

        return mapPath(pathMapping.fromJndiPath(), pathMapping.toRootPath());
    }

    @Override
    public JndiConfigSource withPathMapping(PathMapping onlyPathMapping) {
        return withPathMappings(listOfOne(onlyPathMapping));
    }

    @Override
    public JndiConfigSource withPathMappings(
        PathMapping firstPathMapping,
        PathMapping secondPathMapping,
        PathMapping... morePathMappings
    ) {
        return withPathMappings(listOfTwoOrMore(
            firstPathMapping,
            secondPathMapping,
            morePathMappings
        ));
    }

    @Override
    public JndiConfigSource withPathMappings(List<PathMapping> pathMappings) {

        return new JndiConfigSourceImpl(
            checkNotNull(pathMappings),
            jndiConfigSupplier,
            name
        );
    }

    @Override
    public JndiConfigSource named(String name) {

        return new JndiConfigSourceImpl(
            mappings,
            jndiConfigSupplier,
            checkNotNull(name)
        );
    }

    @Override
    public String toString() {
        return String.format("ConfigSource { %s }", name);
    }
}
