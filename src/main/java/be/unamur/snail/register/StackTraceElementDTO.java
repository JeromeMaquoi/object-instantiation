package be.unamur.snail.register;

import java.util.Objects;

public class StackTraceElementDTO {
    private int lineNumber;
    private MethodElementDTO method;

    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public StackTraceElementDTO withLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
        return this;
    }

    public MethodElementDTO getMethod() {
        return method;
    }

    public void setMethod(MethodElementDTO method) {
        this.method = method;
    }

    public StackTraceElementDTO withMethod(MethodElementDTO method) {
        this.method = method;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        StackTraceElementDTO that = (StackTraceElementDTO) o;
        return lineNumber == that.lineNumber && Objects.equals(method, that.method);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lineNumber, method);
    }

    @Override
    public String toString() {
        return "StackTraceElementDTO{" +
                "lineNumber=" + lineNumber +
                ", method=" + method +
                '}';
    }
}