package be.unamur.snail.objectinstantiation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spoon.Launcher;
import spoon.reflect.CtModel;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.compiler.FileSystemFolder;

import java.util.Map;

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
        launcher.addInputResource(new FileSystemFolder(inputPath));
        launcher.buildModel();

        CtModel model = launcher.getModel();
        FieldWriterTransformer transformer = new FieldWriterTransformer(launcher.getFactory());

        for (CtClass<?> clazz : model.getElements(new TypeFilter<>(CtClass.class))) {
            clazz.accept(transformer);
        }

        // Save the modified model to the output directory
        log.info("Setting source output directory to {}", outputPath);
        launcher.setSourceOutputDirectory(outputPath);
        launcher.prettyprint();
        log.info("Finished output to directory !");

        /*CtModel model = launcher.getModel();
        ObjectCreationCounterVisitor visitor = new ObjectCreationCounterVisitor();

        for (CtClass<?> clazz : model.getElements(new TypeFilter<>(CtClass.class))) {
            for (CtConstructor<?> constructorCall : clazz.getConstructors()) {
                visitor.visitCtConstructor(constructorCall);
            }
        }

        Map<CtConstructor<?>, Integer> constructorDepthMap = visitor.getConstructorDepthMap();

        for (Map.Entry<CtConstructor<?>, Integer> entry : constructorDepthMap.entrySet()) {
            if (entry.getValue() > 1) {
                log.info("Constructor: {}, Depth: {}", entry.getKey().getSignature(), entry.getValue());
            }
        }*/
    }
}