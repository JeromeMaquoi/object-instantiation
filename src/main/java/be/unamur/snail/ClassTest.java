package be.unamur.snail;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtComment;
import spoon.reflect.code.CtComment.CommentType;
import spoon.reflect.declaration.CtClass;

public class ClassTest extends AbstractProcessor<CtClass<?>> {
    private final Logger log = LoggerFactory.getLogger(ClassTest.class);
    @Override
    public void process(CtClass<?> clazz) {
        log.info("class : {}", clazz.getQualifiedName());
        CtComment comment = getFactory().createComment("YOLO", CommentType.JAVADOC);
        clazz.addComment(comment);
    }
}
