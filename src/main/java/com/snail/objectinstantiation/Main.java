package com.snail.objectinstantiation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spoon.Launcher;
import spoon.reflect.CtModel;
import spoon.reflect.code.CtConstructorCall;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.visitor.filter.TypeFilter;

import java.util.Map;

public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        if (args == null || args.length < 1) {
            log.error("No argument provided. Exiting the application");
            System.exit(1);
        }

        String path = args[0];

        Launcher launcher = new Launcher();
        launcher.addInputResource(path);
        launcher.buildModel();

        CtModel model = launcher.getModel();
        InstantiationDepthVisitor visitor = new InstantiationDepthVisitor();

        for (CtClass<?> clazz : model.getElements(new TypeFilter<>(CtClass.class))) {
            //log.info(clazz.toString());
            for (CtConstructor<?> constructorCall : clazz.getConstructors()) {
                visitor.visitCtConstructor(constructorCall);
            }
        }

        Map<CtConstructor<?>, Integer> constructorDepthMap = visitor.getConstructorDepthMap();

        for (Map.Entry<CtConstructor<?>, Integer> entry : constructorDepthMap.entrySet()) {
            log.info("Constructor: {}, Depth: {}", entry.getKey().get, entry.getValue());
        }
    }
}