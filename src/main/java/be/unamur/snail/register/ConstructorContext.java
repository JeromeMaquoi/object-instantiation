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

    public ConstructorContext() {}

    public ConstructorContext withFileName(String fileName) {
        this.fileName = fileName;
        return this;
    }

    public ConstructorContext withClassName(String className) {
        this.className = className;
        return this;
    }

    public ConstructorContext withMethodName(String methodName) {
        this.methodName = methodName;
        return this;
    }

    public ConstructorContext withParameters(List<String> parameters) {
        this.parameters = parameters;
        return this;
    }

    public ConstructorContext withAttributes(Set<AttributeContext> attributes) {
        this.attributes = attributes;
        return this;
    }

    public ConstructorContext withStackTrace(List<StackTraceElement> stackTrace) {
        this.stackTrace = stackTrace;
        return this;
    }

    public ConstructorContext withSnapshotFilePath(String snapshotFilePath) {
        this.snapshotFilePath = snapshotFilePath;
        return this;
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
        return String.format("%s,%s,%s,%s,%s,%s", fileName,className,constructorWithParameters,attributesToCsvRow(),traceString,snapshotFilePath);
    }

    public String attributesToCsvRow() {
        // TODO Comment s'assurer que les attributs seront toujours dans le même sens? Est-ce que la méthode "stream" est ordonnée ou non?
        if (this.attributes.isEmpty()) {
            return "\"0[]\"";
        }
        String attributesStr = this.attributes.stream()
                .map(AttributeContext::toCsvRow)
                .reduce((a,b)->a + "," + b)
                .orElse("");
        return "\"" + this.attributes.size() + "[" + attributesStr + "]\"";
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
