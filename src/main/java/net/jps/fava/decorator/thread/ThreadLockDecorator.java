/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jps.fava.decorator.thread;

import net.jps.fava.function;
import net.jps.fava.decorator.AbstractFunctionDecorator;
import java.util.concurrent.locks.Lock;

/**
 *
 * @author zinic
 */
public class ThreadLockDecorator extends AbstractFunctionDecorator {

    private final Lock lockRef;

    public ThreadLockDecorator(function f, Lock lockRef) {
        super(f);

        this.lockRef = lockRef;
    }

    @Override
    public void before(Object... args) {
        lockRef.lock();
    }

    @Override
    public void after(Object... args) {
        lockRef.unlock();
    }
}
