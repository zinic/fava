/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jps.fava.reflection;

/**
 *
 * @author zinic
 */
public final class Boxer {

    private Boxer() {
    }

    public static Class<?> toObjectClass(Class<?> possiblePrimitive) {
        if (possiblePrimitive.isPrimitive()) {
            if (possiblePrimitive.equals(float.class)) {
                return Float.class;
            }

            if (possiblePrimitive.equals(double.class)) {
                return Double.class;
            }

            if (possiblePrimitive.equals(short.class)) {
                return Short.class;
            }

            if (possiblePrimitive.equals(int.class)) {
                return Integer.class;
            }

            if (possiblePrimitive.equals(long.class)) {
                return Long.class;
            }

            if (possiblePrimitive.equals(boolean.class)) {
                return Boolean.class;
            }

            if (possiblePrimitive.equals(byte.class)) {
                return Byte.class;
            }

            if (possiblePrimitive.equals(char.class)) {
                return Character.class;
            }
        }

        return possiblePrimitive;
    }

    public static Float box(float f) {
        return Float.valueOf(f);
    }

    public static Double box(double d) {
        return Double.valueOf(d);
    }

    public static Short box(short s) {
        return Short.valueOf(s);
    }

    public static Integer box(int i) {
        return Integer.valueOf(i);
    }

    public static Long box(long l) {
        return Long.valueOf(l);
    }

    public static Boolean box(boolean b) {
        return Boolean.valueOf(b);
    }

    public static Byte box(byte b) {
        return Byte.valueOf(b);
    }

    public static Character box(char c) {
        return Character.valueOf(c);
    }

    public static Object box(Object o) {
        return o;
    }
}
