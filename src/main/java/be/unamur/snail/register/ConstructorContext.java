package be.unamur.snail.register;

import java.util.List;
import java.util.Set;

public class ConstructorContext implements CsvWritableContext {
    private String fileName;
    private String className;
    private String methodName;
    private List<String> parameters;
    private Set<AttributeContext> attributes;
    private List<StackTraceElement> stackTrace;
    private String snapshotFilePath;

    public ConstructorContext(String fileName, String className, String methodName, List<String> parameters, Set<AttributeContext> attributes, List<StackTraceElement> stackTrace) {
        this.fileName = fileName;
        this.className = className;
        this.methodName = methodName;
        this.parameters = parameters;
        this.attributes = attributes;
        this.stackTrace = stackTrace;
        this.snapshotFilePath = "";
    }

    public String getMethodName() {
        return methodName;
    }

    @Override
    public String getClassName() {
        return className;
    }

    @Override
    public String getFileName() {
        return fileName;
    }

    @Override
    public String toCsvRow() {
        String constructorWithParameters = createMethodWithParameters(methodName, parameters);
        String traceString = createStackTrace(stackTrace);
        return String.format("%s,%s,%s,%s,%s,%s,%s", fileName,className,constructorWithParameters,attributes.size(),attributesToCsvRow(),traceString,snapshotFilePath);
    }

    public String attributesToCsvRow() {
        String attributesStr = this.attributes.stream()
                .map(AttributeContext::toCsvRow)
                .reduce((a,b)->a + "," + b)
                .orElse("");
        return "\"" + attributesStr + "\"";
    }

    public List<String> getParameters() {
        return parameters;
    }

    public Set<AttributeContext> getAttributes() {
        return attributes;
    }

    public void addAttribute(AttributeContext attributeContext) {
        this.attributes.add(attributeContext);
    }

    public List<StackTraceElement> getStackTrace() {
        return stackTrace;
    }

    public String getSnapshotFilePath() {
        return snapshotFilePath;
    }

    public void setSnapshotFilePath(String snapshotFilePath) {
        this.snapshotFilePath = snapshotFilePath;
    }

    public boolean isEmpty() {
        return methodName == null && className == null && fileName == null;
    }

    @Override
    public String toString() {
        return "ConstructorContext{" +
                "fileName='" + fileName + '\'' +
                ", className='" + className + '\'' +
                ", methodName='" + methodName + '\'' +
                ", parameters=" + parameters +
                ", attributes=" + attributes +
                ", stackTrace=" + stackTrace +
                ", snapshotFilePath='" + snapshotFilePath + '\'' +
                '}';
    }
}
