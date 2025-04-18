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
        launcher.addProcessor(new MethodInstrumentationProcessor());
        launcher.run();
    }
}