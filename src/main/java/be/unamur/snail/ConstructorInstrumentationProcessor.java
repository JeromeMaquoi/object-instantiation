package be.unamur.snail;

import spoon.processing.AbstractProcessor;
import spoon.reflect.code.*;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.filter.TypeFilter;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class ConstructorInstrumentationProcessor extends AbstractProcessor<CtConstructor<?>> {
//    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private static final String PKG = "be.unamur.snail.register.SendUtils";

    @Override
    public void process(CtConstructor<?> constructor) {
        if (constructor.getBody() == null) return;

        String fileName = getFileName(constructor);
        String className = constructor.getDeclaringType().getQualifiedName();
        String constructorName = constructor.getDeclaringType().getQualifiedName();
        List<String> constructorParameters = createConstructorParameterList(constructor);

        CtInvocation<?> initConstructorInvocation = createInitConstructorContextInvocation(fileName, className, constructorName, constructorParameters);
        constructor.getBody().insertBegin(initConstructorInvocation);

        Factory factory = getFactory();
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

    private String getFileName(CtConstructor<?> constructor) {
        String fileName = "Unknown File";
        if (constructor.getPosition() != null && constructor.getPosition().getFile() != null) {
            fileName = constructor.getPosition().getFile().getPath();
        }
        return fileName;
    }

    private List<String> createConstructorParameterList(CtConstructor<?> constructor) {
        List<String> constructorParameters = new ArrayList<>();
        for (CtParameter<?> parameter : constructor.getParameters()) {
            constructorParameters.add(parameter.getType().getQualifiedName());
        }
        return constructorParameters;
    }

    private CtInvocation<?> createSetCallerContextInvocation(Factory factory, CtConstructor<?> constructor) {
        CtTypeReference<?> registerUtilsType = factory.Type().createReference(PKG);
        CtExecutableReference<?> setCallerContextMethod = factory.Executable().createReference(
                registerUtilsType,
                factory.Type().voidPrimitiveType(),
                "setCallerContext"
        );

        CtThisAccess<?> thisAccess = factory.Code().createThisAccess(constructor.getDeclaringType().getReference());

        return factory.Code().createInvocation(
                factory.Code().createTypeAccess(registerUtilsType),
                setCallerContextMethod,
                factory.Code().createLiteral(constructor.getDeclaringType().getSimpleName()),
                thisAccess
        );
    }

    private CtInvocation<?> createInitConstructorContextInvocation(String fileName, String className, String constructorName, List<String> constructorParameters) {
        Factory factory = getFactory();
        CtTypeReference<?> registerUtilsType = factory.Type().createReference(PKG);
        CtExecutableReference<?> initConstructorMethod = factory.Executable().createReference(
                registerUtilsType,
                factory.Type().voidPrimitiveType(),
                "initConstructorContext"
        );
        CtExpression<ArrayList> parameterListLiteral = new MethodInstrumentationProcessor().createParameterListLiteral(factory, constructorParameters);
        return factory.Code().createInvocation(
                factory.Code().createTypeAccess(registerUtilsType),
                initConstructorMethod,
                factory.Code().createLiteral(fileName),
                factory.Code().createLiteral(className),
                factory.Code().createLiteral(constructorName),
                parameterListLiteral
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
