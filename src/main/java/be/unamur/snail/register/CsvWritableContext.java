package be.unamur.snail.register;

import java.util.List;

public interface CsvWritableContext {
    String getClassName();
    String getFileName();
    String toCsvRow();

    default String createMethodWithParameters(String methodName, List<String> parameters) {
        String paramStr = String.join(",", parameters);
        if (!parameters.isEmpty())  return "\"" + methodName + "/" + parameters.size() + "[" + paramStr + "]" + "\"";
        else return "\""+ methodName + "/" + 0 + "\"";
    }

    default String createStackTrace(List<StackTraceElement> stackTrace) {
        String traceStr = stackTrace.stream()
                .map(StackTraceElement::toString)
                .reduce((a, b) -> a + "," + b)
                .orElse("");
        return "\"" + traceStr + "\"";
    }
}
