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

import static org.junit.Assert.*;
import static net.jps.fava.Functions.*;

/**
 *
 * @author zinic
 */
@RunWith(Enclosed.class)
public class FunctionsTest {

    static class AnyThrowable extends Exception {

        public AnyThrowable(String message) {
            super(message);
        }
    }

    public static class WhenCallingFunctions {

        @Test
        public void shouldDelegateFunctionCalls() throws Throwable {
            final function function = defun(new Partial() {

                public String returnString(String a) {
                    return a;
                }
            });

            assertEquals("A", function.invoke("A"));
        }

        @Test
        public void shouldPassThrownExceptionsInFunction() {
            Throwable actual = null;

            try {
                defun(new Partial() {

                    public void methodName() throws AnyThrowable {
                        throw new AnyThrowable("A message");
                    }
                }).invoke();
            } catch (InternalFunctionException fe) {
                actual = fe.getCause() instanceof AnyThrowable ? fe.getCause() : null;
            }

            assertNotNull("should have an exception", actual);
            assertEquals("methodName", actual.getStackTrace()[0].getMethodName());
        }

        @Test(expected = ClassCastException.class)
        public void shouldThrowUpIfWrongTypeIsPassed() throws Throwable {
            final function aFunction = defun(new Partial() {

                public void methodName(Integer a, String b) {
                }
            });

            aFunction.invoke("a", 2);
        }

        public void shouldAcceptNullIfPassed() throws Throwable {
            final function aFunction = defun(new Partial() {

                public void methodName(Integer a, String b) {
                }
            });

            aFunction.invoke(null, null);
        }

        @Test
        public void shouldAcceptNullReferences() throws Throwable {
            final function aFunction = defun(new Partial() {

                public Integer methodName(Integer a) {
                    return a;
                }
            });

            Integer i = null;
            Integer actual = aFunction.invoke(i);

            assertNull(actual);
        }

        @Test
        public void shouldHandlePrimitiveFunctionArgs() throws Exception {
            final function aFunction = defun(new Partial() {

                public Integer methodName(int a) {
                    return a;
                }
            });

            assertEquals(1, aFunction.invoke(1));
        }

        @Test
        public void shouldHandlePrimitiveFunctionReturnValues() throws Exception {
            final function aFunction = defun(new Partial() {

                public int methodName(int a) {
                    return a;
                }
            });

            assertEquals(1, aFunction.invoke(1));
        }
    }
}
