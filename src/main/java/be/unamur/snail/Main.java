package be.unamur.snail;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spoon.Launcher;
import spoon.reflect.CtModel;
import spoon.reflect.code.CtAssignment;
import spoon.reflect.code.CtExpression;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtField;
import spoon.reflect.reference.CtFieldReference;

import java.util.HashSet;
import java.util.Set;

public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(final String[] args) {
        if (args == null || args.length < 2) {
            log.error("Not enough arguments provided. Exiting the application");
            System.exit(1);
        }
        String inputPath = args[0];
        String outputPath = args[1];

        Launcher launcher = new Launcher();
        launcher.addInputResource(inputPath);
        launcher.getEnvironment().setAutoImports(true);
        launcher.addProcessor(new ConstructorInstrumentationProcessor());
        launcher.setSourceOutputDirectory(outputPath);
        launcher.run();
    }

    /*public static void main(String[] args) {
        if (args == null || args.length < 2) {
            log.error("Not enough arguments provided. Exiting the application");
            System.exit(1);
        }
        String inputPath = args[0];
        String outputPath = args[1];

        Launcher launcher = new Launcher();
        launcher.addInputResource(inputPath);
        launcher.buildModel();

        CtModel model = launcher.getModel();
        model.getAllTypes().stream()
                .filter(ctType -> ctType instanceof CtClass<?>)
                .map(ctType -> (CtClass<?>) ctType)
                .forEach(Main::analyzeClass);
    }

    private static void analyzeClass(CtClass<?> ctClass) {
        log.info("Analyzing class {}", ctClass.getQualifiedName());

        for (CtConstructor<?> constructor : ctClass.getConstructors()) {
            log.info("Constructor: {}", constructor.getSignature());
            int initializedFields = countInitializedFields(constructor, ctClass);
            log.info("Initialized fields: {}\n\n", initializedFields);
        }
    }

    private static int countInitializedFields(CtConstructor<?> constructor, CtClass<?> ctClass) {
        Set<CtFieldReference<?>> initializedFields = new HashSet<>();

        constructor.getBody().getStatements().stream()
                .filter(statement -> statement instanceof CtAssignment<?,?>)
                .map(statement -> (CtAssignment) statement)
                .forEach(assignment -> {
                    CtExpression<?> lhs = assignment.getAssigned();
                    if (lhs instanceof CtFieldReference && isFieldOfClass((CtFieldReference<?>) lhs, ctClass)) {
                        initializedFields.add((CtFieldReference<?>) lhs);
                    }
                });
        return initializedFields.size();
    }

    private static boolean isFieldOfClass(CtFieldReference<?> fieldRef, CtClass<?> ctClass) {
        // Check if the field reference belongs to the given class
        return ctClass.getFields().stream()
                .map(CtField::getReference)
                .anyMatch(field -> field.equals(fieldRef));
    }*/

}