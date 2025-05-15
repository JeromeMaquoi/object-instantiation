package be.unamur.snail.objectinstantiation;

import be.unamur.snail.JsonIdentityAnnotationProcessor;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import spoon.Launcher;
import spoon.reflect.CtModel;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtType;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JsonIdentityAnnotationProcessorIT {
    private Launcher launcher;

    @TempDir
    Path tempDir;

    Path inputPath;

    Path outputPath;

    @BeforeEach
    void setup() {
        inputPath = Paths.get("src/test/resources/test-inputs/TestConstructorClassWithAssignments.java");
        outputPath = tempDir.resolve("output");

        launcher = new Launcher();
        launcher.addInputResource(inputPath.toString());
        launcher.setSourceOutputDirectory(outputPath.toString());
        launcher.addProcessor(new JsonIdentityAnnotationProcessor());
        launcher.run();
    }

    @Test
    void annotationShouldBeAddedToClassTest() throws IOException {
        Path outputFile = outputPath.resolve("test/TestConstructorClassWithAssignments.java");
        assertTrue(Files.exists(outputFile), "Output file should be generated");

        CtModel model = launcher.getModel();
        CtType<?> ctClass = model.getAllTypes().stream()
                .filter(t -> t.getSimpleName().equals("TestConstructorClassWithAssignments"))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Could not find TestConstructorClassWithAssignments"));

        List<CtAnnotation<?>> matchingAnnotations = ctClass.getAnnotations().stream()
                .filter(a -> a.getAnnotationType().getQualifiedName().equals(JsonIdentityInfo.class.getName()))
                .toList();
        assertEquals(1, matchingAnnotations.size());

        CtAnnotation<?> annotation = matchingAnnotations.get(0);
        assertEquals(JsonIdentityInfo.class.getName(), annotation.getAnnotationType().getQualifiedName());
        assertTrue(annotation.getValues().get("generator").toString().contains("IntSequenceGenerator"));
        assertEquals("\"@id\"", annotation.getValues().get("property").toString());

        String fileContent = Files.readString(outputFile);
        System.out.println("fileContent: " + fileContent);
    }
}
