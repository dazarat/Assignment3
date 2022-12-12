package entity;

import task2.Property;

public class WrongAnnotationNameClass {
    @Property(name="wrongPropertyName")
    private String name;


    public void setName(String name) {
        this.name = name;
    }

    public WrongAnnotationNameClass() {
    }
}
