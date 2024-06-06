package be.unamur.snail;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spoon.processing.AbstractProcessor;
import spoon.reflect.code.*;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.filter.TypeFilter;

public class FieldInitializationProcessor extends AbstractProcessor<CtConstructor<?>> {
    private final Logger log = LoggerFactory.getLogger(FieldInitializationProcessor.class);

    @Override
    public void process(CtConstructor<?> constructor) {
        Factory factory = getFactory();

        // Create a reference to "this"
        CtTypeReference<?> declaringTypeReference = constructor.getDeclaringType().getReference();
        CtExpression<?> thisReference = factory.createThisAccess(declaringTypeReference);

        // Iterate over all statements in the constructor's body
        for (CtAssignment<?,?> assignment : constructor.getBody().getElements(new TypeFilter<>(CtAssignment.class))) {
            CtExpression<?> assignedExpression = assignment.getAssignment();
            if (assignedExpression instanceof CtConstructorCall<?>) {
                // Create the assignment for the field
                CtAssignment<?,?> newAssignment = assignment.clone();

                CtTypeReference<?> registerUtilsType = factory.Type().createReference("be.unamur.snail.register.RegisterUtils");
                CtExecutableReference<?> registerMethod = factory.Executable().createReference(
                        registerUtilsType,
                        factory.Type().voidPrimitiveType(),
                        "register",
                        factory.Type().OBJECT,
                        factory.Type().OBJECT
                );
                CtInvocation<?> registerInvocation = factory.Code().createInvocation(
                        factory.Code().createTypeAccess(registerUtilsType),
                        registerMethod,
                        thisReference,
                        newAssignment
                );

                // Replace the original assignment with the register invocation
                assignment.replace(registerInvocation);
            }
        }
    }
}
