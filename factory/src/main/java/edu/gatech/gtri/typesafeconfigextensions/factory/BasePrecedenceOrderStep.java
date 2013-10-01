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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static edu.gatech.gtri.typesafeconfigextensions.internal.Check.checkNotNull;

/**
 * Base implementation for {@link PrecedenceOrderStep}.
 * Most {@link PrecedenceOrderStep}s should extend this class.
 *
 * <p>The subtype must provide a {@link List} of elements (<tt>E</tt>) in the
 * constructor and implement {@link #fromHighestToLowestPrecedence(List)}.</p>
 *
 * <p>This class implements {@link #fromLowestToHighestPrecedence()} by invoking
 * {@link #fromHighestToLowestPrecedence(java.util.List)} with a reversed copy
 * of the list.</p>
 *
 * @param <R> The return type of these methods.
 * @param <E> The type of elements in the list.
 */
abstract class BasePrecedenceOrderStep<R, E>
implements PrecedenceOrderStep<R> {

    private final List<E> elements;

    BasePrecedenceOrderStep(List<E> elements) {
        this.elements = checkNotNull(elements);
    }

    abstract R fromHighestToLowestPrecedence(List<E> elements);

    @Override
    public final R fromHighestToLowestPrecedence() {
        return fromHighestToLowestPrecedence(elements);
    }

    @Override
    public final R fromLowestToHighestPrecedence() {
        return fromHighestToLowestPrecedence(reversed(elements));
    }

    private static <T> List<T> reversed(List<T> original) {

        List<T> reversed = new ArrayList<>();
        reversed.addAll(original);
        Collections.reverse(reversed);
        return reversed;
    }
}
