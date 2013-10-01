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

import java.util.HashMap;
import java.util.Map;

import static edu.gatech.gtri.typesafeconfigextensions.internal.Check.checkNotNull;
import static java.util.Collections.unmodifiableMap;

final class HashMapBindings
implements Bindings {

    private final Map<Class<?>, Object> map;

    HashMapBindings(Map<Class<?>, Object> map) {
        this.map = unmodifiableMap(checkNotNull(map));
    }

    <A> HashMapBindings set(Class<A> type, A value) {

        Map<Class<?>, Object> copy = new HashMap<>(map);
        copy.put(checkNotNull(type), checkNotNull(value));
        return new HashMapBindings(copy);
    }

    HashMapBindings remove(Class<?> type) {

        checkNotNull(type);

        if (!map.containsKey(type)) {
            return this;
        }

        Map<Class<?>, Object> copy = new HashMap<>(map);
        copy.remove(type);
        return new HashMapBindings(copy);
    }

    HashMapBindings add(Bindings bindings) {

        Map<Class<?>, Object> copy = new HashMap<>();
        copy.putAll(map);
        copy.putAll(bindings.asMap());
        return new HashMapBindings(copy);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <A> Binding<A> get(Class<A> type) {

        Object object = map.get(checkNotNull(type));

        if (object == null) {
            return NO_BINDING;
        }

        return new SomeBinding<>((A) object);
    }

    @Override
    public Map<Class<?>, Object> asMap() {
        return map;
    }

    private static final class SomeBinding<A> implements Binding<A> {

        private final A instance;

        private SomeBinding(A instance) {
            this.instance = instance;
        }

        @Override
        public A get() {
            return instance;
        }

        @Override
        public boolean isPresent() {
            return true;
        }
    }

    private static final Binding NO_BINDING = new Binding() {

        @Override
        public Object get() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isPresent() {
            return false;
        }
    };
}
