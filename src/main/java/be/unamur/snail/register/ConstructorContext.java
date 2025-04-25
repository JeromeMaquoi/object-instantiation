package be.unamur.snail.register;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ConstructorContext {
    private String fileName;
    private String className;
    private String methodName;
    private List<String> parameters;
    private Set<AttributeContext> attributes = new HashSet<>();
    private List<StackTraceElement> stackTrace;

    public ConstructorContext(String fileName, String className, String methodName, List<String> parameters, Set<AttributeContext> attributes, List<StackTraceElement> stackTrace) {
        this.fileName = fileName;
        this.className = className;
        this.methodName = methodName;
        this.parameters = parameters;
        this.attributes = attributes;
        this.stackTrace = stackTrace;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
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

    public boolean isEmpty() {
        return methodName == null && className == null && fileName == null;
    }

    @Override
    public String toString() {
        return "ConstructorEntityDTO{" +
                "signature='" + methodName + '\'' +
                ", className='" + className + '\'' +
                ", fileName='" + fileName + '\'' +
                ", attributeEntities=" + attributes +
                '}';
    }
}
