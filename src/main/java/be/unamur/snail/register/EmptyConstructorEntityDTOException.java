package be.unamur.snail.register;

public class EmptyConstructorEntityDTOException extends RuntimeException {
    public EmptyConstructorEntityDTOException(Exception e) {
        super("Empty constructor entity exception " + e);
    }
}
