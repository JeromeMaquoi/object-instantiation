package be.unamur.snail.register;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterUtils {
    private final static Logger log = LoggerFactory.getLogger(RegisterUtils.class);

    public static void register(Object currentObject, Object fieldInitialization) {
        log.info("Current object : {}, field initialization : {}", currentObject, fieldInitialization);
    }
}
