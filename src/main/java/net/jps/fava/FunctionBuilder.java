/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jps.fava;

import net.jps.fava.reflection.Boxer;
import net.jps.fava.reflection.ReflectionTools;

import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import javassist.ClassClassPath;
import javassist.ClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMethod;
import javassist.Modifier;

/**
 * TODO: Add destroy method
 * 
 * @author zinic
 */
public class FunctionBuilder {

    private final ClassPool classPoolReference;
    private final List<ClassPath> classPaths;

    public FunctionBuilder() {
        this(ClassPool.getDefault());
    }

    public FunctionBuilder(ClassPool classPoolReference) {
        this.classPoolReference = classPoolReference;

        classPaths = new LinkedList<ClassPath>();
    }

    private CtClass getClassFromPool(Class<?> targetClass) {
        final String className = targetClass.getName();

        final CtClass targetCtClass = classPoolReference.getOrNull(className);

        if (targetCtClass == null) {
            final ClassPath classPathLocator = new ClassClassPath(targetClass);

            synchronized (classPoolReference) {
                if (!classPaths.contains(classPathLocator)) {
                    classPoolReference.insertClassPath(classPathLocator);
                    classPaths.add(classPathLocator);

                    return classPoolReference.getOrNull(className);
                }
            }
        }

        return targetCtClass;
    }

    public function define(Partial target) throws FunctionGenerationException, IllegalArgumentException {
        final Class<?> targetClass = target.getClass();

        if (targetClass.getMethods().length == 0) {
            throw new IllegalArgumentException("No method declared in Function Partial");
        }

        final Method targetMethod = targetClass.getMethods()[0];
        final String targetMethodName = targetMethod.getName();

        if (targetMethodName.equals("invoke")) {
            throw new IllegalArgumentException("Function Partial must not contain an invoke method of any sort");
        }

        final String syntheticClassName = targetClass.getName() + "$_SyntheticInvocable";

        try {
            final CtClass targetCtClass = getClassFromPool(targetClass);
            CtClass freshInvoker = classPoolReference.getOrNull(syntheticClassName);

            if (freshInvoker == null) {
                // Build generator for the new class
                freshInvoker = constructInvoker(
                        classPoolReference.makeClass(syntheticClassName),
                        targetCtClass,
                        targetMethod,
                        targetMethodName);
            }

            return (function) ReflectionTools.construct(freshInvoker.toClass(), target);
        } catch (Exception ex) {
            throw new FunctionGenerationException("Unable to generate function. Reason: " + ex.getMessage(), ex);
        }
    }

    private CtClass constructInvoker(CtClass freshInvoker, CtClass targetCtClass, Method targetMethod, String targetMethodName) throws Exception {
        final Class<?>[] targetMethodParameters = targetMethod.getParameterTypes();

        freshInvoker.addInterface(classPoolReference.get(function.class.getName()));

        // Target object field
        final CtField targetField = new CtField(targetCtClass, "target", freshInvoker);
        targetField.setModifiers(targetField.getModifiers() | Modifier.PRIVATE);
        freshInvoker.addField(targetField);

        // Argument length for determining length matching before attempting to cast args
        final CtField field = new CtField(CtClass.intType, "targetArgLength", freshInvoker);
        field.setModifiers(field.getModifiers() | Modifier.PRIVATE);
        freshInvoker.addField(field);

        // Public constructor
        final CtConstructor cons = new CtConstructor(new CtClass[]{targetCtClass}, freshInvoker);
        cons.setBody(new StringBuilder("{ this.target = $1; this.targetArgLength = ").append(targetMethodParameters.length).append("; }").toString());
        freshInvoker.addConstructor(cons);

        final Class<?> targetReturnTypeClass = targetMethod.getReturnType();

        final boolean hasVoidReturnType = targetReturnTypeClass.equals(Void.TYPE);
        final boolean returnsPrimitive = targetReturnTypeClass.isPrimitive();

        // Invoke method
        final StringBuilder methodStringBuffer = new StringBuilder("public ");
        methodStringBuffer.append(Object.class.getName()).append(" invoke(").append(Object.class.getName()).append("[] args) throws ").append(Exception.class.getName()).append(" {");
        methodStringBuffer.append("if (args == null) { args = new ").append(Object.class.getName()).append("[]{null};}"
                + "if (args.length != this.targetArgLength) "
                + "{throw new ").append(IllegalArgumentException.class.getName()).append("(\"Argument length mismatch for function, ").append(targetCtClass.getName()).append("\");}");

        methodStringBuffer.append("try {");

        if (!hasVoidReturnType) {
            methodStringBuffer.append("return ");

            if (returnsPrimitive) {
                methodStringBuffer.append(Boxer.class.getName()).append(".box(");
            }
        }

        methodStringBuffer.append("this.target.").append(targetMethodName).append("(");

        for (int i = 0; i < targetMethodParameters.length; i++) {
            if (targetMethodParameters[i].equals(Float.TYPE)) {
                methodStringBuffer.append("((").append(Float.class.getName()).append(")args[").append(i).append("]).floatValue()");
            } else if (targetMethodParameters[i].equals(Double.TYPE)) {
                methodStringBuffer.append("((").append(Double.class.getName()).append(")args[").append(i).append("]).doubleValue()");
            } else if (targetMethodParameters[i].equals(Short.TYPE)) {
                methodStringBuffer.append("((").append(Short.class.getName()).append(")args[").append(i).append("]).shortValue()");
            } else if (targetMethodParameters[i].equals(Integer.TYPE)) {
                methodStringBuffer.append("((").append(Integer.class.getName()).append(")args[").append(i).append("]).intValue()");
            } else if (targetMethodParameters[i].equals(Long.TYPE)) {
                methodStringBuffer.append("((").append(Long.class.getName()).append(")args[").append(i).append("]).longValue()");
            } else if (targetMethodParameters[i].equals(Boolean.TYPE)) {
                methodStringBuffer.append("((").append(Boolean.class.getName()).append(")args[").append(i).append("]).booleanValue()");
            } else if (targetMethodParameters[i].equals(Byte.TYPE)) {
                methodStringBuffer.append("((").append(Byte.class.getName()).append(")args[").append(i).append("]).byteValue()");
            } else if (targetMethodParameters[i].equals(Character.TYPE)) {
                methodStringBuffer.append("((").append(Character.class.getName()).append(")args[").append(i).append(i).append("]).charValue()");
            } else {
                methodStringBuffer.append("(args[").append(i).append("] == null ? null : ");
                methodStringBuffer.append("(").append(targetMethodParameters[i].getName()).append(")").append("args[").append(i).append("])");
            }

            if (i + 1 < targetMethodParameters.length) {
                methodStringBuffer.append(", ");
            }
        }

        methodStringBuffer.append(")");

        if (hasVoidReturnType) {
            methodStringBuffer.append(";return null;");
        } else if (returnsPrimitive) {
            methodStringBuffer.append(");");
        } else {
            methodStringBuffer.append(";");
        }

        methodStringBuffer.append("} catch(").append(ClassCastException.class.getName()).append(" cce) { throw cce; }");
        methodStringBuffer.append("catch(").append(Throwable.class.getName()).append(" t) { throw new ").append(InternalFunctionException.class.getName());
        methodStringBuffer.append("(\"Fatal exception caught during function execution. Pump cause for more details. Origin message: \" + t.getMessage(), t);}}");

        // add delegate target method
        final CtMethod delegateMethod = CtMethod.make(methodStringBuffer.toString(), freshInvoker);
        delegateMethod.setModifiers(delegateMethod.getModifiers() | Modifier.VARARGS);
        freshInvoker.addMethod(delegateMethod);

        return freshInvoker;
    }
}
