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

    ConstructorContext(){}

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

    public Set<AttributeContext> getAttributes() {
        return attributes;
    }

    public void addAttribute(AttributeContext attributeContext) {
        this.attributes.add(attributeContext);
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
