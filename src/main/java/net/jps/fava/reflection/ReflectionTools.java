/*
 *  Copyright 2010 John Hopper
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */
package net.jps.fava.reflection;

import java.lang.reflect.Constructor;

/**
 *
 * @author jhopper
 */
public class ReflectionTools {

    private static final Class<?>[] DEFAULT_ANY = new Class<?>[]{Any.class};

    private static final class Any {

        private Any() {
        }
    }

    public static <T> T construct(Class<T> c, Object... parameters) {
        try {
            final Constructor<T> constructor = getConstructor(c, toClassArray(parameters));

            if (constructor != null) {
                return constructor.newInstance(parameters);
            }

            //TODO: throw expcetion
//            localLogger.error("No constructors for class " + c.getCanonicalName() + " answer to constructor");
        } catch (Exception instanciationException) {
//            localLogger.error("Failed to create new instance of class: " + c.getCanonicalName(), instanciationException);
        }

        throw new RuntimeException("Failed to create new exception instance for class: " + c.getCanonicalName());
    }

    public static Class<?>[] objectArrayToClassArray(Object[] o) {
        final Class<?>[] classes = o != null ? new Class<?>[o.length] : DEFAULT_ANY;

        if (o != null) {
            for (int i = 0; i < o.length; i++) {
                classes[i] = o[i] == null ? Any.class : o[i].getClass();
            }
        }

        return classes;
    }

    public static boolean classArraysMatch(Class<?>[] a, Class<?>[] b) {
        if (a.length != b.length) {
            return false;
        }

        for (int i = 0; i < a.length; i++) {
            if (a[i] != Any.class && b[i] != Any.class && !a[i].isAssignableFrom(b[i])) {
                return false;
            }
        }

        return true;
    }

    private static <T> Constructor<T> getConstructor(Class<T> c, Class<?>[] parameters) throws NoSuchMethodException {
        for (Constructor<T> constructor : (Constructor<T>[]) c.getConstructors()) {
            final Class<?>[] constructorParameters = constructor.getParameterTypes();

            if (parameters.length != constructorParameters.length) {
                continue;
            }

            boolean suitable = true;

            for (int i = 0; i < parameters.length; i++) {
                if (parameters[i] == null) {
                    continue;
                }

                if (!constructorParameters[i].isAssignableFrom(parameters[i])) {
                    suitable = false;
                    break;
                }
            }

            if (suitable) {
                return constructor;
            }
        }

        throw new NoSuchMethodException();
    }

    private static Class<?>[] toClassArray(Object... objects) {
        final Class<?>[] classArray = new Class<?>[objects.length];

        for (int i = 0; i < objects.length; i++) {
            classArray[i] = objects[i] != null ? objects[i].getClass() : null;
        }

        return classArray;
    }
}
