package be.unamur.snail.register;

public class DefaultStackTraceProvider implements StackTraceProvider {
    @Override
    public StackTraceElement[] getStackTrace() {
        return Thread.currentThread().getStackTrace();
    }
}
