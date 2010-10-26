/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jps.fava;

/**
 *
 * @author zinic
 */
public class InternalFunctionException extends RuntimeException {

    public InternalFunctionException(String message) {
        super(message);
    }

    public InternalFunctionException(String message, Throwable cause) {
        super(message, cause);
    }
}
