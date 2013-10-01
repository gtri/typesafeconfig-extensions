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

import edu.gatech.gtri.typesafeconfigextensions.internal.Option;

import static edu.gatech.gtri.typesafeconfigextensions.internal.Check.checkNotNull;
import static edu.gatech.gtri.typesafeconfigextensions.internal.Option.some;

/**
 * An origin description which will override the one specified by
 * the {@link com.typesafe.config.ConfigParseOptions parse options}
 * when converting a {@link JndiContext}
 * {@link JndiContext#toConfig() to Config}.
 *
 * @see JndiContext#withOriginDescription(OptionalJndiOriginDescription)
 */
public final class OptionalJndiOriginDescription {

    public static OptionalJndiOriginDescription
    originDescription(String originDescription) {

        return new OptionalJndiOriginDescription(
            some(checkNotNull(originDescription))
        );
    }

    public static OptionalJndiOriginDescription noOriginDescription() {
        return new OptionalJndiOriginDescription(Option.<String>none());
    }

    private final Option<String> originDescription;

    private OptionalJndiOriginDescription(Option<String> originDescription) {
        this.originDescription = checkNotNull(originDescription);
    }

    public boolean isPresent() {
        return originDescription.isSome();
    }

    /**
     * @throws UnsupportedOperationException
     *   if {@link #isPresent()} is {@code false}.
     */
    public String get() {
        return originDescription.get();
    }
}
