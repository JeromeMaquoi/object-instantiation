package be.unamur.snail.register;

public class AttributeEntityDTO {
    private String name;
    private String type;

    public AttributeEntityDTO(String name, String type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return "AttributeEntityDTO{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
