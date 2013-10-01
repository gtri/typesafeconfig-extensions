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

import static edu.gatech.gtri.typesafeconfigextensions.internal.Check.checkNotNull;
import static edu.gatech.gtri.typesafeconfigextensions.jndi.JndiContexts.noContext;

final class NoContext extends BaseContext {

    private final OptionalJndiOriginDescription originDescription;
    private final ConfigParseOptions parseOptions;

    NoContext(
        OptionalJndiOriginDescription originDescription,
        ConfigParseOptions parseOptions
    ) {
        this.originDescription = checkNotNull(originDescription);
        this.parseOptions = checkNotNull(parseOptions);
    }

    @Override
    public JndiContext getContext(String path) {
        return noContext();
    }

    @Override
    public Config toConfig() {

        if (originDescription.isPresent()) {
            return ConfigFactory.empty(originDescription.get());
        }

        {
            String str = parseOptions.getOriginDescription();

            if (str != null) {
                return ConfigFactory.empty(str);
            }
        }

        return ConfigFactory.empty();
    }

    @Override
    public JndiContext
    withOriginDescription(OptionalJndiOriginDescription originDescription) {

        return new NoContext(
            checkNotNull(originDescription),
            parseOptions
        );
    }

    @Override
    public JndiContext withParseOptions(ConfigParseOptions parseOptions) {

        return new NoContext(
            originDescription,
            checkNotNull(parseOptions)
        );
    }
}
