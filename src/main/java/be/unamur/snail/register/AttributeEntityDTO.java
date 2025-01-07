package be.unamur.snail.register;

public class AttributeEntityDTO {
    private String name;
    private String type;
    private String actualType;

    public AttributeEntityDTO(String name, String type, String actualType) {
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

    @Override
    public String toString() {
        return "AttributeEntityDTO{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", actualType='" + actualType + '\'' +
                '}';
    }
}
