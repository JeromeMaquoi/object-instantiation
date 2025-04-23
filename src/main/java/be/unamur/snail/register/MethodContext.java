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
        String methodWithParameters = createMethodWithParameters();
        String traceStr = createStackTrace();
        return String.format("%s,%s,%s,%s", fileName, className, methodWithParameters, traceStr);
    }

    public String createMethodWithParameters() {
        String paramStr = String.join(",", parameters);
        if (!parameters.isEmpty())  return "\"" + methodName + "/" + parameters.size() + "[" + paramStr + "]" + "\"";
        else return "\""+ methodName + "/" + 0 + "\"";
    }

    public String createStackTrace() {
        String traceStr = stackTrace.stream()
                .map(StackTraceElement::toString)
                .reduce((a, b) -> a + "," + b)
                .orElse("");
        return "\"" + traceStr + "\"";
    }
}
