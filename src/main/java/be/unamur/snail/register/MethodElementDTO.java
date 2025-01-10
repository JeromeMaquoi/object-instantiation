package be.unamur.snail.register;

import java.util.List;
import java.util.Objects;

public class MethodElementDTO {
    private String fileName;
    private String className;
    private String methodName;
    private boolean isConstructor;
    private List<AttributeEntityDTO> attributes;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public MethodElementDTO withFileName(String fileName) {
        this.fileName = fileName;
        return this;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public MethodElementDTO withClassName(String className) {
        this.className = className;
        return this;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public MethodElementDTO withMethodName(String methodName) {
        this.methodName = methodName;
        return this;
    }

    public boolean isConstructor() {
        return isConstructor;
    }

    public void setConstructor(boolean constructor) {
        this.isConstructor = constructor;
    }

    public MethodElementDTO withConstructor(boolean constructor) {
        this.isConstructor = constructor;
        return this;
    }

    public List<AttributeEntityDTO> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<AttributeEntityDTO> attributes) {
        this.attributes = attributes;
    }

    public void addAttribute(AttributeEntityDTO attribute) {
        this.attributes.add(attribute);
    }

    public boolean isInitialized() {
        return this.fileName == null && this.className == null && this.methodName == null;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        MethodElementDTO that = (MethodElementDTO) o;
        return isConstructor == that.isConstructor && Objects.equals(fileName, that.fileName) && Objects.equals(className, that.className) && Objects.equals(methodName, that.methodName) && Objects.equals(attributes, that.attributes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fileName, className, methodName, isConstructor, attributes);
    }

    @Override
    public String toString() {
        return "MethodElementDTO{" +
                "filePath='" + fileName + '\'' +
                ", className='" + className + '\'' +
                ", methodName='" + methodName + '\'' +
                ", isConstructor=" + isConstructor +
                ", attributes=" + attributes +
                '}';
    }
}
