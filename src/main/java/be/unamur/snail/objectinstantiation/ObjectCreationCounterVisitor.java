package be.unamur.snail.objectinstantiation;

import spoon.reflect.code.CtConstructorCall;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtNewClass;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.visitor.CtScanner;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ObjectCreationCounterVisitor extends CtScanner {
    private int currentDepth = 0;
    private int maxDepth = 0;
    private final Map<CtConstructor<?>, Integer> constructorDepthMap = new HashMap<>();
    private final Set<String> visitedConstructors = new HashSet<>();

    @Override
    public <T> void visitCtConstructor(CtConstructor<T> constructor) {
        String constructorSignature = constructor.getSignature();
        if (visitedConstructors.contains(constructorSignature)) {
            return;
        }
        visitedConstructors.add(constructorSignature);
        currentDepth = 0;
        maxDepth = 0;
        super.visitCtConstructor(constructor);
        constructorDepthMap.put(constructor, maxDepth);
    }

    @Override
    public <T> void visitCtConstructorCall(CtConstructorCall<T> ctConstructorCall) {
        currentDepth++;
        maxDepth = Math.max(maxDepth, currentDepth);
        CtExecutable<?> executable = ctConstructorCall.getExecutable().getDeclaration();
        if (executable instanceof CtConstructor<?> constructor && !visitedConstructors.contains(constructor.getSignature())) {
            visitCtConstructor(constructor);
        }
        super.visitCtConstructorCall(ctConstructorCall);
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

    public Map<CtConstructor<?>, Integer> getConstructorDepthMap() {
        return constructorDepthMap;
    }
}
