package be.unamur.snail.register;

import java.util.List;

public class MethodContext {
    private final String fileName;
    private final String className;
    private final String methodName;
    private final List<String> parameters;
    private final List<StackTraceElement> stackTrace;

    public MethodContext(String fileName, String className, String methodName, List<String> parameters, List<StackTraceElement> stackTrace) {
        this.fileName = fileName;
        this.className = className;
        this.methodName = methodName;
        this.parameters = parameters;
        this.stackTrace = stackTrace;
    }

    public String toCsvRow() {
        String paramStr = String.join(";", parameters);
        String traceStr = stackTrace.stream()
                .map(StackTraceElement::toString)
                .reduce((a, b) -> a + ";" + b)
                .orElse("");
        return String.format("%s,%s,%s,%s,%s", fileName, className, methodName, paramStr, traceStr);
    }
}
