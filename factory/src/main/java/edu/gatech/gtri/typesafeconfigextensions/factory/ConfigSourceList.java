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

import edu.gatech.gtri.typesafeconfigextensions.internal.Function;
import edu.gatech.gtri.typesafeconfigextensions.internal.equivalence.EqualsEquivalence;
import edu.gatech.gtri.typesafeconfigextensions.internal.equivalence.Equivalence;

import java.util.ArrayList;
import java.util.List;

import static edu.gatech.gtri.typesafeconfigextensions.internal.Check.checkNotNull;
import static edu.gatech.gtri.typesafeconfigextensions.internal.Check.checkNotNullCollectionElements;

final class ConfigSourceList {

    static ConfigSourceList emptyConfigSourceList() {
        return new ConfigSourceList();
    }

    // ordered from highest to lowest precedence
    private final List<NamedConfigSource> sources;

    private ConfigSourceList() {
        sources = new ArrayList<>();
    }

    private ConfigSourceList(List<NamedConfigSource> sources) {
        this.sources = checkNotNull(sources);
    }

    ListInsertionStep<ConfigSourceList, NamedConfigSource, ConfigSourceName>
    insertWithHighestToLowestPrecedence(
        final List<NamedConfigSource> sourcesToInsert
    ) {
        checkNotNullCollectionElements(sourcesToInsert);

        return new ListInsertionStep<>(
            new ListInsertionStepStrategy<
                ConfigSourceList,
                NamedConfigSource,
                ConfigSourceName
            >() {

                @Override
                public List<NamedConfigSource> currentElements() {
                    return sources;
                }

                @Override
                public List<NamedConfigSource> elementsToInsert() {
                    return sourcesToInsert;
                }

                @Override
                public ConfigSourceList wrap(List<NamedConfigSource> list) {
                    return new ConfigSourceList(list);
                }

                @Override
                public ConfigSourceName
                identifierFor(NamedConfigSource configSource) {

                    return configSource;
                }

                @Override
                public Equivalence<ConfigSourceName> identifierEquivalence() {
                    return NAME_EQUIVALENCE;
                }
            }
        );
    }

    ConfigSourceList remove(ConfigSourceName nameOfSourceToRemove) {

        checkNotNull(nameOfSourceToRemove);

        List<NamedConfigSource> filtered = new ArrayList<>();

        for (NamedConfigSource source : sources) {
            if (!NAME_EQUIVALENCE.equals(source, nameOfSourceToRemove)) {
                filtered.add(source);
            }
        }

        return new ConfigSourceList(filtered);
    }

    private static final Equivalence<ConfigSourceName> NAME_EQUIVALENCE =
        new EqualsEquivalence<String>().map(
            new Function<ConfigSourceName, String>() {

                @Override
                public String apply(ConfigSourceName name) {
                    return name.name();
                }
            }
        );

    Iterable<NamedConfigSource> fromHighestToLowestPrecedence() {
        return sources;
    }
}
