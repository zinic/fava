/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.jps.fava.invoker;

import net.jps.fava.function;

/**
 *
 * @author zinic
 */
public interface FunctionInvoker {
    <T> T invoke(function f, Object... args);
}
