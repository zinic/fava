/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jps.fava.decorator;

import net.jps.fava.function;
import net.jps.fava.InternalFunctionException;

/**
 *
 * @author zinic
 */
public abstract class AbstractFunctionDecorator implements Decorator {

    private final function f;

    public AbstractFunctionDecorator(function f) {
        this.f = f;
    }

    @Override
    public <T> T invoke(Object... args) throws InternalFunctionException {
        before(args);

        try {
            return (T) f.invoke(args);
        } finally {
            after(args);
        }
    }
}
