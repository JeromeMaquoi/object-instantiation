package be.unamur.snail.objectinstantiation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spoon.reflect.code.*;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;
import spoon.reflect.visitor.CtScanner;

import java.util.ArrayList;
import java.util.List;

public class FieldWriterTransformer extends CtScanner {
    private final Logger log = LoggerFactory.getLogger(FieldWriterTransformer.class);

    private final Factory factory;

    public FieldWriterTransformer(Factory factory) {
        this.factory = factory;
    }

    @Override
    public <T> void visitCtFieldWrite(CtFieldWrite<T> fieldWrite) {
        CtExecutable<?> parentExecutable = fieldWrite.getParent(CtExecutable.class);
        if (parentExecutable instanceof CtMethod<?>) {
            CtClass<?> containingClass = fieldWrite.getParent(CtClass.class);
            if (containingClass != null) {
                log.info("class : {}, field : {}", containingClass.getSimpleName(), fieldWrite);
                CtInvocation<Void> registerCall = createRegisterCall(fieldWrite);
//                CtStatement parentStatement = fieldWrite.getParent(CtStatement.class);
//                if (parentStatement instanceof CtAssignment<?,?>) {
//                    parentStatement.replace(registerCall);
//                }
                log.info("==============================\n");
            }
        }
        super.visitCtFieldWrite(fieldWrite);
    }

    private <T, A extends T> CtInvocation<Void> createRegisterCall(CtFieldWrite<A> fieldWrite) {
        CtInvocation<Void> registerCall = factory.createInvocation();
        CtType<?> declaringType = fieldWrite.getVariable().getDeclaringType().getDeclaration();
        if (declaringType instanceof CtClass) {
            CtClass<?> declaringClass = (CtClass<?>) declaringType;
            log.info("Class of 'this' : {}", declaringClass.getQualifiedName());

            // Getting the value of "this"
            CtExpression<?> target = fieldWrite.getTarget();

            // Get the assignement expression
            CtAssignment<T, A> assignment = fieldWrite.getParent(CtAssignment.class);
            CtExpression<A> assignmentExpression = assignment.getAssignment();
            log.info("assignmentExpression : {}", assignmentExpression);

            List<CtExpression<?>> arguments = new ArrayList<>();
            arguments.add(target);
            arguments.add(assignment);

            // Set the target and arguments for the "register" invocation
            registerCall.setExecutable(factory.createExecutableReference()
                    .setDeclaringType(factory.Type().createReference("be.unamur.snail.objectinstantiation"))
                    .setSimpleName("register"));
            registerCall.setArguments(arguments);

            log.info(arguments.toString());
        }
        return registerCall;
    }
}
