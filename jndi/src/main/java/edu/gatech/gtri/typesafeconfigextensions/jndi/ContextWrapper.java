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

package edu.gatech.gtri.typesafeconfigextensions.jndi;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigParseOptions;
import com.typesafe.config.ConfigValue;
import edu.gatech.gtri.typesafeconfigextensions.internal.Option;

import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static edu.gatech.gtri.typesafeconfigextensions.internal.Check.checkNotNull;
import static edu.gatech.gtri.typesafeconfigextensions.jndi.JndiContexts.noContext;
import static edu.gatech.gtri.typesafeconfigextensions.internal.Option.none;
import static edu.gatech.gtri.typesafeconfigextensions.internal.Option.some;
import static edu.gatech.gtri.typesafeconfigextensions.jndi.OptionalJndiOriginDescription.noOriginDescription;

final class ContextWrapper
extends BaseContext {

    private final javax.naming.Context jndiContext;
    private final OptionalJndiOriginDescription originDescription;
    private final ConfigParseOptions parseOptions;

    ContextWrapper(javax.naming.Context jndiContext) {

        this(
            checkNotNull(jndiContext),
            noOriginDescription(),
            ConfigParseOptions.defaults()
        );
    }

    ContextWrapper(
        javax.naming.Context jndiContext,
        OptionalJndiOriginDescription originDescription,
        ConfigParseOptions parseOptions
    ) {
        this.jndiContext = checkNotNull(jndiContext);
        this.originDescription = checkNotNull(originDescription);
        this.parseOptions = checkNotNull(parseOptions);
    }

    @Override
    public JndiContext
    withOriginDescription(OptionalJndiOriginDescription originDescription) {

        return new ContextWrapper(
            jndiContext,
            checkNotNull(originDescription),
            parseOptions
        );
    }

    @Override
    public JndiContext withParseOptions(ConfigParseOptions parseOptions) {

        return new ContextWrapper(
            jndiContext,
            originDescription,
            checkNotNull(parseOptions)
        );
    }

    private JndiContext withJndiContext(javax.naming.Context jndiContext) {

        return new ContextWrapper(
            checkNotNull(jndiContext),
            originDescription,
            parseOptions
        );
    }

    private JndiContext withNoJndiContext() {

        return noContext()
            .withOriginDescription(originDescription)
            .withParseOptions(parseOptions);
    }

    List<String> names() {

        try {

            NamingEnumeration<NameClassPair> enumeration =
                jndiContext.list("");

            try {

                List<String> names = new ArrayList<>();

                while (enumeration.hasMore()) {

                    try {
                        names.add(enumeration.next().getName());
                    } catch (NamingException ignored) { }
                }
                return names;
            } finally {
                enumeration.close();
            }
        } catch (NamingException e) {
            return Arrays.asList();
        }
    }

    @Override
    public JndiContext getContext(String path) {

        checkNotNull(path);

        Object value;

        try {
            value = jndiContext.lookup(path);
        } catch (NamingException e) {
            return withNoJndiContext();
        }

        if (value instanceof javax.naming.Context) {
            return withJndiContext((javax.naming.Context) value);
        } else {
            return withNoJndiContext();
        }
    }

    @Override
    public Config toConfig() {

        Config config = emptyConfig();

        for (String name : names()) {

            try {

                Option<? extends ConfigValue> configValue =
                    configValue(jndiContext.lookup(name));

                if (configValue.isSome()) {
                    config = config.withValue(name, configValue.get());
                }
            } catch (NamingException ignored) { }
        }

        return config;
    }

    Option<? extends ConfigValue> configValue(Object object) {

        if (object instanceof javax.naming.Context) {

            javax.naming.Context jndiContext = (javax.naming.Context) object;
            return some(withJndiContext(jndiContext).toConfig().root());
        }

        if (object instanceof String) {

            try {

                return some(
                    ConfigFactory.parseString(
                        String.format("value: %s", (String) object),
                        getParseOptions()
                    ).root().get("value")
                );
            } catch (Exception ignored) { }
        }

        return none();
    }

    ConfigParseOptions getParseOptions() {

        if (originDescription.isPresent()) {
            return parseOptions.setOriginDescription(originDescription.get());
        } else {
            return parseOptions;
        }
    }

    Config emptyConfig() {
        return withNoJndiContext().toConfig();
    }
}
