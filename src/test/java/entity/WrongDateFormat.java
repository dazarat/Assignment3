package entity;

import task2.Property;

import java.time.Instant;

public class WrongDateFormat {

    @Property(format = "dd:MM:yyyy HH.mm")
    Instant timeProperty;

    public void setTimeProperty(Instant timeProperty) {
        this.timeProperty = timeProperty;
    }
}
