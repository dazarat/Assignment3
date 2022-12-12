package entity;

import task2.Property;

import java.time.Instant;
import java.util.Objects;

public class TestClassSuccess {

    private String stringProperty;

    @Property(name="customProperty")
    private String wrongName;

    private Integer numberProperty;

    @Property(name="numberProperty")
    private int myNumber;

    @Property(format = "dd.MM.yyyy HH:mm")
    private Instant timeProperty;

    @Property(name="timeProperty", format = "dd.MM.yyyy HH:mm")
    private Instant time;

    public void setStringProperty(String stringProperty) {
        this.stringProperty = stringProperty;
    }

    public void setWrongName(String wrongName) {
        this.wrongName = wrongName;
    }

    public void setNumberProperty(Integer numberProperty) {
        this.numberProperty = numberProperty;
    }

    public void setMyNumber(int myNumber) {
        this.myNumber = myNumber;
    }

    public void setTimeProperty(Instant timeProperty) {
        this.timeProperty = timeProperty;
    }

    public void setTime(Instant time) {
        this.time = time;
    }

    public String getStringProperty() {
        return stringProperty;
    }

    public String getWrongName() {
        return wrongName;
    }

    public Integer getNumberProperty() {
        return numberProperty;
    }

    public int getMyNumber() {
        return myNumber;
    }

    public Instant getTimeProperty() {
        return timeProperty;
    }

    public Instant getTime() {
        return time;
    }

    public TestClassSuccess() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TestClassSuccess that = (TestClassSuccess) o;
        return getMyNumber() == that.getMyNumber() && Objects.equals(getStringProperty(), that.getStringProperty()) && Objects.equals(getWrongName(), that.getWrongName()) && Objects.equals(getNumberProperty(), that.getNumberProperty()) && Objects.equals(getTimeProperty(), that.getTimeProperty()) && Objects.equals(getTime(), that.getTime());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getStringProperty(), getWrongName(), getNumberProperty(), getMyNumber(), getTimeProperty(), getTime());
    }
}
