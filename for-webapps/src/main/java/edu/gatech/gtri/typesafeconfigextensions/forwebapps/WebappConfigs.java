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

import edu.gatech.gtri.typesafeconfigextensions.factory.BaseFileConfigSourceStep;
import edu.gatech.gtri.typesafeconfigextensions.factory.Bindings;
import edu.gatech.gtri.typesafeconfigextensions.factory.ConfigFactory;
import edu.gatech.gtri.typesafeconfigextensions.factory.FileConfigSourceStep;
import edu.gatech.gtri.typesafeconfigextensions.factory.NamedConfigSource;
import edu.gatech.gtri.typesafeconfigextensions.factory.OptionalPath;
import edu.gatech.gtri.typesafeconfigextensions.factory.PathSpecification;

import javax.servlet.ServletContext;

import static edu.gatech.gtri.typesafeconfigextensions.forwebapps.JndiConfigSourceImpl.defaultJndiConfigSource;
import static edu.gatech.gtri.typesafeconfigextensions.internal.Check.checkNotNull;

/**
 * The expected typical usage of this class is to simply invoke
 * {@link #webappConfigFactory(javax.servlet.ServletContext)}.
 */
public final class WebappConfigs {

    private WebappConfigs() { }

    /**
     * A sensible default configuration factory for a web application.
     *
     * <p>Config sources, in order from highest to lowest precedence:</p>
     * <ul>
     *   <li>System properties</li>
     *   <li>File:
     *     {@code JNDI(webapp.config.directory)/[servlet context path]}</li>
     *   <li>File:
     *     {@code ${webapp.config.directory}/[servlet context path]}</li>
     *   <li>File: {@code JNDI(webapp.config.file)}</li>
     *   <li>File: {@code ${webapp.config.file}}</li>
     *   <li>Classpath resource: {@code application.conf}</li>
     *   <li>Classpath resource: {@code resource.conf}</li>
     * </ul>
     */
    public static ConfigFactory
    webappConfigFactory(ServletContext servletContext) {

        checkNotNull(servletContext);

        return webappConfigFactory()
            .bind(ServletContextPath.class)
            .toInstance(ServletContextPath.fromServletContext(servletContext));
    }

    /**
     * Equivalent to
     * <pre>{@code webappConfigFactory()
     *     .bind(ServletContextPath.class, servletContextPath)
     * }</pre>.
     *
     * @see #webappConfigFactory()
     */
    public static ConfigFactory
    webappConfigFactory(ServletContextPath servletContextPath) {

        checkNotNull(servletContextPath);

        return webappConfigFactory()
            .bind(ServletContextPath.class)
            .toInstance(servletContextPath);
    }

    public static ConfigFactory webappConfigFactory() {

        return ConfigFactory.emptyConfigFactory()
            .bindDefaults()
            .withSources(
                jndi(),
                ConfigFactory.systemProperties(),
                servletContextDirectory().byKey("jndi.webapp.config.directory"),
                servletContextDirectory().byKey("webapp.config.directory"),
                ConfigFactory.configFile().byKey("jndi.webapp.config.file"),
                ConfigFactory.configFile().byKey("webapp.config.file"),
                ConfigFactory.classpathResource("application"),
                ConfigFactory.classpathResource("reference")
            ).fromHighestToLowestPrecedence();
    }

    /**
     * Config that contains {@code jndi.x = y} for each JNDI name
     * {@code java:comp/env/x} mapped to value {@code y}.
     */
    public static JndiConfigSource jndi() {
        return defaultJndiConfigSource();
    }

    /**
     * Config loaded from a file located at
     * {@code [path]/[servlet context path]}.
     *
     * <ul>
     *   <li>{@code [path]} is determined by a {@link PathSpecification}</li>
     *   <li>{@code [servlet context path]} is the
     *     {@link ServletContextPath} from the {@link ConfigFactory}'s
     *     {@link Bindings}.</li>
     * </ul>
     *
     * <p>The config will be empty if any of the following are true:</p>
     *
     * <ul>
     *   <li>If {@link PathSpecification#path(Bindings)}
     *     .{@link OptionalPath#isPresent() isPresent()}
     *     is {@code false}</li>
     *   <li>If no {@link ServletContextPath} is bound</li>
     *   <li>If no config file exists at
     *     {@code [path]/[servlet context path]}</li>
     * </ul>
     */
    public static FileConfigSourceStep servletContextDirectory() {

        return new BaseFileConfigSourceStep() {

            @Override
            public NamedConfigSource by(PathSpecification pathSpecification) {

                checkNotNull(pathSpecification);

                return
                    new ServletContextDirectoryConfigSource(pathSpecification)
                    .named(
                        String.format(
                            "servlet context directory %s",
                            pathSpecification.name()
                        )
                    );
            }
        };
    }
}
