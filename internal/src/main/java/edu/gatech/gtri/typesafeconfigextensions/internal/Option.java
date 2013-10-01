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

package edu.gatech.gtri.typesafeconfigextensions.internal;

import java.util.NoSuchElementException;

import static edu.gatech.gtri.typesafeconfigextensions.internal.Check.checkNotNull;

/**
 * Zero or one instance of <tt>A</tt>.
 *
 * @param <A> The type that is optionally contained by this class.
 */
public abstract class Option<A> {

    Option() { }

    public abstract boolean isSome();

    public abstract A get();

    public static <A> Some<A> some(A a) {
        return new Some<>(a);
    }

    public static <A> None<A> none() {
        return new None<>();
    }
}

final class Some<A> extends Option<A> {

    private final A value;

    Some(A value) {
        this.value = checkNotNull(value);
    }

    @Override
    public boolean isSome() {
        return true;
    }

    @Override
    public A get() {
        return value;
    }
}

final class None<A> extends Option<A> {

    @Override
    public boolean isSome() {
        return false;
    }

    @Override
    public A get() {
        throw new NoSuchElementException();
    }
}
