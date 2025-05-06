package be.unamur.snail;

import spoon.processing.AbstractProcessor;
import spoon.reflect.code.*;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.reference.CtVariableReference;
import spoon.reflect.visitor.filter.TypeFilter;

import java.util.ArrayList;
import java.util.List;

public class ConstructorInstrumentationProcessor extends AbstractProcessor<CtConstructor<?>> {
//    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private static final String PKG = "be.unamur.snail.register.SendUtils";

    @Override
    public void process(CtConstructor<?> constructor) {
        if (constructor.getBody() == null) return;
        Factory factory = getFactory();

        String fileName = getFileName(constructor);
        String className = constructor.getDeclaringType().getQualifiedName();
        String constructorName = constructor.getDeclaringType().getSimpleName();
        List<String> constructorParameters = createConstructorParameterList(constructor);

        CtLocalVariable<?> utilsVariable = createSendUtilsInitializationInvocation();
        CtVariableReference<?> utilsVarRef = utilsVariable.getReference();
        CtExpression<?> utilsAccess = factory.Code().createVariableRead(utilsVarRef, false);

        CtInvocation<?> initConstructorInvocation = createInitConstructorContextInvocation(utilsAccess, fileName, className, constructorName, constructorParameters);
        constructor.getBody().insertBegin(initConstructorInvocation);

        // "utils" variable initialization
        constructor.getBody().insertBegin(utilsVariable);

        for (CtAssignment<?, ?> assignment : constructor.getBody().getElements(new TypeFilter<>(CtAssignment.class))) {
            if (assignment.getAssigned() instanceof CtFieldAccess<?> fieldAccess && (fieldAccess.getTarget() instanceof CtThisAccess<?> || fieldAccess.getTarget() == null)) {
                String fieldName = fieldAccess.getVariable().getSimpleName();
                String fieldType = fieldAccess.getVariable().getType().getQualifiedName();

                CtInvocation<?> prepareMethodInvocation = createAddAttributeMethodInvocation(factory, utilsAccess, fieldName, fieldType, fieldAccess);
                assignment.insertAfter(prepareMethodInvocation);
            }
        }

        CtInvocation<?> getSnapshotInvocation = createGetSnapshotInvocation(factory, utilsAccess, constructor);
        constructor.getBody().insertEnd(getSnapshotInvocation);

        CtInvocation<?> writeConstructorContextInvocation = createWriteConstructorContextInvocation(factory, utilsAccess);
        constructor.getBody().addStatement(writeConstructorContextInvocation);
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

    private CtLocalVariable<?> createSendUtilsInitializationInvocation() {
        Factory factory = getFactory();
        CtTypeReference<?> utilsType = factory.Type().createReference(PKG);
        CtConstructorCall constructorCall = factory.Code().createConstructorCall(utilsType);
        return factory.Code().createLocalVariable(utilsType, "utils", constructorCall);
    }

    private CtInvocation<?> createGetSnapshotInvocation(Factory factory, CtExpression<?> target, CtConstructor<?> constructor) {
        CtTypeReference<?> registerUtilsType = factory.Type().createReference(PKG);
        CtExecutableReference<?> getSnapshotMethod = factory.Executable().createReference(
                registerUtilsType,
                factory.Type().voidPrimitiveType(),
                "getSnapshot"
        );

        CtThisAccess<?> thisAccess = factory.Code().createThisAccess(constructor.getDeclaringType().getReference());

        return factory.Code().createInvocation(
                target,
                getSnapshotMethod,
                thisAccess
        );
    }

    private CtInvocation<?> createWriteConstructorContextInvocation(Factory factory, CtExpression<?> target) {
        CtTypeReference<?> registerUtilsType = factory.Type().createReference(PKG);
        CtExecutableReference<?> writeConstructorContextMethod = factory.Executable().createReference(
                registerUtilsType,
                factory.Type().voidPrimitiveType(),
                "writeConstructorContext"
        );

        return factory.Code().createInvocation(
                target,
                writeConstructorContextMethod
        );
    }



    private CtInvocation<?> createInitConstructorContextInvocation(CtExpression<?> target, String fileName, String className, String constructorName, List<String> constructorParameters) {
        Factory factory = getFactory();
        CtTypeReference<?> sendUtilsType = factory.Type().createReference(PKG);
        CtExecutableReference<?> initConstructorMethodRef = factory.Executable().createReference(
                sendUtilsType,
                factory.Type().voidPrimitiveType(),
                "initConstructorContext"
        );
        CtExpression<ArrayList> parameterListLiteral = new MethodInstrumentationProcessor().createParameterListLiteral(factory, constructorParameters);


        return factory.Code().createInvocation(
                target,
                initConstructorMethodRef,
                factory.Code().createLiteral(fileName),
                factory.Code().createLiteral(className),
                factory.Code().createLiteral(constructorName),
                parameterListLiteral
        );
    }

    private CtInvocation<?> createAddAttributeMethodInvocation(Factory factory, CtExpression<?> target, String fieldName, String fieldType, CtFieldAccess<?> fieldAccess) {
        CtTypeReference<?> registerUtilsType = factory.Type().createReference(PKG);
        CtTypeReference<Void> voidType = factory.Type().voidPrimitiveType();
        CtExecutableReference<?> addAttributeMethod = factory.Executable().createReference(
                registerUtilsType,
                voidType,
                "addAttribute"
        );
        return factory.Code().createInvocation(
                target,
                addAttributeMethod,
                factory.Code().createLiteral(fieldName),
                factory.Code().createLiteral(fieldType),
                fieldAccess
        );
    }
}
