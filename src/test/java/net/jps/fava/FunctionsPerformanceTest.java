/*
 *  Copyright 2010 zinic.
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
package net.jps.fava;

import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import static net.jps.fava.Functions.*;

/**
 *
 * @author zinic
 */
@RunWith(Enclosed.class)
public class FunctionsPerformanceTest {

    public static void PrintTimeTaken(String msg, long start, long stop) {
        System.out.println("Time elapsed - " + ITERATIONS + " Iteration Mean: " + ((stop - start) / 1000000) + "ms - " + msg);
    }
    private static final int ITERATIONS = 50000;

    public static class WhenCallingFunctions {

        @Test
        public void shouldDelegateFunctionCalls() throws Throwable {
            final function staticFunction = new function() {

                @Override
                public <T> T invoke(Object... args) throws InternalFunctionException {
                    return (T) args[0];
                }
            };

            final function dynamicFunction = defun(new Partial() {

                public String go(String a) {
                    return a;
                }
            });

            long start = System.nanoTime();

            for (int i = 0; i < ITERATIONS; i++) {
                dynamicFunction.invoke("A");
            }

            long end = System.nanoTime();

            PrintTimeTaken("Time taken using dnymaic invoke(...)", start, end);

            start = System.nanoTime();

            for (int i = 0; i < ITERATIONS; i++) {
                staticFunction.invoke("a");
            }

            end = System.nanoTime();

            PrintTimeTaken("Time taken using compile time method calling", start, end);
        }
    }
}
