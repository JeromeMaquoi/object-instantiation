package be.unamur.snail;

import spoon.processing.AbstractProcessor;
import spoon.reflect.code.*;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.filter.TypeFilter;

import java.util.stream.Collectors;

public class ConstructorInstrumentationProcessor extends AbstractProcessor<CtConstructor<?>> {
//    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private static final String PKG = "be.unamur.snail.register.SendUtils";

    @Override
    public void process(CtConstructor<?> constructor) {
        if (constructor.getBody() == null) return;

        Factory factory = getFactory();
        String constructorSignature = getSimpleConstructorSignature(constructor);
        String className = constructor.getDeclaringType().getQualifiedName();

        String fileName = "Unknown File";
        if (constructor.getPosition() != null && constructor.getPosition().getFile() != null) {
            fileName = constructor.getPosition().getFile().getPath();
        }

        CtInvocation<?> initConstructorInvocation = createInitConstructorEntityDTOInvocation(factory, constructorSignature, className, fileName);
        constructor.getBody().insertBegin(initConstructorInvocation);

        for (CtAssignment<?, ?> assignment : constructor.getBody().getElements(new TypeFilter<>(CtAssignment.class))) {
            if (assignment.getAssigned() instanceof CtFieldAccess<?> fieldAccess && (fieldAccess.getTarget() instanceof CtThisAccess<?> || fieldAccess.getTarget() == null)) {
                String fieldName = fieldAccess.getVariable().getSimpleName();
                String fieldType = fieldAccess.getVariable().getType().getQualifiedName();

                CtInvocation<?> prepareMethodInvocation = createAddAttributeMethodInvocation(factory, fieldName, fieldType);
                assignment.insertAfter(prepareMethodInvocation);
            }
        }
        CtInvocation<?> sendInvocation = createSendMethodInvocation(factory);
        constructor.getBody().addStatement(sendInvocation);
    }

    private String getSimpleConstructorSignature(CtConstructor<?> constructor) {
        return constructor.getDeclaringType().getSimpleName() + "(" + constructor.getParameters().stream().map(param -> param.getType().getQualifiedName()).collect(Collectors.joining(", ")) + ")";
    }

    private CtInvocation<?> createInitConstructorEntityDTOInvocation(Factory factory, String constructorSignature, String className, String fileName) {
        CtTypeReference<?> registerUtilsType = factory.Type().createReference(PKG);
        CtExecutableReference<?> initConstructorMethod = factory.Executable().createReference(
                registerUtilsType,
                factory.Type().voidPrimitiveType(),
                "initConstructorEntityDTO"
        );
        return factory.Code().createInvocation(
                factory.Code().createTypeAccess(registerUtilsType),
                initConstructorMethod,
                factory.Code().createLiteral(constructorSignature),
                factory.Code().createLiteral(className),
                factory.Code().createLiteral(fileName)
        );
    }

    private CtInvocation<?> createAddAttributeMethodInvocation(Factory factory, String fieldName, String fieldType) {
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
                factory.Code().createLiteral(fieldType)
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
