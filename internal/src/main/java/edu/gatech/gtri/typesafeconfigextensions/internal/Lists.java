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

import java.util.ArrayList;
import java.util.List;

import static edu.gatech.gtri.typesafeconfigextensions.internal.Check.checkNotNull;
import static edu.gatech.gtri.typesafeconfigextensions.internal.Check.checkNotNullCollectionElements;
import static java.util.Arrays.asList;

/**
 * Static methods for manipulatings {@link List}s.
 */
public final class Lists {

    private Lists() { }

    public static <A> List<A> listOfOne(A one) {
        return asList(checkNotNull(one));
    }

    public static <A> List<A> listOfTwoOrMore(
        A one,
        A two,
        A[] more
    ) {
        List<A> list = new ArrayList<>();

        list.add(checkNotNull(one));
        list.add(checkNotNull(two));
        list.addAll(checkNotNullCollectionElements(asList(more)));

        return list;
    }

    public static <A> List<A> listConcat(
        List<? extends A> one,
        List<? extends A> two
    ) {
        List<A> list = new ArrayList<>();

        list.addAll(checkNotNullCollectionElements(one));
        list.addAll(checkNotNullCollectionElements(two));

        return list;
    }
}
