package be.unamur.snail;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtTry;

public class TryWithResourceProcessor extends AbstractProcessor<CtTry> {
    private final Logger log = LoggerFactory.getLogger(TryWithResourceProcessor.class);
    @Override
    public void process(CtTry ctTry) {
        log.info("Found a try block :\n{}", ctTry);
    }
}
