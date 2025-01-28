package be.unamur.snail.register;

import java.util.ArrayList;
import java.util.List;

public class StackTraceSnapshotElementDTO {
    private List<StackTraceElementDTO> ancestorsStackTraceElements = new ArrayList<>();
    private StackTraceElementDTO parent;
    private int lineNumber;
    private MethodElementDTO method;
    private String snapshot; // TODO

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
        StringBuilder builder = new StringBuilder();
        builder.append("StackTraceSnapshotElementDTO{\n  ancestorsStackTraceElements=\n");
        for (StackTraceElementDTO stackTraceElement : ancestorsStackTraceElements) {
            builder.append("      ").append(stackTraceElement).append("\n");
        }
        builder.append("\n  parent=").append(parent).append("\n");
        builder.append("\n  lineNumber=").append(lineNumber).append("\n");
        builder.append("\n  method=").append(method).append("\n");
        builder.append("\n  snapshot=").append(snapshot).append("\n");
        builder.append("}");
        return builder.toString();
        /*return "StackTraceSnapshotElementDTO{\n" +
                "  ancestorsStackTraceElements=" + ancestorsStackTraceElements +
                ",\n  parent=" + parent +
                ",\n  lineNumber=" + lineNumber +
                ",\n  method=" + method +
                '}';*/
    }
}
