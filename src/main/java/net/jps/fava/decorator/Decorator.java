/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jps.fava.decorator;

import net.jps.fava.function;

/**
 *
 * @author zinic
 */
public interface Decorator extends function {

    void before(Object... args);

    void after(Object... args);
}
