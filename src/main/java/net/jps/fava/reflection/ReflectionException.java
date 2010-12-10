/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jps.fava.reflection;

/**
 *
 * @author zinic
 */
public class ReflectionException extends RuntimeException {

    public ReflectionException(String message, Throwable cause) {
        super(message, cause);
    }

    public ReflectionException(String message) {
        super(message);
    }
}
