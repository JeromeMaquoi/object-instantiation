package be.unamur.snail;

import spoon.processing.AbstractProcessor;
import spoon.reflect.code.*;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.filter.TypeFilter;

import java.util.List;

public class MethodInstrumentationProcessor extends AbstractProcessor<CtExecutable<?>> {
    private static final String PKG = "be.unamur.snail.register.SendUtils";

    @Override
    public void process(CtExecutable<?> ctExecutable) {
        if (ctExecutable.getBody() == null) return;

        Factory factory = getFactory();

        if (ctExecutable instanceof CtConstructor<?> constructor) {
            handleConstructor(factory, constructor);
        } else if (ctExecutable instanceof CtMethod<?> method) {
            handleMethod(factory, method);
        }
    }

    private String getFileName(CtExecutable<?> ctExecutable) {
        String fileName = "Unknown File";
        if (ctExecutable.getPosition() != null && ctExecutable.getPosition().getFile() != null) {
            fileName = ctExecutable.getPosition().getFile().getPath();
        }
        return fileName;
    }

    public void handleConstructor(Factory factory, CtConstructor<?> constructor) {
        String fileName = getFileName(constructor);
        String className = constructor.getDeclaringType().getQualifiedName();
        String methodName = constructor.getSimpleName();

        CtInvocation<?> initMethodInvocation = createInitMethodElementDTOInvocation(factory, fileName, className, methodName);
        constructor.getBody().insertBegin(initMethodInvocation);

        for (CtAssignment<?, ?> assignment : constructor.getBody().getElements(new TypeFilter<>(CtAssignment.class))) {
            if (assignment.getAssigned() instanceof CtFieldAccess<?> fieldAccess && (fieldAccess.getTarget() instanceof CtThisAccess<?> || fieldAccess.getTarget() == null)) {
                String fieldName = fieldAccess.getVariable().getSimpleName();
                String fieldType = fieldAccess.getVariable().getType().getQualifiedName();

                CtInvocation<?> prepareMethodInvocation = createAddAttributeMethodInvocation(factory, fieldName, fieldType, fieldAccess);
                assignment.insertAfter(prepareMethodInvocation);
            }
        }

        CtInvocation<?> setCallerContextInvocation = createSetCallerContextInvocation(factory, constructor);
        constructor.getBody().insertEnd(setCallerContextInvocation);

        CtInvocation<?> sendInvocation = createSendMethodInvocation(factory);
        constructor.getBody().addStatement(sendInvocation);
    }

    public void handleMethod(Factory factory, CtMethod<?> method) {
        String fileName = getFileName(method);
        String className = method.getDeclaringType().getQualifiedName();
        String methodName = method.getSimpleName();

        CtInvocation<?> initMethodInvocation = createInitMethodElementDTOInvocation(factory, fileName, className, methodName);
        method.getBody().insertBegin(initMethodInvocation);

        CtInvocation<?> setCallerContextInvocation = createSetCallerContextInvocation(factory, method);
        insertSetCallerContextInvocation(method, setCallerContextInvocation);

        CtInvocation<?> sendInvocation = createSendMethodInvocation(factory);
        method.getBody().addStatement(sendInvocation);
    }

    private CtInvocation<?> createInitMethodElementDTOInvocation(Factory factory, String fileName, String className, String methodName) {
        CtTypeReference<?> registerUtilsType = factory.Type().createReference(PKG);
        CtExecutableReference<?> initConstructorMethod = factory.Executable().createReference(
                registerUtilsType,
                factory.Type().voidPrimitiveType(),
                "initMethodElementDTO"
        );
        return factory.Code().createInvocation(
                factory.Code().createTypeAccess(registerUtilsType),
                initConstructorMethod,
                factory.Code().createLiteral(fileName),
                factory.Code().createLiteral(className),
                factory.Code().createLiteral(methodName)
        );
    }

    private CtInvocation<?> createAddAttributeMethodInvocation(Factory factory, String fieldName, String fieldType, CtFieldAccess<?> fieldAccess) {
        CtTypeReference<?> registerUtilsType = factory.Type().createReference(PKG);
        CtTypeReference<Void> voidType = factory.Type().voidPrimitiveType();
        CtExecutableReference<?> addAttributeMethod = factory.Executable().createReference(
                registerUtilsType,
                voidType,
                "addAttribute"
        );
        return factory.Code().createInvocation(
                factory.Code().createTypeAccess(registerUtilsType),
                addAttributeMethod,
                factory.Code().createLiteral(fieldName),
                factory.Code().createLiteral(fieldType),
                fieldAccess
        );
    }

    private CtInvocation<?> createSetCallerContextInvocation(Factory factory, CtExecutable<?> method) {
        CtTypeReference<?> registerUtilsType = factory.Type().createReference(PKG);
        CtExecutableReference<?> setCallerContextMethod = factory.Executable().createReference(
                registerUtilsType,
                factory.Type().voidPrimitiveType(),
                "setCallerContext"
        );

        CtTypeReference<?> declaringType = getDeclaringType(method);
        CtThisAccess<?> thisAccess = factory.Code().createThisAccess(declaringType);

        return factory.Code().createInvocation(
                factory.Code().createTypeAccess(registerUtilsType),
                setCallerContextMethod,
                factory.Code().createLiteral(declaringType.getSimpleName()),
                thisAccess
        );
    }

    private void insertSetCallerContextInvocation(CtMethod<?> method, CtInvocation<?> setCallerContextInvocation) {
        List<CtReturn<?>> returnStatements = method.getBody().getElements(new TypeFilter<>(CtReturn.class));
        for (CtReturn<?> returnStatement : returnStatements) {
            returnStatement.insertBefore(setCallerContextInvocation);
        }
        if (returnStatements.isEmpty()) {
            method.getBody().insertEnd(setCallerContextInvocation);
        }
    }

    private CtTypeReference<?> getDeclaringType(CtExecutable<?> executable) {
        CtTypeReference<?> declaringType;
        if (executable instanceof CtConstructor<?>) {
            declaringType = ((CtConstructor<?>) executable).getDeclaringType().getReference();
        } else if (executable instanceof CtMethod<?>) {
            declaringType = ((CtMethod<?>) executable).getDeclaringType().getReference();
        } else {
            throw new IllegalArgumentException("Unsupported executable type: " + executable.getClass());
        }
        return declaringType;
    }

    private CtInvocation<?> createSendMethodInvocation(Factory factory) {
        CtTypeReference<?> registerUtilsType = factory.Type().createReference(PKG);
        CtExecutableReference<?> sendMethod = factory.Executable().createReference(
                registerUtilsType,
                factory.Type().voidPrimitiveType(),
                "send"
        );

        return factory.Code().createInvocation(
                factory.Code().createTypeAccess(registerUtilsType),
                sendMethod
        );
    }
}
