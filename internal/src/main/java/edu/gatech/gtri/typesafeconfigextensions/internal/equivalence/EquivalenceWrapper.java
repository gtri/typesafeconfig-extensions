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

package edu.gatech.gtri.typesafeconfigextensions.internal.equivalence;

import static edu.gatech.gtri.typesafeconfigextensions.internal.Check.checkNotNull;

/**
 * A class that holds an instance of <tt>A</tt> and implements equals
 * and hashCode as specified by an {@link Equivalence}. This is useful
 * if you need to put a collection of <tt>A</tt> instances into a HashSet
 * or HashMap using some equality other than <tt>A</tt>'s natural equality.
 *
 * @param <A> The type being wrapped by this container.
 */
public final class EquivalenceWrapper<A> {

    private final A value;
    private final Equivalence<A> equivalence;

    public EquivalenceWrapper(A value, Equivalence<A> equivalence) {

        this.value = checkNotNull(value);
        this.equivalence = checkNotNull(equivalence);
    }

    @Override
    public boolean equals(Object that) {

        if (that == null || !(that instanceof EquivalenceWrapper)) {
            return false;
        }

        EquivalenceWrapper thatWrapper = (EquivalenceWrapper) that;

        A thatValue;
        try {
            thatValue = uncheckedCast(thatWrapper.value);
        } catch (ClassCastException e) {
            return false;
        }

        return equivalence.equals(value, thatValue);
    }

    @SuppressWarnings("unchecked")
    private <T> T uncheckedCast(Object o) {
        return (T) o;
    }

    @Override
    public int hashCode() {
        return equivalence.hashCode(value);
    }
}
