package be.unamur.snail.register;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class StackTraceDTO {
    private List<StackTraceElementDTO> stackTraceElements = new ArrayList<>();
    private List<Float> consumptionValues;

    public List<StackTraceElementDTO> getStackTraceElements() {
        return stackTraceElements;
    }

    public void setStackTraceElements(List<StackTraceElementDTO> stackTraceElements) {
        this.stackTraceElements = stackTraceElements;
    }

    public void addStackTraceElement(StackTraceElementDTO stackTraceElement) {
        this.stackTraceElements.add(stackTraceElement);
    }

    public List<Float> getConsumptionValues() {
        return consumptionValues;
    }

    public void setConsumptionValues(List<Float> consumptionValues) {
        this.consumptionValues = consumptionValues;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        StackTraceDTO that = (StackTraceDTO) o;
        return Objects.equals(stackTraceElements, that.stackTraceElements) && Objects.equals(consumptionValues, that.consumptionValues);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stackTraceElements, consumptionValues);
    }

    @Override
    public String toString() {
        return "StackTraceDTO{" +
                "stackTraceElements=" + stackTraceElements +
                ", consumptionValues=" + consumptionValues +
                '}';
    }
}