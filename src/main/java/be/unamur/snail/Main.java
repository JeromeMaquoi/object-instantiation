package be.unamur.snail;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spoon.Launcher;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(final String[] args) throws IOException {
        if (args == null || args.length < 3) {
            log.error("Not enough arguments provided. Exiting the application");
            System.exit(1);
        }
        String inputPath = args[0];
        String outputPath = args[1];
        String inputRepoPath = args[2];

        Launcher launcher = new Launcher();
        launcher.addInputResource(inputPath);
        launcher.setSourceOutputDirectory(outputPath);

        List<String> classpaths = Files.readAllLines(Paths.get(inputRepoPath + "/classpath.txt"));
        launcher.getEnvironment().setSourceClasspath(classpaths.toArray(new String[0]));

        launcher.addProcessor(new ConstructorInstrumentationProcessor());
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