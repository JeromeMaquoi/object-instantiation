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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ConstructorInstrumentationProcessor extends AbstractProcessor<CtConstructor<?>> {
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private static final String LOG_COUNT_FILE_PATH = "assignment_counts.csv";

    @Override
    public void process(CtConstructor<?> constructor) {
        //log.info("Constructor : {}", constructor.getSignature());
        if (constructor.getBody() == null) return;

        int assignmentCount = 0;
        Factory factory = getFactory();
        String constructorSignature = constructor.getSignature();
        String constructorName = constructor.getSimpleName();
        String className = constructor.getDeclaringType().getSimpleName();

        String fileName = "Unknown File";
        if (constructor.getPosition() != null && constructor.getPosition().getFile() != null) {
            fileName = constructor.getPosition().getFile().getPath();
        }

        for (CtAssignment<?, ?> assignment : constructor.getBody().getElements(new TypeFilter<>(CtAssignment.class))) {
            if (assignment.getAssigned() instanceof CtFieldAccess<?> fieldAccess && (fieldAccess.getTarget() instanceof CtThisAccess<?> || fieldAccess.getTarget() == null)) {
                    assignmentCount++;
                    String fieldName = fieldAccess.getVariable().getSimpleName();
                    String fieldType = fieldAccess.getVariable().getType().getQualifiedName();

                    CtTypeReference<?> declaringTypeReference = constructor.getDeclaringType().getReference();
                    //CtExpression<?> thisReference = factory.createThisAccess(declaringTypeReference);
                    CtInvocation<?> registerInvocation = createRegisterInvocation(factory, assignment, constructorSignature, constructorName, className, fileName, fieldName, fieldType);
                    //log.info("registerInvocation: {}", registerInvocation);
                    assignment.replace(registerInvocation);
                }
        }
        //writeCountToFile(constructor.getSignature(), assignmentCount);
    }

    private CtInvocation<?> createRegisterInvocation(Factory factory, CtAssignment<?, ?> assignment, String constructorSignature, String constructorName, String className, String fileName, String fieldName, String fieldType) {
        CtTypeReference<?> registerUtilsType = factory.Type().createReference("be.unamur.snail.register.RegisterUtils");
        CtTypeReference<Void> voidType = factory.Type().voidPrimitiveType();
        CtExecutableReference<?> registerMethod = factory.Executable().createReference(
                registerUtilsType,
                voidType,
                "register"
        );
        CtAssignment<?, ?> newAssignment = assignment.clone();
        return factory.Code().createInvocation(
                factory.Code().createTypeAccess(registerUtilsType),
                registerMethod,
                newAssignment,
                factory.Code().createLiteral(constructorSignature),
                factory.Code().createLiteral(constructorName),
                factory.Code().createLiteral(className),
                factory.Code().createLiteral(fileName),
                factory.Code().createLiteral(fieldName),
                factory.Code().createLiteral(fieldType)
        );
    }

    /*private void writeCountToFile(String constructorSignature, int count) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(LOG_COUNT_FILE_PATH, true))) {
            log.info("Writing {} to CSV file", constructorSignature);
            File csvFile = new File(LOG_COUNT_FILE_PATH);
            if (csvFile.length() == 0) {
                writer.write("Constructor,Assignment Count");
                writer.newLine();
            }
            writer.write(String.format("\"%s\",%d", constructorSignature, count));
            writer.newLine();
        } catch (IOException e) {
            log.error("Failed to write counts to CSV: {}", e.getMessage());
        }
    }*/
}
