package be.unamur.snail.register;

import java.util.HashSet;
import java.util.Set;

public class ConstructorEntityDTO {
    private String name;
    private String signature;
    private String className;
    private String fileName;
    private Set<AttributeEntityDTO> attributeEntities;

    ConstructorEntityDTO(){}

    public void setName(String name) {
        this.name = name;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void addAttributeEntity(AttributeEntityDTO attributeEntityDTO) {
        attributeEntities.add(attributeEntityDTO);
    }

    public boolean isEmpty() {
        return name != null && signature == null && className == null && fileName == null;
    }
}
