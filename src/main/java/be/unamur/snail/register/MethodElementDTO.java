package be.unamur.snail.register;

import java.util.*;

public class MethodElementDTO {
    private String fileName;
    private String className;
    private String methodName;
    private List<String> parameters;
    private boolean isConstructor;
    private Set<AttributeEntityDTO> constructorAttributes = new HashSet<>();

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
        isConstructor = constructor;
    }

    public MethodElementDTO isConstructor(boolean constructor) {
        isConstructor = constructor;
        return this;
    }

    public List<String> getParameters() {
        return parameters;
    }

    public void setParameters(List<String> parameters) {
        this.parameters = parameters;
    }

    public MethodElementDTO withParameters(List<String> parameters) {
        this.parameters = parameters;
        return this;
    }

    public Set<AttributeEntityDTO> getConstructorAttributes() {
        return constructorAttributes;
    }

    public void setConstructorAttributes(Set<AttributeEntityDTO> constructorAttributes) {
        this.constructorAttributes = constructorAttributes;
    }

    public MethodElementDTO withAttributes(Set<AttributeEntityDTO> attributes) {
        this.constructorAttributes = attributes;
        return this;
    }

    public void addAttribute(AttributeEntityDTO attribute) {
        this.constructorAttributes.add(attribute);
    }

    public boolean isInitialized() {
        return this.fileName == null && this.className == null && this.methodName == null;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        MethodElementDTO that = (MethodElementDTO) o;
        return isConstructor == that.isConstructor && Objects.equals(fileName, that.fileName) && Objects.equals(className, that.className) && Objects.equals(methodName, that.methodName) && Objects.equals(parameters, that.parameters) && Objects.equals(constructorAttributes, that.constructorAttributes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fileName, className, methodName, isConstructor, parameters, constructorAttributes);
    }

    @Override
    public String toString() {
        return "MethodElementDTO{" +
                "fileName='" + fileName + '\'' +
                ", className='" + className + '\'' +
                ", methodName='" + methodName + '\'' +
                ", parameters=" + parameters +
                ", isConstructor=" + isConstructor +
                ", constructorAttributes=" + constructorAttributes +
                '}';
    }
}