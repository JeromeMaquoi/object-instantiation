package be.unamur.snail.register;

import java.util.HashSet;
import java.util.Set;

public class ConstructorEntityDTO {
    private String name;
    private String signature;
    private String className;
    private String fileName;
    private Set<AttributeEntityDTO> attributeEntities = new HashSet<>();

    ConstructorEntityDTO(){}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Set<AttributeEntityDTO> getAttributeEntities() {
        return attributeEntities;
    }

    public void addAttributeEntity(AttributeEntityDTO attributeEntityDTO) {
        this.attributeEntities.add(attributeEntityDTO);
    }

    public boolean isEmpty() {
        return name == null && signature == null && className == null && fileName == null;
    }

    @Override
    public String toString() {
        return "ConstructorEntityDTO{" +
                "name='" + name + '\'' +
                ", signature='" + signature + '\'' +
                ", className='" + className + '\'' +
                ", fileName='" + fileName + '\'' +
                ", attributeEntities=" + attributeEntities +
                '}';
    }
}
