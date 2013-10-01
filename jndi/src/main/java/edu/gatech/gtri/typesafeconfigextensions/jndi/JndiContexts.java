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

import com.typesafe.config.ConfigParseOptions;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import static edu.gatech.gtri.typesafeconfigextensions.internal.Check.checkNotNull;
import static edu.gatech.gtri.typesafeconfigextensions.jndi.OptionalJndiOriginDescription.noOriginDescription;

/**
 * Static methods that provide {@link JndiContext} instances.
 */
public final class JndiContexts {

    private JndiContexts() { }

    /**
     * Invoking {@link JndiContext#toConfig() toConfig()} on this
     * {@code JndiContext} will create a
     * {@link com.typesafe.config.Config} that contains
     * {@code jndi.x = y} for each JNDI name
     * {@code java:comp/env/x} mapped to value {@code y}.
     */
    public static JndiContext jndiContext() {

        return JndiContexts.initialContext()
            .getContext("java:comp/env")
            .withOriginDescription("JNDI");
    }

    public static JndiContext context(javax.naming.Context context) {
        return new ContextWrapper(checkNotNull(context));
    }

    public static JndiContext initialContext() {

        try {
            return context(new InitialContext());
        } catch (NamingException e) {
            return noContext();
        }
    }

    public static JndiContext noContext() {

        return new NoContext(
            noOriginDescription(),
            ConfigParseOptions.defaults()
        );
    }
}
