package be.unamur.snail.register;

import java.util.ArrayList;
import java.util.List;

public class StackTraceSnapshotElementDTO {
    private List<StackTraceElementDTO> ancestorsStackTraceElements = new ArrayList<>();
    private StackTraceElementDTO parent;
    private int lineNumber;
    private MethodElementDTO method;

    public List<StackTraceElementDTO> getAncestorsStackTraceElements() {
        return ancestorsStackTraceElements;
    }

    public void setAncestorsStackTraceElements(List<StackTraceElementDTO> ancestorsStackTraceElements) {
        this.ancestorsStackTraceElements = ancestorsStackTraceElements;
    }

    public void addAncestorStackTraceElementDTO(StackTraceElementDTO stackTraceElement) {
        this.ancestorsStackTraceElements.add(stackTraceElement);
    }

    public StackTraceElementDTO getParent() {
        return parent;
    }

    public void setParent(StackTraceElementDTO parent) {
        this.parent = parent;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public MethodElementDTO getMethod() {
        return method;
    }

    public void setMethod(MethodElementDTO method) {
        this.method = method;
    }

    @Override
    public String toString() {
        return "StackTraceSnapshotElementDTO{" +
                "ancestorsStackTraceElements=" + ancestorsStackTraceElements +
                ", parent=" + parent +
                ", lineNumber=" + lineNumber +
                ", method=" + method +
                '}';
    }
}
