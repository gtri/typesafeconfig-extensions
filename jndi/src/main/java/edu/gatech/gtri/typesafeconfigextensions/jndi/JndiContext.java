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
import com.typesafe.config.ConfigParseOptions;

/**
 * Wraps {@link javax.naming.Context JNDI Context} in a friendlier
 * interface.
 *
 * @see #toConfig()
 * @see #getContext(String)
 */
public interface JndiContext {

    JndiContext getContext(String path);

    Config toConfig();

    /**
     * An origin description which will override the one specified by
     * {@link #withParseOptions(com.typesafe.config.ConfigParseOptions)
     * the parse options}
     * when converting this context {@link #toConfig() to Config}.
     *
     * @see #withOriginDescription(String)
     */
    JndiContext withOriginDescription(
        OptionalJndiOriginDescription originDescription
    );

    /**
     * Equivalent to
     * <pre>{@code
     * withOriginDescription (
     *     OptionalJndiOriginDescription.originDescription(originDescription)
     * )
     * }</pre>.
     *
     * @see #withOriginDescription(OptionalJndiOriginDescription)
     * @see OptionalJndiOriginDescription#originDescription(String)
     */
    JndiContext withOriginDescription(String originDescription);

    JndiContext withParseOptions(ConfigParseOptions parseOptions);
}
