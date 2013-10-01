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

/**
 * <h1>Typesafe Config Extension: JNDI</h1>
 *
 * <p>This project provides interoperability between Typesafe Config
 * and JNDI ({@link javax.naming}).</p>
 *
 * <h2>Example usage</h2>
 *
 * <h3>Converting from {@code javax.naming.Context}
 * to {@code com.typesafe.config.Config}</h3>
 *
 * <pre>{@code
 * import javax.naming.Context;
 * import static edu.gatech.gtri
 *     .typesafeconfigextensions.jndi.JndiContexts.context;
 * }</pre>
 * <pre>{@code
 * Context context;
 * }</pre>
 * <pre>{@code
 * Config config = context(context).toConfig();
 * }</pre>
 *
 * <h3>Retrieving a JNDI variable</h3>
 *
 * <pre>{@code
 * import static edu.gatech.gtri
 *     .typesafeconfigextensions.jndi.JndiContexts.jndiContext;
 * }</pre>
 * <pre>{@code
 * String configValue = jndiContext().toConfig().getString("someJndiName");
 * }</pre>
 */
package edu.gatech.gtri.typesafeconfigextensions.jndi;
