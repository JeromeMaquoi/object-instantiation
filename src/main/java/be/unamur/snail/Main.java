package be.unamur.snail;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spoon.Launcher;
import spoon.reflect.CtModel;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtConstructor;

public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
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
                .filter(ctType -> ctType instanceof CtClass)
                .map(ctType -> (CtClass<?>) ctType)
                .forEach(Main::analyzeClass);
    }

    private static void analyzeClass(CtClass<?> ctClass) {
        log.info("Analyzing class {}", ctClass.getQualifiedName());

        for (CtConstructor<?> constructor : ctClass.getConstructors()) {
            log.info("Constructor: {}", constructor.getSignature());
//            int initializedFields = countInitializedFields(constructor, ctClass);
//            log.info("Initialized fields: {}", initializedFields);
        }
    }

    private static int countInitializedFields(CtConstructor<?> ctConstructor, CtClass<?> ctClass) {
        return 0;
    }

}