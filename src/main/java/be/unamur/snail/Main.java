package be.unamur.snail;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spoon.Launcher;
import spoon.reflect.visitor.DefaultJavaPrettyPrinter;
import spoon.support.JavaOutputProcessor;
import spoon.support.sniper.SniperJavaPrettyPrinter;

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
        launcher.setSourceOutputDirectory(outputPath);
        launcher.getEnvironment().setShouldCompile(true);
        launcher.getEnvironment().setAutoImports(true);
        launcher.getEnvironment().useTabulations(true);
        launcher.getEnvironment().setComplianceLevel(17);

        // Apply transformation to the constructors with new instances initialization
        launcher.addProcessor(new FieldInitializationProcessor());
        //launcher.addProcessor(new ClassTest());

        // Preserve the original formatting and imports
        launcher.getEnvironment().setPrettyPrinterCreator(() -> {
            return new SniperJavaPrettyPrinter(launcher.getEnvironment());
        });

        launcher.buildModel();
        launcher.process();
        launcher.prettyprint();
        log.info("Finished output to directory !");
    }
}