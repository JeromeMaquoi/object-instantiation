package be.unamur.snail;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spoon.processing.AbstractProcessor;
import spoon.reflect.code.*;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.filter.TypeFilter;

import java.util.HashSet;
import java.util.Set;

public class ConstructorInstrumentationProcessor extends AbstractProcessor<CtConstructor<?>> {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    public void process(CtConstructor<?> constructor) {
        log.info("Constructor : {}", constructor.getSignature());
        if (constructor.getBody() == null) return;

        Factory factory = getFactory();

        for (CtAssignment<?, ?> assignment : constructor.getBody().getElements(new TypeFilter<>(CtAssignment.class))) {
            if (assignment.getAssigned() instanceof CtFieldAccess<?> fieldAccess) {
                if (fieldAccess.getTarget() instanceof CtThisAccess<?> || fieldAccess.getTarget() == null) {
                    log.info(String.valueOf(assignment));
                    CtInvocation<?> registerInvocation = createRegisterInvocation(factory, assignment);
                    assignment.replace(registerInvocation);
                }
            }
        }
    }

    private CtInvocation<?> createRegisterInvocation(Factory factory, CtAssignment<?, ?> assignment) {
        // Create a reference to the "register" method
        CtTypeReference<?> registerUtilsType = factory.Type().createReference("be.unamur.snail.register.RegisterUtils");
        CtTypeReference<Void> voidType = factory.Type().voidPrimitiveType();
        // Create a static method reference to "register"
        CtInvocation<Void> invocation = factory.Core().createInvocation();

        invocation.setExecutable(
                factory.Executable().createReference(registerUtilsType, voidType, "register")
        );

        // Pass "this" as the first argument
        CtThisAccess<?> thisAccess = factory.Code().createThisAccess(factory.Type().createReference(assignment.getParent(CtConstructor.class).getDeclaringType()));
        invocation.addArgument(thisAccess);

        // Pass the original assignment as the second argument
        invocation.addArgument(assignment);

        return invocation;
    }
}
