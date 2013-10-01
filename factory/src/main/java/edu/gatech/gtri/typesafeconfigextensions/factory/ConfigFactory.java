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
import com.typesafe.config.ConfigParseOptions;
import com.typesafe.config.ConfigResolveOptions;
import edu.gatech.gtri.typesafeconfigextensions.internal.Function;

import java.util.HashMap;
import java.util.List;

import static edu.gatech.gtri.typesafeconfigextensions.internal.Check.checkNotNull;
import static edu.gatech.gtri.typesafeconfigextensions.internal.Check.checkNotNullCollectionElements;
import static edu.gatech.gtri.typesafeconfigextensions.factory.ConfigSourceList.emptyConfigSourceList;
import static edu.gatech.gtri.typesafeconfigextensions.internal.Lists.listOfOne;
import static edu.gatech.gtri.typesafeconfigextensions.internal.Lists.listOfTwoOrMore;

/**
 * An immutable, adaptable factory for {@link Config}s.
 *
 * <p>Start with {@link #emptyConfigFactory()}.</p>
 *
 * <h2>Example usage</h2>
 *
 * <p>This is roughly equivalent to
 *   {@code com.typesafe.config.ConfigFactory.load()}:</p>
 *
 * <pre>{@code
 * emptyConfigFactory().bindDefaults()
 *     .withSources(
 *         classpathResource("reference"),
 *         classpathResource("application"),
 *         systemProperties()
 *     ).fromLowestToHighestPrecedence()
 * }</pre>
 */
public final class ConfigFactory {

    private final ConfigSourceList sources;
    private final HashMapBindings bindings;

    private ConfigFactory(
        ConfigSourceList sources,
        HashMapBindings bindings
    ) {
        this.sources = checkNotNull(sources);
        this.bindings = checkNotNull(bindings);
    }

    static Config emptyConfig() {
        return com.typesafe.config.ConfigFactory.empty();
    }

    private ConfigFactory withSources(ConfigSourceList sources) {
        return new ConfigFactory(checkNotNull(sources), bindings);
    }

    private ConfigFactory withBindings(HashMapBindings bindings) {
        return new ConfigFactory(sources, checkNotNull(bindings));
    }

    private ConfigResolveOptions getResolveOptions() {

        Binding<ConfigResolveOptions> resolveOptionsBinding =
            bindings.get(ConfigResolveOptions.class);

        if (resolveOptionsBinding.isPresent()) {
            return resolveOptionsBinding.get();
        } else {
            return ConfigResolveOptions.defaults();
        }
    }

    /**
     * Evaluates the {@link ConfigSource}s, {@link #bind(Class) bind}s the
     * resulting {@link Config}, then evaluates the {@link ConfigSource}s
     * again.
     *
     * <p>The second evaluation takes place to allow sources to utilize
     *   values produced by other config sources.</p>
     */
    public Config load() {

        ConfigResolveOptions resolveOptions = getResolveOptions();
        HashMapBindings bindings = this.bindings;
        Config config = emptyConfig();

        for (int i = 0; i < 2; i++) {

            config = emptyConfig();

            for (ConfigSource source
                    : sources.fromHighestToLowestPrecedence()) {

                config = config.withFallback(source.load(bindings));
            }

            config = config.resolve(resolveOptions);
            bindings = bindings.set(Config.class, config);
        }

        return config;
    }

    /**
     * Replace all of this factory's {@link ConfigSource}s with
     * {@code newSources}.
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * ConfigFactory factory =
     *     ConfigFactory.emptyConfigFactory().bindDefaults()
     *         .withSources(
     *             ConfigFactory.classpathResource("application"),
     *             ConfigFactory.classpathResource("reference")
     *         ).fromHighestToLowestPrecedence();
     * }</pre>
     */
    public PrecedenceOrderStep<ConfigFactory> withSources(
        NamedConfigSource firstSource,
        NamedConfigSource secondSource,
        NamedConfigSource... moreSources
    ) {
        return withSources(listOfTwoOrMore(
            firstSource,
            secondSource,
            moreSources
        ));
    }

    /**
     * Replace all of this factory's {@link ConfigSource}s with
     * {@code newSources}.
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * ConfigFactory factory(
     *         List<NamedConfigSource> sourceFromHighestToLowestPrecedence) {
     *
     *     return ConfigFactory
     *         .emptyConfigFactory().bindDefaults()
     *         .withSources(sourceFromHighestToLowestPrecedence)
     *         .fromHighestToLowestPrecedence();
     * }}</pre>
     */
    public PrecedenceOrderStep<ConfigFactory>
    withSources(List<NamedConfigSource> newSources) {

        return new BasePrecedenceOrderStep<ConfigFactory, NamedConfigSource>(
            checkNotNullCollectionElements(newSources)
        ) {
            @Override
            ConfigFactory fromHighestToLowestPrecedence(
                List<NamedConfigSource> list
            ) {
                return withSources(
                    emptyConfigSourceList()
                        .insertWithHighestToLowestPrecedence(list)
                        .atBeginning()
                );
            }
        };
    }

    /**
     * Insert an additional {@link ConfigSource} at some point in the list.
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * ConfigFactory factory = ConfigFactory
     *     .emptyConfigFactory().bindDefaults()
     *     .withSources(
     *         ConfigFactory.classpathResource("three"),
     *         ConfigFactory.classpathResource("one")
     *     ).fromHighestToLowestPrecedence();
     *
     *     factory = factory
     *         .insertSource(ConfigFactory.classpathResource("two"))
     *         .withLowerPrecedenceThan("three");
     * }</pre>
     *
     * <p>The resulting factory is equivalent to</p>
     * <pre>{@code
     * ConfigFactory factory = ConfigFactory
     *     .emptyConfigFactory().bindDefaults()
     *     .withSources(
     *         ConfigFactory.classpathResource("three"),
     *         ConfigFactory.classpathResource("two"),
     *         ConfigFactory.classpathResource("one")
     *     ).fromHighestToLowestPrecedence();
     * }</pre>
     *
     * <p>See the methods on {@link ConfigSourceInsertionStep} for the full
     *   set of insertion options.</p>
     */
    public ConfigSourceInsertionStep<ConfigFactory>
    insertSource(NamedConfigSource sourceToInsert) {

        // Precedence order is arbitrary since there is only one element
        // being inserted.
        return insertSources(listOfOne(sourceToInsert))
            .fromHighestToLowestPrecedence();
    }

    /**
     * Insert additional {@link ConfigSource}s at some point in the list.
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * ConfigFactory factory = ConfigFactory
     *     .emptyConfigFactory().bindDefaults()
     *     .withSources(
     *         ConfigFactory.classpathResource("one"),
     *         ConfigFactory.classpathResource("four")
     *     ).fromLowestToHighestPrecedence();
     *
     * factory = factory
     *     .insertSources(Arrays.<NamedConfigSource>asList(
     *         ConfigFactory.classpathResource("three"),
     *         ConfigFactory.classpathResource("two")
     *     ))
     *     .fromHighestToLowestPrecedence()
     *     .withHigherPrecedenceThan(
     *         ConfigFactory.classpathResource("one")
     *     );
     * }</pre>
     *
     * <p>The resulting factory is equivalent to</p>
     * <pre>{@code
     * ConfigFactory factory = ConfigFactory
     *     .emptyConfigFactory().bindDefaults()
     *     .withSources(
     *         ConfigFactory.classpathResource("four")
     *         ConfigFactory.classpathResource("three"),
     *         ConfigFactory.classpathResource("two"),
     *         ConfigFactory.classpathResource("one")
     *     ).fromHighestToLowestPrecedence();
     * }</pre>
     *
     * <p>See the methods on {@link PrecedenceOrderStep} and
     *   {@link ConfigSourceInsertionStep} for the full set of insertion
     *   options.</p>
     */
    public PrecedenceOrderStep<ConfigSourceInsertionStep<ConfigFactory>>
    insertSources(List<NamedConfigSource> sourcesToInsert) {

        return new BasePrecedenceOrderStep<
            ConfigSourceInsertionStep<ConfigFactory>,
            NamedConfigSource
        >(
            checkNotNullCollectionElements(sourcesToInsert)
        ) {
            @Override
            ConfigSourceInsertionStep<ConfigFactory>
            fromHighestToLowestPrecedence(List<NamedConfigSource> list) {

                final ListInsertionStep<
                    ConfigFactory,
                    NamedConfigSource,
                    ConfigSourceName
                > inserting = sources
                    .insertWithHighestToLowestPrecedence(list)
                    .mapContainer(
                        new Function<ConfigSourceList, ConfigFactory>() {

                            @Override
                            public ConfigFactory
                            apply(ConfigSourceList configSources) {

                                return ConfigFactory.this
                                    .withSources(configSources);
                            }
                        }
                    );

                return new ConfigSourceInsertionStep<ConfigFactory>() {

                    @Override
                    public ConfigFactory withHighestPrecedence() {
                        return inserting.atBeginning();
                    }

                    @Override
                    public ConfigFactory withLowestPrecedence() {
                        return inserting.atEnd();
                    }

                    @Override
                    public ConfigFactory
                    withHigherPrecedenceThan(ConfigSourceName x) {

                        return inserting.before(checkNotNull(x));
                    }

                    @Override
                    public ConfigFactory
                    withLowerPrecedenceThan(ConfigSourceName x) {

                        return inserting.after(checkNotNull(x));
                    }

                    @Override
                    public ConfigFactory replacing(ConfigSourceName x) {
                        return inserting.replacing(checkNotNull(x));
                    }
                };
            }
        };
    }

    /**
     * @see #insertSources(List)
     */
    public PrecedenceOrderStep<ConfigSourceInsertionStep<ConfigFactory>>
    insertSources(
        NamedConfigSource firstSourceToInsert,
        NamedConfigSource secondSourceToInsert,
        NamedConfigSource... moreSourcesToInsert
    ) {
        return insertSources(listOfTwoOrMore(
            firstSourceToInsert,
            secondSourceToInsert,
            moreSourcesToInsert
        ));
    }

    public ConfigFactory removeSource(ConfigSourceName nameOfSourceToRemove) {
        return withSources(sources.remove(checkNotNull(nameOfSourceToRemove)));
    }

    /**
     * Add the {@link #defaultBindings() default bindings}.
     */
    public ConfigFactory bindDefaults() {
        return bind(defaultBindings());
    }

    /**
     * Add an additional binding which maps {@code type} to {@code value}.
     *
     * <p>If {@code type} is already bound, the existing binding will be
     *   overridden.</p>
     */
    public <A> BindStep<ConfigFactory, A> bind(final Class<A> type) {

        checkNotNull(type);

        return new BindStep<ConfigFactory, A>() {

            @Override
            public ConfigFactory toInstance(A instance) {

                return withBindings(
                    bindings.set(type, checkNotNull(instance))
                );
            }

            @Override
            public ConfigFactory toNothing() {

                return withBindings(
                    bindings.remove(type)
                );
            }
        };
    }

    /**
     * Add all of the bindings from {@code bindings}.
     */
    public ConfigFactory bind(Bindings bindings) {

        return withBindings(
            this.bindings.add(checkNotNull(bindings))
        );
    }

    /**
     * Binds a {@link ClassLoader} instance.
     *
     * <p>Equivalent to</p>
     * <pre>{@code
     * bind(ClassLoader.class).toInstance(loader)
     * }</pre>
     *
     * @see #bind(Class)
     */
    public ConfigFactory withClassLoader(ClassLoader loader) {

        return bind(ClassLoader.class)
            .toInstance(checkNotNull(loader));
    }

    /**
     * Binds a {@link ConfigParseOptions} instance.
     *
     * <p>Equivalent to</p>
     * <pre>{@code
     * bind(ConfigParseOptions.class).toInstance(parseOptions)
     * }</pre>
     *
     * @see #bind(Class)
     */
    public ConfigFactory withParseOptions(ConfigParseOptions parseOptions) {

        return bind(ConfigParseOptions.class)
            .toInstance(checkNotNull(parseOptions));
    }

    /**
     * Binds a {@link ConfigResolveOptions} instance.
     *
     * <p>Equivalent to</p>
     * <pre>{@code
     * bind(ConfigResolveOptions.class).toInstance(resolveOptions)
     * }</pre>
     *
     * @see #bind(Class)
     */
    public ConfigFactory
    withResolveOptions(ConfigResolveOptions resolveOptions) {

        return bind(ConfigResolveOptions.class)
            .toInstance(checkNotNull(resolveOptions));
    }

    /**
     * A simple {@link ConfigSourceName} instance that can be useful for
     * referring to config sources.
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * // add a config source with lower precedence than one named "apple"
     * ConfigFactory insertLowerThanApple(
     *         ConfigFactory factory,
     *         NameConfigSource sourceToInsert) {
     *
     *     return factory
     *         .insertSource(sourceToInsert)
     *         .withLowerPrecedenceThan(configSourceNamed("apple"))
     * }}</pre>
     */
    public static ConfigSourceName configSourceNamed(final String name) {

        checkNotNull(name);

        return new ConfigSourceName() {

            @Override
            public String toString() {
                return name;
            }

            @Override
            public String name() {
                return name;
            }
        };
    }

    /**
     * A config factory with no config sources and no bindings.
     *
     * <p>{@code emptyConfigFactory().load()} is equivalent to
     * {@link com.typesafe.config.ConfigFactory#empty()}.</p>
     */
    public static ConfigFactory emptyConfigFactory() {
        return new ConfigFactory(emptyConfigSourceList(), noHashMapBindings());
    }

    /**
     * A config source which simply returns the result of
     * {@link com.typesafe.config.ConfigFactory#systemProperties()}.
     *
     * <p>The config source's {@link ConfigSourceName#name() name}
     * is "system properties".</p>
     */
    public static NamedConfigSource systemProperties() {

        return new SystemPropertiesConfigSource()
            .named("system properties");
    }

    /**
     * A config source that loads Config from the classpath using
     * {@code resourceBasename} as the name of the resource.
     *
     * <p>The {@link ClassLoader} and {@link ConfigParseOptions} parameters
     * are retrieved from the {@link Bindings}. If either is not present,
     * we defer to the defaults from the Config library.</p>
     *
     * <p>See
     * {@code com.typesafe.config.ConfigFactory.parseResourcesAnySyntax}
     * for further details.</p>
     */
    public static NamedConfigSource
    classpathResource(final String resourceBasename) {

        checkNotNull(resourceBasename);

        return new ClasspathResourceConfigSource(resourceBasename)
            .named(String.format("classpath: %s", resourceBasename));
    }

    public static FileConfigSourceStep configFile() {

        return new BaseFileConfigSourceStep() {

            @Override
            public NamedConfigSource by(PathSpecification pathSpecification) {

                checkNotNull(pathSpecification);

                return new FileConfigSource(pathSpecification)
                    .named(String.format("file %s", pathSpecification.name()));
            }
        };
    }

    /**
     * A config source that loads Config by parsing a string.
     *
     * <p>The {@link ConfigParseOptions} parameter is retrieved from the
     * {@link Bindings}. If it is not present, we defer to a default from
     * the Config library.</p>
     *
     * <p>See {@code com.typesafe.config.ConfigFactory.parseString}
     * for further details.</p>
     */
    public static ConfigSource configString(final String configString) {

        checkNotNull(configString);

        return new StringConfigSource(configString);
    }

    private static HashMapBindings noHashMapBindings() {
        return new HashMapBindings(new HashMap<Class<?>, Object>());
    }

    public static Bindings noBindings() {
        return noHashMapBindings();
    }

    /**
     * Some default bindings that most config factories will need.
     *
     * <ul>
     *   <li>
     *     {@link ClassLoader} &rarr;
     *     {@link Thread#currentThread()}.{@link Thread#getContextClassLoader()
     *       getContextClassLoader()}
     *   </li>
     *   <li>
     *     {@link com.typesafe.config.ConfigParseOptions} &rarr;
     *     {@link com.typesafe.config.ConfigParseOptions#defaults()}
     *   </li>
     *   <li>
     *     {@link com.typesafe.config.ConfigResolveOptions} &rarr;
     *     {@link com.typesafe.config.ConfigParseOptions#defaults()}
     *   </li>
     *   <li>
     *     {@link com.typesafe.config.Config} &rarr;
     *     {@link com.typesafe.config.ConfigFactory#empty()
     *       ConfigFactory.empty()}
     *   </li>
     * </ul>
     */
    public static Bindings defaultBindings() {

        return noHashMapBindings()
            .set(
                ClassLoader.class,
                Thread.currentThread().getContextClassLoader()
            )
            .set(
                ConfigParseOptions.class,
                ConfigParseOptions.defaults()
            )
            .set(
                ConfigResolveOptions.class,
                ConfigResolveOptions.defaults()
            )
            .set(
                Config.class,
                emptyConfig()
            );
    }
}
