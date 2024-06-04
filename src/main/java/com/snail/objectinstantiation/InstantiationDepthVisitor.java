package com.snail.objectinstantiation;

import spoon.reflect.code.CtConstructorCall;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtNewClass;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.visitor.CtScanner;

import java.util.HashMap;
import java.util.Map;

public class InstantiationDepthVisitor extends CtScanner {
    private int currentDepth = 0;
    private int maxDepth = 0;
    private final Map<CtMethod<?>, Integer> methodDepthMap = new HashMap<>();

    @Override
    public <T> void visitCtMethod(CtMethod<T> method) {
        currentDepth = 0;
        maxDepth = 0;
        super.visitCtMethod(method);
        methodDepthMap.put(method, maxDepth);
    }

    @Override
    public <T> void visitCtConstructor(CtConstructor<T> constructor) {
        currentDepth ++;
        maxDepth = Math.max(maxDepth, currentDepth);
        super.visitCtConstructor(constructor);
        currentDepth--;
    }

    @Override
    public <T> void visitCtConstructorCall(CtConstructorCall<T> ctConstructorCall) {
        currentDepth++;
        maxDepth = Math.max(maxDepth, currentDepth);
        super.visitCtConstructorCall(ctConstructorCall);
        currentDepth--;
    }

    @Override
    public <T> void visitCtNewClass(CtNewClass<T> newClass) {
        currentDepth++;
        maxDepth = Math.max(maxDepth, currentDepth);
        super.visitCtNewClass(newClass);
        currentDepth--;
    }

    @Override
    public <T> void visitCtInvocation(CtInvocation<T> invocation) {
        // If the invocation is a constructor call or method creating an object, handle it similarly.
        if (invocation.getExecutable().isConstructor()) {
            currentDepth++;
            maxDepth = Math.max(maxDepth, currentDepth);
            super.visitCtInvocation(invocation);
            currentDepth--;
        } else {
            super.visitCtInvocation(invocation);
        }
    }

    public Map<CtMethod<?>, Integer> getMethodDepthMap() {
        return methodDepthMap;
    }
}
