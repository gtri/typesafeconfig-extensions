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
import edu.gatech.gtri.typesafeconfigextensions.internal.equivalence.Equivalence;
import edu.gatech.gtri.typesafeconfigextensions.internal.equivalence.EquivalenceWrapper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static edu.gatech.gtri.typesafeconfigextensions.internal.Check.checkNotNull;
import static edu.gatech.gtri.typesafeconfigextensions.internal.Lists.listConcat;

/**
 * @param <R> The return type for all of the {@link ListInsertionStep} methods.
 * @param <E> The type of element in the list.
 * @param <I> The type of identifier for list elements.
 */
final class ListInsertionStep<R, E, I> {

    private final ListInsertionStepStrategy<R, E, I> strategy;

    ListInsertionStep(ListInsertionStepStrategy<R, E, I> strategy) {
        this.strategy = checkNotNull(strategy);
    }

    R atBeginning() {

        return result(
            listConcat(
                strategy.elementsToInsert(),
                strategy.currentElements()
            )
        );
    }

    R atEnd() {

        return result(
            listConcat(
                strategy.currentElements(),
                strategy.elementsToInsert()
            )
        );
    }

    R before(I relativeToElementIdentifier) {

        checkNotNull(relativeToElementIdentifier);

        boolean inserted = false;

        List<E> list = new ArrayList<>();

        for (E element : strategy.currentElements()) {

            if (strategy.identifierEquivalence().equals(
                strategy.identifierFor(element),
                relativeToElementIdentifier
            )) {
                list.addAll(strategy.elementsToInsert());
                inserted = true;
            }

            list.add(element);
        }

        if (!inserted) {
            throw noSuchElementIdentifier(relativeToElementIdentifier);
        }

        return result(list);
    }

    R after(I relativeToElementIdentifier) {

        checkNotNull(relativeToElementIdentifier);

        boolean inserted = false;

        List<E> list = new ArrayList<>();

        for (E element : strategy.currentElements()) {

            list.add(element);

            if (strategy.identifierEquivalence().equals(
                strategy.identifierFor(element),
                relativeToElementIdentifier
            )) {
                list.addAll(strategy.elementsToInsert());
                inserted = true;
            }
        }

        if (!inserted) {
            throw noSuchElementIdentifier(relativeToElementIdentifier);
        }

        return result(list);
    }

    R replacing(I elementIdentifierToRemove) {

        checkNotNull(elementIdentifierToRemove);

        boolean inserted = false;

        List<E> list = new ArrayList<>();

        for (E element : strategy.currentElements()) {

            if (strategy.identifierEquivalence().equals(
                strategy.identifierFor(element),
                elementIdentifierToRemove
            )) {
                list.addAll(strategy.elementsToInsert());
                inserted = true;
            } else {
                list.add(element);
            }
        }

        if (!inserted) {
            throw noSuchElementIdentifier(elementIdentifierToRemove);
        }

        return result(list);
    }

    <Container2> ListInsertionStep<Container2, E, I> mapContainer(
        final Function<R, Container2> f
    ) {
        return new ListInsertionStep<>(
            new ListInsertionStepStrategy<Container2, E, I>() {

                @Override
                public List<E> currentElements() {
                    return strategy.currentElements();
                }

                @Override
                public List<E> elementsToInsert() {
                    return strategy.elementsToInsert();
                }

                @Override
                public Container2 wrap(List<E> list) {
                    return f.apply(strategy.wrap(list));
                }

                @Override
                public I identifierFor(E element) {
                    return strategy.identifierFor(element);
                }

                @Override
                public Equivalence<I> identifierEquivalence() {
                    return strategy.identifierEquivalence();
                }
            }
        );
    }

    private R result(List<E> result) {
        return strategy.wrap(checkNoDuplicates(result));
    }

    private List<E> checkNoDuplicates(List<E> list) {

        Set<EquivalenceWrapper<I>> set = new HashSet<>();

        for (E element : list) {

            I elementIdentifier = strategy.identifierFor(element);

            if (!set.add(
                new EquivalenceWrapper<>(
                    elementIdentifier,
                    strategy.identifierEquivalence()
                )
            )) {
                throw duplicateElementIdentifier(elementIdentifier);
            }
        }

        return list;
    }

    private IllegalArgumentException
    noSuchElementIdentifier(I elementIdentifier) {

        throw new IllegalArgumentException(
            String.format(
                "No such element id: %s",
                elementIdentifier.toString()
            )
        );
    }

    private IllegalArgumentException
    duplicateElementIdentifier(I elementIdentifier) {

        throw new IllegalArgumentException(
            String.format(
                "Duplicate element id: %s",
                elementIdentifier.toString()
            )
        );
    }
}

/**
 * @param <R> The return type for all of the {@link ListInsertionStep} methods.
 * @param <E> The type of element in the list.
 * @param <I> The type of identifier for list elements.
 */
interface ListInsertionStepStrategy<R, E, I> {

    List<E> currentElements();

    List<E> elementsToInsert();

    R wrap(List<E> list);

    I identifierFor(E element);

    Equivalence<I> identifierEquivalence();
}
