package be.unamur.snail.objectinstantiation;

import be.unamur.snail.ConstructorInstrumentationProcessor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import spoon.Launcher;
import spoon.reflect.CtModel;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.visitor.filter.TypeFilter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConstructorInstrumentationProcessorTest {
    private Launcher launcher;

    @TempDir
    Path tempDir;

    Path inputPath;

    Path outputPath;

    @BeforeEach
    public void setup() {
        inputPath = Paths.get("src/test/resources/test-inputs/");
        outputPath = tempDir.resolve("output");

        launcher = new Launcher();
        launcher.addInputResource(inputPath.toString());
        launcher.setSourceOutputDirectory(outputPath.toString());
        launcher.addProcessor(new ConstructorInstrumentationProcessor());
        launcher.run();
    }

    @Test
    void simpleConstructorTest() throws IOException {
        Path outputFile = outputPath.resolve("TestConstructorClassWithAssignments.java");
        assertTrue(Files.exists(outputFile), "Output file should be generated");

        CtModel model = launcher.getModel();
        CtClass<?> clazz = model.getElements(new TypeFilter<>(CtClass.class)).get(0);
        CtConstructor<?> constructor = clazz.getConstructors().iterator().next();

        long initConstructorInvocationCount = constructor.getBody()
                .getElements(new TypeFilter<>(CtInvocation.class))
                .stream()
                .filter(inv -> inv.getExecutable().getSimpleName().equals("initConstructorEntityDTO"))
                .count();
        assertEquals(1, initConstructorInvocationCount, "Constructor should contain one 'initConstructorEntityDTO' invocation");

        long sendInvocationCount = constructor.getBody()
                .getElements(new TypeFilter<>(CtInvocation.class))
                .stream()
                .filter(inv -> inv.getExecutable().getSimpleName().equals("send"))
                .count();
        assertEquals(1, sendInvocationCount, "Constructor should contain one 'send' invocation");

        long addAttributeInvocationCount = constructor.getBody()
                .getElements(new TypeFilter<>(CtInvocation.class))
                .stream()
                .filter(inv -> inv.getExecutable().getSimpleName().equals("addAttribute"))
                .count();
        assertEquals(2, addAttributeInvocationCount, "Constructor should contain 2 'addAttribute' invocations");

        String fileContent = Files.readString(outputFile);
        System.out.println("Generated file content:\n" + fileContent);
    }

    @Test
    void constructorWithoutAssignmentsTest() throws IOException {
        Path outputFile = outputPath.resolve("TestEmptyConstructorClass.java");
        assertTrue(Files.exists(outputFile), "Output file should be generated");

        CtModel model = launcher.getModel();
        CtClass<?> clazz = model.getElements(new TypeFilter<>(CtClass.class)).get(0);
        CtConstructor<?> constructor = clazz.getConstructors().iterator().next();

        long initConstructorInvocationCount = constructor.getBody()
                .getElements(new TypeFilter<>(CtInvocation.class))
                .stream()
                .filter(inv -> inv.getExecutable().getSimpleName().equals("initConstructorEntityDTO"))
                .count();
        assertEquals(1, initConstructorInvocationCount, "Constructor should contain one 'initConstructorEntityDTO' invocation");

        long sendInvocationCount = constructor.getBody()
                .getElements(new TypeFilter<>(CtInvocation.class))
                .stream()
                .filter(inv -> inv.getExecutable().getSimpleName().equals("send"))
                .count();
        assertEquals(1, sendInvocationCount, "Constructor should contain one 'send' invocation");

        long addAttributeInvocationCount = constructor.getBody()
                .getElements(new TypeFilter<>(CtInvocation.class))
                .stream()
                .filter(inv -> inv.getExecutable().getSimpleName().equals("addAttribute"))
                .count();
        assertEquals(0, addAttributeInvocationCount, "Constructor without assignments should not contain 'addAttribute' invocations");

        String fileContent = Files.readString(outputFile);
        System.out.println("Generated file content:\n" + fileContent);
    }
}
