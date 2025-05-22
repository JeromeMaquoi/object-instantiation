package be.unamur.snail.objectinstantiation;

import be.unamur.snail.ConstructorInstrumentationProcessor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import spoon.Launcher;
import spoon.reflect.CtModel;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.factory.Factory;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.compiler.VirtualFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.assertj.core.api.Assertions.*;

class ConstructorInstrumentationProcessorIT {
    private Launcher launcher;

    @TempDir
    Path tempDir;

    Path inputPath;

    Path outputPath;

    @BeforeEach
    void setup() {
        inputPath = Paths.get("src/test/resources/test-inputs/");
        outputPath = tempDir.resolve("output");

        launcher = new Launcher();
        launcher.addInputResource(inputPath.toString());
        launcher.setSourceOutputDirectory(outputPath.toString());
        launcher.addProcessor(new ConstructorInstrumentationProcessor());
        launcher.run();
    }

    @Test
    void constructorWithAssignmentsTest() throws IOException {
        String className = "TestConstructorClassWithAssignments";
        Path outputFile = outputPath.resolve("test/"+className+".java");
        assertTrue(Files.exists(outputFile), "Output file should be generated");

        CtModel model = launcher.getModel();
        CtClass<?> clazz = model.getElements(new TypeFilter<>(CtClass.class))
                .stream()
                .filter(c -> c.getSimpleName().equals(className))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Class "+className+" not found in the model"));
        CtConstructor<?> constructor = clazz.getConstructors().iterator().next();

        boolean containsSendUtils = constructor.getBody()
                .getStatements()
                .stream()
                .filter(stm -> stm instanceof CtLocalVariable<?>)
                .map(stmt -> (CtLocalVariable<?>) stmt)
                .anyMatch(var -> var.getSimpleName().equals("utils") && var.getType().getQualifiedName().equals("be.unamur.snail.register.SendUtils"));
        assertTrue(containsSendUtils, "Constructor should contain 'SendUtils utils = new SendUtils();'");

        long initConstructorInvocationCount = constructor.getBody()
                .getElements(new TypeFilter<>(CtInvocation.class))
                .stream()
                .filter(inv -> inv.getExecutable().getSimpleName().equals("initConstructorContext"))
                .count();
        assertEquals(1, initConstructorInvocationCount, "Constructor should contain one 'initConstructorContext' invocation");

        long writeConstructorContextInvocationCount = constructor.getBody()
                .getElements(new TypeFilter<>(CtInvocation.class))
                .stream()
                .filter(inv -> inv.getExecutable().getSimpleName().equals("writeConstructorContext"))
                .count();
        assertEquals(1, writeConstructorContextInvocationCount, "Constructor should contain one 'writeConstructorContext' invocation");

        long addAttributeInvocationCount = constructor.getBody()
                .getElements(new TypeFilter<>(CtInvocation.class))
                .stream()
                .filter(inv -> inv.getExecutable().getSimpleName().equals("addAttribute"))
                .count();
        assertEquals(2, addAttributeInvocationCount, "Constructor should contain 2 'addAttribute' invocations");

        long getSnapshotInvocationCount = constructor.getBody()
                .getElements(new TypeFilter<>(CtInvocation.class))
                .stream()
                .filter(inv -> inv.getExecutable().getSimpleName().equals("getSnapshot"))
                .count();
        assertEquals(0, getSnapshotInvocationCount, "Constructor should contain 2 'getSnapshot' invocations");

        String fileContent = Files.readString(outputFile);
        System.out.println("Generated file content:\n" + fileContent);
    }

    @Test
    void constructorWithoutAssignmentsTest() throws IOException {
        String className = "TestEmptyConstructorClass";
        Path outputFile = outputPath.resolve("test/empty/"+className+".java");
        assertTrue(Files.exists(outputFile), "Output file should be generated");

        CtModel model = launcher.getModel();
        CtClass<?> clazz = model.getElements(new TypeFilter<>(CtClass.class))
                .stream()
                .filter(c -> c.getSimpleName().equals(className))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Class TestEmptyConstructorClass not found in the model"));
        CtConstructor<?> constructor = clazz.getConstructors().iterator().next();

        boolean containsSendUtils = constructor.getBody()
                .getStatements()
                .stream()
                .filter(stm -> stm instanceof CtLocalVariable<?>)
                .map(stmt -> (CtLocalVariable<?>) stmt)
                .anyMatch(var -> var.getSimpleName().equals("utils") && var.getType().getQualifiedName().equals("be.unamur.snail.register.SendUtils"));
        assertTrue(containsSendUtils, "Constructor should contain 'SendUtils utils = new SendUtils();'");

        long initConstructorInvocationCount = constructor.getBody()
                .getElements(new TypeFilter<>(CtInvocation.class))
                .stream()
                .filter(inv -> inv.getExecutable().getSimpleName().equals("initConstructorContext"))
                .count();
        assertEquals(1, initConstructorInvocationCount, "Constructor should contain one 'initConstructorContext' invocation");

        long writeConstructorContextInvocationCount = constructor.getBody()
                .getElements(new TypeFilter<>(CtInvocation.class))
                .stream()
                .filter(inv -> inv.getExecutable().getSimpleName().equals("writeConstructorContext"))
                .count();
        assertEquals(1, writeConstructorContextInvocationCount, "Constructor should contain one 'writeConstructorContext' invocation");

        long addAttributeInvocationCount = constructor.getBody()
                .getElements(new TypeFilter<>(CtInvocation.class))
                .stream()
                .filter(inv -> inv.getExecutable().getSimpleName().equals("addAttribute"))
                .count();
        assertEquals(0, addAttributeInvocationCount, "Constructor without assignments should not contain 'addAttribute' invocations");

        long getSnapshotInvocationCount = constructor.getBody()
                .getElements(new TypeFilter<>(CtInvocation.class))
                .stream()
                .filter(inv -> inv.getExecutable().getSimpleName().equals("getSnapshot"))
                .count();
        assertEquals(0, getSnapshotInvocationCount, "Constructor should contain 2 'getSnapshot' invocations");

        String fileContent = Files.readString(outputFile);
        System.out.println("Generated file content:\n" + fileContent);
    }


    @Test
    void literalAssignmentTest() {
        String code = """
            public class LiteralExample {
                private int value;
                public LiteralExample() {
                    this.value = 42;
                }
            }
            """;

        DummyProcessor processor = new DummyProcessor();
        processClassWithProcessor(code, processor);

        assertEquals(1, processor.foundTypes.size());
        assertTrue(processor.foundTypes.contains("literal"));
    }

    @Test
    void constructorCallAssignmentTest() {
        String code = """
            public class ConstructorCallExample {
                private String text;
                public ConstructorCallExample() {
                    this.text = new String("abc");
                }
            }
        """;
        DummyProcessor processor = new DummyProcessor();
        processClassWithProcessor(code, processor);

        assertTrue(processor.foundTypes.contains("constructor call"));
    }

    @Test
    void variableAssignmentTest() {
        String code = """
            public class VariableExample {
                private int x;
                public VariableExample() {
                    int temp = 10;
                    this.x = temp;
                }
            }
        """;
        DummyProcessor processor = new DummyProcessor();
        processClassWithProcessor(code, processor);

        assertEquals(1, processor.foundTypes.size());
        assertThat(processor.foundTypes).contains("variable");
    }

    @Test
    void parameterAssignmentTest() {
        String code = """
            public class ParameterExample {
                private int x;
                public ParameterExample(int x) {
                    this.x = x;
                }
            }
        """;

        DummyProcessor processor = new DummyProcessor();
        processClassWithProcessor(code, processor);

        assertEquals(1, processor.foundTypes.size());
        assertThat(processor.foundTypes).contains("constructor parameter");
    }

    @Test
    void methodCallAssignmentTest() {
        String code = """
            public class MethodCallExample {
                private String val;
                public MethodCallExample() {
                    this.val = getValue();
                }
                private String getValue() { return "ok"; }
            }
        """;

        DummyProcessor processor = new DummyProcessor();
        processClassWithProcessor(code, processor);

        assertEquals(1, processor.foundTypes.size());
        assertThat(processor.foundTypes).contains("method call");
    }

    @Test
    void parameterAndConstructorCallAssignmentTest() {
        String code = """
            public class ParameterAndConstructorCallExample {
                private String a;
                private String b;
                public ParameterAndConstructorCallExample(String a) {
                    this.a = a;
                    this.b = new String("ok");
                }
            }
        """;

        DummyProcessor processor = new DummyProcessor();
        processClassWithProcessor(code, processor);

        assertEquals(2, processor.foundTypes.size());
        assertThat(processor.foundTypes).containsExactlyInAnyOrder("constructor parameter", "constructor call");
    }

    @Test
    void constructorCallAndVariableAssignmentTest() {
        String code = """
            public class ClassExample {
                private String a;
                private String b;
                public ClassExample() {
                    String temp = "ok";
                    this.a = temp;
                    this.b = new String("ok");
                }
            }
        """;

        DummyProcessor processor = new DummyProcessor();
        processClassWithProcessor(code, processor);

        assertEquals(2, processor.foundTypes.size());
        assertThat(processor.foundTypes).containsExactlyInAnyOrder("variable", "constructor call");
    }

    private void processClassWithProcessor(String classCode, DummyProcessor processor) {
        Launcher launcher = new Launcher();
        launcher.addInputResource(new VirtualFile(classCode));
        launcher.buildModel();
        Factory factory = launcher.getFactory();
        processor.setFactory(factory);

        List<? extends CtClass<?>> classes = launcher.getFactory().Class().getAll().stream()
                .filter(t -> t instanceof CtClass<?>)
                .map(t -> ((CtClass<?>) t))
                .toList();
        for (CtClass<?> clazz : classes) {
            for (CtConstructor<?> constructor : clazz.getConstructors()) {
                processor.process(constructor);
            }
        }
    }

    static class DummyProcessor extends ConstructorInstrumentationProcessor {
        List<String> foundTypes = new ArrayList<>();

        @Override
        public String getRightHandSideExpression(CtExpression<?> expression, CtConstructor<?> constructor) {
            String type = super.getRightHandSideExpression(expression, constructor);
            foundTypes.add(type);
            return type;
        }
    }
}
