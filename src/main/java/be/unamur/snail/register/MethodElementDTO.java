package be.unamur.snail.register;

import java.util.Objects;

public class MethodElementDTO {
    private String fileName;
    private String className;
    private String methodName;

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

    public boolean isInitialized() {
        return this.fileName == null && this.className == null && this.methodName == null;
    }


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        MethodElementDTO that = (MethodElementDTO) o;
        return Objects.equals(fileName, that.fileName) && Objects.equals(className, that.className) && Objects.equals(methodName, that.methodName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fileName, className, methodName);
    }

    @Override
    public String toString() {
        return "MethodElementDTO{" +
                "fileName='" + fileName + '\'' +
                ", className='" + className + '\'' +
                ", methodName='" + methodName + '\'' +
                '}';
    }
}