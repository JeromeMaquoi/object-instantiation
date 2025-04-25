package be.unamur.snail.register;

public class AttributeContext {
    private final String name;
    private final String type;
    private final String actualType;

    public AttributeContext(String name, String type, String actualType) {
        this.name = name;
        this.type = type;
        this.actualType = actualType;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getActualType() {
        return actualType;
    }

    public String toCsvRow() {
        return String.format("[%s,%s]", type, actualType);
    }

    @Override
    public String toString() {
        return "AttributeEntityDTO{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", actualType='" + actualType + '\'' +
                '}';
    }
}
