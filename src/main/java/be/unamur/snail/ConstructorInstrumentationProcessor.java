package be.unamur.snail;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtBlock;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtType;

import java.util.HashSet;
import java.util.Set;

public class ConstructorInstrumentationProcessor extends AbstractProcessor<CtConstructor<?>> {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    public void process(CtConstructor<?> constructor) {
        log.info("Constructor : {}", constructor.getSignature());
        CtType<?> declaringType = constructor.getDeclaringType();
        log.info("Declaring type : {}\n", declaringType.toString());

        CtBlock<?> body = constructor.getBody();
        if (body == null) return;

        // Collect the fields of the declaring class
//        Set<String> fieldNames = new HashSet<>();
//        for (CtField<?> field : declaringClass.getFields()) {
//            fieldNames.add(field.getSimpleName());
//        }
    }
}
