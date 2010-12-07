/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jps.fava;

/**
 *
 * @author zinic
 */
public class FunctionGenerationException extends RuntimeException {

    public FunctionGenerationException(String message, Throwable cause) {
        super(message, cause);
    }

    public FunctionGenerationException(String message) {
        super(message);
    }
}
