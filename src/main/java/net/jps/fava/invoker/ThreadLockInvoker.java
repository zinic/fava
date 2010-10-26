/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.jps.fava.invoker;

import net.jps.fava.function;
import java.util.concurrent.locks.Lock;

/**
 *
 * @author zinic
 */
public class ThreadLockInvoker implements FunctionInvoker {
    private final Lock lockReference;

    public ThreadLockInvoker(Lock lockReference) {
        this.lockReference = lockReference;
    }

    @Override
    public <T> T invoke(function f, Object... args) {
        lockReference.lock();

        try {
            return (T) f.invoke(args);
        }finally {
            lockReference.unlock();
        }
    }
}
