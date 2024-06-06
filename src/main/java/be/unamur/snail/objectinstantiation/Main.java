package be.unamur.snail.objectinstantiation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spoon.Launcher;

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

        launcher.getEnvironment().setNoClasspath(true);

        launcher.addProcessor(new FieldInitializationProcessor());
        launcher.buildModel();
        launcher.process();
        launcher.prettyprint();
        log.info("Finished output to directory !");
    }
}