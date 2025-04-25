package be.unamur.snail.register;

import java.util.List;

public class MethodContext implements CsvWritableContext {
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

    @Override
    public String getFileName() {
        return fileName;
    }

    @Override
    public String getClassName() {
        return className;
    }

    @Override
    public String toCsvRow() {
        String methodWithParameters = createMethodWithParameters(methodName, parameters);
        String traceStr = createStackTrace(stackTrace);
        return String.format("%s,%s,%s,%s", fileName, className, methodWithParameters, traceStr);
    }
}
