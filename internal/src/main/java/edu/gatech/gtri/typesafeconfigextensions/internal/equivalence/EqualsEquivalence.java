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

import edu.gatech.gtri.typesafeconfigextensions.internal.Function;

import static edu.gatech.gtri.typesafeconfigextensions.internal.Check.checkNotNull;

/**
 * A trivial {@link Equivalence} implementation that corresponds to
 * <tt>A</tt>'s natural equality.
 *
 * @param <A> The type for which equals and hashCode definitions are
 *   given by this instance.
 */
public final class EqualsEquivalence<A>
implements Equivalence<A> {

    @Override
    public boolean equals(A x, A y) {
        return x.equals(y);
    }

    @Override
    public int hashCode(A x) {
        return x.hashCode();
    }

    public <B> Equivalence<B> map(final Function<? super B, ? extends A> f) {

        checkNotNull(f);

        return new Equivalence<B>() {

            @Override
            public boolean equals(B x, B y) {
                return EqualsEquivalence.this.equals(f.apply(x), f.apply(y));
            }

            @Override
            public int hashCode(B x) {
                return EqualsEquivalence.this.hashCode(f.apply(x));
            }
        };
    }
}
