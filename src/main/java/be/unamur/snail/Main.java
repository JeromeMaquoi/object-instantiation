package be.unamur.snail;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spoon.Launcher;
import spoon.reflect.visitor.DefaultJavaPrettyPrinter;
import spoon.support.JavaOutputProcessor;
import spoon.support.sniper.SniperJavaPrettyPrinter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        //launcher.getEnvironment().setShouldCompile(true);
        //launcher.getEnvironment().setAutoImports(true);
        //launcher.getEnvironment().useTabulations(true);

        launcher.getEnvironment().setComplianceLevel(19);
        launcher.getEnvironment().setPrettyPrinterCreator(() -> new SniperJavaPrettyPrinter(launcher.getEnvironment()));

        //handleDifferentProject(launcher, inputPath);

        // Apply transformation to the constructors with new instances initialization
        //launcher.addProcessor(new TryWithResourceProcessor());
        //launcher.addProcessor(new FieldInitializationProcessor());

        launcher.run();
        log.info("Finished output to directory !");
    }

    private static void handleDifferentProject(Launcher launcher, String inputPath) {
        if (inputPath.contains("spring-boot")) {
            log.info("Spring-boot");
            launcher.getEnvironment().setComplianceLevel(19);

            File inputDir = new File(inputPath);
            Path input = Paths.get(inputDir.toURI());
            try (Stream<Path> walk = Files.walk(input)) {
                List<Path> files = walk
                        .filter(Files::isRegularFile)
                        .filter(p -> p.toString().toLowerCase().endsWith(".java"))
                        .toList();

                if (!files.isEmpty()) {
                    for (Path filePath : files) {
                        String name = filePath.getFileName().toString();
                        if (name.equals("JksSslStoreDetails.java")) {
                            log.info("Processing file : {}", name);
                            launcher.getEnvironment().setPrettyPrinterCreator(() -> new DefaultJavaPrettyPrinter(launcher.getEnvironment()));
                        } else {
                            launcher.getEnvironment().setPrettyPrinterCreator(() -> new SniperJavaPrettyPrinter(launcher.getEnvironment()));
                        }
                    }
                } else {
                    log.info("files null");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            log.info("TODO: handle {}", inputPath);
        }
    }
}