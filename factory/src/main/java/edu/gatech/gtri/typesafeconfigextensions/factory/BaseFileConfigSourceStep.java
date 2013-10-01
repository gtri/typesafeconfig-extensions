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

import java.nio.file.Path;

import static edu.gatech.gtri.typesafeconfigextensions.internal.Check.checkNotNull;

/**
 * Base implementation for {@link FileConfigSourceStep}.
 * Most {@link FileConfigSourceStep}s should extend this class.
 */
public abstract class BaseFileConfigSourceStep
implements FileConfigSourceStep {

    @Override
    public final NamedConfigSource byKey(String key) {
        return by(PathSpecifications.byKey(checkNotNull(key)));
    }

    @Override
    public final NamedConfigSource byPath(Path path) {
        return by(PathSpecifications.byPath(checkNotNull(path)));
    }
}
