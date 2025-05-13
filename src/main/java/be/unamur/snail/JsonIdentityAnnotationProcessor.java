package be.unamur.snail;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import spoon.processing.AbstractProcessor;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.factory.Factory;

public class JsonIdentityAnnotationProcessor extends AbstractProcessor<CtClass<?>> {
    @Override
    public void process(final CtClass<?> ctClass) {
        if (ctClass.isEnum() || ctClass.getAnnotation(JsonIdentityInfo.class) != null) return;

        Factory factory = getFactory();
        CtAnnotation<JsonIdentityInfo> annotation = factory.Annotation().annotate(
                ctClass,
                JsonIdentityInfo.class
        );
        annotation.addValue("generator", ObjectIdGenerators.IntSequenceGenerator.class);
        annotation.addValue("property", "@id");
    }
}
