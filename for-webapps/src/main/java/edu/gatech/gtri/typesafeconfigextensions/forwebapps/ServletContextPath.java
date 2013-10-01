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

package edu.gatech.gtri.typesafeconfigextensions.forwebapps;

import javax.servlet.ServletContext;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static edu.gatech.gtri.typesafeconfigextensions.internal.Check.checkNotNull;

/**
 * A path represented as the list of strings formed by splitting a
 * {@link ServletContext#getContextPath() servlet context path}
 * on {@code "/"}.
 */
public final class ServletContextPath {

    private final List<String> pathComponents;

    ServletContextPath(List<String> pathComponents) {
        this.pathComponents = checkNotNull(pathComponents);
    }

    public List<String> toList() {
        return Collections.unmodifiableList(pathComponents);
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ServletContextPath that = (ServletContextPath) o;

        return pathComponents.equals(that.pathComponents);
    }

    @Override
    public int hashCode() {
        return pathComponents.hashCode();
    }

    @Override
    public String toString() {
        return String.format("ServletContextPath { %s }", pathString());
    }

    private String pathString() {

        if (pathComponents.isEmpty()) {
            return "(root)";
        }

        StringBuilder s = new StringBuilder();

        for (String component : pathComponents) {
            s.append("/").append(component);
        }

        return s.toString();
    }

    public static ServletContextPath
    fromServletContext(ServletContext servletContext) {

        return parse(servletContext.getContextPath());
    }

    public static ServletContextPath parse(String path) {

        List<String> parsed = new ArrayList<>();

        for (String s : path.split("/")) {
            if (!s.isEmpty()) {
                parsed.add(s);
            }
        }

        return new ServletContextPath(parsed);
    }
}
