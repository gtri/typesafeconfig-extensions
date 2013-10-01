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
 * <h1>Typesafe Config Extension: For Webapps</h1>
 *
 * <p>This project provides a sensible default configuration for using
 * Typesafe Config in a servlet container.</p>
 *
 * <p>The main class in this project's API is
 * {@link edu.gatech.gtri.typesafeconfigextensions.forwebapps.WebappConfigs
 * WebappConfigs}.</p>
 *
 * <h2>Objectives</h2>
 *
 * <h3>Config from the filesystem</h3>
 *
 * <p>An application should be able to load config from the filesystem at
 * runtime so WAR deployments can be reconfigured without modifying the
 * WAR.</p>
 *
 * <p>An application should be able to reload config files from the filesystem
 * after server has started. This project does not explicitly support this goal,
 * but it should do nothing that might preclude it.</p>
 *
 * <h3>Config strings and config file paths via JNDI</h3>
 *
 * <p>Someone who deploys multiple applications within the same servlet
 * container should be able to use JNDI environment values to separately
 * configure each context.</p>
 *
 * <p>You should be able to write config directly into a JNDI value or
 * to the filesystem.</p>
 *
 * <h3>Config file paths based on servlet paths</h3>
 *
 * <p>Someone who deploys applications to Tomcat should be able to put
 * config files in {@code [catalina base]/conf/[servlet context path]}.
 * This project does not contain anything Tomcat-specific, but a Tomcat
 * user should be able to accomplish this by referring to
 * {@code ${catalina.base}}.</p>
 *
 * <p>This will require setting a config value to explicitly enable this
 * feature. It shouldn't be enabled by default, because a webapp launched
 * from a build tool rather than by deploying a WAR to a Tomcat installation
 * shouldn't read its config from a Catalina conf directory.</p>
 *
 * <h3>Config file paths via system properties</h3>
 *
 * <p>Someone who launches a servlet container from a build tool should be
 * able to specify the location of config files by setting a system
 * property.</p>
 */
package edu.gatech.gtri.typesafeconfigextensions.forwebapps;
